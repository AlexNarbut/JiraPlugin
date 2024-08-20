package conflplug.chatGbt.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Response<T extends Object> {
    protected T value;
}


