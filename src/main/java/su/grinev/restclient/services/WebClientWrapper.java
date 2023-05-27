package su.grinev.restclient.services;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WebClientWrapper {
    ResponseEntity<String> getRequest(String host, String url, Map<String, String> headersMap);

    ResponseEntity<?> deleteRequest(String host, String url, Map<String, String> headersMap);

    ResponseEntity<String> postRequest(String host, String url, Map<String, String> headersMap, Object requestBody);

    ResponseEntity<?> putRequest(String host, String url, Map<String, String> headersMap, Object requestBody, Class<?> returnType);

    ResponseEntity<?> patchRequest(String host, String url, Map<String, String> headersMap, Object requestBody, Class<?> returnType);
}
