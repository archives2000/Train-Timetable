package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Record representing a complete public transport journey composed of multiple consecutive legs.
 * Legs can either be transport segments or walking transfers.
 * <p>
 * Each journey ensures:
 * - It is not empty.
 * - Alternates correctly between transport and walking segments.
 * - Has legs with consistent timing (departure must not precede the previous arrival).
 * - The arrival stop of a leg matches the departure stop of the next leg.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 *
 * @param legs journey's steps
 */
public record Journey(List<Leg> legs) {

    /**
     * Constructs a Journey and validates its structure.
     *
     * @param legs the list of legs that constitute the journey.
     * @throws IllegalArgumentException if:
     *      - The given list of legs is empty.
     *      - There is no alternation between transport and walking segments.
     *      - A departure time precedes the arrival time of the previous leg.
     *      - The arrival stop of a leg does not correspond to the departure stop of the next leg.
     */
    public Journey {
        legs = List.copyOf(legs);
        Preconditions.checkArgument(!legs.isEmpty());

        for (int i = 1; i < legs.size(); i++) {
            Leg previousLeg = legs.get(i - 1);
            Leg currentLeg = legs.get(i);

            Preconditions.checkArgument(
                    (previousLeg instanceof Leg.Foot &&
                            currentLeg instanceof Leg.Transport)
                            || (previousLeg instanceof Leg.Transport &&
                            currentLeg instanceof Leg.Foot));

            Preconditions.checkArgument(!previousLeg.arrTime().isAfter(currentLeg.depTime()));

            Preconditions.checkArgument(previousLeg.arrStop().equals(currentLeg.depStop()));
        }
    }

    /**
     * Sealed interface representing a leg (step) of a journey.
     * Implemented by Transport and Foot, as intermediate stops do not
     * represent movement on their own.
     */
    public sealed interface Leg {

        Stop depStop();
        LocalDateTime depTime();
        Stop arrStop();
        LocalDateTime arrTime();
        List<IntermediateStop> intermediateStops();

        /**
         * Computes the duration of the leg.
         *
         * @return the duration of the leg.
         */
        default Duration duration() {
            return Duration.between(depTime(), arrTime());
        }

        /**
         * Represents an intermediate stop within a transport leg.
         *
         * @param stop     the stop location.
         * @param arrTime  the arrival time at the stop.
         * @param depTime  the departure time from the stop.
         */
        record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime) {

            /**
             * Constructs an intermediate stop, ensuring validity.
             *
             * @param stop    the stop location.
             * @param arrTime the arrival time at the stop.
             * @param depTime the departure time from the stop.
             * @throws NullPointerException     if stop is null.
             * @throws IllegalArgumentException if depTime is before arrTime
             */
            public IntermediateStop {
                Objects.requireNonNull(stop);
                Preconditions.checkArgument(!depTime.isBefore(arrTime));
            }
        }

        /**
         * Represents a transport leg of a journey.
         *
         * @param depStop          the departure stop.
         * @param depTime          the departure time.
         * @param arrStop          the arrival stop.
         * @param arrTime          the arrival time.
         * @param intermediateStops a list of intermediate stops between departure and arrival.
         * @param vehicle          the type of vehicle used.
         * @param route            the route name.
         * @param destination      the final destination of the transport leg.
         */
        record Transport(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime,
                         List<IntermediateStop> intermediateStops, Vehicle vehicle,
                         String route, String destination)
                implements Leg {

            /**
             * Constructs a transport leg, ensuring validity.
             *
             * @param depStop          the departure stop.
             * @param depTime          the departure time.
             * @param arrStop          the arrival stop.
             * @param arrTime          the arrival time.
             * @param intermediateStops the intermediate stops list.
             * @param vehicle          the vehicle type.
             * @param route            the route name.
             * @param destination      the destination name.
             * @throws NullPointerException     if any parameter is null.
             * @throws IllegalArgumentException if the departure time is after the arrival time.
             */
            public Transport {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(vehicle);
                Objects.requireNonNull(route);
                Objects.requireNonNull(destination);
                Preconditions.checkArgument(!depTime.isAfter(arrTime));
                intermediateStops = List.copyOf(intermediateStops);
            }
        }

        /**
         * Represents a walking leg of a journey.
         *
         * @param depStop the departure stop.
         * @param depTime the departure time.
         * @param arrStop the arrival stop.
         * @param arrTime the arrival time.
         */
        record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime)
                implements Leg {

            /**
             * Constructs a walking leg, ensuring validity.
             *
             * @param depStop the departure stop.
             * @param depTime the departure time.
             * @param arrStop the arrival stop.
             * @param arrTime the arrival time.
             * @throws NullPointerException     if any parameter is null.
             * @throws IllegalArgumentException if the arrival time is before the departure time.
             */
            public Foot {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrTime);
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            @Override
            public List<IntermediateStop> intermediateStops() {
                return List.of();
            }

            /**
             * Determines if this walking leg represents a transfer
             * (i.e., between platforms at the same station).
             *
             * @return true if the walking leg is a transfer, false otherwise.
             */
            public boolean isTransfer() {
                return depStop.name().equals(arrStop.name());
            }
        }
    }

    /**
     * Retrieves the first stop of the journey.
     *
     * @return the first stop of the journey.
     */
    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    /**
     * Retrieves the last stop of the journey.
     *
     * @return the last stop of the journey.
     */
    public Stop arrStop() {
        return legs.getLast().arrStop();
    }

    /**
     * Retrieves the departure time of the journey.
     *
     * @return the departure time of the journey.
     */
    public LocalDateTime depTime() {
        return legs.getFirst().depTime();
    }

    /**
     * Retrieves the arrival time of the journey.
     *
     * @return the arrival time of the journey.
     */
    public LocalDateTime arrTime() {
        return legs.getLast().arrTime();
    }

    /**
     * Computes the total duration of the journey.
     *
     * @return the total journey duration.
     */
    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }
}