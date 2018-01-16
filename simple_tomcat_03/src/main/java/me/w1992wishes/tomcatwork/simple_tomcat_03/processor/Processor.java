package me.w1992wishes.tomcatwork.simple_tomcat_03.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 资源处理接口
 *
 * Created by w1992wishes
 * on 2018/1/15.
 */
public abstract class Processor {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Processor processor;

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    /**
     * 根据url判断由哪个具体的Processor去处理
     *
     * @param url
     */
    abstract boolean match(String url);

    /**
     * 责任链模式以合适的processor处理
     *
     * @param request
     * @param response
     */
    public void process(HttpRequest request, HttpResponse response){
        if (match(request.getRequestURI())){
            action(request, response);
        } else {
            processor.process(request, response);
        }
    }

    /**
     * 模板方法模式处理
     *
     * @param request
     * @param response
     */
    protected abstract void action(HttpRequest request, HttpResponse response);

}
