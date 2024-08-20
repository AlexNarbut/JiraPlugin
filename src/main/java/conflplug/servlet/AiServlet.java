package conflplug.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.Maps;
import conflplug.chatGbt.ChatGbtGot;
import conflplug.chatGbt.model.completion.ChatCompletion;
import conflplug.chatGbt.model.response.ErrorResponse;
import conflplug.chatGbt.model.response.Response;
import conflplug.chatGbt.model.response.SuccessResponse;
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

    private final String defaultError = "An error has occurred";
    private final String jsonError = "Data is not recognized";

    private String chatId;

    @Inject
    public AiServlet(TemplateRenderer templateRenderer, PageBuilderService pageBuilderService) {
        this.templateRenderer = templateRenderer;
        this.pageBuilderService = pageBuilderService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            Response<ChatCompletion> answer = ChatGbtGot.getInstance().askQuestion(chatId, question, defaultError, jsonError);

            if (answer instanceof SuccessResponse) {
                ChatCompletion chatCompletion = answer.getValue();

                chatId = chatCompletion.getId();

                StringBuilder builder = new StringBuilder();
                ChatCompletion.Choice[] choices = chatCompletion.getChoices();
                for (ChatCompletion.Choice choice : choices) {
                    builder.append(choice.getMessage().getContent()).append("\n");
                }
                context.put(ANSWER_PARAM_KEY, builder.toString());
            } else if (answer instanceof ErrorResponse) {
                chatId = null;
                context.put(ANSWER_PARAM_KEY, ((ErrorResponse<ChatCompletion>) answer).getMessage());
            }
        }

        context.put(QUESTION_PARAM_KEY, question);

        resp.setContentType(HTML_CONTENT_TYPE);
        templateRenderer.render(MAIN_TEMPLATE, context, resp.getWriter());
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
