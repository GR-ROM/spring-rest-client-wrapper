package su.grinev.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import su.grinev.restclient.example.CoordsHandler;

@SpringBootTest
class RestRpcClientApplicationTests {

	@Autowired
	private CoordsHandler restClient;

	@Test
	void contextLoads() {
		System.out.println(restClient.getCoords("AST_162949f3-e6a1-4766-b4c3-761870fe07b6-deleted-26585"));
	}

}
