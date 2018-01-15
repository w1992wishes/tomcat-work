package me.w1992wishes.tomcatwork.simple_tomcat_03.startup;

import me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http.HttpConnector;

/**
 * 应用程序包含三个模块：连接器模块，启动模块，核心模块
 * 1 连接器模块
 * 1.1 连接器及其支持类（等待http请求：HttpConnector  创建Request和Response：HttpProcessor）
 * 1.2 表示HTTP请求的类(HttpRequest)及其支持类
 * 1.3 表示HTTP响应的类（HttpResponse) 及其支持类
 * 1.4 外观类
 * 1.5 常量类
 *
 * 2 核心模块
 * 2.1 servletProcessor
 * 2.2 StaticResourceProcessor
 *
 * 3 启动类，用于启动应用程序
 * Created by wanqinfeng on 2017/2/5.
 */
public final class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
