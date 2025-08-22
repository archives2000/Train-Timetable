package ch.epfl.rechor.timetable;

import java.time.LocalDate;

import java.time.LocalDate;

/**
 * Interface representing a time table for public transportation.
 * This interface provides access to various components of a timetable, including stations,
 * platforms, routes, transfers, trips, and connections.
 *
 * Each method retrieves structured data related to the timetable, and default methods help
 * determine whether a given stop ID corresponds to a station or a platform.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public interface TimeTable {

    /**
     * Retrieves the indexed stations of the timetable.
     *
     * @return an instance of Stations representing all indexed stations.
     */
    Stations stations();

    /**
     * Retrieves the station aliases of the timetable.
     *
     * @return an instance of StationAliases representing alternative station names.
     */
    StationAliases stationAliases();

    /**
     * Retrieves the indexed platforms of the timetable.
     *
     * @return an instance of Platforms representing all indexed platforms.
     */
    Platforms platforms();

    /**
     * Retrieves the routes available in the timetable.
     *
     * @return an instance of Routes representing all routes in the timetable.
     */
    Routes routes();

    /**
     * Retrieves the indexed transfers available in the timetable.
     *
     * @return an instance of Transfers representing all indexed transfers.
     */
    Transfers transfers();

    /**
     * Retrieves the trips available for a given date.
     *
     * @param date the date for which trips should be retrieved.
     * @return an instance of Trips representing all trips available on the given date.
     */
    Trips tripsFor(LocalDate date);

    /**
     * Retrieves the connections available for a given date.
     *
     * @param date the date for which connections should be retrieved.
     * @return an instance of Connections representing all connections available on the given date.
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Checks whether the given stop ID corresponds to a station.
     *
     * @param stopId the ID of the stop to check.
     * @return true if the given ID corresponds to a station, false otherwise.
     */
    default boolean isStationId(int stopId) {
        return stopId < stations().size();
    }

    /**
     * Checks whether the given stop ID corresponds to a platform.
     *
     * @param stopId the ID of the stop to check.
     * @return true if the given ID corresponds to a platform, false otherwise.
     */
    default boolean isPlatformId(int stopId) {
        return stopId >= stations().size();
    }

    /**
     * Retrieves the station ID corresponding to the given stop ID.
     * If the stop ID corresponds to a platform, the method returns the station ID
     * associated with that platform.
     *
     * @param stopId the ID of the stop.
     * @return the station ID corresponding to the given stop ID.
     */
    default int stationId(int stopId) {
        if (isPlatformId(stopId)) {
            int idxPlatform = stopId - stations().size();
            return platforms().stationId(idxPlatform);
        }
        return stopId;
    }

    /**
     * Retrieves the platform name corresponding to the given stop ID.
     * If the stop ID corresponds to a platform, the method returns its name; otherwise,
     * it returns null.
     *
     * @param stopId the ID of the stop.
     * @return the platform name if the stop ID corresponds to a platform, null otherwise.
     */
    default String platformName(int stopId) {
        int idxPlatform = stopId - stations().size();
        return isPlatformId(stopId) ? platforms().name(idxPlatform) : null;
    }
}
