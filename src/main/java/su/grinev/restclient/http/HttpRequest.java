package su.grinev.restclient.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class HttpRequest {
    private final String method;
    private final String host;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
}
