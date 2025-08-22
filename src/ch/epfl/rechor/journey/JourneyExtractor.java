package ch.epfl.rechor.journey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.journey.Journey.Leg.Transport;
import ch.epfl.rechor.journey.Journey.Leg.IntermediateStop;
import ch.epfl.rechor.journey.Journey.Leg.Foot;
import ch.epfl.rechor.journey.Journey.Leg;
import static ch.epfl.rechor.Bits32_24_8.unpack24;
import static ch.epfl.rechor.Bits32_24_8.unpack8;
import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * Class used to extract journeys for a given departure station
 * out of a profile containing all the journeys for all the stations of the swiss timetable.
 *
 *  @author Matteo Lazzari (397247)
 *  @author Pamphil Nedev (380400)
 */
public final class JourneyExtractor {

    /**
     * Private constructor as the class is not instantiable,
     * and only used as a utility class.
     */
    private JourneyExtractor() {}

    private static LocalDateTime dateFromMins
            (int minutes, LocalDate date) {
        LocalDateTime midnightDate = date.atStartOfDay();
        return midnightDate.plusMinutes(minutes);
    }

    private static Stop newStop(TimeTable timeTable, int stopId) {
        int depStationId = timeTable.stationId(stopId);
        return new Stop(timeTable.stations().name(depStationId),
                timeTable.platformName(stopId),
                timeTable.stations().longitude(depStationId),
                timeTable.stations().latitude(depStationId));
    }

    private static Leg newFootLeg(TimeTable timetable, LocalDate date,
                                  int startingMinutes, int startStopId, int arrStopId) {

        int startStationId = timetable.stationId(startStopId);
        int arrStationId = timetable.stationId(arrStopId);

        int minutesByFoot = timetable.transfers().minutesBetween(startStationId, arrStationId);
        LocalDateTime depTime = dateFromMins(startingMinutes, date);
        int arrTimeMinutes = startingMinutes + minutesByFoot;
        LocalDateTime arrTime = dateFromMins(arrTimeMinutes, date);
        Stop depStop = newStop(timetable, startStopId);
        Stop arrStop = newStop(timetable, arrStopId);

        return new Foot(depStop, depTime, arrStop, arrTime);

    }

    /**
     * Method used to construct journeys for an entire day for a given departure station.
     *
     * @param profile a profile for the swiss transports for a given date
     * @param depStationId the station from which we start our journey
     * @return a list of journey corresponding to the departure station for a given date
     */
    public static List <Journey> journeys(Profile profile, int depStationId) {

        LocalDate currentDate = profile.date();
        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();
        Trips trips = profile.trips();
        Routes routes = profile.timeTable().routes();
        ParetoFront paretoFront = profile.forStation(depStationId);
        List <Journey> journeyList = new ArrayList<>(paretoFront.size());

        paretoFront.forEach((long criteria) -> {


            int journeydepMins = depMins(criteria);
            int journeyChanges = changes(criteria);
            int journeyArrMins = arrMins(criteria);
            int journeyArrStationId = profile.arrStationId();

            List<Journey.Leg> currentLegs = new ArrayList<>();

            int connectionID = unpack24(payload(criteria));
            int connectionDepStopId = connections.depStopId(connectionID);
            int connectionDepStationId = timeTable.stationId(connectionDepStopId);
            int connectionArrStopId = connections.arrStopId(connectionID);
            int connectionArrStationId = timeTable.stationId(connectionArrStopId);
            int connectionDepMins = connections.depMins(connectionID);
            int connectionArrMins = connections.arrMins(connectionID);
            int numberOfIntermediateStops = unpack8(payload(criteria));

            LocalDateTime currentLegArrTime;
            Stop currentLegArrStop;

            if (depStationId != connectionDepStationId) {
                Leg beginningLegFoot = newFootLeg(timeTable,
                        currentDate, journeydepMins, depStationId, connectionDepStopId);
                currentLegs.add(beginningLegFoot);

            }

            boolean firstTransportAdded = false;
            while (journeyChanges >= 0) {
                if (firstTransportAdded) {

                    criteria = profile.forStation(connectionArrStationId).
                            get(journeyArrMins, journeyChanges);
                    connectionID = unpack24(payload(criteria));
                    numberOfIntermediateStops = unpack8(payload(criteria));
                    connectionDepStopId = connections.depStopId(connectionID);
                    connectionDepMins = connections.depMins(connectionID);
                    currentLegs.add(newFootLeg(timeTable,currentDate,connectionArrMins,
                            connectionArrStopId, connectionDepStopId));

                }

                List < IntermediateStop > intermediateStopList = new ArrayList<>();

                for (int i = 0; i < numberOfIntermediateStops; i++) {
                    int intermediateStopDepMins = connections.arrMins(connectionID);
                    LocalDateTime arrTime = dateFromMins(intermediateStopDepMins, currentDate);
                    connectionID = connections.nextConnectionId(connectionID);
                    int intermediateStopId = connections.depStopId(connectionID);
                    Stop stop = newStop(timeTable, intermediateStopId);
                    int intermediateStopArrMins = connections.depMins(connectionID);
                    LocalDateTime depTime = dateFromMins(intermediateStopArrMins, currentDate);
                    intermediateStopList.add(new IntermediateStop(stop,arrTime,depTime));
                }

                Stop currentLegDepStop = newStop(timeTable, connectionDepStopId);
                LocalDateTime currentLegDepTime = dateFromMins(connectionDepMins, currentDate);
                connectionArrStopId  = connections.arrStopId(connectionID);
                connectionArrMins = connections.arrMins(connectionID);
                currentLegArrTime = dateFromMins(connectionArrMins, currentDate);
                currentLegArrStop = newStop(timeTable, connectionArrStopId);
                int tripId = connections.tripId(connectionID);
                int routeId = trips.routeId(tripId);
                String destination = trips.destination(tripId);
                Vehicle vehicle = routes.vehicle(routeId);
                String route = routes.name(routeId);

                Transport transportStep = new Transport(currentLegDepStop,
                        currentLegDepTime, currentLegArrStop, currentLegArrTime,
                        intermediateStopList, vehicle, route, destination);

                currentLegs.add(transportStep);
                connectionArrStationId = timeTable.stationId(connectionArrStopId);
                firstTransportAdded = true;

                journeyChanges--;
            }

            if (connectionArrStationId != journeyArrStationId)  {

                Leg endLegFoot = newFootLeg(timeTable,
                        currentDate, connectionArrMins, connectionArrStopId, journeyArrStationId);
                currentLegs.add(endLegFoot);

            }


            journeyList.add(new Journey(currentLegs));

        });
        journeyList.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));
        return journeyList;
    }
}