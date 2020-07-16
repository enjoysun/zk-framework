package com.lit.soullark.framework.registrar;

import com.lit.soullark.framework.annoations.LarkBind;
import com.lit.soullark.framework.handles.impl.LarkInvocationHandler;
import com.lit.soullark.framework.model.CuratorFrameworkProperties;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * @Author myou
 * @Date 2020/7/2  6:01 下午
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(CuratorFramework.class)
@ConditionalOnSingleCandidate(CuratorFramework.class)
@ConditionalOnBean(CuratorFramework.class)
@EnableConfigurationProperties(CuratorFrameworkProperties.class)
public class AutoBindBeanRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, BeanClassLoaderAware {

    // 可用上下文
    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;
    private Environment environment;
    private ClassLoader classLoader;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 扫描器实现
     */
    private static class LarkClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

        public LarkClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
            super(registry, useDefaultFilters);
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            return super.doScan(basePackages);
        }

        protected void registerFilters() {
            addIncludeFilter(new AnnotationTypeFilter(LarkBind.class));
        }
    }

    /**
     * 设置class扫描器
     */
    private class LarkClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            if (beanDefinition.getMetadata().isInterface()) {
                try {
                    Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), AutoBindBeanRegistrar.this.classLoader);
                    return !target.isAnnotation();
                } catch (ClassNotFoundException ex) {
                    return false;
                }
            }
            return false;
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        LarkClassPathBeanDefinitionScanner larkClassPathBeanDefinitionScanner = new LarkClassPathBeanDefinitionScanner(beanDefinitionRegistry, false);
        larkClassPathBeanDefinitionScanner.setResourceLoader(this.resourceLoader);
        larkClassPathBeanDefinitionScanner.registerFilters();
        String packagePath = this.environment.getProperty("soul.lark.zk.framework.mapper", String.class, "");
        String[] paths = packagePath.split(",");
        for (String pack : paths) {
            larkClassPathBeanDefinitionScanner.doScan(pack);
        }
    }
}
