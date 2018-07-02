package com.fibbery.commons.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author fibbery
 * @date 18/7/2
 */
public class ConfigUtils {

    public static Integer getPid() {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String name = bean.getName();
        String[] properties = name.split("@");
        return Integer.parseInt(properties[0]);
    }
}
