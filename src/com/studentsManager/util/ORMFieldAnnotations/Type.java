package com.studentsManager.util.ORMFieldAnnotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Type {
    enum fieldType{
        STRING,DATE,POWER,INT
    }

    fieldType type() default fieldType.STRING;
}
