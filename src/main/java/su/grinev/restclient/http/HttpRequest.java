package su.grinev.restclient.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public record HttpRequest(String method, String host, String path, Map<String, String> headers, String body) {
}
