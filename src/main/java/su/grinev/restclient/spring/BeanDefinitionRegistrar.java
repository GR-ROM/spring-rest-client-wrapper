package su.grinev.restclient.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import su.grinev.restclient.annotations.RestRpcClient;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestRpcClient.class));
        String basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        Set<BeanDefinition> candidateComponents = new LinkedHashSet<>(scanner.findCandidateComponents(basePackage));

        candidateComponents.forEach(candidateComponent -> {
            ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition) candidateComponent;
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            Assert.isTrue(annotationMetadata.isInterface(), "@RestClient can only be specified on an interface");
            Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(RestRpcClient.class.getCanonicalName());
            registerBuilder(registry, annotationMetadata, attributes, candidateComponent);
        });
    }

    private void registerBuilder(BeanDefinitionRegistry registry,
                                 AnnotationMetadata annotationMetadata,
                                 Map<String, Object> attributes,
                                 BeanDefinition candidateComponent) {
        String configName = null;
        if (!CollectionUtils.isEmpty(attributes)) {
            configName = "config." + annotationMetadata.getClassName();
            registerConfig(attributes, configName, registry);
        }
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
        definition.addConstructorArgValue(className);
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
        String aliasName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(candidateComponent, registry);
        String name = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, name, new String[]{aliasName});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void registerConfig(Map<String, Object> attributes, String configName, BeanDefinitionRegistry registry) {

    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                return !RestRpcClient.class.getCanonicalName().equals(beanDefinition.getMetadata().getClassName());
            }
        };
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}