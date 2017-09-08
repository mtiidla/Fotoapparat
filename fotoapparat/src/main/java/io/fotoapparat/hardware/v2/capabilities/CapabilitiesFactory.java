package io.fotoapparat.hardware.v2.capabilities;

import static io.fotoapparat.hardware.v2.parameters.converters.FlashConverter.exposureModeToFlash;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.fotoapparat.hardware.Capabilities;
import io.fotoapparat.hardware.operators.CapabilitiesOperator;
import io.fotoapparat.hardware.v2.connection.CameraConnection;
import io.fotoapparat.hardware.v2.parameters.converters.FocusConverter;
import io.fotoapparat.parameter.Flash;
import io.fotoapparat.parameter.FocusMode;
import io.fotoapparat.parameter.Size;

/**
 * Creates the {@link Capabilities} of a {@link io.fotoapparat.hardware.v2.Camera2}.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CapabilitiesFactory implements CapabilitiesOperator {

    private final CameraConnection cameraConnection;

    public CapabilitiesFactory(CameraConnection cameraConnection) {
        this.cameraConnection = cameraConnection;
    }

    @Override
    public Capabilities getCapabilities() {
        return new Capabilities(
                availableJpegSizes(),
                availablePreviewSizes(),
                availableFocusModes(),
                availableFlashModes(),
                false,
                orientation()
        );
    }

    @SuppressWarnings("ConstantConditions")
    private Set<Size> availableJpegSizes() {
        return characteristics().getJpegOutputSizes();
    }

    @SuppressWarnings("ConstantConditions")
    private Set<Size> availablePreviewSizes() {
        HashSet<Size> filteredOutputSizes = new HashSet<>();
        for (Size outputSize : characteristics().getSurfaceOutputSizes()) {
            if (outputSize.width <= PreviewSizeInfo.MAX_PREVIEW_WIDTH && outputSize.height <= PreviewSizeInfo.MAX_PREVIEW_HEIGHT) {
                filteredOutputSizes.add(outputSize);
            }
        }

        return filteredOutputSizes;
    }

    @SuppressWarnings("ConstantConditions")
    private Set<FocusMode> availableFocusModes() {
        Set<FocusMode> focusModes = new HashSet<>();
        for (int afMode : characteristics().autoFocusModes()) {
            focusModes.add(FocusConverter.afModeToFocus(afMode));
        }

        return focusModes;
    }

    @SuppressWarnings("ConstantConditions")
    private Set<Flash> availableFlashModes() {
        if (characteristics().isFlashAvailable()) {
            return availableFlashUnitModes();
        }
        return Collections.singleton(Flash.OFF);

    }

    @SuppressWarnings("ConstantConditions")
    private Set<Flash> availableFlashUnitModes() {
        Set<Flash> flashes = new HashSet<>();
        flashes.add(Flash.OFF);
        flashes.add(Flash.TORCH);

        int[] autoExposureModes = characteristics().autoExposureModes();

        for (int autoExposureMode : autoExposureModes) {
            Flash flash = exposureModeToFlash(autoExposureMode);
            if (flash != null) {
                flashes.add(flash);
            }
        }

        return flashes;
    }

    private int orientation() {
        return characteristics().getSensorOrientation();
    }

    private Characteristics characteristics() {
        return cameraConnection.getCharacteristics();
    }

}
