package su.grinev.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import su.grinev.restclient.example.TestInterface;
import su.grinev.restclient.exceptions.RpcErrorException;

@SpringBootTest
class RestRpcClientApplicationTests {

	@Autowired
	private TestInterface testInterface;

	@Test
	void contextLoads() {
		try {
			System.out.println(testInterface.getRequest("test", 2, 2));
		} catch (RpcErrorException ex) {
			System.out.println(ex.getErrorResponse() + " " + ex.getNestedException().toString());
		}
	}

}
