package excel.common;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author 王宇
 * @DATE 2018/3/12.
 * @Description Description: spring 上下文类
 */
public final class SpringApplicationContext {



    private static ApplicationContext ac = null;

    private static ApplicationContext mvcAc = null;

    private SpringApplicationContext(){}




    public static Object getBean(String id){
        Object bean = null;
        try{
            bean = mvcAc.getBean(id);
        }catch(Exception e){
            bean = ac.getBean(id);
        }
        return bean;
    }


    public static boolean isSingleton(String id){
        boolean result;
        try{
            result = mvcAc.isSingleton(id);
        }catch(Exception e){
            result = ac.isSingleton(id);
        }
        return result;
    }


    public static <T> Map<String,T> getBeansByType(Class<T> type){
        try{
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(mvcAc, type);
        }catch(Exception e){
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(ac, type);
        }
    }


    public static Set<String> getBeanKeyByType(Class<?> type){
        return SpringApplicationContext.getBeansByType(type).keySet();
    }


    public static void initApplicationContext(ApplicationContext ac){

        SpringApplicationContext.ac = ac;

    }

    public static void initMvcApplicationContext(ApplicationContext mvcAc){
        SpringApplicationContext.mvcAc = mvcAc;
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        Map<String, Object> map1 = new HashMap<String, Object>(0);
        Map<String, Object> map2 = new HashMap<String, Object>(0);

        if(ac != null){
            map1 = ac.getBeansWithAnnotation(annotationType);
        }

        if(mvcAc != null){
            map2 = mvcAc.getBeansWithAnnotation(annotationType);
        }

        Map<String, Object> result = new HashMap<String, Object>(map1.size() + map2.size());
        result.putAll(map1);
        result.putAll(map2);
        return result;
    }


    public static boolean containsBean(String id){
        if(mvcAc.containsBean(id)){
            return true;
        }
        if(ac.containsBean(id)){
            return true;
        }
        return false;
    }




}
