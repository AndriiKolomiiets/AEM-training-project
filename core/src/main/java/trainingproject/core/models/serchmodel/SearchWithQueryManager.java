package trainingproject.core.models.serchmodel;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import java.util.ArrayList;
import java.util.List;

import static javax.jcr.query.Query.JCR_SQL2;

public class SearchWithQueryManager implements SearchWithQuery {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String QUERY_TEMPLATE = "SELECT * FROM [dam:Asset] WHERE ISDESCENDANTNODE('%s') AND CONTAINS(*, '%s')";

    @Override
    public List<String> getPaths(String linkTo, String text, ResourceResolver resourceResolver) {
        List<String> references = null;
        Session session = null;
        try {

            references = new ArrayList<>();
            session = resourceResolver.adaptTo(Session.class);

            if (session != null) {
            }
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(String.format(QUERY_TEMPLATE, linkTo, text), JCR_SQL2);
            QueryResult result = query.execute();
            NodeIterator nodeIter = result.getNodes();
            while (nodeIter.hasNext()) {
                Node newNode = nodeIter.nextNode();
                String fileReference = newNode.getPath();
                references.add(fileReference);
            }
        } catch (RepositoryException | NullPointerException e) {
            LOGGER.debug("Exception occurred: {}", e.getMessage());
        } finally {
            if (session != null && session.isLive()) {
                session.logout();
            }
        }
        return references;
    }
}
