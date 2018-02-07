package me.w1992wishes.tomcatwork.simple_tomcat_07.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public interface ServerSocketFactory {

    ServerSocket createSocket(int port) throws IOException;

    ServerSocket createSocket(int port, int backlog) throws IOException;

    ServerSocket createSocket(int port, int backlog, InetAddress ifAddress) throws IOException;

}