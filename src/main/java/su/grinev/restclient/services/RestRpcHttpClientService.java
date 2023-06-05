package su.grinev.restclient.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import su.grinev.restclient.http.HttpRequest;

@RequiredArgsConstructor
@Service
public class RestRpcHttpClientService implements RestRpcGateway {

    private final WebClientWrapper webClientWrapper;

    @Override
    public ResponseEntity doSyncRequest(HttpRequest httpRequest) {
        ResponseEntity<String> responseEntity = switch (httpRequest.method()) {
            case "GET" -> webClientWrapper.getRequest(
                    httpRequest.host(),
                    httpRequest.path(),
                    httpRequest.headers());
            case "POST" -> webClientWrapper.postRequest(
                    httpRequest.host(),
                    httpRequest.path(),
                    httpRequest.headers(),
                    httpRequest.body());
            default -> throw new IllegalStateException("Unsupported HTTP method: " + httpRequest.method());
        };
        return responseEntity;
    }

    @Override
    public Mono<?> doAsyncRequest(HttpRequest httpRequest) {
        throw new IllegalStateException("Method not implemented");
    }
}
