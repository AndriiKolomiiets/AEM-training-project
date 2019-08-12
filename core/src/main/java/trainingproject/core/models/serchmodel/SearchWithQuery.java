package trainingproject.core.models.serchmodel;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface SearchWithQuery {
    List<String> getPaths(String linkTo, String text, ResourceResolver resourceResolver);
}
