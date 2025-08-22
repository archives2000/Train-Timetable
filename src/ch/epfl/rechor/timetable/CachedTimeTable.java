package ch.epfl.rechor.timetable;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A lightweight wrapper around a timetable that caches
 * the most recently requested day’s trips and connections.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public class CachedTimeTable implements TimeTable {
    private final TimeTable timeTable;
    private Connections connections;
    private Trips trips;
    private LocalDate currentDate;

    /**
     * Wraps the given timetable with a cache.
     *
     * @param timetable the underlying timetable to be cached
     */
    public CachedTimeTable (TimeTable timetable) {
        this.timeTable = timetable;
    }

    @Override
    public Stations stations() {
        return timeTable.stations();
    }

    @Override
    public StationAliases stationAliases() {
        return timeTable.stationAliases();
    }

    @Override
    public Platforms platforms() {
        return timeTable.platforms();
    }

    @Override
    public Routes routes() {
        return timeTable.routes();
    }

    @Override
    public Transfers transfers() {
        return timeTable.transfers();
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        if (!Objects.equals(date, currentDate)) {
            updateFieds(date);
            return trips;
        }
        return trips;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        if (!Objects.equals(currentDate,date)) {
            updateFieds(date);
            return connections;
        }
        return connections;
    }

    public void updateFieds(LocalDate date) {
        connections = timeTable.connectionsFor(date);
        trips = timeTable.tripsFor(date);
        currentDate = date;
    }
}