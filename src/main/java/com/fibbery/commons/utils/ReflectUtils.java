package com.fibbery.commons.utils;

import java.util.List;

/**
 * 
 * @author fibbery
 * @date 2017/12/13
 *
 */
public class ReflectUtils{
	/**
	 * 扫描包下所有类
	 * @param basePackage 需要扫描的包
	 * @param clazz  
	 * @return
	 */
	public static List<Class> loadClasses(String basePackage,Class<?> clazz){
		String packagePath = basePackage.replace(".", "/");
		System.out.println(packagePath);
		return null;
	}
	
}