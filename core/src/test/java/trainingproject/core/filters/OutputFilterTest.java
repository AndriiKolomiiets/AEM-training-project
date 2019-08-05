package trainingproject.core.filters;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.Rendition;
import com.day.cq.commons.ImageHelper;
import com.day.image.Layer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.FilterChain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ImageHelper.class, OutputFilter.class})
public class OutputFilterTest {
    private OutputFilter outputFilter;
    private static final int WRITE_QUALITY = 1;
    private static final int ROTATE_UPSIDE_DOWN_DEGREES = 180;
    private static final String VALUE_MAP_FILE_REFERENCE = "fileReference";
    private static final String IMAGE_RENDITION_ORIGINAL = "original";
    //<editor-fold desc="Mocks">
    @Mock
    private Layer layer;
    @Mock
    private OutputFilterConfig outputFilterConfig;
    @Mock
    private Asset asset;
    @Mock
    FilterChain filterChain;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;
    @Mock
    Resource resource;
    @Mock
    Resource imageResource;
    @Mock
    ValueMap valueMap;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    Rendition original;
    //</editor-fold>

    @Before
    public void init() {
        outputFilter = new OutputFilter();
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(VALUE_MAP_FILE_REFERENCE, String.class)).thenReturn(VALUE_MAP_FILE_REFERENCE);
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(VALUE_MAP_FILE_REFERENCE)).thenReturn(imageResource);
        when(imageResource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getRendition(IMAGE_RENDITION_ORIGINAL)).thenReturn(original);

    }

    @Test
    public void Activate_GrayScaleAndUpTurnDown_Assigned() {
        when(outputFilterConfig.grayScale()).thenReturn(true);
        when(outputFilterConfig.turnUpDown()).thenReturn(false);
        outputFilter.activate(outputFilterConfig);
        verify(outputFilterConfig).grayScale();
        verify(outputFilterConfig).turnUpDown();
        boolean grayScaleTest = outputFilter.isGrayScale();
        boolean turnUpDownTest = outputFilter.isTurnUpDown();
        assertTrue(grayScaleTest);
        assertFalse(turnUpDownTest);
    }

    @Test
    public void DoFilter_CallTurnUpDownAndGrayScaleWhenBothAreTrue_Success() throws Exception {
        when(outputFilterConfig.grayScale()).thenReturn(true);
        when(outputFilterConfig.turnUpDown()).thenReturn(true);
        outputFilter.activate(outputFilterConfig);
        PowerMockito.mockStatic(ImageHelper.class);
        PowerMockito.when(ImageHelper.createLayer(original))
                .thenReturn(layer);
        outputFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse, filterChain);
        verify(layer).rotate(ROTATE_UPSIDE_DOWN_DEGREES);
        verify(layer).grayscale();
        verify(layer).getMimeType();
        verify(layer).write(null, WRITE_QUALITY, slingHttpServletResponse.getOutputStream());
        verifyNoMoreInteractions(layer);
    }

    @Test
    public void DoFilter_CallTurnUpDownWhenTurnDownIsTrue_Success() throws Exception {
        when(outputFilterConfig.grayScale()).thenReturn(false);
        when(outputFilterConfig.turnUpDown()).thenReturn(true);
        outputFilter.activate(outputFilterConfig);
        PowerMockito.mockStatic(ImageHelper.class);
        PowerMockito.when(ImageHelper.createLayer(original))
                .thenReturn(layer);
        outputFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse, filterChain);
        verify(layer).rotate(ROTATE_UPSIDE_DOWN_DEGREES);
        verify(layer, never()).grayscale();
        verify(layer).getMimeType();
        verify(layer).write(null, WRITE_QUALITY, slingHttpServletResponse.getOutputStream());
        verifyNoMoreInteractions(layer);
    }

    @Test
    public void DoFilter_CallGrayScaleWhenGrayScaleIsTrue_Success() throws Exception {
        when(outputFilterConfig.grayScale()).thenReturn(true);
        when(outputFilterConfig.turnUpDown()).thenReturn(false);
        outputFilter.activate(outputFilterConfig);
        PowerMockito.mockStatic(ImageHelper.class);
        PowerMockito.when(ImageHelper.createLayer(original))
                .thenReturn(layer);
        outputFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse, filterChain);
        verify(layer).grayscale();
        verify(layer, never()).rotate(ROTATE_UPSIDE_DOWN_DEGREES);
        verify(layer).getMimeType();
        verify(layer).write(null, WRITE_QUALITY, slingHttpServletResponse.getOutputStream());
        verifyNoMoreInteractions(layer);
    }
}