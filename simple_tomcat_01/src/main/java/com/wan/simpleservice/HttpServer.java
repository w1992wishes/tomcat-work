package com.wan.simpleservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by 万勤锋 on 2017/1/23.
 */
public class HttpServer {

    //log
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    //resource path
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    //shutdown command
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    //the shutdown command receiver
    private static boolean shutdown = false;

    public static void main(String[] args) {
        LOGGER.debug("webroot path " + WEB_ROOT);
        HttpServer server = new HttpServer();
        server.await();
    }

    private static void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            LOGGER.error("server socket created fail", e);
            System.exit(1);
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
                LOGGER.error("communication with client fail! ", e);
                continue;
            }
        }
    }
}
