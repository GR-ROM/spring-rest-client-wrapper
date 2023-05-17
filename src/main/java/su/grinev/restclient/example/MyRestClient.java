package su.grinev.restclient.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import su.grinev.restclient.annotations.RestClient;

@RestClient
public interface MyRestClient {
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    String getData();
}