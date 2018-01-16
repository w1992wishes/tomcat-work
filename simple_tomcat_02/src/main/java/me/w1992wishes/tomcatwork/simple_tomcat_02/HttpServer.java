package me.w1992wishes.tomcatwork.simple_tomcat_02;

import me.w1992wishes.tomcatwork.simple_tomcat_02.processor.DefaultProcessor;
import me.w1992wishes.tomcatwork.simple_tomcat_02.processor.Processor;
import me.w1992wishes.tomcatwork.simple_tomcat_02.processor.ServletProcessor;
import me.w1992wishes.tomcatwork.simple_tomcat_02.processor.StaticResourceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wanqinfeng on 2017/1/24.
 */
public class HttpServer {
    //log
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    //shutdown command
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    //the shutdown command receiver
    private static boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    private void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            LOGGER.error("server socket create fail!", e);
            System.exit(1);
        }

        //loop wait for a request
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

                //check if this is a request for a servlet or
                //a static resource
                //a request for a servlet begins with "/servlet/"
                Processor servletProcessor = new ServletProcessor();
                Processor staticProcessor = new StaticResourceProcessor();
                Processor defaultProcessor = new DefaultProcessor();
                staticProcessor.setProcessor(defaultProcessor);
                servletProcessor.setProcessor(staticProcessor);
                servletProcessor.process(request, response);

                //close the socket
                socket.close();
                //check if the previous URI is a shutdown command
                shutdown = request.getUri() == null ? false : request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (IOException e) {
                LOGGER.error("connection with client fail", e);
                System.exit(1);
            }
        }
    }
}
