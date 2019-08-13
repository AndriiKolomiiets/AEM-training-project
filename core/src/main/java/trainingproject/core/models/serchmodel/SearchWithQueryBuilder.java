package trainingproject.core.models.serchmodel;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

public class SearchWithQueryBuilder implements SearchWithQuery {

    private static final String PATH = "path";
    private static final String TYPE = "type";
    private static final String ASSET = "dam:Asset";
    private static final String FULLTEXT = "fulltext";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> getPaths(String linkTo, String text, ResourceResolver resourceResolver) {
        List<String> references = new ArrayList<>();

        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put(PATH, linkTo);
        predicateMap.put(TYPE, ASSET);
        predicateMap.put(FULLTEXT, text);

        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        if (queryBuilder == null) {
            return Collections.emptyList();
        }

        Session session = resourceResolver.adaptTo(Session.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
        SearchResult result = query.getResult();
        Iterator<Node> nodeItr = result.getNodes();
        try {
            while (nodeItr.hasNext()) {
                Node node = nodeItr.next();
                String fileReference = node.getPath();
                references.add(fileReference);
            }
        } catch (RepositoryException e) {
            LOGGER.debug("Exception occurred: {}", e.getMessage());
        } finally {
            if (session != null && session.isLive()) {
                session.logout();
            }
        }
        return references;
    }
}
