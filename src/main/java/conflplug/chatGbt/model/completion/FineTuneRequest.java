package conflplug.chatGbt.model.completion;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FineTuneRequest {
    @SerializedName("training_file")
    @NonNull
    String trainingFile;

    @SerializedName("validation_file")
    String validationFile;

    String model;

    @SerializedName("n_epochs")
    Integer nEpochs;

    @SerializedName("batch_size")
    Integer batchSize;

    @SerializedName("learning_rate_multiplier")
    Double learningRateMultiplier;

    @SerializedName("prompt_loss_weight")
    Double promptLossWeight;

    @SerializedName("compute_classification_metrics")
    Boolean computeClassificationMetrics;

    @SerializedName("classification_n_classes")
    Integer classificationNClasses;

    @SerializedName("classification_positive_class")
    String classificationPositiveClass;

    @SerializedName("classification_betas")
    List<Double> classificationBetas;

    String suffix;

    public static class FineTuneRequestBuilder {
        private String trainingFile;
        private String validationFile;
        private String model;
        private Integer nEpochs;
        private Integer batchSize;
        private Double learningRateMultiplier;
        private Double promptLossWeight;
        private Boolean computeClassificationMetrics;
        private Integer classificationNClasses;
        private String classificationPositiveClass;
        private List<Double> classificationBetas;
        private String suffix;

        FineTuneRequestBuilder() {
        }

        public FineTuneRequestBuilder trainingFile(@NonNull String trainingFile) {
            if (trainingFile == null) {
                throw new NullPointerException("trainingFile is marked non-null but is null");
            } else {
                this.trainingFile = trainingFile;
                return this;
            }
        }

        public FineTuneRequestBuilder validationFile(String validationFile) {
            this.validationFile = validationFile;
            return this;
        }

        public FineTuneRequestBuilder model(String model) {
            this.model = model;
            return this;
        }

        public FineTuneRequestBuilder nEpochs(Integer nEpochs) {
            this.nEpochs = nEpochs;
            return this;
        }

        public FineTuneRequestBuilder batchSize(Integer batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public FineTuneRequestBuilder learningRateMultiplier(Double learningRateMultiplier) {
            this.learningRateMultiplier = learningRateMultiplier;
            return this;
        }

        public FineTuneRequestBuilder promptLossWeight(Double promptLossWeight) {
            this.promptLossWeight = promptLossWeight;
            return this;
        }

        public FineTuneRequestBuilder computeClassificationMetrics(Boolean computeClassificationMetrics) {
            this.computeClassificationMetrics = computeClassificationMetrics;
            return this;
        }

        public FineTuneRequestBuilder classificationNClasses(Integer classificationNClasses) {
            this.classificationNClasses = classificationNClasses;
            return this;
        }

        public FineTuneRequestBuilder classificationPositiveClass(String classificationPositiveClass) {
            this.classificationPositiveClass = classificationPositiveClass;
            return this;
        }

        public FineTuneRequestBuilder classificationBetas(List<Double> classificationBetas) {
            this.classificationBetas = classificationBetas;
            return this;
        }

        public FineTuneRequestBuilder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public FineTuneRequest build() {
            return new FineTuneRequest(this.trainingFile, this.validationFile, this.model, this.nEpochs, this.batchSize, this.learningRateMultiplier, this.promptLossWeight, this.computeClassificationMetrics, this.classificationNClasses, this.classificationPositiveClass, this.classificationBetas, this.suffix);
        }

        public String toString() {
            return "FineTuneRequest.FineTuneRequestBuilder(trainingFile=" + this.trainingFile + ", validationFile=" + this.validationFile + ", model=" + this.model + ", nEpochs=" + this.nEpochs + ", batchSize=" + this.batchSize + ", learningRateMultiplier=" + this.learningRateMultiplier + ", promptLossWeight=" + this.promptLossWeight + ", computeClassificationMetrics=" + this.computeClassificationMetrics + ", classificationNClasses=" + this.classificationNClasses + ", classificationPositiveClass=" + this.classificationPositiveClass + ", classificationBetas=" + this.classificationBetas + ", suffix=" + this.suffix + ")";
        }
    }
}
