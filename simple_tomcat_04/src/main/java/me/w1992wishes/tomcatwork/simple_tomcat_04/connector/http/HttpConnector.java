package me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http;

import me.w1992wishes.tomcatwork.simple_tomcat_04.Constants;
import me.w1992wishes.tomcatwork.simple_tomcat_04.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.Connector;
import me.w1992wishes.tomcatwork.simple_tomcat_04.exception.LifecycleException;
import me.w1992wishes.tomcatwork.simple_tomcat_04.net.DefaultServerSocketFactory;
import me.w1992wishes.tomcatwork.simple_tomcat_04.net.ServerSocketFactory;
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
     * Create (or allocate) and return an available processor for use in
     * processing a specific HTTP request, if possible.  If the maximum
     * allowed processors have already been created and are in use, return
     * <code>null</code> instead.
     */
    private HttpProcessor createProcessor() {

        synchronized (processors) {
            if (processors.size() > 0) {
                return ((HttpProcessor) processors.pop());
            }
            if ((maxProcessors > 0) && (curProcessors < maxProcessors)) {
                return (newProcessor());
            } else {
                if (maxProcessors < 0) {
                    // if (debug >= 2)
                    // log("createProcessor: Creating new processor");
                    return (newProcessor());
                } else {
                    // if (debug >= 2)
                    // log("createProcessor: Cannot create new processor");
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


    @Override
    public void run() {

        // Loop until we receive a shutdown command
        while (!stopped){
            // Accept the next incoming connection from the server socket
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

            // Hand this socket off to an appropriate processor
            HttpProcessor processor = createProcessor();
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

    @Override
    public void start() throws LifecycleException {
        if(started){
            LOGGER.error("httpConnector.alreadyStarted");
            throw new LifecycleException("httpConnector.alreadyStarted");
        }
        threadName = "HttpConnector[" + port + "]";
        started = true;

        // Start our background thread
        threadStart();

        // Create the specified minimum number of processors
    }

    @Override
    public void stop(){

    }
}