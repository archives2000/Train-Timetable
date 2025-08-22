package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Class allowing access to a platform timetable in a flattened representation.
 * This class provides methods to retrieve platform names and associated station IDs efficiently.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class BufferedPlatforms implements Platforms {

    /**
     * Constant representing the field index of names inside the platform representation.
     */
    private static final int NAME_ID = 0;

    /**
     * Constant representing the field index of stations inside the platform representation.
     */
    private static final int STATION_ID = 1;

    /**
     * Attribute storing the structured buffer of platforms.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Attribute storing the table of platform names.
     */
    private final List<String> stringTable;

    /**
     * Constructs a BufferedPlatforms instance.
     *
     * @param stringTable a list of platform names associated with their respective indexes.
     * @param buffer      a ByteBuffer containing structured platform data.
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        Structure structurePlatform = new Structure(
                Structure.field(NAME_ID, Structure.FieldType.U16),
                Structure.field(STATION_ID, Structure.FieldType.U16));
        this.structuredBuffer = new StructuredBuffer(structurePlatform, buffer);
    }

    /**
     * Retrieves the name of the platform corresponding to the given ID.
     *
     * @param id the index of the platform whose name is to be retrieved.
     * @return the name of the platform.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public String name(int id) throws IndexOutOfBoundsException {
        int nameIndex = structuredBuffer.getU16(NAME_ID, id);
        return stringTable.get(nameIndex);
    }

    /**
     * Retrieves the station ID associated with the given platform ID.
     *
     * @param id the index of the platform whose station ID is to be retrieved.
     * @return the station ID corresponding to the platform.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public int stationId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(STATION_ID, id);
    }

    /**
     * Returns the total number of platforms.
     *
     * @return the size of the platform dataset.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
