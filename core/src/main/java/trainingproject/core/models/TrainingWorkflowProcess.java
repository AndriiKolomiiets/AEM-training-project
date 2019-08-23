package trainingproject.core.models;

import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(service = WorkflowProcess.class,
        property = {"process.label=Training Project WeRetail Process"})
public class TrainingWorkflowProcess implements WorkflowProcess {
    private static final String DIALOG_PATH_PROPERTY = "pagePath";
    private static final String REPOSITORY_PATH_VALIDATION = "content/we-retail";
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {

        String pathFromDialogue = workItem.getNode().getMetaDataMap().get(DIALOG_PATH_PROPERTY).toString();
        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        Session session = workflowSession.getSession();
        try {
            if (pathFromDialogue.contains(REPOSITORY_PATH_VALIDATION) && session.getNode(pathFromDialogue) == null) {
                session.move(payloadPath, pathFromDialogue);
                session.save();
            }
        } catch (RepositoryException e) {
            LOGGER.info("Class {} has got an exception: {}", getClass(), e.getMessage());
        } finally {
            session.logout();
        }
    }
}
