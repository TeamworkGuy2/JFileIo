package twg2.io.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import twg2.arrays.ArrayUtil;

/** A persistent helper for reading binary/text content from files.
 * Internally cached arrays/buffers are used to reduce garbage. Initial calls make take longer than subsequent calls due to cache initialization.
 * WARNING: this class is not thread safe, cached arrays/buffers are used internally to reduce generated garbage, create a new instance per thread.
 * @author TeamworkGuy2
 * @since 2015-9-19
 */
public class FileReadUtil {
	// thread local instance
	private static final ThreadLocal<FileReadUtil> threadLocalInst = ThreadLocal.withInitial(() -> new FileReadUtil());
	// 1 MB
	private static int MAX_CHUNK_SIZE = 1024 * 1024;
	/** the initial size of the temp buffers */
	int defaultChunkSize = 8192;
	/** a single threaded unsafe byte buffer */
	private byte[] tmpByteBuf;
	/** a single threaded unsafe char buffer */
	private char[] tmpCharBuf;
	// ==== stats ====
	private Stats statsInst;
	private int totalByteReads;
	private int byteBufResizeCount;
	private int totalCharReads;
	private int charBufResizeCount;


	public FileReadUtil() {
	}


	public Stats getStats() {
		return statsInst != null ? statsInst : (statsInst = new Stats());
	}


	public byte[] readBytes(final File file) throws IOException {
		return readFile(new FileInputStream(file), defaultChunkSize);
	}


	public byte[] readBytes(final InputStream is) throws IOException {
		return readFile(is, defaultChunkSize);
	}


	/** read an input stream until it is exhausted of data.
	 * The input stream is not closed when the method returns.
	 * The input stream is read in blocks and (probably) does not need to be buffered for performance.
	 * @param is the input stream to read
	 * @param chunkSize the number of bytes to read per chunk from the underlying
	 * file since the file size is not known
	 * @return the data found in the input stream as a byte array
	 * @throws IOException if there is an error reading the input stream
	 */
	public byte[] readFile(final InputStream is, final int chunkSize) throws IOException {
		return (byte[])readFile0(is, chunkSize);
	}


	Object readFile0(final InputStream is, final int chunkSize) throws IOException {
		int totalSize = 0;
		int variableChunkSize = chunkSize;

		// Read initially available data
		int available = is.available();
		available = available < chunkSize ? chunkSize : available;
		byte[] buffer = getByteArray(available);
		int initialReadSize = is.read(buffer, 0, buffer.length);
		totalByteReads++;
		int readSize = initialReadSize;
		totalSize += readSize;

		// Create temporarily buffer
		byte[] temp = new byte[chunkSize];

		// Try to read any additional data, if successful, continue reading until the end of the data is reached
		if((readSize = is.read(temp, 0, temp.length)) != -1) {
			totalByteReads++;
			totalSize += readSize;
			// Create new array large enough to hold the original array and new data, and extra room for more data
			buffer = updateByteArrayRef(combine(buffer, 0, initialReadSize, temp, 0, readSize, buffer.length + readSize + chunkSize));

			// Continue to read additional data until no more data is available to read
			while((readSize = is.read(buffer, totalSize, variableChunkSize)) != -1) {
				totalByteReads++;
				totalSize += readSize;
				// increase chunk size based on algorithm to minimize number of allocation arrays
				variableChunkSize = increaseChunkSize(variableChunkSize, chunkSize);

				// Store buffer temporarily, create larger buffer, and move data to larger buffer
				temp = buffer;
				buffer = updateByteArrayRef(new byte[buffer.length + variableChunkSize]);
				System.arraycopy(temp, 0, buffer, 0, temp.length);
			}
		}

		// Trim the buffer to the exact size read
		temp = buffer;
		int totalSizeClamped = totalSize < 0 ? 0 : totalSize;
		buffer = new byte[totalSizeClamped];
		System.arraycopy(temp, 0, buffer, 0, totalSizeClamped);
		return buffer;
	}


	public char[] readChars(final File file) throws IOException {
		return readFile(new FileReader(file), defaultChunkSize);
	}


	public char[] readChars(final Reader reader) throws IOException {
		return readFile(reader, defaultChunkSize);
	}


	public String readString(final Reader reader) throws IOException {
		String str = (String)readFile0(reader, defaultChunkSize, true);
		return str;
	}


	/** read a reader until it is exhausted of data.
	 * The reader is not closed when the method returns.
	 * The reader is read in blocks and (probably) does not need to be buffered for performance.
	 * @param reader the {@link Reader} to read from
	 * @param chunkSize the number of bytes to read per chunk from the underlying
	 * file since the file size is not known
	 * @return the data found in the input stream as a byte array
	 * @throws IOException if there is an error reading the input stream
	 */
	public char[] readFile(final Reader reader, final int chunkSize) throws IOException {
		return (char[])readFile0(reader, chunkSize, false);
	}


	Object readFile0(final Reader reader, final int chunkSize, final boolean asString) throws IOException {
		int totalSize = 0;
		int variableChunkSize = chunkSize;

		// Read initially available data
		char[] buffer = getCharArray(chunkSize);
		int initialReadSize = reader.read(buffer, 0, buffer.length);
		totalCharReads++;
		int readSize = initialReadSize;
		totalSize += readSize;
		// Create temporarily buffer
		char[] temp = new char[chunkSize];

		// Try to read any additional data, if successful, continue reading until the end of the data is reached
		if((readSize = reader.read(temp, 0, temp.length)) != -1) {
			totalCharReads++;
			totalSize += readSize;
			// Create new array large enough to hold the original array and new data, and extra room for more data
			buffer = updateCharArrayRef(combine(buffer, 0, initialReadSize, temp, 0, readSize, buffer.length + readSize + chunkSize));

			// Continue to read additional data until no more data is available to read
			while((readSize = reader.read(buffer, totalSize, variableChunkSize)) != -1) {
				totalCharReads++;
				totalSize += readSize;
				// increase chunk size based on algorithm to minimize number of allocation arrays
				variableChunkSize = increaseChunkSize(variableChunkSize, chunkSize);

				// Store buffer temporarily, create larger buffer, and move data to larger buffer
				temp = buffer;
				buffer = updateCharArrayRef(new char[buffer.length + variableChunkSize]);
				System.arraycopy(temp, 0, buffer, 0, temp.length);
			}
		}

		if(asString) {
			String str = new String(buffer, 0, totalSize < 0 ? 0 : totalSize);
			return str;
		}

		// Trim the buffer to the exact size read
		temp = buffer;
		int totalSizeClamped = totalSize < 0 ? 0 : totalSize;
		buffer = new char[totalSizeClamped];
		System.arraycopy(temp, 0, buffer, 0, totalSizeClamped);
		return buffer;
	}


	public void readTo(final Reader reader, final StringBuilder dst) throws IOException {
		char[] chars = readFile(reader, defaultChunkSize);
		dst.append(chars, 0, chars.length);
	}


	public void readTo(final Reader reader, final Writer dst) throws IOException {
		char[] chars = readFile(reader, defaultChunkSize);
		dst.write(chars, 0, chars.length);
	}


	byte[] updateByteArrayRef(byte[] ary) {
		//System.out.println("byte array now size " + ary.length);
		byteBufResizeCount++;
		tmpByteBuf = ary;
		return ary;
	}


	byte[] getByteArray(int size) {
		return (tmpByteBuf != null && tmpByteBuf.length > size) ? tmpByteBuf : updateByteArrayRef(new byte[size]);
	}


	char[] updateCharArrayRef(char[] ary) {
		//System.out.println("char array now size " + ary.length);
		charBufResizeCount++;
		tmpCharBuf = ary;
		return ary;
	}


	char[] getCharArray(int size) {
		return (tmpCharBuf != null && tmpCharBuf.length > size) ? tmpCharBuf : updateCharArrayRef(new char[size]);
	}


	public static int increaseChunkSize(int currentChunkSize, int originalChunkSize) {
		if(currentChunkSize >= MAX_CHUNK_SIZE) {
			return currentChunkSize;
		}
		return currentChunkSize << 1; // size * 2
	}


	public static byte[] combine(byte[] ary1, int off1, int len1, byte[] ary2, int off2, int len2, int totalReturnLength) {
		return ArrayUtil.concat(ary1, off1, len1, ary2, off2, len2, 0, totalReturnLength);
	}


	public static char[] combine(char[] ary1, int off1, int len1, char[] ary2, int off2, int len2, int totalReturnLength) {
		return ArrayUtil.concat(ary1, off1, len1, ary2, off2, len2, 0, totalReturnLength);
	}


	public static FileReadUtil threadLocalInst() {
		return threadLocalInst.get();
	}




	public class Stats {

		public int getCharCacheSize() {
			return tmpCharBuf.length;
		}


		public int getByteCacheSize() {
			return tmpByteBuf.length;
		}


		public int getByteReadCount() {
			return totalByteReads;
		}


		public int getCharReadCount() {
			return totalCharReads;
		}


		public int getByteCacheResizeCount() {
			return byteBufResizeCount;
		}


		public int getCharCacheResizeCount() {
			return charBufResizeCount;
		}


		@Override
		public String toString() {
			return "FileReadUtilStats: { " +
					"byteCache: { size: " + tmpByteBuf.length + ", readCount: " + totalByteReads + ", cacheResizes: " + byteBufResizeCount + " }, " +
					"charCache: { size: " + tmpCharBuf.length + ", readCount: " + totalCharReads + ", cacheResizes: " + charBufResizeCount + " } }";
		}

	}

}
