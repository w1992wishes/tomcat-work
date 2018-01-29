package me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Constants;
import me.w1992wishes.tomcatwork.simple_tomcat_05.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_05.LifecycleListener;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.Connector;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_05.exception.LifecycleException;
import me.w1992wishes.tomcatwork.simple_tomcat_05.net.DefaultServerSocketFactory;
import me.w1992wishes.tomcatwork.simple_tomcat_05.net.ServerSocketFactory;
import me.w1992wishes.tomcatwork.simple_tomcat_05.util.LifecycleSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;
import java.util.Vector;

public class HttpConnector  implements Connector, Lifecycle, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnector.class);

    /**
     * Has this component been initialized yet?
     */
    private boolean initialized = false;

    /**
     * Has this component been started yet?
     */
    private boolean started = false;

    /**
     * The shutdown signal to our background thread
     */
    private boolean stopped = false;

    private ServerSocket serverSocket = null;

    private String address = null;

    private int port = 8080;

    private int acceptCount = 10;

    private ServerSocketFactory factory = null;

    private String threadName = null;

    /**
     * The background thread.
     */
    private Thread thread = null;

    /**
     * Timeout value on the incoming connection.
     * Note : a value of 0 means no timeout.
     */
    private int connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    /**
     * Use TCP no delay ?
     */
    private boolean tcpNoDelay = true;

    /**
     * The thread synchronization object.
     */
    private Object threadSync = new Object();

    /**
     * The set of processors that have been created but are not currently
     * being used to process a request.
     */
    private Stack processors = new Stack();

    /**
     * The set of processors that have ever been created.
     */
    private Vector created = new Vector();

    /**
     * The minimum number of processors to start at initialization time.
     */
    protected int minProcessors = 5;

    /**
     * The maximum number of processors allowed, or <0 for unlimited.
     */
    private int maxProcessors = 20;

    /**
     * The current number of processors that have been created.
     */
    private int curProcessors = 0;

    /**
     * The Container used for processing requests received by this Connector.
     */
    protected Container container = null;

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * Create (or allocate) and return an available processor for use in
     * processing a specific HTTP request, if possible.  If the maximum
     * allowed processors have already been created and are in use, return
     * <code>null</code> instead.
     */
    private HttpProcessor createProcessor() {

        synchronized (processors) {
            //如果processors池中有，则直接返回
            if (processors.size() > 0) {
                return ((HttpProcessor) processors.pop());
            }
            //如果processors池中没有，则判断当前processors是否大于最大processors数，小于则新建
            if ((maxProcessors > 0) && (curProcessors < maxProcessors)) {
                return (newProcessor());
            } else {
                if (maxProcessors < 0) {
                    return (newProcessor());
                } else {
                    return (null);
                }
            }
        }

    }

    /**
     * Create and return a new processor suitable for processing HTTP
     * requests and returning the corresponding responses.
     */
    private HttpProcessor newProcessor() {

        HttpProcessor processor = new HttpProcessor(this, curProcessors++);
        if (processor instanceof Lifecycle) {
            try {
                ((Lifecycle) processor).start();
            } catch (LifecycleException e) {
                LOGGER.error("new Processor failure", e);
                return (null);
            }
        }
        created.addElement(processor);
        return (processor);

    }

    /**
     * Recycle the specified Processor so that it can be used again.
     *
     */
    void recycle(HttpProcessor processor) {
        processors.push(processor);
    }

    @Override
    public void run() {

        // Loop until we receive a shutdown command
        while (!stopped){
            // 第一步，Accept the next incoming connection from the server socket
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                if (connectionTimeout > 0){
                    socket.setSoTimeout(connectionTimeout);
                }
                socket.setTcpNoDelay(tcpNoDelay);
            } catch (IOException e) {
                try {
                    // If reopening fails, exit
                    synchronized (threadSync) {
                        if (started && !stopped){
                            LOGGER.error("accept error: ", e);
                        }
                        if (!stopped) {
                            serverSocket.close();
                            serverSocket = open();
                        }
                    }
                } catch (IOException ioe){
                    LOGGER.error("socket reopen, io problem: ", ioe);
                    break;
                }

                continue;
            }

            // 第二步、Hand this socket off to an appropriate processor
            HttpProcessor processor = createProcessor();
            if (processor == null) {
                try {
                    LOGGER.error("httpConnector.noProcessor");
                    socket.close();
                } catch (IOException e) {
                    ;
                }
                continue;
            }
            processor.assign(socket);

            // The processor will recycle itself when it finishes
        }

        synchronized (threadSync) {
            threadSync.notifyAll();
        }

    }

    @Override
    public void initialize() {
        if (initialized){
            LOGGER.error("httpConnector has initialized");
            throw new RuntimeException ();
        }

        this.initialized=true;

        // Establish a server socket on the specified port
        try {
            serverSocket = open();
        }catch (IOException e){
            LOGGER.error("open server socket failure", e);
            throw new RuntimeException(e);
        }
    }

    private ServerSocket open() throws IOException {

        ServerSocketFactory factory = getFactory();

        // If no address is specified, open a connection on all addresses
        if (address == null) {
            LOGGER.error("httpConnector.allAddresses");
            try {
                return (factory.createSocket(port, acceptCount));
            } catch (BindException be){
                throw new BindException(be.getMessage() + ":" + port);
            }
        }

        // Open a server socket on the specified address
        try {
            InetAddress is = InetAddress.getByName(address);
            LOGGER.error("httpConnector.anAddress {}", address);
            try {
                return (factory.createSocket(port, acceptCount, is));
            } catch (BindException be) {
                throw new BindException(be.getMessage() + ":" + address +  ":" + port);
            }
        } catch (Exception e) {
            LOGGER.error("httpConnector.noAddress {}", address);
            try {
                return (factory.createSocket(port, acceptCount));
            } catch (BindException be) {
                throw new BindException(be.getMessage() + ":" + port);
            }
        }

    }

    public ServerSocketFactory getFactory() {

        if (this.factory == null) {
            synchronized (this) {
                this.factory = new DefaultServerSocketFactory();
            }
        }
        return (this.factory);

    }

    /**
     * Start the background processing thread.
     */
    private void threadStart() {

        LOGGER.info("httpConnector.starting");

        thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * Stop the background processing thread.
     */
    private void threadStop() {

        LOGGER.info("httpConnector.stopping");

        stopped = true;
        try {
            threadSync.wait(5000);
        } catch (InterruptedException e) {
            ;
        }
        thread = null;

    }

    @Override
    public void start() throws LifecycleException {
        if(started){
            LOGGER.error("httpConnector.alreadyStarted");
            throw new LifecycleException("httpConnector.alreadyStarted");
        }
        threadName = "HttpConnector[" + port + "]";
        lifecycle.fireLifecycleEvent(START_EVENT, null);
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

    @Override
    public void stop() throws LifecycleException {
        // Validate and update our current state
        if (!started)
            throw new LifecycleException("httpConnector.notStarted");
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        // Gracefully shut down all processors we have created
        for (int i = created.size() - 1; i >= 0; i--) {
            HttpProcessor processor = (HttpProcessor) created.elementAt(i);
            if (processor instanceof Lifecycle) {
                try {
                    ((Lifecycle) processor).stop();
                } catch (LifecycleException e) {
                    LOGGER.error("HttpConnector.stop", e);
                }
            }
        }

        synchronized (threadSync) {
            // Close the server socket we were using
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    ;
                }
            }
            // Stop our background thread
            threadStop();
        }
        serverSocket = null;
    }

    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    /**
     * Get the lifecycle listeners associated with this lifecycle. If this
     * Lifecycle has no listeners registered, a zero-length array is returned.
     */
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycle.findLifecycleListeners();
    }

    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to add
     */
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    /**
     * Return the Container used for processing requests received by this
     * Connector.
     */
    public Container getContainer() {
        return (container);
    }

    /**
     * Set the Container used for processing requests received by this
     * Connector.
     *
     * @param container The new Container to use
     */
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * Return the port number on which we listen for HTTP requests.
     */
    public int getPort() {
        return (this.port);
    }
}