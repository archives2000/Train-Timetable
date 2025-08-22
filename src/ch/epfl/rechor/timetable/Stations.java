package ch.epfl.rechor.timetable;

/**
 * Interface representing indexed stations.
 * This interface provides methods to retrieve station names and their geographical coordinates.
 * <p>
 * All methods throw an {@link IndexOutOfBoundsException} if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to {@link #size()}.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public interface Stations extends Indexed {

    /**
     * Retrieves the name of the station corresponding to the given index.
     *
     * @param id the index of the station.
     * @return the name of the station.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    String name(int id) throws IndexOutOfBoundsException;

    /**
     * Retrieves the longitude of the station corresponding to the given index.
     * The longitude is expressed in degrees.
     *
     * @param id the index of the station.
     * @return the longitude of the station in degrees.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    double longitude(int id) throws IndexOutOfBoundsException;

    /**
     * Retrieves the latitude of the station corresponding to the given index.
     * The latitude is expressed in degrees.
     *
     * @param id the index of the station.
     * @return the latitude of the station in degrees.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    double latitude(int id) throws IndexOutOfBoundsException;
}
