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
    public ResponseEntity<?> doSyncRequest(HttpRequest httpRequest) {
        ResponseEntity<?> responseEntity = switch (httpRequest.getMethod()) {
            case "GET" -> webClientWrapper.getRequest(httpRequest.getHost(), httpRequest.getPath(), httpRequest.getHeaders());
            default -> throw new IllegalStateException("Unsupported HTTP method: " + httpRequest.getMethod());
        };
        return responseEntity;
    }

    @Override
    public Mono<?> doAsyncRequest(HttpRequest httpRequest) {
        throw new IllegalStateException("Method not implemented");
    }
}
