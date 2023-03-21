package org.bsm.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * spring工具类
 */
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    private static Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
        SpringUtils.environment = applicationContext.getEnvironment();
    }

    /**
     * 获取对象
     *
     * @return Object 一个以所给名字注册的bean的实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }


    /**
     * 根据类获取bean
     *
     */
    public static <T> T getBean(Class<T> clz) {
        return beanFactory.getBean(clz);
    }

    public static <T> T getBean(String name, Class<T> clz) {
        return beanFactory.getBean(name, clz);
    }


    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return activeProfiles[0];
    }

    /**
     * 根据配置的key和类型获取字段值
     *
     */
    public static <T> T getProfile(String key, Class<T> clazz) {
        return environment.getProperty(key, clazz);
    }

    public static String getHttpPath() {
        return getProfile("server.ip", String.class) + ":" + getProfile("server.port", String.class);
    }
}