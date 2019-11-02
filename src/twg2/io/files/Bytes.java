package twg2.io.files;

/** A utility class for converting primitive types to/from bytes in big-endian order.
 * For example converting an integer to 4 bytes and storing those 4 bytes
 * at a specific location in a byte array.
 * @author TeamworkGuy2
 * @since 2014-4-27
 */
public final class Bytes {

	private Bytes() { throw new AssertionError("cannot instantiate Bytes"); }


	/** Write a double to the specified byte array as 8 bytes (big-endian order)
	 * @param value the double to write
	 * @param b the byte array to write the 'value' to
	 * @param offset the offset into the array at which to write the 8 bytes
	 */
	public static void writeDouble(double value, byte[] b, int offset) {
		writeLong(Double.doubleToRawLongBits(value), b, offset);
	}


	/** Write a float to the specified byte array as 4 bytes (big-endian order)
	 * @param value the float to write
	 * @param b the byte array to write the 'value' to
	 * @param offset the offset into the array at which to write the 4 bytes
	 */
	public static void writeFloat(float value, byte[] b, int offset) {
		writeInt(Float.floatToRawIntBits(value), b, offset);
	}


	/** Write a long to the specified byte array as 8 bytes (big-endian order)
	 * @param value the long to write
	 * @param b the byte array to write the 'value' to
	 * @param offset the offset into the array at which to write the 8 bytes
	 */
	public static void writeLong(long value, byte[] b, int offset) {
		b[offset] = (byte)(value >>> 56);
		b[offset+1] = (byte)(value >>> 48);
		b[offset+2] = (byte)(value >>> 40);
		b[offset+3] = (byte)(value >>> 32);
		b[offset+4] = (byte)(value >>> 24);
		b[offset+5] = (byte)(value >>> 16);
		b[offset+6] = (byte)(value >>> 8);
		b[offset+7] = (byte)(value );
	}


	/** Write an int to the specified byte array as 4 bytes (big-endian order)
	 * @param value the integer to write
	 * @param b the byte array to write the 'value' to
	 * @param offset the offset into the array at which to write the 4 bytes
	 */
	public static void writeInt(int value, byte[] b, int offset) {
		b[offset] = (byte)(value >>> 24);
		b[offset+1] = (byte)(value >>> 16);
		b[offset+2] = (byte)(value >>> 8);
		b[offset+3] = (byte)(value );
	}


	/** Write a short to the specified byte array as 2 bytes (big-endian order)
	 * @param value the short to write
	 * @param b the byte array to write the 'value' to
	 * @param offset the offset into the array at which to write the 2 bytes
	 */
	public static void writeShort(short value, byte[] b, int offset) {
		b[offset] = (byte)(value >>> 8);
		b[offset+1] = (byte)(value );
	}


	/** Write a boolean to the specified byte array as 1 byte (1=true, 0=false) (big-endian order)
	 * @param value the boolean to write
	 * @param b the byte array to write the 'value' to
	 * @param offset the offset into the array at which to write the byte
	 */
	public static void writeBoolean(boolean value, byte[] b, int offset) {
		b[offset] = (byte)(value ? 1 : 0);
	}


	/** Read a double value from the specified location in the specified array (assumes big-endian order)
	 * @param b the array to read the double from
	 * @param offset the offset into the array at which to read the 8 bytes
	 * @return eight bytes read from the indices {@code [offset, offset+3]} and
	 * converted to a double
	 */
	public static double readDouble(byte[] b, int offset) {
		return Double.longBitsToDouble(readLong(b, offset));
	}


	/** Read a float value from the specified location in the specified array (assumes big-endian order)
	 * @param b the array to read the float from
	 * @param offset the offset into the array at which to read the 4 bytes
	 * @return four bytes read from the indices {@code [offset, offset+3]} and
	 * converted to a float
	 */
	public static final float readFloat(byte[] b, int offset) {
		return Float.intBitsToFloat(readInt(b, offset));
	}


	/** Read a long value from the specified location in the specified array (assumes big-endian order)
	 * @param b the array to read the long from
	 * @param offset the offset into the array at which to read the 8 bytes
	 * @return eight bytes read from the indices {@code [offset, offset+3]} and converted to
	 * a long by {@code ((long)b[offset] << 56) | ((long)(b[offset+1] & 0xFF) << 48) |
	 * ((long)(b[offset+2] & 0xFF) << 40) | ((long)(b[offset+3] & 0xFF) << 32) |
	 * ((long)(b[offset+4] & 0xFF) << 24) | ((b[offset+5] & 0xFF) << 16) |
	 * ((b[offset+6] & 0xFF) << 8) | (b[offset+7] & 0xFF);}
	 */
	public static final long readLong(byte[] b, int offset) {
		return ((long)b[offset] << 56) |
				((long)(b[offset+1] & 0xFF) << 48) |
				((long)(b[offset+2] & 0xFF) << 40) |
				((long)(b[offset+3] & 0xFF) << 32) |
				((long)(b[offset+4] & 0xFF) << 24) |
				((b[offset+5] & 0xFF) << 16) |
				((b[offset+6] & 0xFF) << 8) |
				(b[offset+7] & 0xFF);
	}


	/** Read an integer value from the specified location in the specified array (assumes big-endian order)
	 * @param b the array to read the integer from
	 * @param offset the offset into the array at which to read the 4 bytes
	 * @return four bytes read from the indices {@code [offset, offset+3]} and converted to
	 * an integer by {@code (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3]}
	 */
	public static final int readInt(byte[] b, int offset) {
		return (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3];
	}


	/** Read a short value from the specified location in the specified array (assumes big-endian order)
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to read the 2 bytes
	 * @return two bytes read from indices {@code offset} and {@code offset+1} and converted to
	 * a short by {@code (b[offset] << 8) | b[offset+1]}
	 */
	public static final short readShort(byte[] b, int offset) {
		return (short)((b[offset] << 8) | b[offset+1]);
	}


	/** Read a boolean value from the specified location in the specified byte array (1=true, 0=false) (assumes big-endian order)
	 * @param b the array to read the boolean from
	 * @param offset the offset into the array at which to read the byte
	 * @return two bytes read from indices {@code offset} and {@code offset+1} and converted to
	 * a short by {@code (b[offset] << 8) | b[offset+1]}
	 */
	public static final boolean readBoolean(byte[] b, int offset) {
		return (b[offset] == 0 ? false : true);
	}

}
