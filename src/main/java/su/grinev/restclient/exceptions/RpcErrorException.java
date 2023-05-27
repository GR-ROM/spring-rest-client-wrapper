package su.grinev.restclient.exceptions;

import lombok.Getter;

@Getter
public class RpcErrorException extends RuntimeException {

    private final String errorResponse;

    private final Throwable nestedException;

    public RpcErrorException(String responseBody) {
        super(responseBody);
        this.errorResponse = responseBody;
        this.nestedException = null;
    }

    public RpcErrorException(Throwable nestedException) {
        super();
        this.nestedException = nestedException;
        this.errorResponse = "";
    }

    public RpcErrorException() {
        super();
        this.errorResponse = "";
        this.nestedException = null;
    }

}
