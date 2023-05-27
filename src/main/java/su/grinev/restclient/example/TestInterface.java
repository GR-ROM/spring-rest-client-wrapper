package su.grinev.restclient.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.grinev.restclient.annotations.RestRpcClient;

@RestRpcClient(host = "http://localhost:8080")
public interface TestInterface {
    @GetMapping(path = "/test")
    String getRequest(@RequestParam String test, @RequestParam int a, @RequestParam int b);
}
