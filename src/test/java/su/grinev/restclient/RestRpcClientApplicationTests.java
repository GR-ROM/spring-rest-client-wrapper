package su.grinev.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import su.grinev.restclient.example.CoordsHandler;
import su.grinev.restclient.exceptions.RpcErrorException;

@SpringBootTest
class RestRpcClientApplicationTests {

	@Autowired
	private CoordsHandler restClient;

	@Test
	void contextLoads() {
		try {
			System.out.println(restClient.getCoords("G36_1000000038"));
		} catch (RpcErrorException ex) {
			System.out.println(ex.getErrorResponse() + " " + ex.getNestedException().toString());
		}
	}

}
