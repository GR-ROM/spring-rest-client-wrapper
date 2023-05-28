package su.grinev.restclient.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestRpcClient {

    String host() default "";

    String[] headers() default {};

    @AliasFor("host")
    String myhost() default "";
}