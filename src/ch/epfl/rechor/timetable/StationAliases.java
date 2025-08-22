package ch.epfl.rechor.timetable;

/**
 * Interface representing station aliases.
 * This interface allows retrieving alias names for stations as well as the corresponding
 * official station names.
 * All methods throw an {@link IndexOutOfBoundsException} if the given index
 * is out of range, meaning it is less than 0 or greater than or equal to {@link #size()}.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public interface StationAliases extends Indexed {

    /**
     * Retrieves the alias name of a station at the given index.
     *
     * @param id the index of the alias to retrieve.
     * @return the alias of the station.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    String alias(int id) throws IndexOutOfBoundsException;

    /**
     * Retrieves the official station name corresponding to the alias at the given index.
     *
     * @param id the index of the alias whose associated station name is to be retrieved.
     * @return the official name of the station to which the alias corresponds.
     * @throws IndexOutOfBoundsException if the given index is out of bounds.
     */
    String stationName(int id) throws IndexOutOfBoundsException;
}
