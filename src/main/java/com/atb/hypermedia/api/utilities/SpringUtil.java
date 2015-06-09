package com.atb.hypermedia.api.utilities;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Provides access to the spring context from within the application
 * where annotation based injection is not possible
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext springContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        springContext = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClazz) {

        if ( springContext != null )
            return springContext.getBean(beanClazz);
        else
            return null;
    }
}
