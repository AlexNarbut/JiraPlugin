package conflplug.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Scanned
@Named("AiServlet")
public class AiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AiServlet.class);

    @ComponentImport
    private final TemplateRenderer templateRenderer;

    @Inject
    public AiServlet(@ComponentImport final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        //templateRenderer.render("ai_form_form.vm", resp.getWriter());
        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>Hello World3</body></html>");

        //templateRenderer.render("ai_form_form.vm", resp.getWriter());
    }

}