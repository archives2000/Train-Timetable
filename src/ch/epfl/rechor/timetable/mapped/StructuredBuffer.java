package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.nio.ByteBuffer;

/**
 * Class used to work with buffers, following a given structure.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public class StructuredBuffer {

    /**
     * The buffer that is passed to the structured buffer.
     */
    private final ByteBuffer buffer;

    /**
     * The structure of our structured buffer.
     */
    private final Structure structure;

    /**
     * Public constructor taking a structure and a Bytebuffer in  arguments.
     *
     * @param structure the given Structure
     * @param buffer the given buffer, that has to follow the constraints of the structure
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer) {
        Preconditions.checkArgument(buffer.capacity() % structure.totalSize() == 0);

        this.structure = structure;
        this.buffer = buffer;
    }

    /**
     * The number of elements are the number of bytes divided by the number of bytes
     * (total size) of a certain structure.
     *
     * @return the number of elements of the "array".
     */
    public int size() {
        return buffer.capacity()/structure.totalSize();
    }

    /**
     * Checks whether the given element index surpasses the size of the buffer.
     *
     * @param elementIndex the index of the element in the array
     */
    private void checkElementIndex(int elementIndex) {
        if(elementIndex < 0 || elementIndex >= size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Reads an unsigned 8-bit value (U8) from the buffer.
     *
     * @param fieldIndex the index of the field (from the structure)
     * @param elementIndex the position of the element from which we want to evaluate a byte value
     * @return the corresponding byte value
     */
    public int getU8(int fieldIndex, int elementIndex) {
        checkElementIndex(elementIndex);
        byte U8 = buffer.get(structure.offset(fieldIndex, elementIndex));
        return Byte.toUnsignedInt(U8);
    }

    /**
     * Reads an unsigned 8-bit value (U16) from the buffer.
     *
     * @param fieldIndex the index of the field (from the structure)
     * @param elementIndex the position of the element from which we want to extract a short value
     * @return the corresponding short value
     */
    public int getU16(int fieldIndex, int elementIndex) {
        checkElementIndex(elementIndex);

        short U16 = buffer.getShort(structure.offset(fieldIndex, elementIndex));
        return Short.toUnsignedInt(U16);
    }

    /**
     * Reads an unsigned 8-bit value (S32) from the buffer.
     *
     * @param fieldIndex the index of the field (from the structure)
     * @param elementIndex the position of the element from which we want to extract an int value
     * @return the corresponding int value
     */
    public int getS32(int fieldIndex, int elementIndex) {
        checkElementIndex(elementIndex);

        return buffer.getInt(structure.offset(fieldIndex, elementIndex));
    }
}
