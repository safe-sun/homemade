package com.safesun.homemade.spring;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ApplicationContext {
    private final Map<String, Object> beanMap = new HashMap<>();

    private final Set<String> beanDefinitionNameSet = new HashSet<>();

    public ApplicationContext(String basePackage) throws Exception {
        initContext(basePackage);
    }

    public Object getBean(String beanName) {
        return beanMap.get(beanName);
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
        scanPackage(basePackage).stream().filter(this::canCreate).map(this::wrapBeanDefinition).forEach(this::createBean);
    }

    protected void createBean(BeanDefinition beanDefinition) {
        try {
            String beanName = beanDefinition.getBeanName();
            if (beanMap.containsKey(beanName)) {
                return;
            }
            doCreateBean(beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = constructor.newInstance();
        if (beanDefinition.getPostMethod() != null) {
            beanDefinition.getPostMethod().invoke(bean);
        }
        beanMap.put(beanDefinition.getBeanName(), bean);
    }

    protected boolean canCreate(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class);
    }

    protected BeanDefinition wrapBeanDefinition(Class<?> clazz) {
        try {
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            if (beanDefinitionNameSet.contains(beanDefinition.getBeanName())) {
                throw new RuntimeException("bean name is duplicated");
            }
            beanDefinitionNameSet.add(beanDefinition.getBeanName());
            return beanDefinition;
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
