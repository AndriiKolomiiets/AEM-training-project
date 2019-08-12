package trainingproject.core.models;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trainingproject.core.models.serchmodel.SearchWithQuery;
import trainingproject.core.models.serchmodel.SearchWithQueryFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SearchModel.class})
public class SearchModelTest {
    private static final String TYPE = null;
    private static final String LINK_TO = null;
    private static final String TEXT = null;
    private ResourceResolver resourceResolver = null;


    @Mock
    private SearchWithQueryFactory searchWithQueryFactory;
    @Mock
    private SearchWithQuery searchWithQueryFactoryExecutor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void GetResult_QueryBuilderAsAnArgument_ReturnsList() throws Exception {
        PowerMockito.whenNew(SearchWithQueryFactory.class).withAnyArguments().thenReturn(searchWithQueryFactory);
        when(searchWithQueryFactory.getExecutor(TYPE)).thenReturn(searchWithQueryFactoryExecutor);
        List<String> paths = new ArrayList<>();
        paths.add("test");
        when(searchWithQueryFactoryExecutor.getPaths(LINK_TO, TEXT, resourceResolver)).thenReturn(paths);
        SearchModel searchModel = new SearchModel();
        List testPaths = searchModel.getResult();
        assertEquals(paths, testPaths);
    }
}