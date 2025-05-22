package com.safesun.homemade.spring.component;

import com.safesun.homemade.spring.Autowire;
import com.safesun.homemade.spring.Component;

@Component
public class Dog {
    @Autowire
    private Cat cat;
}
