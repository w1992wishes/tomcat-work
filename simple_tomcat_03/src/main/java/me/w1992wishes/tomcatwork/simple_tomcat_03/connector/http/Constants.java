package me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http;

import java.io.File;

public final class Constants {
  public static final String WEB_ROOT =
    System.getProperty("user.dir") + File.separator  + "webroot";
  public static final String PACKAGE = "com.wan.servletservice.connector.http";
  public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
  public static final int PROCESSOR_IDLE = 0;
  public static final int PROCESSOR_ACTIVE = 1;
}
