package conflplug.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.Maps;
import conflplug.chatGbt.model.completion.ChatCompletion;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Map;

import java.net.HttpURLConnection;


@Scanned
@Named("AiServlet")
public class AiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AiServlet.class);


    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final PageBuilderService pageBuilderService;

    private final String MAIN_TEMPLATE = "/templates/ai_form.vm";
    private final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final String QUESTION_PARAM_KEY = "question";
    private final String ANSWER_PARAM_KEY = "answer";
    private final String TIME_PARAM_KEY = "time";

    private final String CHAT_GBT_API_URL = "https://api.openai.com/v1/chat/completions";
    private final String CHAT_GBT_API_KEY = "sk-t9XwSQ1eisTtcNpPvTgZt5PXMqOMr_bKotWTKrJId3T3BlbkFJjJvXmLAJ_xP1E3F2iqak7_BqufbNlK8QcpSAC8xvAA";
    private final String GBT_MODEL = "gpt-3.5-turbo";

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Inject
    public AiServlet(TemplateRenderer templateRenderer, PageBuilderService pageBuilderService) {
        this.templateRenderer = templateRenderer;
        this.pageBuilderService = pageBuilderService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // loading web resources(css,js)
//        this.pageBuilderService
//                .assembler()
//                .resources()
//                .requireWebResource("custom-jira-report:test-resource");

        // map for parameters
        Map<String, Object> context = Maps.newHashMap();

        response.setContentType(HTML_CONTENT_TYPE);
        templateRenderer.render(MAIN_TEMPLATE, context, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> context = Maps.newHashMap();
        String question = req.getParameter(QUESTION_PARAM_KEY);
        if (question != null) {
            String time = requestTime();
            context.put(TIME_PARAM_KEY, time);

            String answer = askChatGbt(question);
            context.put(ANSWER_PARAM_KEY, answer);
        }

        context.put(QUESTION_PARAM_KEY, question);

        resp.setContentType(HTML_CONTENT_TYPE);
        templateRenderer.render(MAIN_TEMPLATE, context, resp.getWriter());
    }

    private String askChatGbt(String question) {
        String defaultError = "An error has occurred";
        String jsonError = "Data is not recognized";
        try {
            URL obj = new URL(CHAT_GBT_API_URL);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + CHAT_GBT_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String json = "{\"model\": \"" + GBT_MODEL + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + question + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();


            int responseCode = connection.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
// Response from ChatGPT
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                ChatCompletion completion = jsonMapper.readValue(response.toString(), ChatCompletion.class);

                StringBuilder builder = new StringBuilder();
                ChatCompletion.Choice[] choices = completion.getChoices();
                for (ChatCompletion.Choice choice : choices) {
                    builder.append(choice.getMessage().getContent()).append("\n");
                }
                return builder.toString();
            } else {
                return defaultError + " (Response code: " + responseCode + ")";
            }
        }
        catch (JsonParseException | JsonMappingException e) {
            return jsonError + (e.getCause() != null ? "(" +e.getCause().toString() + ")" : "");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return defaultError;
    }

    private final String urlString = "http://www.google.com"; // URL запроса

    private String requestTime() {
        String answer = "System request failed";
        try {
            // Создание объекта URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Настройка метода запроса
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Получение ответа
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Проверка успешного ответа
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String dateHeader = connection.getHeaderField("Date");
                answer = dateHeader != null ? dateHeader : answer;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
