package trainingproject.core.models;

import junitx.util.PrivateAccessor;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trainingproject.core.models.serchmodel.SearchWithQuery;
import trainingproject.core.models.serchmodel.SearchWithQueryFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SearchModel.class})
public class SearchModelTest {
    private static final String TYPE = "type";
    private static final String LINK_TO = "linkTo";
    private static final String TEXT = "text";

    private final String TEST_PATH = "test";
    private final List<String> TEST_PATHS = Collections.singletonList(TEST_PATH);

    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private SearchWithQueryFactory searchWithQueryFactory;
    @Mock
    private SearchWithQuery searchWithQueryFactoryExecutor;
    @InjectMocks
    private SearchModel sut;

    @Before
    public void init() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        PrivateAccessor.setField(sut, "type", TYPE);
        PrivateAccessor.setField(sut, "linkTo", LINK_TO);
        PrivateAccessor.setField(sut, "text", TEXT);
    }

    @Test
    public void GetResult_QueryBuilderAsAnArgument_ReturnsList() throws Exception {
        PowerMockito.whenNew(SearchWithQueryFactory.class).withAnyArguments().thenReturn(searchWithQueryFactory);
        when(searchWithQueryFactory.getExecutor(TYPE)).thenReturn(searchWithQueryFactoryExecutor);
        when(searchWithQueryFactoryExecutor.getPaths(LINK_TO, TEXT, resourceResolver)).thenReturn(TEST_PATHS);

        List testPaths = sut.getResult();

        assertEquals(TEST_PATHS, testPaths);
    }
}