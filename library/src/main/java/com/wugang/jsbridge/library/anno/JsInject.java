package com.wugang.jsbridge.library.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lwg on 17-7-3.
 * 只注入被该注解标记的方法或者类，如果标记在类上 类中所有的public方法都会被注入
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,ElementType.TYPE})
public @interface JsInject {
  /**
   * 注入的方法名
   * @return
   */
  String value() default "";
}
