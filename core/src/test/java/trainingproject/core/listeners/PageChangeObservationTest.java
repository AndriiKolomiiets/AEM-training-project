package trainingproject.core.listeners;

import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.osgi.service.component.ComponentContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.*;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.VersionManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Collections.class, PageChangeObservation.class, Value.class})
public class PageChangeObservationTest {
    private final static String MIXIN_TYPE = "jcr:mixinTypes";
    private final static String MIXIN_VERSIONABLE = "mix:versionable";
    private final static String eventPath = "/content/node/testproject/event/jcr:content";
    private final static String pagePath = "/content/node/testproject/event/";
    private final static String beforeValue = "element";

    private Map<String, Object> props;

    @InjectMocks
    private PageChangeObservation pageChangeObservation;
    @Mock
    private SlingRepository repository;
    @Mock
    private Workspace workspace;
    @Mock
    private ObservationManager observationManager;
    @Mock
    private Session session;
    @Mock
    private EventIterator eventIterator;
    @Mock
    private Event event;
    @Mock
    private VersionManager versionManager;
    @Mock
    private Node node;
    @Mock
    private Map info;
    @Mock
    private Map<String, Map<String, Object>> singletonMap;
    @Mock
    private ComponentContext componentContext;
    @Mock
    private Value value;
    @Mock
    private Property property;

    @Before
    public void init() throws RepositoryException {
        props = new HashMap<>();
        when(repository.login(any(SimpleCredentials.class))).thenReturn(session);
    }

    @Test
    public void Activate_AddNewListener_Success() throws RepositoryException {
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getObservationManager()).thenReturn(observationManager);
        doNothing().when(observationManager).addEventListener(any(EventListener.class), anyInt(),
                anyString(), anyBoolean(), anyObject(), anyObject(), anyBoolean());

        pageChangeObservation.activate(componentContext);

        verify(observationManager).addEventListener(any(EventListener.class), anyInt(),
                anyString(), anyBoolean(), anyObject(), anyObject(), anyBoolean());
        verifyNoMoreInteractions(observationManager);
    }

    @Test
    public void Deactivate_LogoutFromSession_Success() {
        doNothing().when(session).logout();
        when(session.isLive()).thenReturn(true);

        pageChangeObservation.deactivate();

        verify(session).logout();
    }

    @Test
    public void OnEvent_WhenPropertyAddedWithMixinTypes_CreateJob() throws RepositoryException {
        when(eventIterator.hasNext()).thenReturn(true, false);
        when(eventIterator.nextEvent()).thenReturn(event);
        when(event.getPath()).thenReturn(eventPath);
        when(session.getNode(pagePath)).thenReturn(node);
        when(node.getPath()).thenReturn(pagePath);
        when(value.getString()).thenReturn(MIXIN_VERSIONABLE);
        when(node.getProperty(MIXIN_TYPE)).thenReturn(property);
        Value values[] = new Value[]{value};
        when(property.getValues()).thenReturn(values);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getVersionManager()).thenReturn(versionManager);
        when(node.hasProperty(MIXIN_TYPE)).thenReturn(true);
        doNothing().when(node).addMixin(anyString());
        doNothing().when(session).save();
        doNothing().when(session).refresh(true);
        when(event.getType()).thenReturn(1);
        when(info.get(anyString())).thenReturn(value);
        when(value.toString()).thenReturn(beforeValue);
        when(event.getPath()).thenReturn(eventPath);
        PowerMockito.mockStatic(Collections.class);
        PowerMockito.when(Collections.singletonMap(beforeValue, props))
                .thenReturn(singletonMap);

        pageChangeObservation.onEvent(eventIterator);

        verify(versionManager).checkin(pagePath);
    }

    @Test
    public void OnEvent_WhenPropertyDeletedWithoutMixinTypes_CreateJob() throws RepositoryException {
        when(eventIterator.hasNext()).thenReturn(true, false);
        when(eventIterator.nextEvent()).thenReturn(event);
        when(event.getPath()).thenReturn(eventPath);
        when(session.getNode(pagePath)).thenReturn(node);
        when(node.getPath()).thenReturn(pagePath);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getVersionManager()).thenReturn(versionManager);
        when(node.hasProperty(MIXIN_TYPE)).thenReturn(false);
        doNothing().when(node).addMixin(anyString());
        doNothing().when(session).save();
        doNothing().when(session).refresh(true);
        when(event.getType()).thenReturn(2);
        when(event.getInfo()).thenReturn(info);
        when(info.get(anyString())).thenReturn(value);
        when(value.toString()).thenReturn(beforeValue);
        when(event.getPath()).thenReturn(eventPath);
        PowerMockito.mockStatic(Collections.class);
        PowerMockito.when(Collections.singletonMap(beforeValue, props))
                .thenReturn(singletonMap);

        pageChangeObservation.onEvent(eventIterator);

        verify(session).save();
        verify(session).refresh(true);
    }
}