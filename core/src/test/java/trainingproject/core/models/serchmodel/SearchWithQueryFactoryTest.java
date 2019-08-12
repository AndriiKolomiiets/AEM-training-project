package trainingproject.core.models.serchmodel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SearchWithQueryFactoryTest {
    private SearchWithQueryFactory queryFactory;
    private String QUERY_BUILDER = "QueryBuilder";
    private String QUERY_MANAGER = "QueryManager";
    private String TEST_TEXT = "TestText";


    @Before
    public void init(){
        queryFactory = new SearchWithQueryFactory();
    }

    @Test
    public void GetExecutor_TypeEqualsQueryBuilder_NewSearchWithQueryBuilderCreated() {
        SearchWithQuery executor = queryFactory.getExecutor(QUERY_BUILDER);
        assertSame(executor.getClass().getName(), SearchWithQueryBuilder.class.getName());
    }

    @Test
    public void GetExecutor_TypeEqualsQueryManager_NewSearchWithQueryManagerCreated() {
        SearchWithQuery executor = queryFactory.getExecutor(QUERY_MANAGER);
        assertSame(executor.getClass().getName(), SearchWithQueryManager.class.getName());
    }

    @Test
    public void GetExecutor_TypeNotEqualToQueryManagerOrQueryBuilder_ReturnNull() {
        SearchWithQuery executor = queryFactory.getExecutor(TEST_TEXT);
        assertNull(executor);
    }
}