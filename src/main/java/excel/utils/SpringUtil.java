package excel.utils;

import com.google.common.collect.Maps;
import excel.common.SpringApplicationContext;

import java.util.Map;

/**
 * @Author 王宇
 * @DATE 2018/3/12.
 * @Description
 */
public final class SpringUtil {




    public static <T> T getBean(String beanId, Class<T> tClass) {
        T obj = null;
        try {
            obj = (T) getBean(beanId);
        } catch (Throwable e) {
        }
        if (obj == null) {
            return getBean(tClass);
        }
        return obj;
    }

    public static <T> T getBean(Class<T> tClass) {
        try {
            Map<String, T> map = SpringApplicationContext.getBeansByType(tClass);
            if (CollectionsTools.isNotEmpty(map)) {
                return map.values().iterator().next();
            }
        } catch (Throwable e) {
        }
        try {
            return tClass.newInstance();
        } catch (Throwable e) {
        }
        return null;
    }


    public static <T> Map<String, T> getBeans(Class<T> tClass) {
        try {
            Map<String, T> map = SpringApplicationContext.getBeansByType(tClass);
            if (CollectionsTools.isNotEmpty(map)) {
                return map;
            }
        } catch (Throwable e) {
        }
        return Maps.newHashMap();
    }

    public static <T> T getBean(String beanId) {
        try {
            return (T) SpringApplicationContext.getBean(beanId);
        } catch (Throwable e) {
            return null;
        }
    }

    private SpringUtil() {

    }



}
