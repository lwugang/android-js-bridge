package com.wugang.jsbridge.library.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lwg on 17-7-3.
 * 只注入被该注解标记的方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsInject {
  /**
   * 注入的方法名
   * @return
   */
  String value() default "";
}
