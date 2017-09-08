package io.fotoapparat.hardware.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.mockito.BDDMockito.given;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import static io.fotoapparat.test.TestUtils.asSet;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import io.fotoapparat.hardware.Capabilities;
import io.fotoapparat.hardware.v1.capabilities.CapabilitiesFactory;
import io.fotoapparat.parameter.Flash;
import io.fotoapparat.parameter.FocusMode;
import io.fotoapparat.parameter.Size;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("deprecation")
public class CapabilitiesFactoryTest {

    @Mock
    Camera camera;
    @Mock
    Camera.Parameters parameters;

    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

    CapabilitiesFactory testee;

    @Before
    public void setUp() throws Exception {
        given(parameters.getSupportedFocusModes())
                .willReturn(Collections.<String>emptyList());
        given(parameters.getSupportedFlashModes())
                .willReturn(Collections.<String>emptyList());
        given(parameters.getSupportedPictureSizes())
                .willReturn(Collections.<Camera.Size>emptyList());
        given(parameters.getSupportedPreviewSizes())
                .willReturn(Collections.<Camera.Size>emptyList());
        given(parameters.isZoomSupported())
                .willReturn(false);
        cameraInfo.orientation = 0;

        testee = new CapabilitiesFactory();
    }

    @Test
    public void mapFocusModes() throws Exception {
        // Given
        given(parameters.getSupportedFocusModes())
                .willReturn(asList(
                        Camera.Parameters.FOCUS_MODE_AUTO,
                        Camera.Parameters.FOCUS_MODE_FIXED,
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
                        Camera.Parameters.FOCUS_MODE_INFINITY,
                        Camera.Parameters.FOCUS_MODE_MACRO,
                        "something random"
                ));

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(
                asSet(
                        FocusMode.AUTO,
                        FocusMode.FIXED,
                        FocusMode.CONTINUOUS_FOCUS,
                        FocusMode.INFINITY,
                        FocusMode.MACRO
                ),
                capabilities.supportedFocusModes()
        );
    }

    @Test
    public void mapFocusModes_EmptyList_AlwaysIncludeFixed() throws Exception {
        // Given
        given(parameters.getSupportedFocusModes())
                .willReturn(Collections.<String>emptyList());

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(
                singleton(FocusMode.FIXED),
                capabilities.supportedFocusModes()
        );
    }

    @Test
    public void mapFlashModes() throws Exception {
        // Given
        given(parameters.getSupportedFlashModes())
                .willReturn(asList(
                        Camera.Parameters.FLASH_MODE_AUTO,
                        Camera.Parameters.FLASH_MODE_ON,
                        Camera.Parameters.FLASH_MODE_RED_EYE,
                        Camera.Parameters.FLASH_MODE_TORCH,
                        Camera.Parameters.FLASH_MODE_OFF
                ));

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(
                asSet(
                        Flash.AUTO,
                        Flash.ON,
                        Flash.AUTO_RED_EYE,
                        Flash.TORCH,
                        Flash.OFF
                ),
                capabilities.supportedFlashModes()
        );
    }

    @Test
    public void mapFlashModes_Null() throws Exception {
        // Given
        given(parameters.getSupportedFlashModes())
                .willReturn(null);    // because why the fuck not, right Google?

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(
                singleton(Flash.OFF),
                capabilities.supportedFlashModes()
        );
    }

    @Test
    public void mapPictureSizes() throws Exception {
        // Given
        given(parameters.getSupportedPictureSizes())
                .willReturn(asList(
                        makeSize(10, 10),
                        makeSize(20, 20)
                ));

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(
                asSet(
                        new Size(10, 10),
                        new Size(20, 20)
                ),
                capabilities.supportedPictureSizes()
        );
    }

    @Test
    public void mapPreviewSizes() throws Exception {
        // Given
        given(parameters.getSupportedPreviewSizes())
                .willReturn(asList(
                        makeSize(10, 10),
                        makeSize(20, 20)
                ));

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(
                asSet(
                        new Size(10, 10),
                        new Size(20, 20)
                ),
                capabilities.supportedPreviewSizes()
        );
    }

    @Test
    public void zoomSupported() throws Exception {
        // Given
        given(parameters.isZoomSupported())
                .willReturn(true);

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertTrue(capabilities.isZoomSupported());
    }

    @Test
    public void sensorOrientation() throws Exception {
        // Given
        cameraInfo.orientation = 90;

        // When
        Capabilities capabilities = testee.fromParameters(parameters, cameraInfo);

        // Then
        assertEquals(90, capabilities.getSensorOrientation());
    }

    @NonNull
    private Camera.Size makeSize(int width, int height) {
        Camera.Size size = camera.new Size(0, 0);
        size.width = width;
        size.height = height;

        return size;
    }
}