package com.sample.backend.util.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelHeader {
    // header 관련
    String headerText() default "";
    boolean essential() default false;

    int sort() default 0;
    int width() default 10;
    
    // body 관련
    boolean editable() default false;
    HorizontalAlignment align() default HorizontalAlignment.LEFT;
    String dateFormat() default "";
}
