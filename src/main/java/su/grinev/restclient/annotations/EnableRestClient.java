package su.grinev.restclient.annotations;

import org.springframework.context.annotation.Import;
import su.grinev.restclient.spring.BeanRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(BeanRegistrar.class)
public @interface EnableRestClient {
}