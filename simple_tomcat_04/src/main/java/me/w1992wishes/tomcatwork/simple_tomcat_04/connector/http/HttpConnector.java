package me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http;

import me.w1992wishes.tomcatwork.simple_tomcat_04.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.Connector;
import me.w1992wishes.tomcatwork.simple_tomcat_04.net.DefaultServerSocketFactory;
import me.w1992wishes.tomcatwork.simple_tomcat_04.net.ServerSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class HttpConnector  implements Connector, Lifecycle, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnector.class);

    private boolean initialized = false;

    private ServerSocket serverSocket = null;

    private String address = null;

    private int port = 8080;

    private int acceptCount = 10;

    private ServerSocketFactory factory = null;

    @Override
    public void run() {

    }

    @Override
    public void initialize() {
        if (initialized){
            LOGGER.error("has initialized");
            throw new RuntimeException ();
        }

        this.initialized=true;
        Exception eRethrow = null;

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
            return (factory.createSocket(port, acceptCount));
        }

        // 获取本机上所有可能的InetAddress
        InetAddress[] addresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
        int i;
        for (i = 0; i < addresses.length; i++) {
            if (addresses[i].getHostAddress().equals(address))
                break;
        }
        if (i < addresses.length) {
            return (factory.createSocket(port, acceptCount, addresses[i]));
        } else {
            return (factory.createSocket(port, acceptCount));
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

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }
}