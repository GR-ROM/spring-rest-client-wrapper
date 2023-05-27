package su.grinev.restclient.reflections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import su.grinev.restclient.annotations.RestRpcClient;
import su.grinev.restclient.http.HttpRequest;
import su.grinev.restclient.services.RestRpcGateway;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyInvocationHandler implements InvocationHandler {
    private final Class<?> targetClass;
    private final RestRpcGateway restRpcGateway;
    private final ObjectMapper objectMapper;

    public ProxyInvocationHandler(Class<?> targetClass, RestRpcGateway restRpcGateway, ObjectMapper objectMapper) {
        this.targetClass = targetClass;
        this.restRpcGateway = restRpcGateway;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RestRpcClient RestRpcClient = method.getDeclaringClass().getAnnotation(RestRpcClient.class);
        if (RestRpcClient == null) {
            throw new IllegalStateException("Class must be annotated with @RestClient");
        }

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (requestMapping == null && getMapping == null) {
            throw new IllegalStateException("Method should be annotated with @RequestMapping or @GetMapping");
        }

        HttpMethod httpMethod = null;
        String[] path = new String[1];
        if (requestMapping != null) {
            path[0] = requestMapping.path()[0];
            httpMethod = requestMapping.method()[0].asHttpMethod();
        } else if (getMapping != null) {
            path[0] = getMapping.path()[0];
            httpMethod = HttpMethod.GET;
        }

        final String baseUrl = RestRpcClient.host();

        Map<String, Object> requestParameters = getRequestParameters(method, args);
        Map<String, Object> pathVariables = getPathVariables(method, args);
        if (!pathVariables.isEmpty()) {
            pathVariables.entrySet().forEach(entry -> path[0] = path[0].replace("{" + entry.getKey() + "}", entry.getValue().toString()));
        }

        if (requestParameters.size() > 0) {
            String parameters = requestParametersToUrl(requestParameters);
            path[0] = path[0] + parameters;
        }

        String jsonBody = objectMapper.writeValueAsString(getRequestBody(method, args));
        HttpRequest httpRequest = new HttpRequest(httpMethod.name(), baseUrl, path[0], Collections.EMPTY_MAP, jsonBody, method.getReturnType());

        if (method.getReturnType() == Mono.class) {
            return restRpcGateway.doAsyncRequest(httpRequest);
        } else {
            ResponseEntity<?> responseEntity = restRpcGateway.doSyncRequest(httpRequest);
            if (method.getReturnType() == ResponseEntity.class) {
                return responseEntity;
            } else {
                return responseEntity.getBody();
            }
        }
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

    private Object getRequestBody(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i != parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                return args[i];
            }
        }
        return null;
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