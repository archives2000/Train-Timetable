package ch.epfl.rechor.timetable;

/**
 * Represents a collection of train connections. This interface extends Indexed,
 * as connections are structured in a sequential manner in the Swiss timetable.
 *
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of bounds, meaning it is less than 0 or greater than or equal to size().
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public interface Connections extends Indexed {

    /**
     * Returns the departure stop ID of the connection at the specified index.
     *
     * @param id the index of the connection
     * @return the ID of the departure stop
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int depStopId(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the departure time of the connection at the specified index,
     * expressed in minutes from midnight.
     *
     * @param id the index of the connection
     * @return the departure time in minutes from midnight
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int depMins(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the arrival stop ID of the connection at the specified index.
     *
     * @param id the index of the connection
     * @return the ID of the arrival stop
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int arrStopId(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the arrival time of the connection at the specified index,
     * expressed in minutes from midnight.
     *
     * @param id the index of the connection
     * @return the arrival time in minutes from midnight
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int arrMins(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the trip ID associated with the connection at the specified index.
     *
     * @param id the index of the connection
     * @return the trip ID
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int tripId(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the position of the connection within its trip.
     *
     * @param id the index of the connection
     * @return the position of the connection in the trip
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int tripPos(int id) throws IndexOutOfBoundsException;

    /**
     * Returns the index of the next connection in the journey.
     * If the given index corresponds to the last connection in the journey,
     * it returns the index of the first connection.
     *
     * @param id the index of the connection
     * @return the index of the next connection in the journey, or the first connection
     *          if the given index is the last one
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    int nextConnectionId(int id) throws IndexOutOfBoundsException;
}
