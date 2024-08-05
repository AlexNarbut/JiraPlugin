package conflplug.chatGbt.model.completion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatCompletion {
    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("created")
    private long created;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private Choice[] choices;

    @JsonProperty("usage")
    private Choice.Usage usage;

    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Choice {
        @JsonProperty("index")
        private int index;

        @JsonProperty("message")
        private Message message;

        @JsonProperty("logprobs")
        private String logprobs;

        @JsonProperty("finish_reason")
        private String finishReason;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class Message {
            @JsonProperty("role")
            private String role;
            @JsonProperty("content")
            private String content;
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class Usage {
            @JsonProperty("prompt_tokens")
            private int promptTokens;
            @JsonProperty("completion_tokens")
            private int completionTokens;
            @JsonProperty("total_tokens")
            private int totalTokens;
        }
    }
}
