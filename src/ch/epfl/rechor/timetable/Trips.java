package ch.epfl.rechor.timetable;

/**
 * Interface representing public transport trips in the Swiss timetable.
 * This interface provides methods to retrieve information about trips,
 * including their associated routes and destinations.
 *
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size().
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public interface Trips extends Indexed {

    /**
     * Retrieves the route ID associated with the given trip index.
     *
     * @param id the index of the trip.
     * @return the route ID corresponding to the given trip index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    int routeId(int id) throws IndexOutOfBoundsException;

    /**
     * Retrieves the destination name associated with the given trip index.
     *
     * @param id the index of the trip.
     * @return the destination name corresponding to the given trip index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    String destination(int id) throws IndexOutOfBoundsException;
}
