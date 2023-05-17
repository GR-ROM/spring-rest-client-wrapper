package su.grinev.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import su.grinev.restclient.example.MyRestClient;

@SpringBootTest
class RestClientApplicationTests {

	@Autowired
	private MyRestClient restClient;

	@Test
	void contextLoads() {
		restClient.getData();
	}

}
