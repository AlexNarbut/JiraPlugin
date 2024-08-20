package conflplug.chatGbt.model.completion;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompletionRequest {
    String model;
    String prompt;
    String suffix;
    @SerializedName("max_tokens")
    Integer maxTokens;
    Double temperature;
    @SerializedName("top_p")
    Double topP;
    Integer n;
    Boolean stream;
    Integer logprobs;
    Boolean echo;
    List<String> stop;
    @SerializedName("presence_penalty")
    Double presencePenalty;
    @SerializedName("frequency_penalty")
    Double frequencyPenalty;
    @SerializedName("best_of")
    Integer bestOf;
    @SerializedName("logit_bias")
    Map<String, Integer> logitBias;
    String user;


    public static class CompletionRequestBuilder {
        private String model;
        private String prompt;
        private String suffix;
        private Integer maxTokens;
        private Double temperature;
        private Double topP;
        private Integer n;
        private Boolean stream;
        private Integer logprobs;
        private Boolean echo;
        private List<String> stop;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Integer bestOf;
        private Map<String, Integer> logitBias;
        private String user;

        CompletionRequestBuilder() {
        }

        public CompletionRequestBuilder model(String model) {
            this.model = model;
            return this;
        }

        public CompletionRequestBuilder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public CompletionRequestBuilder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public CompletionRequestBuilder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public CompletionRequestBuilder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public CompletionRequestBuilder topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public CompletionRequestBuilder n(Integer n) {
            this.n = n;
            return this;
        }

        public CompletionRequestBuilder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        public CompletionRequestBuilder logprobs(Integer logprobs) {
            this.logprobs = logprobs;
            return this;
        }

        public CompletionRequestBuilder echo(Boolean echo) {
            this.echo = echo;
            return this;
        }

        public CompletionRequestBuilder stop(List<String> stop) {
            this.stop = stop;
            return this;
        }

        public CompletionRequestBuilder presencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public CompletionRequestBuilder frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }


        public CompletionRequestBuilder bestOf(Integer bestOf) {
            this.bestOf = bestOf;
            return this;
        }


        public CompletionRequestBuilder logitBias(Map<String, Integer> logitBias) {
            this.logitBias = logitBias;
            return this;
        }

        public CompletionRequestBuilder user(String user) {
            this.user = user;
            return this;
        }

        public com.theokanning.openai.completion.CompletionRequest build() {
            return new com.theokanning.openai.completion.CompletionRequest(this.model, this.prompt, this.suffix, this.maxTokens, this.temperature, this.topP, this.n, this.stream, this.logprobs, this.echo, this.stop, this.presencePenalty, this.frequencyPenalty, this.bestOf, this.logitBias, this.user);
        }

        public String toString() {
            return "CompletionRequest.CompletionRequestBuilder(model=" + this.model + ", prompt=" + this.prompt + ", suffix=" + this.suffix + ", maxTokens=" + this.maxTokens + ", temperature=" + this.temperature + ", topP=" + this.topP + ", n=" + this.n + ", stream=" + this.stream + ", logprobs=" + this.logprobs + ", echo=" + this.echo + ", stop=" + this.stop + ", presencePenalty=" + this.presencePenalty + ", frequencyPenalty=" + this.frequencyPenalty + ", bestOf=" + this.bestOf + ", logitBias=" + this.logitBias + ", user=" + this.user + ")";
        }
    }
}
