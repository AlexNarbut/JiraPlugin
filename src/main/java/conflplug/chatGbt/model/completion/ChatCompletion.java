package conflplug.chatGbt.model.completion;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatCompletion {
    @SerializedName("id")
    private String id;

    @SerializedName("object")
    private String object;

    @SerializedName("created")
    private long created;

    @SerializedName("model")
    private String model;

    @SerializedName("choices")
    private Choice[] choices;

    @SerializedName("usage")
    private Choice.Usage usage;

    @SerializedName("system_fingerprint")
    private String systemFingerprint;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Choice {
        @SerializedName("index")
        private int index;

        @SerializedName("message")
        private Message message;

        @SerializedName("logprobs")
        private String logprobs;

        @SerializedName("finish_reason")
        private String finishReason;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class Message {
            @SerializedName("role")
            private String role;
            @SerializedName("content")
            private String content;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class Usage {
            @SerializedName("prompt_tokens")
            private int promptTokens;
            @SerializedName("completion_tokens")
            private int completionTokens;
            @SerializedName("total_tokens")
            private int totalTokens;
        }
    }
}
