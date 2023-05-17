package su.grinev.restclient.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.client.RestTemplate;
import su.grinev.restclient.spring.ProxyInvocationHandler;

import java.lang.reflect.Proxy;

public class MyFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> restClientInterface;
    private final RestTemplate restTemplate;

    public MyFactoryBean(Class<T> restClientInterface, RestTemplate restTemplate) {
        this.restClientInterface = restClientInterface;
        this.restTemplate = restTemplate;
    }

    @Override
    public T getObject() throws Exception {
        ProxyInvocationHandler invocationHandler = new ProxyInvocationHandler(restTemplate, restClientInterface);
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{restClientInterface}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return restClientInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}