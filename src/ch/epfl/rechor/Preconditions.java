package ch.epfl.rechor;

/**
 * Class used in various files of the project to throw exceptions.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public final class Preconditions {

    /**
     * Private constructor, as the class is final and also not instantiable.
     */
    private Preconditions(){}

    /**
     * Checks that the given argument is true.
     *
     * @param shouldBeTrue the boolean argument to check
     * @throws IllegalArgumentException if the argument is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}