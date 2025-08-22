package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Class allowing access to alias station timetable in a flattened representation.
 * This class provides efficient retrieval of alias names and their corresponding station names.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class BufferedStationAliases implements StationAliases {

    /**
     * Constant representing the field index of alias names inside the station aliases representation.
     */
    private static final int ALIAS_ID = 0;

    /**
     * Constant representing the field index of station names inside the station aliases representation.
     */
    private static final int STATION_NAME_ID = 1;

    /**
     * Structure defining the format of station aliases in the structured buffer.
     */
    private static final Structure structureStationAliases = new Structure(
            Structure.field(ALIAS_ID, Structure.FieldType.U16),
            Structure.field(STATION_NAME_ID, Structure.FieldType.U16));

    /**
     * Attribute storing the structured buffer of station aliases.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Attribute storing the table of alias and station names.
     */
    private final List<String> stringTable;

    /**
     * Constructs a BufferedStationAliases instance.
     *
     * @param stringTable a list of alias and station names associated with their respective indexes.
     * @param buffer      a ByteBuffer containing structured alias station data.
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredBuffer = new StructuredBuffer(structureStationAliases, buffer);
    }

    /**
     * Retrieves the alias name corresponding to the given ID.
     *
     * @param id the index of the alias name to be retrieved.
     * @return the alias name.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public String alias(int id) throws IndexOutOfBoundsException {
        int aliasIndex = structuredBuffer.getU16(ALIAS_ID, id);
        return stringTable.get(aliasIndex);
    }

    /**
     * Retrieves the station name corresponding to the given alias ID.
     *
     * @param id the index of the alias whose associated station name is to be retrieved.
     * @return the station name corresponding to the alias.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public String stationName(int id) throws IndexOutOfBoundsException {
        int stationNameIndex = structuredBuffer.getU16(STATION_NAME_ID, id);
        return stringTable.get(stationNameIndex);
    }

    /**
     * Returns the total number of alias-station mappings.
     *
     * @return the size of the alias dataset.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
