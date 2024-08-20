package conflplug.servlet;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.web.bean.PagerFilter;
import conflplug.chatGbt.ChatGbtGot;
import conflplug.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Scanned
@Named("FileScanServlet")
public class FileScanServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(FileScanServlet.class);

    @ComponentImport
    private final TemplateRenderer templateRenderer;

    @ComponentImport
    private final SearchService searchService;
    @ComponentImport
    private final AttachmentManager attachmentManager;
    @ComponentImport
    private final AttachmentPathManager attachmentPathManager;
    @ComponentImport
    private JiraAuthenticationContext authenticationContext;

    private final String MAIN_TEMPLATE = "/templates/file_scan_page.vm";
    private final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final String defaultGbtError = "An Gbt error has occurred";
    private final String gbtJsonError = "Gbt data is not recognized";
    private final String gbtQuestion = "Опиши краткое содержание файла";

    private final String FILE_PATH_PARAM_KEY = "fileInput";

    @Inject
    public FileScanServlet(TemplateRenderer templateRenderer,
                           SearchService searchService,
                           AttachmentManager attachmentManager,
                           AttachmentPathManager attachmentPathManager,
                           JiraAuthenticationContext authenticationContext) {
        this.templateRenderer = templateRenderer;
        this.searchService = searchService;
        this.attachmentManager = attachmentManager;
        this.attachmentPathManager = attachmentPathManager;
        this.authenticationContext = authenticationContext;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> context = Maps.newHashMap();

        response.setContentType("text/html");
        response.getWriter().println("<html><body>");

        // Получаем список Issue с файлами во вложении
        ApplicationUser user = authenticationContext.getLoggedInUser();

        JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
        Query query = jqlClauseBuilder.issue().isNotEmpty().buildQuery();
        PagerFilter pagerFilter = PagerFilter.getUnlimitedFilter();

        try {
            SearchResults results = searchService.search(user, query, pagerFilter);
            if (results != null && results.getResults() != null && !results.getResults().isEmpty()) {
                String fileDirectory = attachmentPathManager.getAttachmentPath();
                response.getWriter().println("<p>Issues with pdf</p>");
                Issue issue;
                for (Object object : results.getResults()) {
                    issue = (Issue) object;
                    for (Attachment attachment : attachmentManager.getAttachments(issue)) {
                        if (attachment != null && FileUtils.getInstance().isPdfMimetype(attachment.getMimetype())) {
                            response.getWriter().println("<p>Issue: " + issue.getKey() + "</p>");
                            response.getWriter().println("<p>Attachment: " + attachment.getFilename() + "</p>");
                            File file = FileUtils.getInstance().getAttachmentFile(attachment);
                            if (file != null) {
                                String answer = ChatGbtGot.getInstance().analyzePdfFile(
                                        file,
                                        gbtQuestion,
                                        defaultGbtError,
                                        gbtJsonError
                                );
                                response.getWriter().println("<p>Answer: " + answer + "</p>");
                            }

                            throw new IllegalArgumentException();
                        }
                    }
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().println("</body></html>");

//        response.setContentType(HTML_CONTENT_TYPE);
//        templateRenderer.render(MAIN_TEMPLATE, context, response.getWriter());
    }

    private final String TEXT_SUMMARY = "— Eh bien, mon prince. Gênes et Lucques ne sont plus que des apanages, des поместья, de la famille Buonaparte. Non, je vous préviens que si vous ne me dites pas que nous avons la guerre, si vous vous permettez encore de pallier toutes les infamies, toutes les atrocités de cet Antichrist (ma parole, j'y crois) — je ne vous connais plus, vous n'êtes plus mon ami, vous n'êtes plus мой верный раб, comme vous dites 1.";
}