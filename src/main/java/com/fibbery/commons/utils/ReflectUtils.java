package com.fibbery.commons.utils;

import java.net.URISyntaxException;
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
	 * @param clazz 接口类
	 * @return
	 */
	public static List<Class> loadClasses(String basePackage,Class<?> clazz) throws URISyntaxException {
		String packagePath = basePackage.replace(".", "/");
        
		return null;
	}

}
