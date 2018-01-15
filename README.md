# Tomcat Work

## 一、前言

当初第一次看《深入剖析tomcat》时，是去年刚学java没多久，那时虽说能看懂，但肯定是没领会到里面的设计思想，
只能算是跟着代码走了一遍，基本上没思考，这次打算把代码再看看，看能不能多悟出点东西。

## 二、路线

1. simple_tomcat01 一个简单的程序，用于了解tomcat底层是一句socket通信；
2. simple_tomcat02 01只能加载静态资源，02区分静态资源和Servlet的加载，主要是利用反射调用静态资源
（在02中使用RequestFacade和ResponseFacade是因为request中的parse方法和response的sendStaticResource方法不应该在servlet被使用，
使用Facade进行限制）；