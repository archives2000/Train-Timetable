package ch.epfl.rechor.journey;
import ch.epfl.rechor.Preconditions;
import java.util.Objects;

/**
 * Record representing a stop in the journey.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 *
 * @param name the name of the location of the stop
 * @param platformName the name of the platform, which can be a letter or a number (e.g. "3A", "1")
 * @param longitude the longitude of the stop, must be between -180 and 180
 * @param latitude the latitude of the stop, must be between -90 and 90
 */

public record Stop(String name, String platformName, double longitude, double latitude) {

    /**
     * Constructor used to initialize the fields and checking whether the inputs are correct.
     *
     * @param name name of the stop
     * @param platformName name of the platform
     * @param longitude longitude of the stop
     * @param latitude latitude of the stop
     * @throws NullPointerException thrown if no name is given
     * @throws IllegalArgumentException in the case the location,
     * consisting of the longitude, latitude is incorrect
     */
    public Stop {
        Objects.requireNonNull(name);
        Preconditions.checkArgument(Math.abs(longitude) <= 180 && Math.abs(latitude) <= 90);
    }
}

