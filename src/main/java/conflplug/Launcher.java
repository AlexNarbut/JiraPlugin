package conflplug;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.concurrent.GuardedBy;

@Scanned
@Named("Launcher")
@Component
public class Launcher implements LifecycleAware, InitializingBean, DisposableBean {
    private final Logger logger = Logger.getLogger(this.getClass());

    private final EventPublisher eventPublisher;

    @GuardedBy("this")
    private final Set<LifecycleEvent> lifecycleEvents = EnumSet.noneOf(LifecycleEvent.class);

    /**
     * Used to keep track of everything that needs to happen before we are sure that it is safe
     * to talk to all of the components we need to use, particularly the {@code SchedulerService}
     * and Active Field.  We will not try to initialize until all of them have happened.
     */
    enum LifecycleEvent {
        AFTER_PROPERTIES_SET,
        PLUGIN_ENABLED,
        LIFECYCLE_AWARE_ON_START
    }

    @Inject
    public Launcher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * This is received from Spring after the bean's properties are set.  We need to accept this to know when
     * it is safe to register an event listener.
     */
    @Override
    public void afterPropertiesSet() {
        this.logger.error("================afterPropertiesSet================");
        this.registerListener();
        this.onLifecycleEvent(LifecycleEvent.AFTER_PROPERTIES_SET);
    }

    @EventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.logger.error("================onPluginModuleEnabled================");
    }

    /**
     * This is received from the plugin system after the plugin is fully initialized.  It is not safe to use
     * Active Field before this event is received.
     */
    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        this.logger.error("================onPluginEnabled " + event.getPlugin().getKey() + "================");
        if ("custom-jira-report".equals(event.getPlugin().getKey())) {

            this.onLifecycleEvent(LifecycleEvent.PLUGIN_ENABLED);

        }
    }

    @EventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.logger.error("================onPluginModuleDisabled================");
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.logger.error("================onPluginDisabled================");
    }

    /**
     * This is received from SAL after the system is really up and running from its perspective. This includes
     * things like the database being set up and other tricky things like that.  This needs to happen before we
     * try to schedule anything, or the scheduler's tables may not be in a good state on a clean install.
     */
    @Override
    public void onStart() {
        this.logger.error("================onStart================");
        this.onLifecycleEvent(LifecycleEvent.LIFECYCLE_AWARE_ON_START);
    }

    @Override
    public void onStop() {
        this.logger.error("================onStop================");
    }

    /**
     * This is received from Spring when we are getting destroyed.  We should make sure we do not leave any
     * event listeners or job runners behind; otherwise, we could leak the current plugin context, leading to
     * exceptions from destroyed OSGi proxies, memory leaks, and strange behaviour in general.
     */
    @Override
    public void destroy() {

        this.logger.error("================destroy================");
        this.unregisterListener();
    }

    /**
     * The latch which ensures all of the plugin/application lifecycle progress is completed before we call
     * {@code launch()}.
     */
    private void onLifecycleEvent(LifecycleEvent event) {
        if (this.isLifecycleReady(event)) {

            unregisterListener();

            try {

                this.launch();

            } catch (Exception ex) {

                this.logger.error("Shit happens(", ex);

            }
        }
    }

    /**
     * The event latch.
     * <p>
     * When something related to the plugin initialization happens, we call this with
     * the corresponding type of the event.  We will return {@code true} at most once, when the very last type
     * of event is triggered.  This method has to be {@code synchronized} because {@code EnumSet} is not
     * thread-safe and because we have multiple accesses to {@code lifecycleEvents} that need to happen
     * atomically for correct behaviour.
     * </p>
     *
     * @param event the lifecycle event that occurred
     * @return {@code true} if this completes the set of initialization-related events; {@code false} otherwise
     */
    synchronized private boolean isLifecycleReady(LifecycleEvent event) {
        return this.lifecycleEvents.add(event) && this.lifecycleEvents.size() == LifecycleEvent.values().length;
    }

    /**
     * Do all the things we can't do before the system is fully up.
     */
    private void launch() {
        this.logger.error("Im sexy and i know it!!!!");
    }

    /**
     * Register listener
     */
    private void registerListener() {
        this.eventPublisher.register(this);
    }

    /**
     * Unregister listener
     */
    private void unregisterListener() {
        this.eventPublisher.unregister(this);
    }
}