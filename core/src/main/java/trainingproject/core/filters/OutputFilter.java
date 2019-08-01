package trainingproject.core.filters;

import com.adobe.granite.asset.api.Asset;
import com.day.cq.commons.ImageHelper;
import com.day.image.Layer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.*;
import java.io.IOException;
import java.util.Objects;

@Component(service = Filter.class,
        immediate = true,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Filter for changing images. Training-project",
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                Constants.SERVICE_RANKING + ":Integer=-500",
                EngineConstants.SLING_FILTER_PATTERN + "=.*/we-retail/.*.(jpg|jpeg|png)",
        })
@Designate(ocd = OutputFilterConfig.class)
public class OutputFilter implements javax.servlet.Filter {

    private Boolean grayScale;
    private Boolean turnUpDown;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Activate
    @Modified
    public void activate(OutputFilterConfig config) {
        this.grayScale = config.grayScale();
        this.turnUpDown = config.turnUpDown();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;

        Resource resource = request.getResource();
        String fileReference = resource.getValueMap().get("fileReference", String.class);

        Resource imageResource = fileReference != null ? request.getResourceResolver().getResource(fileReference) : resource;

        Asset asset = Objects.requireNonNull(imageResource).adaptTo(Asset.class);

        Layer layer = ImageHelper.createLayer(Objects.requireNonNull(asset).getRendition("original"));

        if (turnUpDown) {
            layer.rotate(180);
        }
        if (grayScale) {
            layer.grayscale();
        }
        response.setContentType(layer.getMimeType());

        layer.write(null, 1, response.getOutputStream());

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
