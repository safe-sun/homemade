package com.safesun.homemade.dynamicproxy;

public interface MyHandler {
    String methodBody(String methodName);

    default void setProxy(MyInterface proxy) throws Exception {

    }
}
