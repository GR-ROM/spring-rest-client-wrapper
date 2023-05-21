package su.grinev.restclient.services;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class WebClientWrapperImpl implements WebClientWrapper {

    public static final int TIMEOUT = 3000;

    public ResponseEntity<?> getRequest(String host, String url, Map<String, String> headersMap, Class resultClazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headersMap.forEach((key, value) -> headers.add(key, value));

        return (ResponseEntity<?>) webClient(host).get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(resultClazz)
                .block(Duration.ofMillis(TIMEOUT));
    }

    @SneakyThrows
    private WebClient webClient(String host) {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .sslProvider(SslProvider.JDK)
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .responseTimeout(Duration.ofMillis(TIMEOUT));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(host)
                .filter(errorHandler())
                .build();
    }

    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.error("HTTP status code: {}", clientResponse.statusCode().value());
                return Mono.empty();
            } else {
                log.info("HTTP status code: {}", clientResponse.statusCode().value());
                return Mono.just(clientResponse);
            }
        });
    }

}
