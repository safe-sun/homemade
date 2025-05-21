package com.safesun.homemade.dynamicproxy;

import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) throws Exception {
        MyInterface proxy = ProxyFactory.createProxyObject(new PrintlnMethodNameHandler());
        proxy.method1();
        proxy.method2();
        proxy.method3();
        System.out.println("-------------------");
        MyInterface proxy1 = ProxyFactory.createProxyObject(new LogHandler(proxy));
        proxy1.method1();
        proxy1.method2();
        proxy1.method3();
    }
}

class PrintlnMethodNameHandler implements MyHandler {

    @Override
    public String methodBody(String methodName) {
        return """
                System.out.println("%s");
                """.formatted(methodName);
    }
}

class LogHandler implements MyHandler {
    private MyInterface myInterface;

    public LogHandler(MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    @Override
    public void setProxy(MyInterface proxy) throws Exception {
        Class<? extends MyInterface> aClass = proxy.getClass();
        Field field = aClass.getDeclaredField("myInterface");
        field.setAccessible(true);
        field.set(proxy, myInterface);
    }

    @Override
    public String methodBody(String methodName) {
        return """
                System.out.println("before");
                myInterface.%s();
                System.out.println("after");
                """.formatted(methodName);
    }
}
