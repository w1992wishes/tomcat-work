package me.w1992wishes.tomcatwork.simple_tomcat_02.facade;

import me.w1992wishes.tomcatwork.simple_tomcat_02.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Created by wanqinfeng on 2017/1/25.
 */
public class ResponseFacade implements ServletResponse {

    private Response response;

    public ResponseFacade(Response response){
        this.response = response;
    }

    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    public String getContentType() {
        return response.getContentType();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    public void setCharacterEncoding(String s) {
        response.setCharacterEncoding(s);
    }

    public void setContentLength(int i) {
        response.setContentLength(i);
    }

    public void setContentLengthLong(long l) {
        response.setContentLengthLong(l);
    }

    public void setContentType(String s) {
        response.setContentType(s);
    }

    public void setBufferSize(int i) {
        response.setBufferSize(i);
    }

    public int getBufferSize() {
        return response.getBufferSize();
    }

    public void flushBuffer() throws IOException {
        response.flushBuffer();
    }

    public void resetBuffer() {
        response.resetBuffer();
    }

    public boolean isCommitted() {
        return response.isCommitted();
    }

    public void reset() {
        response.resetBuffer();
    }

    public void setLocale(Locale locale) {
        response.setLocale(locale);
    }

    public Locale getLocale() {
        return response.getLocale();
    }
}
