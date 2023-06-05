package su.grinev.restclient.reflections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import su.grinev.restclient.annotations.RestRpcClient;
import su.grinev.restclient.http.HttpRequest;
import su.grinev.restclient.services.RestRpcGateway;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ProxyInvocationHandler implements InvocationHandler {

    private final RestRpcGateway restRpcGateway;
    private final ObjectMapper objectMapper;
    private final Map<Method, HttpRequest> cachedRequests;

    public ProxyInvocationHandler(Class<?> targetClass, RestRpcGateway restRpcGateway, ObjectMapper objectMapper) {
        this.restRpcGateway = restRpcGateway;
        this.objectMapper = objectMapper;
        this.cachedRequests = new HashMap<>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpRequest httpRequest = extractRequestParameters(method, args);

        if (method.getReturnType() == Mono.class) {
            return restRpcGateway.doAsyncRequest(httpRequest);
        } else {
            ResponseEntity<String> responseEntity = restRpcGateway.doSyncRequest(httpRequest);
            if (method.getReturnType() != String.class) {
                Object responseObject = objectMapper.readValue(responseEntity.getBody(), method.getReturnType());
                if (method.getReturnType() == ResponseEntity.class) {
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.putAll(responseEntity.getHeaders());
                    return new ResponseEntity<>(responseObject, responseHeaders, responseEntity.getStatusCode());
                } else {
                    return responseObject;
                }
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

    private Map<String, String> getHeadersFromAnnotation(String[] headersArray) {
        Map<String, String> headers = new HashMap<>();
        for (String header : headersArray) {
            String[] headerParts = header.split("=");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }
        return headers;
    }

    private Map<String, String> getHeadersFromArguments(Object[] args) {
        Map<String, String> headers = new HashMap<>();
        for (Object arg : args) {
            if (arg instanceof HttpHeaders argHeaders) { headers.putAll(argHeaders.toSingleValueMap()); }
        }
        return headers;
    }

    private HttpRequest extractParamsFromAnnotations(Method method) {
        return null;
    }

    private HttpRequest extractRequestParameters(Method method, Object[] args) {
        RestRpcClient restRpcClient = method.getDeclaringClass().getAnnotation(RestRpcClient.class);
        if (restRpcClient == null) {
            throw new IllegalStateException("Class must be annotated with @RestClient");
        }

        List<? extends Annotation> mappingAnnotation = Stream.of(method.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(RequestMapping.class) || a instanceof RequestMapping)
                .toList();

        if (mappingAnnotation.size() != 1) {
            throw new IllegalStateException("Method must be annotated exactly one annotation the following: @RequestMapping, " +
                    "@GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping");
        }

        HttpMethod httpMethod = null;
        Map<String, String> headers = new HashMap<>();
        final String baseUrl = restRpcClient.host();

        headers.putAll(getHeadersFromArguments(args));
        headers.putAll(getHeadersFromAnnotation(restRpcClient.headers()));
        String[] path = new String[1];

        if (mappingAnnotation.get(0) instanceof RequestMapping requestMapping) {
            path[0] = requestMapping.path()[0];
            httpMethod = requestMapping.method()[0].asHttpMethod();
            headers.putAll(getHeadersFromAnnotation(requestMapping.headers()));
        } else if (mappingAnnotation.get(0) instanceof PostMapping postMapping) {
            path[0] = postMapping.path()[0];
            httpMethod = HttpMethod.POST;
            headers.putAll(getHeadersFromAnnotation(postMapping.headers()));
        } else if (mappingAnnotation.get(0) instanceof GetMapping getMapping) {
            path[0] = getMapping.path()[0];
            httpMethod = HttpMethod.GET;
            headers.putAll(getHeadersFromAnnotation(getMapping.headers()));
        } else if (mappingAnnotation.get(0) instanceof PutMapping putMapping) {
            path[0] = putMapping.path()[0];
            httpMethod = HttpMethod.PUT;
            headers.putAll(getHeadersFromAnnotation(putMapping.headers()));
        } else if (mappingAnnotation.get(0) instanceof PatchMapping patchMapping) {
            path[0] = patchMapping.path()[0];
            httpMethod = HttpMethod.PATCH;
            headers.putAll(getHeadersFromAnnotation(patchMapping.headers()));
        } else if (mappingAnnotation.get(0) instanceof DeleteMapping deleteMapping) {
            path[0] = deleteMapping.path()[0];
            httpMethod = HttpMethod.DELETE;
            headers.putAll(getHeadersFromAnnotation(deleteMapping.headers()));
        }
        Map<String, Object> requestParameters = getRequestParameters(method, args);
        Map<String, Object> pathVariables = getPathVariables(method, args);

        if (!pathVariables.isEmpty()) { pathVariables.forEach((key, value) -> path[0] = path[0].replace("{" + key + "}", value.toString())); }
        if (requestParameters.size() > 0) {
            String parameters = requestParametersToUrl(requestParameters);
            path[0] = path[0] + parameters;
        }

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(getRequestBody(method, args));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return HttpRequest.builder()
                .host(baseUrl)
                .path(path[0])
                .method(httpMethod.toString())
                .headers(headers)
                .body(jsonBody)
                .build();
    }
}