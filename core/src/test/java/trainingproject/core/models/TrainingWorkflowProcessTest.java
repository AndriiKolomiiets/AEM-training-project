package trainingproject.core.models;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.day.cq.workflow.model.WorkflowNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TrainingWorkflowProcess.class})
public class TrainingWorkflowProcessTest {
    private final static String PATH_FROM_DIALOGUE = "content/we-retail/content";
    private final static String GET_PATH_FROM_DIALOGUE_KEY = "pagePath";
    private final static String PAYLOAD = "content/training";

    @InjectMocks
    private TrainingWorkflowProcess trainingWorkflowProcess;
    @Mock
    WorkItem workItem;
    @Mock
    WorkflowSession workflowSession;
    @Mock
    Session session;
    @Mock
    WorkflowNode workflowNode;
    @Mock
    WorkflowData workflowData;
    @Mock
    MetaDataMap map;

    @Test
    public void Execute_WhenPathFromDialogIsOk_Success() throws RepositoryException, WorkflowException {
        when(workItem.getNode()).thenReturn(workflowNode);
        when(map.get(GET_PATH_FROM_DIALOGUE_KEY)).thenReturn(PATH_FROM_DIALOGUE);
        map.put(GET_PATH_FROM_DIALOGUE_KEY, PATH_FROM_DIALOGUE);
        when(workflowNode.getMetaDataMap()).thenReturn(map);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(PAYLOAD);
        when(workflowSession.getSession()).thenReturn(session);
        when(session.getNode(PATH_FROM_DIALOGUE)).thenReturn(null);
        doNothing().when(session).move(PAYLOAD, PATH_FROM_DIALOGUE);
        doNothing().when(session).save();

        trainingWorkflowProcess.execute(workItem, workflowSession, map);

        verify(session).move(PAYLOAD, PATH_FROM_DIALOGUE);
        verify(session).save();
    }
}