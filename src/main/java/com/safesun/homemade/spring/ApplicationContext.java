package com.safesun.homemade.spring;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private final Map<BeanDefinition, Object> beanMap = new HashMap<>();

    // cache to avoid recursive dependency
    private final Map<BeanDefinition, Object> initingBeanMap = new HashMap<>();

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    public ApplicationContext(String basePackage) throws Exception {
        initContext(basePackage);
    }

    public Object getBean(String beanName) {
        return beanMap.get(beanDefinitionMap.get(beanName));
    }

    public <T> T getBean(Class<T> beanType) {
        return beanMap.values().stream()
                .filter(o -> beanType.isAssignableFrom(o.getClass()))
                .map(o -> (T) o)
                .findAny()
                .orElse(null);
    }

    public <T> List<T> getBeans(Class<T> beanType) {
        return beanMap.values().stream()
                .filter(o -> beanType.isAssignableFrom(o.getClass()))
                .map(o -> (T) o)
                .toList();
    }

    public void initContext(String basePackage) throws Exception {
        scanPackage(basePackage).stream().filter(this::canCreate).forEach(this::wrapBeanDefinition);
        beanDefinitionMap.values().forEach(this::createBean);
    }

    protected Object createBean(BeanDefinition beanDefinition) {
        try {
            if (beanMap.containsKey(beanDefinition)) {
                return beanMap.get(beanDefinition);
            }
            if (initingBeanMap.containsKey(beanDefinition)) {
                return initingBeanMap.get(beanDefinition);
            }
            return doCreateBean(beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = constructor.newInstance();
        initingBeanMap.put(beanDefinition, bean);

        autowireBean(bean, beanDefinition);

        Method postMethod = beanDefinition.getPostMethod();
        if (postMethod != null) {
            postMethod.invoke(bean);
        }
        beanMap.put(beanDefinition, initingBeanMap.remove(beanDefinition));

        return bean;
    }

    protected void autowireBean(Object bean, BeanDefinition beanDefinition) throws Exception {
        for (Field f : beanDefinition.getAutowireFields()) {
            f.setAccessible(true);
            BeanDefinition fieldBeanDefinition = beanDefinitionMap.values().stream()
                    .filter(o -> f.getType().isAssignableFrom(o.getBeanClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("autowire fail, bean not found"));
            Object maybeInitedBean = createBean(fieldBeanDefinition);
            f.set(bean, maybeInitedBean);
        }
    }

    protected boolean canCreate(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class);
    }

    protected void wrapBeanDefinition(Class<?> clazz) {
        try {
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            if (beanDefinitionMap.containsKey(beanDefinition.getBeanName())) {
                throw new RuntimeException("bean name is duplicated");
            }
            beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Class<?>> scanPackage(String basePackage) throws Exception {
        List<Class<?>> list = new ArrayList<>();
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replace(".", File.separator));
        if (resource == null) {
            throw new RuntimeException("basePackage is null");
        }
        Path path = Paths.get(resource.toURI());

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Path absolutePath = file.toAbsolutePath();
                if (absolutePath.toString().endsWith(".class")) {
                    String replace = absolutePath.toString().replace(File.separator, ".");
                    int index = replace.indexOf(basePackage);
                    String substring = replace.substring(index, replace.length() - ".class".length());
                    try {
                        list.add(Class.forName(substring));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return list;
    }
}
