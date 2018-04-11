package cn.lunzn.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口信息注解类
 * 
 * @author  clark
 * @version  [版本号, 2017年9月4日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.ANNOTATION_TYPE})
public @interface Location
{
    /**
     * 接口信息<br>
     * 接口地址前缀
     */
    String module();
}
