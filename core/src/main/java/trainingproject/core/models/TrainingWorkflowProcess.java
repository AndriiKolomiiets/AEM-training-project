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
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        final String dialogPathProperty = "pagePath";
        final String repositoryPathValidation = "content/we-retail";

        String pathFromDialogue = workItem.getNode().getMetaDataMap().get(dialogPathProperty).toString();
        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        Session session = workflowSession.getSession();
        try {
            if (pathFromDialogue.contains(repositoryPathValidation) && session.getNode(pathFromDialogue) == null) {
                session.move(payloadPath, pathFromDialogue);
                session.save();
            }
        } catch (RepositoryException e) {
            LOGGER.info("Class {} has got an exception: {}", getClass(), e.getMessage());
        }
    }
}
