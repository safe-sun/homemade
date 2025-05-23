package com.safesun.homemade.spring.component;

import com.safesun.homemade.spring.Autowire;
import com.safesun.homemade.spring.Component;
import com.safesun.homemade.spring.PostConstruct;

@Component
public class Cat {
    @Autowire
    private Cat cat;

    @Autowire(required = false)
    private Dog dog;

    @PostConstruct
    public void init() {
        System.out.printf("Cat init: [%s, %s]%n", cat, dog);
    }
}
