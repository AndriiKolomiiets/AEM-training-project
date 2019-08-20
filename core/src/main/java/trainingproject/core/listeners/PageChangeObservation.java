package trainingproject.core.listeners;

import org.apache.sling.api.resource.ResourceResolverFactory;
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
import javax.jcr.version.VersionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true,
        service = EventListener.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Training repository change observation",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*"
        })
public class PageChangeObservation implements EventListener {
    private final static String MIXIN_VERSIONABLE = "mix:versionable";
    private final static String NODE_PATH = "/content/myapp";
    private final static String MIXIN_TYPE = "jcr:mixinTypes";
    private final static String JCR_CONTENT = "jcr:content";
    private final static String JOB_TOPIC = "log/write/deleted/nodes";

    private final static String SUBSTRING_SEPARATOR = "/";
    private final static String BEFORE_VALUE = "beforeValue";
    private final static String REMOVED_PROPERTIES = "REMOVED_PROPERTIES";
    private final static String ADMIN = "admin";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Session session;
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private SlingRepository repository;
    @Reference
    private JobManager jobManager;
    private final Map<String, Object> props = new HashMap<>();

    @Activate
    public void activate(ComponentContext context) {
//        ResourceResolver resolver = null;
        try {
            session = repository.login(new SimpleCredentials(ADMIN, ADMIN.toCharArray()));
            if (session != null) {
                session.getWorkspace().getObservationManager().addEventListener(
                        this,
                        Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.NODE_ADDED | Event.NODE_REMOVED | Event.PROPERTY_REMOVED,
                        NODE_PATH,
                        true,
                        null,
                        null,
                        false
                );
                logger.info("Listener added.");
            }
        } catch (RepositoryException e) {
            logger.debug("Under PageChangeObservation was got exception: {}", e.getMessage());
        }
    }

    @Deactivate
    public void deactivate() {
        if (session != null) {
            session.logout();
        }
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        int START_SUBSTRING_INDEX = 0;
        int SUBSTRING_INDEX_TO_CUT = 24;
        while (eventIterator.hasNext()) {
            Event event = eventIterator.nextEvent();
            String eventPath;
            String pagePath = null;
            try {
                eventPath = event.getPath();
                if (eventPath.contains(JCR_CONTENT)) {
                    pagePath = eventPath.substring(START_SUBSTRING_INDEX, eventPath.indexOf(JCR_CONTENT));
                } else {
                    pagePath = session.getNode(eventPath.substring(START_SUBSTRING_INDEX, eventPath.indexOf(SUBSTRING_SEPARATOR, SUBSTRING_INDEX_TO_CUT))).getPath();
                }
            } catch (RepositoryException e) {
                logger.debug("Under PageChangeObservation was got exception: {}", e.getMessage());
            }
            VersionManager versionManager;
            try {
                versionManager = session.getWorkspace().getVersionManager();
                Node node = session.getNode(pagePath);
                boolean hasVersinableMixinType = false;
                if (node.hasProperty(MIXIN_TYPE)) {
                    hasVersinableMixinType = Arrays.stream(node.getProperty(MIXIN_TYPE).getValues())
                            .anyMatch(this::isVersionable);
                }

                if (!hasVersinableMixinType) {
                    node.addMixin(MIXIN_VERSIONABLE);
                    session.save();
                    session.refresh(true);
                }

                if (event.getType() == Event.NODE_REMOVED || event.getType() == Event.PROPERTY_REMOVED) {
                    String beforeValue = ((Value) event.getInfo().get(BEFORE_VALUE)).getString();
                    props.put(event.getPath(), beforeValue);
                } else {
                    versionManager.checkin(pagePath);
                }

            } catch (RepositoryException e) {
                logger.debug("Under PageChangeObservation was got exception: {}", e.getMessage());
            }
        }
        jobManager.addJob(JOB_TOPIC, Collections.singletonMap(REMOVED_PROPERTIES, props));
        props.clear();
    }

    private boolean isVersionable(Value value) {
        try {
            return value.getString().equals(MIXIN_VERSIONABLE);
        } catch (RepositoryException e) {
            logger.debug("Under PageChangeObservation was got exception: {}", e.getMessage());
        }
        return false;
    }
}