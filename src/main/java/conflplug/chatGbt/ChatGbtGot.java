package conflplug.chatGbt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import conflplug.chatGbt.model.completion.ChatCompletion;
import conflplug.chatGbt.model.completion.CompletionMessageRequest;
import conflplug.chatGbt.model.response.ErrorResponse;
import conflplug.chatGbt.model.response.Response;
import conflplug.chatGbt.model.response.SuccessResponse;
import conflplug.utils.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatGbtGot {
    private static ChatGbtGot instance;

    public static synchronized ChatGbtGot getInstance() {
        if (instance == null) {
            instance = new ChatGbtGot();
        }
        return instance;
    }

    private OpenAiService service;

    private ChatGbtGot() {
        try {
            //service = new OpenAiService(CHAT_GBT_API_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final String CHAT_GBT_BASE_URL = "https://api.openai.com/v1/";
    private final String CHAT_GBT_COMPLETIONS_URL = CHAT_GBT_BASE_URL + "chat/completions";
    private final String CHAT_GBT_SUMMARIZATION_URL = CHAT_GBT_BASE_URL + "summarization";
    private final String CHAT_GBT_FILE_ASSISTANCE_URL = CHAT_GBT_BASE_URL + "files";

    private final String CHAT_GBT_API_KEY = "API-KEY";
    private final String GBT_3_5_MODEL = "gpt-3.5-turbo";
    private final String GBT_4_1106_MODEL = "gpt-4-1106-preview";
    private final String GBT_TEXT_DAVINCI_MODEL = "text-davinci-003";


    public Response<ChatCompletion> askQuestion(String chatId, String question, String defaultError, String parseError) {
        Gson gson = new GsonBuilder().create();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(CHAT_GBT_COMPLETIONS_URL);
        httpPost.setHeader("Authorization", "Bearer " + CHAT_GBT_API_KEY);
        httpPost.setHeader("Content-Type", "application/json");

        String str = "{\"model\": \"" + GBT_3_5_MODEL + "\"," +
                " \"messages\": [{\"role\": \"user\", \"content\": \"" + question + "\"}]}";
        StringEntity stringEntity = new StringEntity(str, "UTF-8");

        List<CompletionMessageRequest> messages = new ArrayList<>();
        messages.add(new CompletionMessageRequest("user", question));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("model", GBT_3_5_MODEL);
        jsonObject.add("messages", gson.toJsonTree(messages, new TypeToken<List<CompletionMessageRequest>>() {
        }.getType()).getAsJsonArray());
        if (!TextUtils.isEmpty(chatId)) jsonObject.addProperty("conversation_id", chatId);


        try {
            httpPost.setEntity(new StringEntity(jsonObject.toString(), "UTF-8"));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

                ChatCompletion completion = gson.fromJson(response, ChatCompletion.class);
                return new SuccessResponse<>(completion);

            } else {
                return new ErrorResponse<>(new IllegalAccessException(), defaultError + " (Response code: " + statusCode + ")");
            }
        } catch (IOException e) {
            return new ErrorResponse<>(e,
                    parseError + (e.getCause() != null ? "(" + e.getCause().toString() + ")" : ""));
        }
    }

    // Apache HttpClient
    // OkHttp

    public String analyzePdfFile(File file, String question, String defaultError, String parseError) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(CHAT_GBT_FILE_ASSISTANCE_URL);
        httpPost.setHeader("Authorization", "Bearer " + CHAT_GBT_API_KEY);
        httpPost.setHeader("Content-Type", "application/json");

        String content = FileUtils.getInstance().readFile(file);
        int maxContent = Math.min(content.length(), 1000);

        StringEntity stringEntity = new StringEntity(
                "{" +
                        "\"model\": \"" + GBT_TEXT_DAVINCI_MODEL + "\", " +
                        "\"max_tokens\": " + 1000 + ", " +
                        "\"content\": \"" + content.substring(0, maxContent) + "\"",
                //"\"messages\": [{\"role\": \"user\", \"content\": \"" + content + "\"}]" + "}",
                "UTF-8");
        httpPost.setEntity(stringEntity);

        try {
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//            builder.addBinaryBody("file", file);
//            builder.addTextBody("model", GBT_TEXT_DAVINCI_MODEL);
//            HttpEntity multipart = builder.build();
//            httpPost.setEntity(multipart);


            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                return responseBody != null ? responseBody : defaultError;
            } else return defaultError + " (Response code: " + statusCode + ")";
        }
//        catch (JsonParseException | JsonMappingException e) {
//            return parseError + (e.getCause() != null ? "(" + e.getCause().toString() + ")" : "");
//        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return defaultError;
    }


    public String summary(String content, String defaultError, String parseError) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(CHAT_GBT_COMPLETIONS_URL);
        httpPost.setHeader("Authorization", "Bearer " + CHAT_GBT_API_KEY);
        httpPost.setHeader("Content-Type", "application/json");

        StringEntity stringEntity = new StringEntity(
                "{" +
                        "\"model\": \"" + GBT_TEXT_DAVINCI_MODEL + "\", " +
                        "\"max_tokens\": " + 100 + ", " +
                        "\"messages\": [{\"role\": \"user\", \"content\": \"" + content + "\"}]"
                        + "}",
                "UTF-8");
        httpPost.setEntity(stringEntity);

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

                Gson gson = new GsonBuilder().create();
                ChatCompletion completion = gson.fromJson(response, ChatCompletion.class);

                StringBuilder builder = new StringBuilder();
                ChatCompletion.Choice[] choices = completion.getChoices();
                for (ChatCompletion.Choice choice : choices) {
                    builder.append(choice.getMessage().getContent()).append("\n");
                }
                return builder.toString();

            } else {
                return defaultError + " (Response code: " + statusCode + ")";
            }
        } catch (JsonParseException | JsonMappingException e) {
            return parseError + (e.getCause() != null ? "(" + e.getCause().toString() + ")" : "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return defaultError;
    }

}
