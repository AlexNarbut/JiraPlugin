package conflplug.chatGbt.model.response;

import lombok.Getter;

@Getter
public class SuccessResponse<T extends Object> extends Response<T> {

    public SuccessResponse(T value) {
        super(value);
    }
}