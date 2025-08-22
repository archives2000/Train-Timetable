package ch.epfl.rechor.timetable;

/**
 * Interface used to represent indexed data from the swiss timetable, designed to
 * be extended by all interfaces that handle such data.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public interface Indexed {

    /**
     * Method returning the size of the indexed data.
     *
     * @return the size of the list
     */
    int size ();
}
