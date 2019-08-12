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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SearchWithQueryManagerTest {
    private String linkTo;
    private String text;

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        linkTo = "content/";
        text = "test text";
    }

    @Test
    public void GetPaths_ViaQueryManagerWithLinkToAndTextNotNull_ReturnsList() throws RepositoryException {
        SearchWithQueryManager searchWithQueryManager = new SearchWithQueryManager();
        String fileReference = "content/dam";
        List testPaths = new ArrayList<>();
        final String QUERY_TEMPLATE = "SELECT * FROM [dam:Asset] WHERE ISDESCENDANTNODE('%s') AND CONTAINS(*, '%s')";
        String sqlStatement = String.format(QUERY_TEMPLATE, linkTo, text);
        String JSR_SQL2 = "JCR-SQL2";

        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getWorkspace()).thenReturn(workspace);
        when(session.getWorkspace().getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(sqlStatement, JSR_SQL2)).thenReturn(query);
        when(query.execute()).thenReturn(result);
        when(result.getNodes()).thenReturn(nodeIter);
        when(nodeIter.nextNode()).thenReturn(newNode);
        when(nodeIter.hasNext()).thenReturn(true, false);
        when(newNode.getPath()).thenReturn(fileReference);

        testPaths.add(fileReference);
        List<String> paths = searchWithQueryManager.getPaths(linkTo, text, resourceResolver);

        assertEquals(testPaths, paths);
    }
}