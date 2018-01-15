package me.w1992wishes.tomcatwork.simple_tomcat_02.facade;

import me.w1992wishes.tomcatwork.simple_tomcat_02.Request;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * 外观类
 *
 * Created by wanqinfeng on 2017/1/25.
 */
public class RequestFacade implements ServletRequest {

    private Request request;

    public RequestFacade(Request request){
        this.request = request;
    }

    public Object getAttribute(String s) {
        return request.getAttribute(s);
    }

    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        request.setCharacterEncoding(s);
    }

    public int getContentLength() {
        return request.getContentLength();
    }

    public long getContentLengthLong() {
        return request.getContentLengthLong();
    }

    public String getContentType() {
        return request.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public String getParameter(String s) {
        return request.getParameter(s);
    }

    public Enumeration<String> getParameterNames() {
        return request.getParameterNames();
    }

    public String[] getParameterValues(String s) {
        return request.getParameterValues(s);
    }

    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public String getProtocol() {
        return request.getProtocol();
    }

    public String getScheme() {
        return request.getScheme();
    }

    public String getServerName() {
        return request.getServerName();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public void setAttribute(String s, Object o) {
        request.setAttribute(s, o);
    }

    public void removeAttribute(String s) {
        request.removeAttribute(s);
    }

    public Locale getLocale() {
        return request.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return request.getRequestDispatcher(s);
    }

    public String getRealPath(String s) {
        return request.getRealPath(s);
    }

    public int getRemotePort() {
        return request.getRemotePort();
    }

    public String getLocalName() {
        return request.getLocalName();
    }

    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    public int getLocalPort() {
        return request.getLocalPort();
    }

    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return request.startAsync();
    }

    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return request.startAsync(servletRequest, servletResponse);
    }

    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }

    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }

    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }
}
