package ch.epfl.rechor.timetable;

/**
 * Interface representing indexed platform from the swiss timetable.
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size().
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public interface Platforms extends Indexed {

    /**
     * Returns the name of the platform with the given ID.
     *
     * @param id the index of the platform
     * @return the name of the platform with the given id
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    String name(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the ID of the station to which the platform belongs.
     *
     * @param id the index of the platform
     * @return the index of the station
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int stationId(int id) throws IndexOutOfBoundsException;
}
