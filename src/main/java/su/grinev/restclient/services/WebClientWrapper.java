package su.grinev.restclient.services;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WebClientWrapper {
    ResponseEntity<?> getRequest(String host, String url, Map<String, String> headersMap, Class returnType);

    ResponseEntity<?> deleteRequest(String host, String url, Map<String, String> headersMap, Class returnType);

    ResponseEntity<?> postRequest(String host, String url, Map<String, String> headersMap, Object requestBody, Class returnType);

    ResponseEntity<?> putRequest(String host, String url, Map<String, String> headersMap, Object requestBody, Class<?> returnType);

    ResponseEntity<?> patchRequest(String host, String url, Map<String, String> headersMap, Object requestBody, Class<?> returnType);
}
