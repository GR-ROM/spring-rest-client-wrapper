package su.grinev.restclient.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;

public class MyFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> restClientInterface;
    @Autowired
    private RestTemplate restTemplate;

    public MyFactoryBean(Class<T> restClientInterface) {
        this.restClientInterface = restClientInterface;
    }

    @Override
    public T getObject() throws Exception {
        ProxyInvocationHandler invocationHandler = new ProxyInvocationHandler(restClientInterface, restTemplate);
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