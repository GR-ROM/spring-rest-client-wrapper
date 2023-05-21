package su.grinev.restclient.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import su.grinev.restclient.reflections.ProxyInvocationHandler;
import su.grinev.restclient.services.WebClientWrapper;

import java.lang.reflect.Proxy;

public class ProxyFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> restClientInterface;
    @Autowired
    private WebClientWrapper webClientWrapper;

    public ProxyFactoryBean(Class<T> restClientInterface) {
        this.restClientInterface = restClientInterface;
    }

    @Override
    public T getObject() throws Exception {
        ProxyInvocationHandler invocationHandler = new ProxyInvocationHandler(restClientInterface, webClientWrapper);
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