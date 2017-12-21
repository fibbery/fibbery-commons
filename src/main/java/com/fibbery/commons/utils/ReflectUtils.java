package com.fibbery.commons.utils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;

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
	 * @param clazz 接口类  如果为null则忽略,直接扫描全部
	 * @return
	 */
	public static Set<Class> loadClasses(String basePackage, Class<?> clazz, boolean isRecursive) throws URISyntaxException {
		String packagePath = basePackage.replace(".", "/");
        Set<Class> classes = new LinkedHashSet<Class>();

        //如果clazz不为null且不为接口类,则不扫描
        if (clazz != null && !clazz.isInterface()) {
            return classes;
        }

        Enumeration<URL> resources = null;
        try {
            resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while(resources.hasMoreElements()) {
                URL dir = resources.nextElement();
                if ("file".equals(dir.getProtocol())) {
                    System.out.println("进行文件夹扫描");
                    String filePath= URLDecoder.decode(dir.getFile(), "UTF-8");
                    scanFileDir(filePath, basePackage, clazz, isRecursive, classes);
                } else if ("jar".equals(dir.getProtocol())) {
                    System.out.println("进行jar扫描");
                    JarURLConnection connection = (JarURLConnection) dir.openConnection();
                    Enumeration<JarEntry> entries = connection.getJarFile().entries();
                    List<JarEntry> filterEntries = new ArrayList<>();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if(entryName.startsWith(packagePath) && entryName.endsWith(".class")){
                            filterEntries.add(entry);
                        }
                    }
                    filterEntries.forEach(entry -> {
                        String className = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
                        try {
                            Class subClazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                            List<Class> interfaces = Arrays.asList(subClazz.getInterfaces());
                            boolean fit = isRecursive || subClazz.getPackage().getName().equals(basePackage); // 判断是否可以循环子包
                            if ((clazz == null || interfaces.contains(clazz)) && fit) {
                                classes.add(subClazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return classes;
	}

    private static void scanFileDir(String filePath, String packageName, Class<?> clazz, final boolean isRecursive, Set<Class> classes) {
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles(file -> (isRecursive && file.isDirectory()) || (file.getName().endsWith(".class")));

        assert files != null;
        Arrays.stream(files).forEach(file -> {
            if (file.isDirectory()) {
                String newPackageName = packageName + "." + file.getName();
                scanFileDir(file.getAbsolutePath(), newPackageName, clazz, isRecursive, classes);
            }else{
                //获取class文件
                String className = file.getName().substring(0, file.getName().length() - 6);
                //Class.forName()获取会触发类的static方法,没有使用classLoader干净
                try {
                    Class<?> subClazz = Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className);
                    List<Class> interfaces = Arrays.asList(subClazz.getInterfaces());
                    if (clazz == null || interfaces.contains(clazz)) {
                        classes.add(subClazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    private static String getRootPath(URL resource) {
        System.out.println(resource.getFile());
        return resource.getFile();
    }



}
