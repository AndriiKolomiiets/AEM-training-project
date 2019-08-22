package trainingproject.core.models;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SlingJobConsumer.class})
public class SlingJobConsumerTest {
    private final static String ROOT_PATH = "/var/log/removedProperties";
    private final static String NT_RESOURCE = "nt:unstructured";
    private final static String REMOVED_PROPERTIES = "REMOVED_PROPERTIES";
    private final static String NODE_NAME_PREFIX = "deletedNode-";
    private final static String pagePath = "/content/node/testproject/event/";
    private final static String beforeValue = "element";

    @InjectMocks
    private SlingJobConsumer slingJobConsumer;
    @Mock
    private SlingRepository repository;
    @Mock
    private Session session;
    @Mock
    private Job job;
    @Mock
    private Node rootNode;
    @Mock
    private Node logNode;

    @Test
    public void Process_SessionSaveAndLogout_Success() throws RepositoryException {

        Map<String, String> names = new HashMap<>();
        names.put(beforeValue, pagePath);
        when(repository.login(any(SimpleCredentials.class))).thenReturn(session);
        when(job.getProperty(REMOVED_PROPERTIES)).thenReturn(names);
        when(session.getNode(ROOT_PATH)).thenReturn(rootNode);
        for (Map.Entry<String, String> entry : names.entrySet()) {
            when(rootNode.addNode(NODE_NAME_PREFIX + entry.hashCode())).thenReturn(logNode);
            doNothing().when(logNode).setPrimaryType(NT_RESOURCE);
        }
        doNothing().when(session).save();
        doNothing().when(session).logout();

        slingJobConsumer.process(job);

        verify(session).save();
        verify(session).logout();
    }
}