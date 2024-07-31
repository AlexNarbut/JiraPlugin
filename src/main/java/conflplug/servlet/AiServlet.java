package conflplug.servlet;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    private final String CHAT_GBT_API_URL = "https://api.chatgpt.com/v1/converse";
    private final String CHAT_GBT_API_KEY = "sk-proj-dZ02qE2fynGpdNfUPPa7T3BlbkFJYyAJPZF5wpeHtW7rPWG9";


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
            URL url = new URL(CHAT_GBT_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + CHAT_GBT_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            String requestBody = "{\"question\":\"" + question + "\"}";

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.close();

            int responseCode = connection.getResponseCode();
            System.out.println("Response status: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(responseBody.toString());
                return jsonObject.getString("answer");
            } else {
                return defaultError + " (" + connection.getResponseMessage() + ")";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            return jsonError;
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
