package su.grinev.restclient.annotations;

import org.springframework.context.annotation.Import;
import su.grinev.restclient.spring.BeanDefinitionRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(BeanDefinitionRegistrar.class)
public @interface EnableRpcClient {
}