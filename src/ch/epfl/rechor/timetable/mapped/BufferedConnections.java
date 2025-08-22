package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Class used to represent and extract data out of the representation of a Connection.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public final class BufferedConnections implements Connections {

    /**
     * Constant representing the field index of departure stops index, inside a connection array.
     */
    private static final int DEP_STOP_ID = 0;

    /**
     * Constant representing the field index of the departure time, inside a connection array.
     */
    private static final int DEP_MINUTES = 1;

    /**
     * Constant representing the field index of arrival stops index, inside a connection array.
     */
    private static final int ARR_STOP_ID = 2;

    /**
     * Constant representing the field index of arrival time (in minutes), inside a connection array.
     */
    private static final int ARR_MINUTES = 3;

    /**
     * Constant representing the field index of TRIP_POS_ID: the current id of the course of the
     * current connection
     * as well as the position of the connection in the course (in the eight less significant bits).
     */
    private static final int TRIP_POS_ID = 4;

    /**
     * Attribute storing an int buffer that stores for each connection,
     * the index of the next connection in the connection array.
     */
    private final IntBuffer intBuffer;

    /**
     * Attribute storing the structured buffer of connections.
     */
    private final StructuredBuffer structuredBuffer;

    /**
     * Final attribute storing the structure.
     */
    private static final Structure connectionStructure = new Structure(Structure.field(DEP_STOP_ID,
            Structure.FieldType.U16),
            Structure.field(DEP_MINUTES, Structure.FieldType.U16), Structure.field(ARR_STOP_ID,
            Structure.FieldType.U16),
            Structure.field(ARR_MINUTES, Structure.FieldType.U16), Structure.field(TRIP_POS_ID,
            Structure.FieldType.S32));

    /**
     * Constructs a buffered connection object using a structured buffer and a successor buffer.
     *
     * @param buffer the buffer that will be structured containing the five fields of a connection
     * @param succBuffer the buffer that will contain the id of the next connection for each connection
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer) {
        intBuffer = succBuffer.asIntBuffer();
        structuredBuffer = new StructuredBuffer(connectionStructure,buffer);
    }

    /**
     * Returns the ID of the departure stop for the given connection.
     *
     * @param id the index of the connection, from which we evaluate the departure stop index
     * @return the departure stop index, in the array containing all the stations
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int depStopId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(DEP_STOP_ID,id);
    }

    /**
     * Returns the departure time (in minutes after midnight) for the given connection.
     *
     * @param id the index of the connection, from which we evaluate the departure time
     * @return the departure time in minutes after midnight of the given connection
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int depMins(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(DEP_MINUTES, id);
    }
    /**
     * Returns the ID of the arrival stop for the given connection.
     *
     * @param id the index of the connection, from which we evaluate the arrival stop index
     * @return the arrival stop index, in the array containing all the stations
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int arrStopId(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(ARR_STOP_ID,id);
    }

    /**
     * Returns the arrival time (in minutes after midnight) for the given connection.
     *
     * @param id the index of the connection, from which we evaluate the arrival time
     * @return the arrival time in minutes after midnight of the given connection
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int arrMins(int id) throws IndexOutOfBoundsException {
        return structuredBuffer.getU16(ARR_MINUTES,id);
    }

    /**
     * Returns the trip ID of the given connection.
     *
     * @param id the index of the connection, from which we evaluate the index of the course
     *           of the current connection
     * @return the index of the course of the current connection
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int tripId(int id) throws IndexOutOfBoundsException {
        return Bits32_24_8.unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    /**
     * Returns the position of the connection within its trip.
     *
     * @param id the index of the connection, from which we evaluate the position
     *           of the connection inside the course
     * @return the position of the connection inside the course it belongs to.
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int tripPos(int id) throws IndexOutOfBoundsException {
        return Bits32_24_8.unpack8(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    /**
     * Returns the index of the next connection that follows the given one.
     *
     * @param id the index of the connection we want to evaluate the next connection id
     * @return the index of the next connection in the array containing the connections.
     * @throws IndexOutOfBoundsException if the given index exceeds the number of connection
     */
    @Override
    public int nextConnectionId(int id) throws IndexOutOfBoundsException {
        return intBuffer.get(id);
    }

    /**
     * The size of the array containing the connections.
     *
     * @return the size of the array containing the connections
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
