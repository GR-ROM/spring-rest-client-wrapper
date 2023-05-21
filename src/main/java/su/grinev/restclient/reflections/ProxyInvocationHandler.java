package su.grinev.restclient.reflections;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.grinev.restclient.annotations.RestRpcClient;
import su.grinev.restclient.services.WebClientWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyInvocationHandler implements InvocationHandler {
    private final Class<?> targetClass;
    private final WebClientWrapper webClientWrapper;

    public ProxyInvocationHandler(Class<?> targetClass, WebClientWrapper webClientWrapper) {
        this.targetClass = targetClass;
        this.webClientWrapper = webClientWrapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RestRpcClient RestRpcClient = method.getDeclaringClass().getAnnotation(RestRpcClient.class);
        if (RestRpcClient == null) {
            throw new IllegalStateException("Class must be annotated with @RestClient");
        }

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            throw new IllegalStateException("Method should be annotated with @RequestMapping");
        }

        Map<String, Object> requestParameters = getRequestParameters(method, args);
        System.out.println(requestParameters);

        String host = RestRpcClient.host();
        String url = requestMapping.value()[0];
        if (requestParameters.size() > 0) {
            String parameters = requestParametersToUrl(requestParameters);
            url = url + parameters;
        }
        HttpMethod httpMethod = requestMapping.method()[0].asHttpMethod();

        ResponseEntity<?> responseEntity = switch (httpMethod.name()) {
            case "GET" -> webClientWrapper.getRequest(host, url, Collections.EMPTY_MAP, method.getReturnType());
            default -> throw new IllegalStateException("Unsupported HTTP method: " + httpMethod);
        };

        return responseEntity.getBody();
    }

    private Map<String, Object> getRequestParameters(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> requestParameters = new LinkedHashMap<>();
        for (int i = 0; i != parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                requestParameters.put(parameters[i].getName(), args[i]);
            }
        }
        return requestParameters;
    }

    private Map<String, Object> getPathVariables(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> requestParameters = new LinkedHashMap<>();
        for (int i = 0; i != parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                requestParameters.put(parameters[i].getName(), args[i]);
            }
        }
        return requestParameters;
    }

    private String requestParametersToUrl(Map<String, Object> requestParameters) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicBoolean first = new AtomicBoolean(true);
        requestParameters.forEach((key, value) -> {
            if (first.get()) {
                stringBuilder.append("?").append(key).append("=").append(value.toString());
                first.set(false);
            } else {
                stringBuilder.append("&").append(key).append("=").append(value.toString());
            }
        });
        return stringBuilder.toString();
    }
}