package com.dhcc.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
* @ClassName: OAuthRequired 
* @Description: TODO(需要微信验证) 
* @author zhouxin
* @date 2015年9月18日 下午2:25:59 
*
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface OAuthRequired {
	
	String BlhMethod() ;
}
