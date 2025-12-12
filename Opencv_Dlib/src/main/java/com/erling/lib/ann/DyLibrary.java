package com.erling.lib.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) //运行时保留
public @interface DyLibrary {
    String  DynamicPath() default "";
    String  TomlPath() default "";
}


