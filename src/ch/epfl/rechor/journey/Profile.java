package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Record used to represent a profile, that can be seen as a two dimension
 * array with a list of ParetoFront for each station.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 *
 * @param timeTable
 * @param date the date that corresponds to the profile
 * @param arrStationId the index of the station of the profile
 * @param stationFront a list of ParetoFront for our profile
 */
public record Profile(TimeTable timeTable, LocalDate date, int arrStationId,
                      List<ParetoFront> stationFront) {
    public Profile {
        stationFront = List.copyOf(stationFront);
    }

    /**
     * The connections of the profile for the date of the profile.
     *
     * @return the connections of the profile for the date of the profile
     */
    public Connections connections() {
        return timeTable.connectionsFor(date);
    }

    /**
     * The trips of the profile for the given date.
     *
     * @return the trips of the profile for the given date
     */
    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    /**
     * Returns the ParetoFront for the given index station.
     *
     * @param stationId the index of the station for which we get the ParetoFront
     * @return the ParetoFront of the station in parameters
     * @throws IndexOutOfBoundsException
     */
    public ParetoFront forStation(int stationId) throws IndexOutOfBoundsException {
        return stationFront.get(stationId);
    }

    /**
     * Nested class used to instantiate a new Profile.
     */
    public static final class Builder {

        /**
         * The timetable associated to our profile we want to build.
         */
        TimeTable timeTable;

        /**
         * The date of the profile.
         */
        LocalDate date;

        /**
         * The arrival station index for our profile.
         */
        int arrStationId;

        /**
         * An array containing all the ParetoFront associated to our stations.
         */
        ParetoFront.Builder [] stationsFront;

        /**
         * An array containing all the ParetoFront (without departure minutes)
         * for our trips.
         */
        ParetoFront.Builder [] tripsFront;

        /**
         * The constructor stores the given arguments and initializes two primitive arrays
         * intended to hold the Pareto front builders for stops and trips.
         *
         * @param timeTable a timetable from the swiss official schedule
         * @param date the date associated to the profile
         * @param arrStationId the arrival station id for all the stations of our profile
         */
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;
            stationsFront = new ParetoFront.Builder[timeTable.stations().size()];
            tripsFront = new ParetoFront.Builder[timeTable.tripsFor(date).size()];
        }

        /**
         * Returns the Pareto front builder associated with the given station ID.
         *
         * @param stationId the station for which we retrieve the ParetoFront
         * @return the builder of the ParetoFront associated to our station given in parameters
         */
        public ParetoFront.Builder forStation(int stationId) {
            return stationsFront[stationId];
        }

        /**
         * Method used to change our array stationsFront with a new ParetoFront.builder.
         *
         * @param stationId the station for which we retrieve the ParetoFront
         * @param builder the ParetoFront.Builder we want to add at
         *                the index stationId in the list stationsFront
         */
        public void setForStation(int stationId, ParetoFront.Builder builder) {

            stationsFront[stationId] = builder;
        }

        /**
         * Returns the Pareto front builder associated with the given trip ID.
         *
         * @param tripId the index of the trip from which we retrieve the ParetoFront.Builder
         * @return the ParetoFront.Builder associated to the index of the trip given
         */
        public ParetoFront.Builder forTrip(int tripId) {
            return tripsFront[tripId];
        }

        /**
         * Method used to change our array tripsFront with a new ParetoFront.builder.
         *
         * @param tripId the index of the trip
         * @param builder the builder we want to add to the list tripsFront at the given index (tripId)
         */
        public void setForTrip(int tripId, ParetoFront.Builder builder) {
            tripsFront [tripId] = builder;
        }

        /**
         * Returns a profile by building each element of the list of Pareto front in construction.
         *
         * @return a new profile
         */
        public Profile build() {
            List <ParetoFront> frontList = new ArrayList<>();
            for (ParetoFront.Builder builder : stationsFront) {
                if (Objects.isNull(builder)) {
                    frontList.add(ParetoFront.EMPTY);
                } else {
                    frontList.add(builder.build());
                }
            }
            return new Profile(timeTable,date,arrStationId, frontList);
        }
    }
}
