package su.grinev.restclient.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import su.grinev.restclient.annotations.RestRpcClient;

@RestRpcClient(host = "http://localhost:8080")
public interface CoordsHandler {
    @RequestMapping(value = "/coords", method = RequestMethod.GET)
    String getCoords(@RequestParam String imei);
}