Spring rest client wrapper
---
A simple declarative Spring wrapper library for working with REST API services without boilerplate code.

Example of using:
````java
// Create an interface annotated with @RestRpcClient without implementation
@RestRpcClient(host = "http://host:8080")
public interface RestService {
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    String getData(@RequestParam String parameter);
}

// Call a method to send a GET request
String response = RestService.getData("param1");
````
