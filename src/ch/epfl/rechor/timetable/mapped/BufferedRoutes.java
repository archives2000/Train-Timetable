package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.util.List;
import java.nio.ByteBuffer;

/**
 * Class used to represent all the routes of the Swiss timetable in a structured way.
 * This class provides efficient access to route information such as names and vehicle types.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class BufferedRoutes implements Routes {

    /**
     * Constant representing the field index of names inside the route representation.
     */
    private static final int NAME_ID = 0;

    /**
     * Constant representing the field index of vehicle types inside the route representation.
     */
    private static final int KIND = 1;

    /**
     * Attribute storing the structured buffer of routes.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Attribute storing the table of route names.
     */
    private final List<String> stringTable;

    /**
     * Structure defining the format of a route in the structured buffer.
     */
    private static final Structure routeStructure = new Structure(
            Structure.field(NAME_ID, Structure.FieldType.U16),
            Structure.field(KIND, Structure.FieldType.U8));

    /**
     * Constructs a BufferedRoutes instance.
     *
     * @param stringTable a list of route names associated with their respective indexes.
     * @param buffer      a ByteBuffer containing structured route data.
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer) {
        structuredBuffer = new StructuredBuffer(routeStructure, buffer);
        this.stringTable = stringTable;
    }

    /**
     * Retrieves the vehicle type corresponding to the given route ID.
     *
     * @param id the index of the route whose vehicle type is to be retrieved.
     * @return the vehicule type associated with the route.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public Vehicle vehicle(int id) throws IndexOutOfBoundsException {
        int posVehicle = structuredBuffer.getU8(KIND, id);
        return Vehicle.ALL.get(posVehicle);
    }

    /**
     * Retrieves the name of the route corresponding to the given ID.
     *
     * @param id the index of the route whose name is to be retrieved.
     * @return the name of the route.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public String name(int id) throws IndexOutOfBoundsException {
        int listIdx = structuredBuffer.getU16(NAME_ID, id);
        return stringTable.get(listIdx);
    }

    /**
     * Returns the total number of routes.
     *
     * @return the size of the route dataset.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
