package com.safesun.homemade.spring.component;

import com.safesun.homemade.spring.BeanPostProcessor;
import com.safesun.homemade.spring.Component;

@Component
public class MyPost implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.printf("%s: postBefore%n", beanName);
        if (bean instanceof Dog beanDog) {
            //Dog d = new Dog();
            //d.age = 10000;
            //bean = d;
            beanDog.age = 10;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.printf("%s: postAfter%n", beanName);
        return bean;
    }
}
