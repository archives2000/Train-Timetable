package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Class providing access to trips in a flattened representation.
 * This class allows efficient retrieval of trip route IDs and their destinations.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class BufferedTrips implements Trips {

    /**
     * Constant representing the field index of route IDs inside the structured trip representation.
     */
    private static final int ROUTE_ID = 0;

    /**
     * Constant representing the field index of destination IDs inside the structured
     * trip representation.
     */
    private static final int DESTINATION_ID = 1;

    /**
     * Structure defining the format of trips in the structured buffer.
     */
    private static final Structure tripsStructure = new Structure(
            Structure.field(ROUTE_ID, Structure.FieldType.U16),
            Structure.field(DESTINATION_ID, Structure.FieldType.U16));

    /**
     * Attribute storing the structured buffer of trips.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Attribute storing the table of route and destination names.
     */
    private final List<String> stringTable;

    /**
     * Constructs a BufferedTrips instance from a given buffer.
     *
     * @param stringTable a list of route names and destinations associated with their
     *                    respective indexes.
     * @param buffer      a ByteBuffer containing structured trip data.
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        structuredBuffer = new StructuredBuffer(tripsStructure, buffer);
    }

    /**
     * Retrieves the route ID associated with a given trip.
     *
     * @param id the index of the trip whose route ID is to be retrieved.
     * @return the route ID of the trip.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public int routeId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(ROUTE_ID, id);
    }

    /**
     * Retrieves the destination name of a given trip.
     *
     * @param id the index of the trip whose destination is to be retrieved.
     * @return the name of the destination.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public String destination(int id) throws IndexOutOfBoundsException {
        int stringIdx = structuredBuffer.getU16(DESTINATION_ID, id);
        return stringTable.get(stringIdx);
    }

    /**
     * Returns the total number of trips.
     *
     * @return the number of trips available in the dataset.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
