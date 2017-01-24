package com.wan.servletservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by wanqinfeng on 2017/1/24.
 */
public class HttpServer1 {
    //log
    private static Logger LOGGER = LoggerFactory.getLogger(HttpServer1.class);

    //resource path
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    //shutdown command
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
}
