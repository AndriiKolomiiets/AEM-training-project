package trainingproject.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainingproject.core.models.serchmodel.SearchWithQuery;
import trainingproject.core.models.serchmodel.SearchWithQueryFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Model(adaptables = Resource.class)
public class SearchModel {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject
    @Named("text")
    @Optional
    protected String text;
    @Inject
    @Named("linkTo")
    @Optional
    private String linkTo;
    @Inject
    @Named("type")
    @Optional
    protected String type;
    @Inject
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init() {
    }

    List getResult() {
        SearchWithQueryFactory searchWithQueryFactory = new SearchWithQueryFactory();
        SearchWithQuery searchWithQueryFactoryExecutor = searchWithQueryFactory.getExecutor(type);
        List paths = searchWithQueryFactoryExecutor.getPaths(linkTo, text, resourceResolver);

        LOGGER.info("Search was performed. Text to find: {}, path(s) where file was found: {}", text, paths);
        return paths;
    }
}
