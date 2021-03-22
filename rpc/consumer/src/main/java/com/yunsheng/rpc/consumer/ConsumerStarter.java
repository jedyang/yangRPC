package com.yunsheng.rpc.consumer;

import cn.hutool.core.util.StrUtil;
import com.yunsheng.rpc.common.anno.YangReference;
import com.yunsheng.rpc.consumer.config.Constants;
import com.yunsheng.rpc.consumer.config.ReferenceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consumer启动配置类
 * 1，实现BeanFactoryPostProcessor
 * BeanFactoryPostProcessor 是 Spring 容器加载 Bean 的定义之后以及 Bean 实例化之前执行，
 * 所以 BeanFactoryPostProcessor 可以在 Bean 实例化之前获取 Bean 的配置元数据，并允许用户对其修改。
 * 而 BeanPostProcessor 是在 Bean 初始化后执行，它并不能修改 Bean 的配置信息。
 * 2, 实现BeanClassLoaderAware
 * BeanFactory需要classloader去加载bean class
 * 3, 实现ApplicationContextAware
 * 需要判断spring的context中是否已经加载过某个bean
 *
 * @author yunsheng
 */
@Slf4j
@Component
public class ConsumerStarter implements BeanFactoryPostProcessor, BeanClassLoaderAware, ApplicationContextAware {

//    @Value("${rpc.registry.type}")
//    private String registryType;
//
//    @Value("${rpc.registry.address}")
//    private String registryAddr;

    private ApplicationContext applicationContext;
    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> referenceBeanDefs = new LinkedHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
//            log.info("beanDefinitionName:" + beanDefinitionName);
            // BeanFactoryPostProcessor中对bean的操作，通过BeanDefinition进行
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
//            log.info("beanClassName:{}", beanClassName);
            if (beanClassName != null) {
                // 通过类名称得到Class实例
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                // 对类实例的每个field使用callback方法进行处理
                ReflectionUtils.doWithFields(clazz, this::filedCallback);
            }
        }

        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
        this.referenceBeanDefs.forEach((beanName, beanDefinition) -> {
            if (applicationContext.containsBean(beanName)) {
                throw new IllegalArgumentException("spring context already has the bean: " + beanName);
            }
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            log.info("注册spring bean成功:" + beanName);
        });
    }



    /**
     * 处理field的回调方法
     *
     * @param field
     */
    private void filedCallback(Field field) {
//        log.info("filedCallback:{}", field.getName());
        YangReference annotation = AnnotationUtils.getAnnotation(field, YangReference.class);
        // 如果有YangReference注解
        if (null != annotation) {
            log.info("YangReference Anno:{}", field.getName());
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ReferenceBean.class);
            // 指定初始化方法，在这里面进行反射
//            builder.setInitMethodName(Constants.BEAN_INIT_METHOD_NAME);
            builder.setInitMethodName("init");
            builder.addPropertyValue("interfaceClass", field.getType());
            builder.addPropertyValue("serviceVersion", annotation.version());
            builder.addPropertyValue("timeout", annotation.timeout());
            builder.addPropertyValue("registryType", "ZOOKEEPER");
            builder.addPropertyValue("registryAddr", "127.0.0.1:2181");

            BeanDefinition beanDefinition = builder.getBeanDefinition();
            referenceBeanDefs.put(field.getName(), beanDefinition);
        }
    }
}
