package trainingproject.core.models.serchmodel;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SearchWithQueryBuilderTest {
    private String linkTo;
    private String text;
    private static final String PATH = "path";
    private static final String TYPE = "type";
    private static final String ASSET = "dam:Asset";
    private static final String FULLTEXT = "fulltext";

    @Mock
    private SearchResult result;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Session session;
    @Mock
    private QueryBuilder queryBuilder;
    @Mock
    private QueryManager queryManager;
    @Mock
    private Query query;
    @Mock
    private Iterator<Node> nodeIter;
    @Mock
    private Node newNode;
    @Mock
    private Workspace workspace;
    @Mock
    private PredicateGroup predicates;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        linkTo = "content/";
        text = "test text";
    }

    @Test
    public void GetPaths_ViaQueryBuilderWithLinkToAndTextNotNull_ReturnsList() throws RepositoryException {
        SearchWithQueryBuilder searchWithQueryManager = new SearchWithQueryBuilder();
        String fileReference = "content/dam";

        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put(PATH, linkTo);
        predicateMap.put(TYPE, ASSET);
        predicateMap.put(FULLTEXT, text);

        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        predicates = PredicateGroup.create(predicateMap);
        when(queryBuilder.createQuery(predicates, session)).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getNodes()).thenReturn(nodeIter);
        when(nodeIter.hasNext()).thenReturn(true, false);
        when(nodeIter.next()).thenReturn(newNode);
        when(newNode.getPath()).thenReturn(fileReference);
        List testPaths = new ArrayList<>();
        testPaths.add(fileReference);

        List<String> paths = searchWithQueryManager.getPaths(linkTo, text, resourceResolver);

        assertEquals(testPaths, paths);
    }
}