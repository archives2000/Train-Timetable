package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * Interface representing indexed transfers.
 * This interface provides methods to retrieve transfer details between stations,
 * including departure station, arrival station, and transfer duration.
 * <p>
 * All methods throw an IndexOutOfBoundsException if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to size()
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public interface Transfers extends Indexed {

    /**
     * Retrieves the departure station ID for a given transfer.
     *
     * @param id the index of the transfer.
     * @return the departure station ID.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    int depStationId(int id) throws IndexOutOfBoundsException;

    /**
     * Retrieves the duration of the transfer in minutes.
     *
     * @param id the index of the transfer.
     * @return the duration of the transfer in minutes.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    int minutes(int id) throws IndexOutOfBoundsException;

    /**
     * Retrieves the packed interval representing all transfers arriving at the given station.
     * The returned value is an encoded integer that can be decoded using PackedRange.
     *
     * @param stationId the ID of the arrival station.
     * @return a packed integer representing the interval of transfers arriving at the station.
     * @throws IndexOutOfBoundsException if the given station ID is out of bounds.
     */
    int arrivingAt(int stationId) throws IndexOutOfBoundsException;

    /**
     * Retrieves the transfer duration between two given stations.
     *
     * @param depStationId the ID of the departure station.
     * @param arrStationId the ID of the arrival station.
     * @return the transfer duration in minutes.
     * @throws NoSuchElementException if no transfer exists between the given stations.
     * @throws IndexOutOfBoundsException if either station ID is invalid.
     */
    int minutesBetween(int depStationId, int arrStationId)
            throws NoSuchElementException, IndexOutOfBoundsException;
}
