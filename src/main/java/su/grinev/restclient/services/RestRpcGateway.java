package su.grinev.restclient.services;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import su.grinev.restclient.http.HttpRequest;

public interface RestRpcGateway {

    ResponseEntity<String> doSyncRequest(HttpRequest httpRequest);

    Mono<?> doAsyncRequest(HttpRequest httpRequest);
}
