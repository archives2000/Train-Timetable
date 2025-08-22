package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;
/**
 * Class providing access to the station timetable in a flattened representation.
 * This class allows efficient retrieval of station names and their geographical coordinates.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class BufferedStations implements Stations {

    /**
     * Constant representing the field index of station names inside the structured representation.
     */
    private static final int NAME_ID = 0;

    /**
     * Constant representing the field index of longitude values inside the structured representation.
     */
    private static final int LON = 1;

    /**
     * Constant representing the field index of latitude values inside the structured representation.
     */
    private static final int LAT = 2;

    /**
     * Structure defining the format of stations in the structured buffer.
     */
    private static final Structure structureStation = new Structure(
            Structure.field(NAME_ID, Structure.FieldType.U16),
            Structure.field(LON, Structure.FieldType.S32),
            Structure.field(LAT, Structure.FieldType.S32));

    /**
     * Attribute storing the structured buffer of station data.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Attribute storing the table of station names.
     */
    private final List<String> stringTable;

    /**
     * Constructs a BufferedStations instance.
     *
     * @param stringTable a list of station names associated with their respective indexes.
     * @param buffer      a ByteBuffer containing structured station data.
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.structuredBuffer = new StructuredBuffer(structureStation, buffer);
    }

    /**
     * Retrieves the name of the station corresponding to the given ID.
     *
     * @param id the index of the station whose name is to be retrieved.
     * @return the name of the station.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public String name(int id) throws IndexOutOfBoundsException {
        int nameIndex = structuredBuffer.getU16(NAME_ID, id);
        return stringTable.get(nameIndex);
    }

    /**
     * Retrieves the longitude of the station corresponding to the given ID.
     *
     * @param id the index of the station whose longitude is to be retrieved.
     * @return the longitude of the station in degrees.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public double longitude(int id) throws IndexOutOfBoundsException {
        double longitudeIndex = structuredBuffer.getS32(LON, id);
        return StrictMath.scalb(longitudeIndex * 360, -32);
    }

    /**
     * Retrieves the latitude of the station corresponding to the given ID.
     *
     * @param id the index of the station whose latitude is to be retrieved.
     * @return the latitude of the station in degrees.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public double latitude(int id) throws IndexOutOfBoundsException {
        double latitudeIndex = structuredBuffer.getS32(LAT, id);
        return StrictMath.scalb(latitudeIndex * 360, -32);
    }

    /**
     * Returns the total number of stations.
     *
     * @return the number of stations available in the dataset.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
