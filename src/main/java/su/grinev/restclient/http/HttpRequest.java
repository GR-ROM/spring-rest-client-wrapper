package su.grinev.restclient.http;

import lombok.Getter;

import java.util.Map;

@Getter
public class HttpRequest {
    private final String method;
    private final String host;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
    private final Class<?> expectedResponseClass;

    public HttpRequest(String method, String host, String path, Map<String, String> headers, String body, Class<?> expectedResponseClass) {
        this.method = method;
        this.host = host;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.expectedResponseClass = expectedResponseClass;
    }
}
