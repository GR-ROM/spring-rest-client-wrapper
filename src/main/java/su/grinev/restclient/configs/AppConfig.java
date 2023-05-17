package su.grinev.restclient.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import su.grinev.restclient.annotations.EnableRestClient;


@Configuration
public class AppConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}