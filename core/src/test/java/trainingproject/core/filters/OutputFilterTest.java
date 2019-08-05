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
    }

    @Test
    public void Activate_GrayScaleAndUpTurnDown_Assigned() {
        when(outputFilterConfig.grayScale()).thenReturn(true);
        when(outputFilterConfig.turnUpDown()).thenReturn(false);
        outputFilter.activate(outputFilterConfig);
        verify(outputFilterConfig, times(1)).grayScale();
        verify(outputFilterConfig, times(1)).turnUpDown();
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
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("fileReference", String.class)).thenReturn("fileReference");
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource("fileReference")).thenReturn(imageResource);
        when(imageResource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getRendition("original")).thenReturn(original);

        PowerMockito.mockStatic(ImageHelper.class);
        PowerMockito.when(ImageHelper.createLayer(original))
                .thenReturn(layer);

        outputFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse, filterChain);
        verify(layer, times(1)).rotate(180);
        verify(layer, times(1)).grayscale();
    }

    @Test
    public void DoFilter_CallTurnUpDownWhenTurnDownIsTrue_Success() throws Exception {
        when(outputFilterConfig.grayScale()).thenReturn(false);
        when(outputFilterConfig.turnUpDown()).thenReturn(true);

        outputFilter.activate(outputFilterConfig);
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("fileReference", String.class)).thenReturn("fileReference");
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource("fileReference")).thenReturn(imageResource);
        when(imageResource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getRendition("original")).thenReturn(original);

        PowerMockito.mockStatic(ImageHelper.class);
        PowerMockito.when(ImageHelper.createLayer(original))
                .thenReturn(layer);

        outputFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse, filterChain);
        verify(layer, times(1)).rotate(180);
        verify(layer, never()).grayscale();
    }

    @Test
    public void DoFilter_CallGrayScaleWhenGrayScaleIsTrue_Success() throws Exception {
        when(outputFilterConfig.grayScale()).thenReturn(true);
        when(outputFilterConfig.turnUpDown()).thenReturn(false);

        outputFilter.activate(outputFilterConfig);
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("fileReference", String.class)).thenReturn("fileReference");
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource("fileReference")).thenReturn(imageResource);
        when(imageResource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getRendition("original")).thenReturn(original);

        PowerMockito.mockStatic(ImageHelper.class);
        PowerMockito.when(ImageHelper.createLayer(original))
                .thenReturn(layer);
        outputFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse, filterChain);
        verify(layer, times(1)).grayscale();
        verify(layer, never()).rotate(180);
    }

}