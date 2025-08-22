package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Vehicle;
import javafx.scene.image.Image;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class gives access to the vehicle's icons.
 * These images need to be loaded only once in order to not waste memory or slow the program.
 * Each image is loaded at each fist use and is then stored in a cache (from where it's extracted).
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class VehicleIcons {

    private static final Map<Vehicle, Image> cachedIcons = new EnumMap<>(Vehicle.class);

    /**
     * The method returns the icon corresponding to the vehicle given in argument.
     *
     * @param vehicle the vehicle for which we want to load the corresponding icon.
     * @return the icon corresponding to the vehicle.
     */
    public static Image iconFor(Vehicle vehicle) {
        return cachedIcons.computeIfAbsent(vehicle, v-> new Image(v.name() + ".png"));
    }
}