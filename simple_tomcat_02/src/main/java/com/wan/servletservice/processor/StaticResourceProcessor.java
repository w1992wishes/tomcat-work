package com.wan.servletservice.processor;

import com.wan.servletservice.Request;
import com.wan.servletservice.Response;

import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/1/24.
 */
public class StaticResourceProcessor {
    public void process(Request request, Response response) throws IOException {
        response.sendStaticResource();
    }
}
