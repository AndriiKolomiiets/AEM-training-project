package trainingproject.core.models.serchmodel;

public class SearchWithQueryFactory {

    public SearchWithQuery getExecutor(String type) {
        String QUERY_BUILDER = "QueryBuilder";
        String QUERY_MANAGER = "QueryManager";

        if (type.equals(QUERY_BUILDER)) {
            return new SearchWithQueryBuilder();
        } else if (type.equals(QUERY_MANAGER)) {
            return new SearchWithQueryManager();
        } else return null;
    }
}
