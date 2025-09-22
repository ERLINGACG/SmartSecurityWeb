package com.erling.lib.instance;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LibraryAnn {
    String WindowsPath() default "";
    String LinuxPath() default "";

 }
