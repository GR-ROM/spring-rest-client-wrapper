package su.grinev.restclient.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
public class HttpResponse {

    private int httpStatusCode;
    private Map<String, String> headers;
    private String responseBody;

}
