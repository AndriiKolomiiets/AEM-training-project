package trainingproject.core.models.serchmodel;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SearchWithQueryManagerTest {

    private static final String LINK_TO = "content/";
    private static final String TEXT = "testText";
    private static final String fileReference = "content/dam";
    private static final String QUERY = "SELECT * FROM [dam:Asset] WHERE ISDESCENDANTNODE('content/') AND CONTAINS(*, 'testText')";
    private static final String JSR_SQL2 = "JCR-SQL2";

    private List<String> testPaths = Collections.singletonList(fileReference);

    @Mock
    private QueryResult result;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Session session;
    @Mock
    private QueryManager queryManager;
    @Mock
    private Query query;
    @Mock
    private NodeIterator nodeIter;
    @Mock
    private Node newNode;
    @Mock
    private Workspace workspace;

    private SearchWithQueryManager searchWithQueryManager;

    @Before
    public void setUp() {
        searchWithQueryManager = new SearchWithQueryManager();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void GetPaths_ViaQueryManagerWithLinkToAndTextNotNull_ReturnsList() throws RepositoryException {
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getWorkspace()).thenReturn(workspace);
        when(session.getWorkspace().getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(QUERY, JSR_SQL2)).thenReturn(query);
        when(query.execute()).thenReturn(result);
        when(result.getNodes()).thenReturn(nodeIter);
        when(nodeIter.nextNode()).thenReturn(newNode);
        when(nodeIter.hasNext()).thenReturn(true, false);
        when(newNode.getPath()).thenReturn(fileReference);

        List<String> paths = searchWithQueryManager.getPaths(LINK_TO, TEXT, resourceResolver);

        assertEquals(testPaths, paths);
    }
}