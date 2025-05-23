package com.safesun.homemade.spring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class BeanDefinition {
    private String beanName;
    private Class<?> beanClass;
    private Constructor<?> constructor;
    private AutowireField[] autowireFields;
    private Method customInitMethod;

    public BeanDefinition(Class<?> beanClass) {
        try {
            this.beanClass = beanClass;
            Component component = beanClass.getDeclaredAnnotation(Component.class);
            this.beanName = component.name().isEmpty() ? beanClass.getSimpleName() : component.name();
            constructor = beanClass.getConstructor();

            autowireFields = Arrays.stream(beanClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Autowire.class))
                    .map(field -> new AutowireField(field, field.getAnnotation(Autowire.class).required()))
                    .toArray(AutowireField[]::new);

            Arrays.stream(beanClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                    .findFirst()
                    .ifPresent(method -> this.customInitMethod = method);
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

    public Method getCustomInitMethod() {
        return customInitMethod;
    }

    public AutowireField[] getAutowireFields() {
        return autowireFields;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
}
