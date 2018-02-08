# Tomcat Work

## 一、前言

当初第一次看《深入剖析tomcat》时，是去年刚学java没多久，那时虽说能看懂，但肯定是没领会到里面的设计思想，
只能算是跟着代码走了一遍，基本上没思考，这次打算把代码再看看，看能不能多悟出点东西。

有一点需要声明，《深入剖析tomcat》是以tomcat4为样本进行分析的，版本老了一点，但大体的设计思想并不会有太大的变化。

## 二、模块介绍

### 2.1、simple_tomcat01

一个简单的程序，只是创建一个ServerSocket，监听8080端口，然后解析监听到的socket，简单返回一个静态文本，意在了解tomcat底层是
依据socket通信；

```
private static void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
        }
        //Loop waiting for a request
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();
                //create Request object and parse
                Request request = new Request(input);
                request.parse();

                //create Response object
                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                //close the socket
                socket.close();

                //check if the previous uri is a shutdown command
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (IOException e) {
            }
        }
    }
```

### 2.2、simple_tomcat02

simple_tomcat01只能加载静态资源，simple_tomcat02区分静态资源和Servlet的加载，先从监听到的连接中获取url，从url中获取servlet
的名字，然后是利用反射生成servlet实例，调用servlet的service方法:

```
protected void action(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_ROOT);
            //the classPath of repository is taken from the
            //createClassLoader method n
            //org.apache.catalina.startup.ClassLoaderFactory
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            LOGGER.debug("class path: {}", classPath.getCanonicalPath());
            LOGGER.debug("url: {}", repository);
            //the code for forming the URL is taken form
            //the addRepository method in
            //org.apache.catalina.loader.StandardClassLoader
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            LOGGER.error("servlet process fail", e);
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class fail", e);
        }

        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            RequestFacade requestFacade = new RequestFacade(request);
            ResponseFacade responseFacade = new ResponseFacade(response);
            servlet.service((ServletRequest) requestFacade, (ServletResponse) responseFacade);
        } catch (Exception e) {
            LOGGER.error("", e);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }
```

在simple_tomcat02中使用RequestFacade和ResponseFacade是因为request中的parse方法和response的sendStaticResource方法不应该在servlet被使用，
使用Facade进行限制；

### 2.3、simple_tomcat03

simple_tomcat03在simple_tomcat02的基础上进行了拆分和补充，首先增加了一个专门的启动类Bootstrap：

```java
public final class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
```

其次HttpServer一分为二，一是HttpConnector，起一个线程专门用来接收http请求：

```
 public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            LOGGER.error("connect socket failure", e);
            System.exit(1);
        }
        while (!stopped) {
            // Accept the next incoming connection from the server socket
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                LOGGER.debug("-------- debug: {} ------------", Thread.currentThread().getName());
            } catch (Exception e) {
                continue;
            }
            // Hand this socket off to an HttpProcessor
            HttpProcessor processor = new HttpProcessor(this);
            processor.process(socket);
        }
    }
```


二是HttpProcessor，用于创建HttpRequest、HttpResponse，并做一些解析任务，最后同样交由Processor（这个Processor是根据责任链模式
设计，用来处理静态资源或者加载Servlet的）来处理：

```
public void process(Socket socket) {
        SocketInputStream input = null;
        OutputStream output = null;
        try {
            input = new SocketInputStream(socket.getInputStream(), 2048);
            output = socket.getOutputStream();

            // create HttpRequest object and parse
            request = new HttpRequest(input);

            // create HttpResponse object
            response = new HttpResponse(output);
            response.setRequest(request);

            response.setHeader("Server", "W1992wishes Servlet Container");

            parseRequest(input, output);
            parseHeaders(input);

            //check if this is a request for a servlet or a static resource
            //a request for a servlet begins with "/servlet/"
            Processor servletProcessor = new ServletProcessor();
            Processor staticProcessor = new StaticResourceProcessor();
            Processor defaultProcessor = new DefaultProcessor();
            staticProcessor.setProcessor(defaultProcessor);
            servletProcessor.setProcessor(staticProcessor);
            servletProcessor.process(request, response);

            // Close the socket
            socket.close();
            // no shutdown for this application
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

其他新增的类都是辅助解析request。

### 2.4、simple_tomcat04

simple_tomcat03只能一个一个接受请求，simple_tomcat04进行了改进，可以接受多个请求。

首先一个专门的启动类，创建默认连接器HttpConnector：

```
public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        SimpleContainer container = new SimpleContainer();
        connector.setContainer(container);

        try {
            // initialize主要是open服务端socket，默认监听8080端口
            connector.initialize();
            //start主要是将服务端监听的socket交给processor处理，其中processor添加了一个缓存池，避免了每次都创建一个processor
            connector.start();

            //make the application wait util we press any key
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

HttpConnector的initialize()方法创建ServerSocket，监听8080端口：

```
public void initialize() {
        if (initialized){
            throw new RuntimeException ();
        }
        // Establish a server socket on the specified port
        try {
            serverSocket = open();
        }catch (IOException e){
        }
    }
```

HttpConnector的start()方法做两件事，一件事是开一个线程循环接受socket，接收到了就交给HttpProcessor，继续等待接受新来的socket
另一件事是默认创建5个HttpProcessor，每个HttpProcessor都会启动一个线程，等待处理分配来的socket：

```
public void start() throws LifecycleException {
        if(started){
            throw new LifecycleException("httpConnector.alreadyStarted");
        }
        threadName = "HttpConnector[" + port + "]";
        started = true;

        // Start our background thread， 在一个独立线程中处理到达的连接
        threadStart();

        // Create the specified minimum number of processors， 默认先创建最小数量的processor，放入空闲栈中
        while (curProcessors < minProcessors) {
            if ((maxProcessors > 0) && (curProcessors >= maxProcessors))
                break;
            HttpProcessor processor = newProcessor();
            recycle(processor);
        }
    }
```

之所以能够同时处理多个连接请求的原理就在这，HttpConnector只接受Socket，然后分配给已经创建好了的多个HttpProcessor，由HttpProcessor
另起线程去处理请求，HttpProcessor的run方法：

```
public void run() {

        // Process requests until we receive a shutdown signal
        while (!stopped) {

            // Wait for the next socket to be assigned
            Socket socket = await();
            if (socket == null) {
                continue;
            }

            // Process the request from this socket
            try {
                process(socket);
            } catch (Throwable t) {
            }

            // Finish up this request
            connector.recycle(this);

        }

        // Tell threadStop() we have shut ourselves down successfully
        synchronized (threadSync) {
            threadSync.notifyAll();
        }

    }
```

这里为了节省资源，HttpProcessor处理完一个请求后，会被回收到池中，等待分配下一个连接请求。

simple_tomcat04不同于simple_tomcat03的另外一点是出现了容器的概念，HttpProcessor只创建HttpRequest和HttpResponse，但并不处理
业务逻辑，业务逻辑都放到SimpleContainer处理：

```
public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
        //check if this is a request for a servlet or a static resource
        //a request for a servlet begins with "/servlet/"
        Processor servletProcessor = new ServletProcessor();
        Processor staticProcessor = new StaticResourceProcessor();
        Processor defaultProcessor = new DefaultProcessor();
        staticProcessor.setProcessor(defaultProcessor);
        servletProcessor.setProcessor(staticProcessor);
        servletProcessor.process(request, response);
    }
```

### 2.5 simple_tomcat05

代码增加了很多，但貌似都没有用起来，反而有干扰作用，但后续总有用的，就还是放在那吧。

simple_tomcat05旨在理解tomcat的容器设计：

首先要明白，tomcat4中提供了engine，host，context及wrapper四种容器：

1. Enigne：Engine是最顶层的容器，它是host容器的组合。其标准实现类为：StandardEngine。
2. Host：Host是engine的子容器，它是context容器的集合。其标准实现类为：StandardHost。
3. Context：Context是host的子容器，它是wrapper容器的集合。其标准实现类为：StandardContext，StandardContext是tomcat中最大的一个类。它封装的是每个web app。
4. Wrapper：Wrapper是context的子容器，它封装的处理资源的每个具体的servlet。其标准实现类为：StandardWrapper。

关于容器的概念，可以参考一篇博客，简单了解一下：

[Tomcat架构分析之Container容器](http://blog.csdn.net/chen_fly2011/article/details/54930410)

simple_tomcat04中已经出现过简单容器的概念，tomcat由连接器（像HttpConnector）监听请求，然后将请求交给处理器（像HttpProcessor）
解析连接，解析请求头，解析请求体，创建请求对象等，然后具体的处理是交给容器的。

Tomcat的容器设计很有层次感，大的容器由小的容器组成，同时每个容器又都包含Pipeline，Valve，以责任链模式的形式进行调用。

了解了一些基本原理后，看一下tomcat的类设计：

![](http://p35fthlny.bkt.clouddn.com/20180126_simple_tomcat_01.png)

四个层次的容器都有一个标准实现，同时又提供了一个ContainerBase作为标准实现的抽象父类，提供一些通用的方法，设计是很清晰的。

simple_tomcat05的代码分两部分：

跟着BootStrap01的代码走，是简单的Wrapper容器实现，抛开LifeCycle相关的代码，和Simple_tomcat04的区别就在于引入了管道
Pipeline和阀Valve；

跟着BootStrap02的代码走，是简单的Context容器实现，是在Wrapper容器的基础上，再引入了Context容器，也好理解；

管道和阀的设计是基于责任链模式，Pipeline维护Valve数组，可以增加和移出，并且都有一个BasicValve，然后是通过Pipeline的内部类
ValveContext来实现遍历的。

### 2.6 simple_tomcat06

simple_tomcat06 旨在了解tomcat的生命周期实现。

tomcat4中有一个Lifecycle接口：

```java
public interface Lifecycle {

    // ----------------------------------------------------- Manifest Constants

    String START_EVENT = "start";

    String BEFORE_START_EVENT = "before_start";

    String AFTER_START_EVENT = "after_start";

    String STOP_EVENT = "stop";

    String BEFORE_STOP_EVENT = "before_stop";

    String AFTER_STOP_EVENT = "after_stop";

    // --------------------------------------------------------- Public Methods

    void start() throws LifecycleException;

    void stop() throws LifecycleException;

    void addLifecycleListener(LifecycleListener listener);

    LifecycleListener[] findLifecycleListeners();

    void removeLifecycleListener(LifecycleListener listener);

}
```

几乎所有的组件都实现了这个接口，像StandardContext，StandardWrapper，StandardPipeline，StandardContextValve等等；

还有一个事件LifecycleEvent，事件的类型定义在Lifecycle接口中:

```
 public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
        super(lifecycle);
        this.lifecycle = lifecycle;
        this.type = type;
        this.data = data;
    }
```

最后还有一个LifecycleListener:

```java
public interface LifecycleListener {

    /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event LifecycleEvent that has occurred
     */
    void lifecycleEvent(LifecycleEvent event);

}
```

这三个类是典型的事件-监听器设计，Lifecycle充当事件源，LifecycleListener是监听器，LifecycleEvent是事件，为了更好复用，还提供了
LifecycleSupport工具类:

![](http://p35fthlny.bkt.clouddn.com/20180129_simple_tomcat_02.png)

它们之间的UML类图大致如下：

![](http://p35fthlny.bkt.clouddn.com/20180129_simple_tomcat_03.png)

这里面LifecycleSupport这个工具类充当了事件源的角色，注册到里面的监听器接收到事件产生时，就会触发相应动作。

simple_tomcat06的代码很简单，就增加了一个简单监听器，simple_tomcat05中关于生命周期的代码在这里才正式用上。

在BootStrap中添加如下：

![](http://p35fthlny.bkt.clouddn.com/20180129_simple_tomcat_04.png)

### 2.7 simple_tomcat07

这个子项目主要用来了解tomcat04的类加载机制。

我们知道java的双亲委派模型，加载某个类时，先是通过Bootstrap ClassLoader加载，然后再通过ExtClassLoader加载，再就是AppClassLoader
，这是出于安全性的考虑。

一个Servlet容器应该有自定义的类加载，不应该直接使用系统类加载器，因为Servlet不应该完全信任它正在运行的某个Servlet类，如果使用
系统类加载器载入servlet类使用的所有类，那么servlet就能够访问所有的类，包括当前运行的JVM中环境变量CLASSPATH指明路径下所有的类，
这十分危险。servlet应该只运行载入WEB-INF/classes目录下的类和从WEB-INF/lib目录下载入类。

一般而言tomcat的类加载器会和一个Context相关联，因为不同的应用应该有自己的加载器，只加载自己目录下的类。

比较复杂，没有完全写通，懒癌又犯了，可以大概看一下WebappClassLoader的loadClass方法，了解一下tomcat的加载机制。

Therefore, from the perspective of a web application, class or resource loading looks in the following repositories, in this order:

Bootstrap classes of your JVM
/WEB-INF/classes of your web application
/WEB-INF/lib/*.jar of your web application
System class loader classes (described above)
Common class loader classes (described above)

If the web application class loader is configured with <Loader delegate="true"/> then the order becomes:

Bootstrap classes of your JVM
System class loader classes (described above)
Common class loader classes (described above)
/WEB-INF/classes of your web application
/WEB-INF/lib/*.jar of your web application









