package ch.epfl.rechor;


/**
 * Utility class helpful to manipulate positive or null int values (extractions and packing of values).
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class Bits32_24_8 {

    /**
     * Mask constant.
     */
    final static int MASK_8_BIT_LSB = 0xFF;

    /**
     * Private constructor, as the class is not instantiable.
     */
    private Bits32_24_8 () {}

    /**
     * Method used to pack two integers values into one.
     *
     * @param bits24 the bits embedded in the range 8-31 in the new integer value
     * @param bits8 value embedded in the 8 first bits
     * @return an int value consisting of bits24 for the most significant bits
     * and bits8 for the 8 least significant bits
     */
    public static int pack(int bits24, int bits8) {
        Preconditions.checkArgument(((bits24 >>> 24) == 0) && ((bits8 >>> 8) == 0));
        return (bits24 << 8) | bits8;
    }

    /**
     * Method used to reveal the value of the 24 most significant bits from an integer bits32
     *
     * @param bits32 the integer value from which we extract bits from the position 8-32;
     * @return an integer value consisting of bits from the position 8-32
     */
    public static int unpack24(int bits32) {
        return bits32 >>> 8;
    }

    /**
     * Method used to find the value of the 8 least significant bits from a given integer (bits32)
     *
     * @param bits32 the integer value from which we extract bits from the position 0-7;
     * @return the 8 least significant bits
     */
    public static int unpack8(int bits32) {
        return bits32 & MASK_8_BIT_LSB;
    }
}



