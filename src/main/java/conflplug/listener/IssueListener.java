package conflplug.listener;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.user.LoginEvent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

@Scanned
@Named("IssueListener")
@Component
public class IssueListener implements InitializingBean, DisposableBean {
    private final Logger logger = Logger.getLogger(this.getClass());

    @ComponentImport
    private final EventPublisher eventPublisher;

    @Inject
    public IssueListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register(this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent event) throws Exception {
        this.logger.error(event.toString());
    }

    @EventListener
    public void onIssueEvent(LoginEvent event) throws Exception {
        this.logger.error(event.toString());
    }
}