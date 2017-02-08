package com.wan.tomcat.startup;

import com.wan.tomcat.core.SimpleContainer;
import org.apache.catalina.connector.http.HttpConnector;

/**
 * Created by wanqinfeng on 2017/2/8.
 */
public final class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        SimpleContainer container = new SimpleContainer();
        connector.setContainer(container);

        try {
            connector.initialize();
            connector.start();

            //make the application wait util we press any key
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
