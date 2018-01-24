# Tomcat Work

## 一、前言

当初第一次看《深入剖析tomcat》时，是去年刚学java没多久，那时虽说能看懂，但肯定是没领会到里面的设计思想，
只能算是跟着代码走了一遍，基本上没思考，这次打算把代码再看看，看能不能多悟出点东西。

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


