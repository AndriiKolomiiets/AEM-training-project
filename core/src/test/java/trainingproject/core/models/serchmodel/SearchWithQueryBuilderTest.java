package trainingproject.core.models.serchmodel;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SearchWithQueryBuilder.class, PredicateGroup.class})
public class SearchWithQueryBuilderTest {
    private static final String linkTo = "content/";
    private static final String text = "test text";
    private static final String PATH = "path";
    private static final String TYPE = "type";
    private static final String ASSET = "dam:Asset";
    private static final String FULLTEXT = "fulltext";
    private static final String FILE_REFERENCE = "content/dam";
    private SearchWithQueryBuilder searchWithQueryManager;
    @Mock
    private SearchResult result;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Session session;
    @Mock
    private QueryBuilder queryBuilder;
    @Mock
    private Query query;
    @Mock
    private Iterator<Node> nodeIter;
    @Mock
    private Node newNode;
    @Mock
    private PredicateGroup predicates;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        searchWithQueryManager = new SearchWithQueryBuilder();
    }

    @Test
    public void GetPaths_ViaQueryBuilderWithLinkToAndTextNotNull_ReturnsList() throws RepositoryException {

        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put(PATH, linkTo);
        predicateMap.put(TYPE, ASSET);
        predicateMap.put(FULLTEXT, text);

        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        PowerMockito.mockStatic(PredicateGroup.class);
        PowerMockito.when(PredicateGroup.create(predicateMap)).thenReturn(predicates);
        when(queryBuilder.createQuery(predicates, session)).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getNodes()).thenReturn(nodeIter);
        when(nodeIter.hasNext()).thenReturn(true, false);
        when(nodeIter.next()).thenReturn(newNode);
        when(newNode.getPath()).thenReturn(FILE_REFERENCE);
        List testPaths = new ArrayList<>();
        testPaths.add(FILE_REFERENCE);

        List<String> paths = searchWithQueryManager.getPaths(linkTo, text, resourceResolver);

        assertEquals(testPaths, paths);
    }
}