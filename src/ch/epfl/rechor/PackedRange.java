package ch.epfl.rechor;

/**
 * Utility class used to work with intervals, creating intervals,
 * as well as extracting the data out of an interval.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public final class PackedRange {

    /**
     * Masks constants.
     */
    final static int MASK_8_BIT = 0xFF;
    final static int MASK_24_BIT = 0xFFFFFF;

    /**
     * Private constructor, as PackedRange cannot be instantiated.
     */
    private PackedRange () {}

    /**
     * Method used to pack an interval inside an integer value.
     *
     * @param startInclusive the first integer in the interval
     * @param endExclusive the integer marking the end of the interval (excluded)
     * @throws IllegalArgumentException if:
     *      - startInclusive cannot be represented with 24 bits
     *      - representing values in the interval takes more than 8 bits
     * @return the integer containing the information about the interval
     */
    public static int pack(int startInclusive, int endExclusive) {
        return Bits32_24_8.pack(startInclusive, (endExclusive - startInclusive));
    }

    /**
     * Method used to extract the length of the interval,
     *      i.e. the cardinality of the interval.
     *
     * @param interval integer from which we extract the length of the interval
     * @return  lhe length of the interval
     */
    public static int length (int interval) {
        return interval & MASK_8_BIT;
    }

    /**
     * Method used to extract the start of the interval, which was previously packed into an integer.
     *
     * @param interval integer from which we extract the start of the interval
     * @return the start of the interval
     */
    public static int startInclusive(int interval) {
        int shiftedStart = interval >> 8;
        return shiftedStart & MASK_24_BIT;
    }

    /**
     * Method used to extract the end of the interval (excluded), who was previously packed
     * into an integer.
     *
     * @param interval integer from which we extract the end of the interval
     * @return the end of the interval
     */
    public static int endExclusive(int interval) {
        return startInclusive(interval) + length(interval);
    }
}