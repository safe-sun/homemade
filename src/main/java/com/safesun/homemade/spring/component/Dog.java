package com.safesun.homemade.spring.component;

import com.safesun.homemade.spring.Autowire;
import com.safesun.homemade.spring.PostConstruct;


public class Dog {
    public int age = 5;

    @Autowire
    private Cat cat;

    @PostConstruct
    public void init() {
        System.out.printf("Cat init: [%s]%n", cat);
    }
}
