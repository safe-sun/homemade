package com.safesun.homemade.spring;

import java.lang.reflect.Field;

public class AutowireField {
    Field field;
    boolean requiredFlag;

    public AutowireField(Field field, boolean requiredFlag) {
        this.field = field;
        this.requiredFlag = requiredFlag;
    }
}
