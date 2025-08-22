package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 * Class providing access to transfer times between stations in a flattened representation.
 * This class enables efficient retrieval of transfer durations and station relationships.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public final class BufferedTransfers implements Transfers {

    /**
     * Constant representing the field index of departure station IDs inside the transfer representation.
     */
    private static final int DEP_STATION_ID = 0;

    /**
     * Constant representing the field index of arrival station IDs inside the transfer representation.
     */
    private static final int ARR_STATION_ID = 1;

    /**
     * Constant representing the field index of transfer durations (in minutes) inside the
     * transfer representation.
     */
    private static final int TRANSFER_MINUTES = 2;

    /**
     * Structure defining the format of transfers in the structured buffer.
     */
    private static final Structure structureTransfers = new Structure(
            Structure.field(DEP_STATION_ID, Structure.FieldType.U16),
            Structure.field(ARR_STATION_ID, Structure.FieldType.U16),
            Structure.field(TRANSFER_MINUTES, Structure.FieldType.U8));

    /**
     * Attribute storing the structured buffer of transfers.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Array storing packed intervals representing transfer connections for each station.
     */
    private final int[] arrayOfPackedChanges;

    /**
     * Constructs a BufferedTransfers instance from a given buffer.
     * It initializes the structured buffer and precomputes packed intervals for transfers.
     *
     * @param buffer a ByteBuffer containing structured transfer data.
     */
    public BufferedTransfers(ByteBuffer buffer) {
        structuredBuffer = new StructuredBuffer(structureTransfers, buffer);

        int maxStationsIdx = 0;
        for (int i = 0; i < size(); i++) {
            int currentArrId = structuredBuffer.getU16(ARR_STATION_ID, i);
            if (currentArrId > maxStationsIdx) {
                maxStationsIdx = currentArrId;
            }
        }

        int lastExcludedIdx = 0;
        int[] arrStationsPackedInterval = new int[++maxStationsIdx];

        while (lastExcludedIdx < size()) {
            int startIdx = lastExcludedIdx;
            int currentArrId = structuredBuffer.getU16(ARR_STATION_ID, startIdx);
            while (lastExcludedIdx < size()
                    && structuredBuffer.getU16(ARR_STATION_ID, lastExcludedIdx) == currentArrId) {
                lastExcludedIdx++;
            }
            arrStationsPackedInterval[currentArrId] = PackedRange.pack(startIdx, lastExcludedIdx);
        }
        arrayOfPackedChanges = arrStationsPackedInterval;
    }

    /**
     * Retrieves the departure station ID for a given transfer.
     *
     * @param id the index of the transfer.
     * @return the departure station ID.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public int depStationId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(DEP_STATION_ID, id);
    }

    /**
     * Retrieves the duration of the transfer in minutes.
     *
     * @param id the index of the transfer.
     * @return the duration of the transfer in minutes.
     * @throws IndexOutOfBoundsException if the given ID is invalid.
     */
    @Override
    public int minutes(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU8(TRANSFER_MINUTES, id);
    }

    /**
     * Retrieves the packed interval representing all transfers arriving at the given station.
     *
     * @param stationId the ID of the arrival station.
     * @return the packed interval representing all transfers arriving at the station.
     * @throws IndexOutOfBoundsException if the given station ID is invalid.
     */
    @Override
    public int arrivingAt(int stationId) throws IndexOutOfBoundsException {
        return arrayOfPackedChanges[stationId];
    }

    /**
     * Retrieves the transfer duration between two given stations.
     *
     * @param depStationId the departure station ID.
     * @param arrStationId the arrival station ID.
     * @return the transfer duration in minutes.
     * @throws NoSuchElementException     if no transfer exists between the given stations.
     * @throws IndexOutOfBoundsException if either station ID is invalid.
     */
    @Override
    public int minutesBetween(int depStationId, int arrStationId) throws NoSuchElementException,
            IndexOutOfBoundsException {
        int startIdx = arrivingAt(arrStationId);
        int startIdx_ArrStation = PackedRange.startInclusive(startIdx);
        int endIdx_ArrStation = PackedRange.endExclusive(arrivingAt(arrStationId));

        for (int i = startIdx_ArrStation; i < endIdx_ArrStation; i++) {
            if (depStationId(i) == depStationId) {
                return minutes(i);
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the total number of transfers.
     *
     * @return the number of transfers available in the dataset.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
