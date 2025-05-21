package com.safesun.homemade.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class BeanDefinition {
    private String beanName;
    private Constructor<?> constructor;
    private Method postMethod;

    public BeanDefinition(Class<?> beanClass) {
        try {
            Component component = beanClass.getDeclaredAnnotation(Component.class);
            this.beanName = component.name().isEmpty() ? beanClass.getSimpleName() : component.name();
            constructor = beanClass.getConstructor();

            Arrays.stream(beanClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                    .findFirst()
                    .ifPresent(method -> this.postMethod = method);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBeanName() {
        return beanName;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Method getPostMethod() {
        return postMethod;
    }
}
