package su.grinev.restclient.services;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WebClientWrapper {
    ResponseEntity<?> getRequest(String host, String url, Map<String, String> headers, Class resultClazz);
}
