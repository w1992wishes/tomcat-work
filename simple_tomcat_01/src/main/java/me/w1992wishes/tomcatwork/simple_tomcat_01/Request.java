package me.w1992wishes.tomcatwork.simple_tomcat_01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 万勤锋 on 2017/1/23.
 */
public class Request {
    private InputStream input;
    private String uri;

    //log
    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse() {
        //Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = input.read(buffer);
        } catch (IOException e) {
            LOGGER.error("read from inputstream fail", e);
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        LOGGER.debug(request.toString());
        uri = parseUri(request.toString());

    }

    //从请求行中获取URI， 在请求中搜索第一个和第二个空格
    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 2, index2);
        }
        return null;
    }

    public String getUri() {
        return uri;
    }
}
