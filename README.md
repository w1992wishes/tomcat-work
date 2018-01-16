# Tomcat Work

## 一、前言

当初第一次看《深入剖析tomcat》时，是去年刚学java没多久，那时虽说能看懂，但肯定是没领会到里面的设计思想，
只能算是跟着代码走了一遍，基本上没思考，这次打算把代码再看看，看能不能多悟出点东西。

## 二、路线

1. simple_tomcat01 一个简单的程序，用于了解tomcat底层是依据socket通信；
2. simple_tomcat02 01只能加载静态资源，02区分静态资源和Servlet的加载，主要是利用反射生成servlet实例
（在02中使用RequestFacade和ResponseFacade是因为request中的parse方法和response的sendStaticResource方法不应该在servlet被使用，
使用Facade进行限制）；
3. simple_tomcat03 03在02的基础上进行了拆分和补充，首先增加了一个专门的启动类Bootstrap，HttpServer一分为二，一是
HttpConnector（起一个线程用来接收http请求），二是HttpProcessor（用于创建HttpRequest、HttpResponse，并做一些解析任务），最后
同样交由Processor来处理，其他新增的类都是辅助解析request。