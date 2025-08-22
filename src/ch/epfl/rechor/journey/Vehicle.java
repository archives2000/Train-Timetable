package ch.epfl.rechor.journey;

import java.util.List;

/**
 * Enum used to list all the vehicles that can be used for transport.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public enum Vehicle {
    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR;

    /**
     * Immutable list containing all vehicles types,
     * in their declaration order.
     */
    public static final List<Vehicle> ALL = List.of(values());
}
