package conflplug.chatGbt.model.response;

import lombok.Getter;

@Getter
public class ErrorResponse<T extends Object> extends Response<T> {
    private final Exception exception;
    private final String message;

    public ErrorResponse(Exception exception, String message) {
        super(null);
        this.exception = exception;
        this.message = message;
    }
}