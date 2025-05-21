package com.safesun.homemade.dynamicproxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyFactory {
    private static final AtomicInteger i = new AtomicInteger();

    private static String interfaceName;

    private static Map<String, Method> methodMap = new HashMap<>();

    public static File createJavaFile(String className, MyHandler handler) throws IOException {
        String method1Body = handler.methodBody("method1");
        String method2Body = handler.methodBody("method2");
        String method3Body = handler.methodBody("method3");

        String content = """
                package com.safesun.homemade.dynamicproxy;

                public class %s implements MyInterface {
                    MyInterface myInterface;

                    @Override
                    public void method1() {
                        %s
                    }

                    @Override
                    public void method2() {
                        %s
                    }

                    @Override
                    public void method3() {
                        %s
                    }
                }
                """.formatted(className, method1Body, method2Body, method3Body);
        File javaFile = new File(className + ".java");
        Files.writeString(javaFile.toPath(), content);
        return javaFile;
    }

    private static String getClassName() {
        return "MyInterface$proxy" + i.getAndIncrement();
    }

    public static MyInterface newInstance(String className, MyHandler handler) throws Exception {
        Class<?> aClass = ProxyFactory.class.getClassLoader().loadClass(className);
        Constructor<?> constructor = aClass.getConstructor();
        MyInterface proxy = (MyInterface) constructor.newInstance();
        handler.setProxy(proxy);
        return proxy;
    }

    public static MyInterface createProxyObject(MyHandler handler) throws Exception {
        String className = getClassName();
        File javaFile = createJavaFile(className, handler);
        Compiler.compile(javaFile);
        return newInstance("com.safesun.homemade.dynamicproxy." + className, handler);
    }

    public static void parseClass(Class<?> clazz) {
        interfaceName = clazz.getSimpleName();
        Method[] methods = clazz.getMethods();
        for (var m : methods) {
            methodMap.put(m.getName(), m);
        }
    }
}
