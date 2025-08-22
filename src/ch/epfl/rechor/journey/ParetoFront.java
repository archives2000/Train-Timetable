package ch.epfl.rechor.journey;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

/**
 * Class used to represent a front,
 * composed of tuples (long values) that represent the characteristics of a journey,
 * We either have two criteria: the arrival time and the changes (in a long), or three criteria,
 * by including the departure time.
 * Instances are created by the builder class, who can instantiate a new front.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class ParetoFront {

    /**
     * Front tuples.
     */
    private final long [] frontTuples;

    /**
     * The constant EMPTY, used to represent an empty front
     */
    public static final ParetoFront EMPTY = new ParetoFront(new long [0]);

    /**
     * Private constructor, so a front can only be built by the builder nested inside
     * the ParetoFront class.
     *
     * @param packedCriteria, the array that is given by the builder
     */
    private ParetoFront (long [] packedCriteria) {
        this.frontTuples = packedCriteria;
    }

    /**
     * Method returning the size of the front.
     *
     * @return the size of the front
     */
    public int size() {
        return frontTuples.length;
    }

    /**
     * Method used to search inside a Pareto front a tuple with the given criteria.
     *
     * @param arrMins the arrival time we want in the long tuple returned.
     * @param changes the changes that we want to have ir the front.
     * @return the long found inside the pareto Front, that meets the given criteria.
     * @throws NoSuchElementException if no element with the given criteria is found.
     */
    public long get(int arrMins, int changes) throws NoSuchElementException {
        for (int i = 0; i < size(); i++) {
            if (PackedCriteria.arrMins(frontTuples[i]) == arrMins
                    && PackedCriteria.changes(frontTuples[i]) == changes) {
                return frontTuples[i];
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Method used to accept an action on each element in the front.
     *
     * @param action the action
     */
    public void forEach(LongConsumer action) {
        for (int i = 0; i < size(); i++) {
            action.accept(frontTuples[i]);
        }
    }

    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (PackedCriteria.hasDepMins(frontTuples[i])) {
                sb.append(" tuple " + i + ": (" +
                        PackedCriteria.depMins(frontTuples[i]) + " , " +
                        PackedCriteria.arrMins(frontTuples[i]) + " , " +
                        PackedCriteria.changes(frontTuples[i]) + ")");
            } else {
                sb.append(" tuple " + i + ": (" +
                        PackedCriteria.arrMins(frontTuples[i]) + " , " +
                        PackedCriteria.changes(frontTuples[i]) + ")");
            }
        }
        return sb.toString();
    }

    /**
     * The type Builder with two private attributes:
     * a long array, used to store the current front and
     * an int attribute, used to keep track of the effective size of the array.
     */
    public static class Builder {
        private long [] frontInConstruction;
        private int effectiveSize;

        final static long MASK_32_BIT = 0xFFFFFFFFL;

        /**
         * Instantiates a new Builder, and initializes the attributes.
         */
        public Builder() {
            frontInConstruction = new long[2];
            effectiveSize = 0;
        }

        /**
         * Instantiates a new builder using another Pareto front builder.
         *
         * @param that the other builder used to instantiate a new one
         */
        public Builder (Builder that) {
            frontInConstruction = that.frontInConstruction;
            effectiveSize = that.effectiveSize;
        }

        /**
         * Method returning true if the current front in construction is empty.
         *
         * @return true if the current front is empty
         */
        public boolean isEmpty(){
            return (effectiveSize == 0);
        }

        /**
         * Clear the current front by putting the effective size as 0.
         *
         * @return the builder
         */
        public Builder clear(){
            effectiveSize = 0;
            return this;
        }

        private long withMaxPayload(long criteria) {
            return criteria | MASK_32_BIT;
        }

        private void frontResize() {
            if ((effectiveSize + 1) > frontInConstruction.length) {
                int newCapacity =  (int) (1.5 * frontInConstruction.length);
                frontInConstruction = Arrays.copyOf(frontInConstruction,newCapacity);
            }
        }

        /**
         * Method used to add a new (long) tuple to the current front in construction.
         *
         * @param packedTuple the packed tuple
         * @return the builder
         */
        public Builder add(long packedTuple) {


            int insertionIdx = 0;

            while((insertionIdx < effectiveSize) &&
                    withMaxPayload(packedTuple) > (frontInConstruction[insertionIdx]))
            {
                if (PackedCriteria.dominatesOrIsEqual(frontInConstruction[insertionIdx],
                        packedTuple))
                {
                    return this;
                }
                insertionIdx++;
            }

            int compactFrontIdx = insertionIdx;

            for (int src = insertionIdx; src < effectiveSize; src += 1) {
                if (PackedCriteria.dominatesOrIsEqual(packedTuple, frontInConstruction[src])) {
                    continue;
                }
                if (compactFrontIdx != src) {
                    frontInConstruction[compactFrontIdx] = frontInConstruction[src];
                }
                compactFrontIdx++;
            }

            effectiveSize = compactFrontIdx;
            frontResize();

            System.arraycopy(frontInConstruction, insertionIdx, frontInConstruction,
                    insertionIdx + 1,
                    effectiveSize - insertionIdx);
            frontInConstruction[insertionIdx] = packedTuple;
            effectiveSize++;

            return this;
        }

        /**
         * Add a criteria to the front with the given arrival time, changes,
         * and payload but no departure time.
         *
         * @param arrMins the given arrival time in minutes
         * @param changes the given changes
         * @param payload the given payload
         * @return the builder
         */
        public Builder add (int arrMins, int changes, int payload) {
            long packedTuple = PackedCriteria.pack(arrMins,changes,payload);

            add(packedTuple);
            return this;
        }

        /**
         * Adds to the front all the tuples in construction by the given builder.
         *
         * @param that the that
         * @return the builder
         */
        public Builder addAll(Builder that) {
            for (int i = 0; i < that.effectiveSize ; i++) {
                add(that.frontInConstruction[i]);
            }
            return this;
        }

        /**
         * Method used to determine whether each tuple from the given builder
         * are dominated or equal by at least one tuple in "this".
         *
         * @param that the builder compared to the current builder
         * @param depMins the departure time that is injected into the tuples
         *                of that before the comparisons
         * @return true, if all the tuples from that are equal
         * or dominated by at least one tuple in the current builder, false otherwise.
         */
        public boolean fullyDominates(Builder that, int depMins) {
            for (int i = 0; i < that.effectiveSize; i++) {
                boolean isTupleFromThatDominatedOrEqualInThis = false;
                long criteriaWithDepMins =
                        PackedCriteria.withDepMins(that.frontInConstruction[i],depMins);

                for (int j = 0; j < effectiveSize; j++) {
                    if (PackedCriteria.dominatesOrIsEqual(frontInConstruction[j],criteriaWithDepMins))
                    {
                        isTupleFromThatDominatedOrEqualInThis = true;
                        break;
                    }
                }
                if (!isTupleFromThatDominatedOrEqualInThis) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Method used to accept an action on each element in the front that is built.
         *
         * @param action the action
         */
        public void forEach(LongConsumer action) {
            for (int i = 0; i < effectiveSize; i++) {
                action.accept(frontInConstruction[i]);
            }
        }

        /**
         * Method used to create an object of type ParetoFront, once the front is finished.
         * The front is copied by the constructor.
         *
         * @return the final pareto front
         */
        public ParetoFront build () {
            return new ParetoFront(Arrays.copyOf(frontInConstruction,effectiveSize));
        }

        @Override
        public String toString () {
            StringBuilder sb = new StringBuilder();
            sb.append("Front: { ");
            for (int i = 0; i < effectiveSize; i++) {
                if (PackedCriteria.hasDepMins(frontInConstruction[i])) {
                    sb.append("(" +
                            PackedCriteria.depMins(frontInConstruction[i]) + "," +
                            PackedCriteria.arrMins(frontInConstruction[i]) + ","  +
                            PackedCriteria.changes(frontInConstruction[i]) + ") ");
                } else {
                    sb.append("(" +
                            PackedCriteria.arrMins(frontInConstruction[i]) + "," +
                            PackedCriteria.changes(frontInConstruction[i]) + ") ");
                }
            }
            sb.append(" }");
            return sb.toString();
        }
    }
}
