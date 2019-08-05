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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

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
public class OutputFilter implements Filter {
    private static final int ROTATE_UPSIDE_DOWN_DEGREES = 180;
    private static final int WRITE_QUALITY = 1;
    private static final String VALUE_MAP_FILE_REFERENCE = "fileReference";
    private static final String IMAGE_RENDITION_ORIGINAL = "original";
    private boolean grayScale;
    private boolean turnUpDown;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;

        Resource imageResource = getImageResource(request);
        Asset asset = getAsset(imageResource);
        Layer layer = getLayer(asset);

        if (layer != null) {
            transformImages(layer);
            writeLayerToResponse(response, layer);
        }

        filterChain.doFilter(request, response);
        LOGGER.info("Filter has finished successfully.");
    }

    @Override
    public void destroy() {
    }

    @Activate
    @Modified
    void activate(OutputFilterConfig config) {
        this.grayScale = config.grayScale();
        this.turnUpDown = config.turnUpDown();
        LOGGER.info("OutputFilterConfig activation done: grayScale {}, turnUpDown: {}", grayScale, turnUpDown);
    }

    boolean isGrayScale() {
        return grayScale;
    }

    boolean isTurnUpDown() {
        return turnUpDown;
    }

    private void writeLayerToResponse(SlingHttpServletResponse response, Layer layer) throws IOException {
        response.setContentType(layer.getMimeType());
        layer.write(null, WRITE_QUALITY, response.getOutputStream());
        LOGGER.info("Layer was written to response, layer: {}", layer);
    }

    private void transformImages(Layer layer) {
        if (turnUpDown) {
            layer.rotate(ROTATE_UPSIDE_DOWN_DEGREES);
            LOGGER.info("Images rotated upside down, layer: {}", layer);
        }
        if (grayScale) {
            layer.grayscale();
            LOGGER.info("Images color turned into grey, layer: {}", layer);
        }
    }

    private Asset getAsset(Resource imageResource) {
        return imageResource != null ? imageResource.adaptTo(Asset.class) : null;
    }

    private Layer getLayer(Asset imageAsset) {
        return imageAsset != null ? ImageHelper.createLayer(imageAsset.getRendition(IMAGE_RENDITION_ORIGINAL)) : null;
    }

    private Resource getImageResource(SlingHttpServletRequest request) {
        Resource resource = request.getResource();
        String fileReference = resource.getValueMap().get(VALUE_MAP_FILE_REFERENCE, String.class);

        return fileReference != null ? request.getResourceResolver().getResource(fileReference) : resource;
    }
}
