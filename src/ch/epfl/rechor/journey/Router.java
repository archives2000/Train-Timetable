package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Transfers;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static ch.epfl.rechor.journey.ParetoFront.Builder;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * This record represents a router, which holds a timetable and whose method based on the
 * timetable generates a profile for a given date and arrival station. It uses the CSA algorithm.
 *
 * @param timeTable the given timetable to use
 */
public record Router(TimeTable timeTable) {

    /**
     * Computes the pareto front profile for reaching the arrival station id on the given date.
     *
     * @param date the date for which the optimal journeys must be computed
     * @param arrStationId arrival station id
     * @return profile containing a pareto front for each station.
     */
    public Profile profile (LocalDate date, int arrStationId) {

        final Profile.Builder profile = new Profile.Builder(timeTable, date, arrStationId);
        final Stations stations = timeTable.stations();
        final Connections connections = timeTable.connectionsFor(date);
        final Transfers transfers = timeTable.transfers();

        final int[] arrayOfTransfersDuration = new int[stations.size()];

        Arrays.fill(arrayOfTransfersDuration, -1);
        int firstChangeToArrStation = PackedRange.startInclusive(transfers.arrivingAt(arrStationId));
        int lastChangeToArrStation = PackedRange.endExclusive(transfers.arrivingAt(arrStationId));

        for (int i = firstChangeToArrStation; i < lastChangeToArrStation ; i++) {

            int correspondingStation = transfers.depStationId(i);
            arrayOfTransfersDuration[correspondingStation] =
                    transfers.minutesBetween(correspondingStation,arrStationId);
        }

        Builder front = new Builder();
        Builder arrStationBuilder;
        Builder tripsBuilder;

        for (int i = 0; i < connections.size(); i++) {

            int connectionDepStationId = timeTable.stationId(connections.depStopId(i));
            int connectionArrStationId = timeTable.stationId(connections.arrStopId(i));
            int connectionDepMins = connections.depMins(i);
            int connectionArrMins = connections.arrMins(i);
            int tripId = connections.tripId(i);

            int currentConnectionTripPos = connections.tripPos(i);

            tripsBuilder = profile.forTrip(tripId) != null ?
                    profile.forTrip(tripId) : new Builder();

            arrStationBuilder = profile.forStation(connectionArrStationId) != null ?
                    profile.forStation(connectionArrStationId) : new Builder();

            Builder depStationFront = profile.forStation(connectionDepStationId) != null ?
                    profile.forStation(connectionDepStationId) : new Builder();

            int associatedPayload = i;

            if (arrayOfTransfersDuration[connectionArrStationId] != -1){

                long tuple = PackedCriteria.pack(connectionArrMins +
                                arrayOfTransfersDuration[connectionArrStationId],
                        0, associatedPayload);
                front.add(tuple);
            }

            front.addAll(tripsBuilder);

            arrStationBuilder.forEach((long criteria) -> {

                if((depMins(criteria) >= connectionArrMins)) {
                    long packedTuple = pack(arrMins(criteria), changes(criteria) + 1,
                            associatedPayload);
                    front.add(packedTuple);
                }
            });

            if (front.isEmpty()) continue;

            tripsBuilder.addAll(front);
            profile.setForTrip(tripId,tripsBuilder);

            if (!depStationFront.fullyDominates(front,connectionDepMins)) {

                int firstChangeToDepStation = PackedRange.startInclusive
                        (transfers.arrivingAt(connectionDepStationId));
                int lastChangeToDepStation = PackedRange.endExclusive(
                        transfers.arrivingAt(connectionDepStationId)) - 1;

                for (int j = firstChangeToDepStation; j <= lastChangeToDepStation; j++) {

                    Builder stationToDepStationFront =
                            profile.forStation(transfers.depStationId(j)) != null ?
                            profile.forStation(transfers.depStationId(j)) : new Builder();

                    int d = connectionDepMins - transfers.minutes(j);

                    int connectionId = i;
                    front.forEach( (long criteria) -> {
                        int connexionToLeave = PackedCriteria.payload(criteria);
                        int numberOfIntermediateStops = connections.tripPos(connexionToLeave)
                                - currentConnectionTripPos;
                        int payl = Bits32_24_8.pack(connectionId,numberOfIntermediateStops);


                        long tupleWithoutDepMins = PackedCriteria.pack(arrMins(criteria),
                                changes(criteria),payl);
                        long addedTuple = PackedCriteria.withDepMins(tupleWithoutDepMins, d);

                        stationToDepStationFront.add(addedTuple);
                    });
                    profile.setForStation(transfers.depStationId(j), stationToDepStationFront);
                }
            }
            front.clear();
        }
        return profile.build();
    }
}