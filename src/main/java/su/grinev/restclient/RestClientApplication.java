package su.grinev.restclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import su.grinev.restclient.annotations.EnableRestClient;

@EnableRestClient
@SpringBootApplication
public class RestClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}

}
