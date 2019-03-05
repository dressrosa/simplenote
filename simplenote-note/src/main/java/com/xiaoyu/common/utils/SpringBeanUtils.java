/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 注入applicationContext
 * 
 * @author xiaoyu 2016年3月18日
 */
@Component
public class SpringBeanUtils implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    /**
     * 获取bean
     * 
     * @author xiaoyu
     * @param name
     * @return
     * @time 2016年3月18日下午6:18:08
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) SpringBeanUtils.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> cls) {
        return (T) SpringBeanUtils.applicationContext.getBean(cls);
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        SpringBeanUtils.applicationContext = arg0;
    }

    @Override
    public void destroy() throws Exception {
        SpringBeanUtils.applicationContext = null;
    }
}
