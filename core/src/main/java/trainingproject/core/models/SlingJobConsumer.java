package trainingproject.core.models;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.Map;

@Component(service = JobConsumer.class,
        immediate = true,
        property = JobConsumer.PROPERTY_TOPICS + "=log/write/deleted/nodes")
public class SlingJobConsumer implements JobConsumer {

    private final static String ROOT_PATH = "/var/log/removedProperties";
    private final static String NT_RESOURCE = "nt:unstructured";
    private final static String ADMIN = "admin";
    private final static String REMOVED_PROPERTIES = "REMOVED_PROPERTIES";
    private final static String NODE_NAME_PREFIX = "deletedNode-";
    private final static String PROPERTY_NAME = "propertyName";
    private final static String PROPERTY_VALUE = "propertyValue";
    private final static String SUBSTRING_SEPARATOR = "/";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Session session;

    @Reference
    private SlingRepository repository;

    @Override
    public JobResult process(Job job) {
        Map<String, String> names = (Map) job.getProperty(REMOVED_PROPERTIES);
        try {
            session = repository.login(new SimpleCredentials(ADMIN, ADMIN.toCharArray()));
            Node rootNode = session.getNode(ROOT_PATH);

            for (Map.Entry<String, String> entry : names.entrySet()) {
                Node logNode = rootNode.addNode(NODE_NAME_PREFIX + entry.hashCode());
                logNode.setPrimaryType(NT_RESOURCE);
                logNode.setProperty(PROPERTY_NAME, StringUtils.substringAfterLast(entry.getKey(), SUBSTRING_SEPARATOR));
                logNode.setProperty(PROPERTY_VALUE, entry.getValue());
            }

            session.save();
        } catch (RepositoryException e) {
            logger.debug("Under SlingJobConsumer was got an exception: {}", e.getMessage());
        }
        session.logout();
        return JobResult.OK;
    }
}
