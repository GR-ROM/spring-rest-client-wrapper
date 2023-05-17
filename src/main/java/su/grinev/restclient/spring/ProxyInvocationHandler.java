package su.grinev.restclient.spring;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyInvocationHandler implements InvocationHandler {
    private final RestTemplate restTemplate;
    private final Class<?> targetClass;

    public ProxyInvocationHandler(Class<?> targetClass, RestTemplate restTemplate) {
        this.targetClass = targetClass;
        this.restTemplate = restTemplate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            throw new IllegalStateException("Method should be annotated with @RequestMapping");
        }

        String url = requestMapping.value()[0];
        HttpMethod httpMethod = requestMapping.method()[0].asHttpMethod();

        ResponseEntity<?> responseEntity;
        switch (httpMethod.name()) {
            case "GET":
                responseEntity = restTemplate.getForEntity(url, method.getReturnType(), args);
                break;
            case "POST":
                responseEntity = restTemplate.postForEntity(url, args[0], method.getReturnType());
                break;
            default:
                throw new IllegalStateException("Unsupported HTTP method: " + httpMethod);
        }

        return responseEntity.getBody();
    }
}