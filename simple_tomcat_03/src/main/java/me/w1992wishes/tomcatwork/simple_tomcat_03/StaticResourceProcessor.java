package me.w1992wishes.tomcatwork.simple_tomcat_03;

import me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor {

  public void process(HttpRequest request, HttpResponse response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
