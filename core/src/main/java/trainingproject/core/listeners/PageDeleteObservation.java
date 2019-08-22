package trainingproject.core.listeners;

import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true,
        service = EventListener.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Training repository delete observation",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*"
        })
public class PageDeleteObservation implements EventListener {
    private final static String MIXIN_VERSIONABLE = "mix:versionable";
    private final static String NODE_PATH = "/content/myapp";
    private final static String MIXIN_TYPE = "jcr:mixinTypes";
    private final static String JCR_CONTENT = "jcr:content";
    private final static String JOB_TOPIC = "log/write/deleted/nodes";

    private final static String BEFORE_VALUE = "beforeValue";
    private final static String REMOVED_PROPERTIES = "REMOVED_PROPERTIES";
    private final static String ADMIN = "admin";
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Session session;
    @Reference
    private SlingRepository repository;
    @Reference
    private JobManager jobManager;
    private final Map<String, Object> props = new HashMap<>();

    @Activate
    void activate(ComponentContext context) {
        try {
            session = repository.login(new SimpleCredentials(ADMIN, ADMIN.toCharArray()));
            if (session != null) {
                session.getWorkspace().getObservationManager().addEventListener(
                        this,
                        Event.NODE_REMOVED | Event.PROPERTY_REMOVED,
                        NODE_PATH,
                        true,
                        null,
                        new String[]{"cq:Page"},
                        false
                );
                LOG.info("Listener {} added.", getClass());
            }
        } catch (RepositoryException e) {
            LOG.error("Under PageChangeObservation was got exception: {}", e.getMessage());
            if (session.isLive()) {
                session.logout();
            }
        }
    }

    @Deactivate
    public void deactivate() {
        if (session != null && session.isLive()) {
            session.logout();
        }
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        while (eventIterator.hasNext()) {
            Event event = eventIterator.nextEvent();
            try {
                String pagePath = getPagePath(event);
                makeVersionableIfNeeded(pagePath);
                String beforeValue = ((Value) event.getInfo().get(BEFORE_VALUE)).getString();
                props.put(event.getPath(), beforeValue);
            } catch (RepositoryException e) {
                LOG.debug("Under PageChangeObservation was got exception: {}", e.getMessage());
            }
        }
        jobManager.addJob(JOB_TOPIC, Collections.singletonMap(REMOVED_PROPERTIES, props));
        props.clear();
    }

    private String getPagePath(Event event) throws RepositoryException {
        String eventPath = event.getPath();
        int startSubstringIndex = 0;
        String SUBSTRING_SEPARATOR = "/";

        return eventPath.contains(JCR_CONTENT)
                ? eventPath.substring(startSubstringIndex, eventPath.indexOf(JCR_CONTENT))
                : session.getNode(eventPath.substring(startSubstringIndex, eventPath.indexOf(SUBSTRING_SEPARATOR))).getPath();
    }

    private void makeVersionableIfNeeded(String nodePath) throws RepositoryException {
        Node node = session.getNode(nodePath);
        if (node.hasProperty(MIXIN_TYPE) && Arrays.stream(node.getProperty(MIXIN_TYPE).getValues()).anyMatch(this::isVersionable)) {
            return;
        }
        node.addMixin(MIXIN_VERSIONABLE);
        session.save();
        session.refresh(true);
    }

    private boolean isVersionable(Value value) {
        try {
            return value.getString().equals(MIXIN_VERSIONABLE);
        } catch (RepositoryException e) {
            LOG.debug("Under PageChangeObservation was got exception: {}", e.getMessage());
        }
        return false;
    }
}
