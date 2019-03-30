package twg2.io.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

/** A persistent helper for reading binary/text content from files.
 * Internally cached arrays/buffers are used to reduce array allocations and garbage. Initial calls may take longer than subsequent calls due to cache initialization.
 * WARNING: this class is not thread safe, cached arrays/buffers are used internally to reduce generated garbage, create one new instance per thread or use {@link #threadLocalInst()}.
 * @author TeamworkGuy2
 * @since 2015-9-19
 */
public class FileReadUtil {
	// thread local instance
	private static final ThreadLocal<FileReadUtil> threadLocalInst = ThreadLocal.withInitial(() -> new FileReadUtil());
	// 10 MB
	private static int MAX_CHUNK_SIZE = 10 * 1024 * 1024;
	// defaults which can be set by static methods
	private static volatile int staticDefaultChunkSize = 8192;
	private static volatile Charset defaultCharset = Charset.forName("UTF-8");

	private CharsetDecoder charsetDecoder;

	/** the initial size of the temp buffers */
	private int defaultChunkSize;
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


	/** Create a FileReadUtil with default 'UTF-8' charset for text decoding and initial buffered read() chunk size of 8192
	 */
	public FileReadUtil() {
		this(defaultCharset, staticDefaultChunkSize);
	}


	/** Create a FileReadUtil instance
	 * @param charset the charset to use for text decoding, if null 'UTF-8' is used
	 * @param defaultChunkSize the initial size of growth chunks when reading buffered input, must be greater than 1
	 */
	public FileReadUtil(Charset charset, int defaultChunkSize) {
		if(defaultChunkSize < 2) {
			throw new IllegalArgumentException("defaultChunkSize must be greater than 1");
		}
		charset = charset != null ? charset : defaultCharset;
		this.defaultChunkSize = defaultChunkSize;
		this.charsetDecoder = charset.newDecoder()
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);
	}


	public Stats getStats() {
		return statsInst != null ? statsInst : (statsInst = new Stats());
	}


	// ==== read byte[] ====

	/** Read a byte[] from a {@link File} using {@link FileInputStream} and the default buffer chunk size.
	 * @see #readBinary(InputStream, int)
	 */
	public byte[] readBytes(File file) throws IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			int readCnt = readBinary(is, defaultChunkSize);
			return copyBuffer(this.tmpByteBuf, readCnt);
		}
	}


	/** Read a byte[] from a {@link File} using {@link FileInputStream} and the default buffer chunk size.
	 * @see #readBinary(InputStream, int)
	 */
	public byte[] readBytes(File file, int chunkSize) throws IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			int readCnt = readBinary(is, chunkSize);
			return copyBuffer(this.tmpByteBuf, readCnt);
		}
	}


	/** Read a byte[] from an {@link InputStream} using the default buffer chunk size.
	 * @see #readBinary(InputStream, int)
	 */
	public byte[] readBytes(InputStream is) throws IOException {
		int readCnt = readBinary(is, defaultChunkSize);
		return copyBuffer(this.tmpByteBuf, readCnt);
	}


	/** Read a byte[] from an {@link InputStream} using a custom buffer chunk size.
	 * The input stream is NOT closed by this method.
	 * The input stream is read in blocks and (probably) does not need to be buffered for performance.
	 * @param is the input stream to read (stream is NOT closed by this method)
	 * @param chunkSize the number of bytes to read per chunk from the underlying
	 * file since the file size is not known
	 * @return the data found in the input stream as a byte array
	 * @throws IOException if there is an error reading the input stream
	 */
	public byte[] readBytes(InputStream is, int chunkSize) throws IOException {
		int readCnt = readBinary(is, chunkSize);
		return copyBuffer(this.tmpByteBuf, readCnt);
	}


	/** Read all available bytes from {@code in}.
	 * @return return number of bytes written into {@link #tmpByteBuf}
	 * @throws IOException
	 */
	protected int readBinary(InputStream is, int chunkSize) throws IOException {
		if(chunkSize < 2) {
			throw new IllegalArgumentException("chunkSize must be greater than 1");
		}
		int totalSize = 0;
		int variableChunkSize = chunkSize;

		// Read initially available data
		int available = is.available();
		available = available < chunkSize ? chunkSize : available;
		byte[] buffer = getByteBuf(available);
		int readSize = is.read(buffer, 0, buffer.length);
		totalByteReads++;
		totalSize += readSize;

		int tmpReadNext = -1;
		// read 1 byte to see if there is more input (performance optimization before allocating new buffer)
		if((tmpReadNext = is.read()) > -1) {
			totalByteReads++;
			totalSize++;
			if(totalSize > (int)(buffer.length * 0.8f) - 1) {
				// Create new array large enough to hold the original array and room for more data
				byte[] temp = setByteBuf(new byte[buffer.length + variableChunkSize]);
				System.arraycopy(buffer, 0, temp, 0, readSize);
				buffer = temp;
			}
			// save the 1 byte read
			buffer[readSize] = (byte)(tmpReadNext & 0xFF); // [readSize] is the index after the last byte from the initial read()

			// Continue to read additional data until no more data is available to read
			while((readSize = is.read(buffer, totalSize, buffer.length - totalSize)) != -1) {
				totalByteReads++;
				totalSize += readSize;
				if(totalSize > (int)(buffer.length * 0.8f) - 1) {
					// increase chunk size based on algorithm to minimize number of allocation arrays
					variableChunkSize = increaseChunkSize(variableChunkSize, chunkSize);

					// Create larger buffer and move data to larger buffer
					byte[] temp = buffer;
					buffer = setByteBuf(new byte[buffer.length + variableChunkSize]);
					System.arraycopy(temp, 0, buffer, 0, temp.length);
				}
			}
		}

		return totalSize < 0 ? 0 : totalSize;
	}


	// ==== read byte[] -> char[] ====

	/** Read a char[] from an {@link InputStream} using {@link FileInputStream} and the default UTF-8 charset decoder and default buffer chunk size.
	 * @see #readChars(InputStream, CharsetDecoder, int)
	 */
	public char[] readChars(File file) throws IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			int readCnt = readText(is, charsetDecoder, defaultChunkSize);
			return copyBuffer(this.tmpCharBuf, readCnt);
		}
	}


	/** Read a char[] from an {@link File} using {@link FileInputStream} and the default UTF-8 charset decoder and a custom buffer chunk size.
	 * @see #readChars(InputStream, CharsetDecoder, int)
	 */
	public char[] readChars(File file, int chunkSize) throws IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			int readCnt = readText(is, charsetDecoder, chunkSize);
			return copyBuffer(this.tmpCharBuf, readCnt);
		}
	}


	/** Read a char[] from an {@link InputStream} using the default UTF-8 charset decoder and default buffer chunk size.
	 * This method is optimized to perform the least array allocations necessary to read and
	 * decode text data by using internal cached arrays in this instance.
	 * @param is the input stream to read (stream is NOT closed by this method)
	 * @return the char[] read from the input stream
	 * @throws IOException
	 */
	public char[] readChars(InputStream is) throws IOException {
		int readCnt = readText(is, charsetDecoder, defaultChunkSize);
		return copyBuffer(this.tmpCharBuf, readCnt);
	}


	/** Read a char[] from an {@link InputStream} using the default UTF-8 charset decoder and a custom buffer chunk size.
	 * @see #readChars(InputStream, CharsetDecoder, int)
	 */
	public char[] readChars(InputStream is, int chunkSize) throws IOException {
		int readCnt = readText(is, charsetDecoder, chunkSize);
		return copyBuffer(this.tmpCharBuf, readCnt);
	}


	/** Read a char[] from an {@link InputStream} using the specified charset decoder and custom buffer chunk size.
	 * This method is optimized to perform the least array allocations necessary to read and
	 * decode text data by using internal cached arrays in this instance.
	 * @param is the input stream to read (stream is NOT closed by this method)
	 * @param decoder the {@link CharsetDecoder} to use
	 * @param chunkSize the number of bytes to read per chunk from the underlying
	 * file since the file size is not known
	 * @return the char[] read from the file
	 * @throws IOException
	 */
	public char[] readChars(InputStream is, CharsetDecoder decoder, int chunkSize) throws IOException {
		int readCnt = readText(is, decoder, chunkSize);
		return copyBuffer(this.tmpCharBuf, readCnt);
	}


	// ==== read byte[] -> String ====


	/** Read a String from an {@link File} using {@link FileInputStream} and the default UTF-8 charset decoder and default buffer chunk size.
	 * @see #readString(InputStream, CharsetDecoder, int)
	 */
	public String readString(File file) throws IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			int readCnt = readText(is, charsetDecoder, defaultChunkSize);
			return new String(this.tmpCharBuf, 0, readCnt);
		}
	}


	/** Read a String from an {@link File} using {@link FileInputStream} and the default UTF-8 charset decoder and a custom buffer chunk size.
	 * @see #readString(InputStream, CharsetDecoder, int)
	 */
	public String readString(File file, int chunkSize) throws IOException {
		try(FileInputStream is = new FileInputStream(file)) {
			int readCnt = readText(is, charsetDecoder, chunkSize);
			return new String(this.tmpCharBuf, 0, readCnt);
		}
	}


	/** Read a String from an {@link InputStream} using the default UTF-8 charset decoder and default buffer chunk size.
	 * @see #readString(InputStream, CharsetDecoder, int)
	 */
	public String readString(InputStream is) throws IOException {
		int readCnt = readText(is, charsetDecoder, defaultChunkSize);
		return new String(this.tmpCharBuf, 0, readCnt);
	}


	/** Read a String from an {@link InputStream} using the default UTF-8 charset decoder and a custom buffer chunk size.
	 * @see #readString(InputStream, CharsetDecoder, int)
	 */
	public String readString(InputStream is, int chunkSize) throws IOException {
		int readCnt = readText(is, charsetDecoder, chunkSize);
		return new String(this.tmpCharBuf, 0, readCnt);
	}


	/** Read a String from an {@link InputStream} using the specified charset decoder and custom buffer chunk size.
	 * This method is optimized to perform the least array allocations necessary to read and
	 * decode text data by using internal cached arrays in this instance.
	 * @param is the input stream to read (stream is NOT closed by this method)
	 * @param decoder the {@link CharsetDecoder} to use
	 * @param chunkSize the number of bytes to read per chunk from the underlying
	 * file since the file size is not known
	 * @return the string read from the file
	 * @throws IOException
	 */
	public String readString(InputStream is, CharsetDecoder decoder, int chunkSize) throws IOException {
		int readCnt = readText(is, decoder, chunkSize);
		return new String(this.tmpCharBuf, 0, readCnt);
	}


	/** Read an {@link InputStream} until it is empty with the fewest array allocations possible.
	 * @param is the input stream to read (stream is NOT closed by this method)
	 * @param decoder the {@link CharsetDecoder} to use
	 * @param chunkSize the number of bytes to read per chunk from the underlying
	 * file since the file size is not known
	 * @param returnType 1 = {@link #RETURN_RAW_ARRAY} to return shared char[], 2 = {@link #RETURN_NEW_TRIMMED_ARRAY} to return new char[], 3 = {@link #RETURN_STRING} to return a string
	 * @return depending on {@code returnType} returns {@link #tmpCharBuf} char[], or a new trimmed char[], or a string
	 * @throws IOException
	 */
	protected int readText(InputStream is, CharsetDecoder decoder, int chunkSize) throws IOException {
		// read the stream bytes into share temp byte[]
		int readSize = readBinary(is, chunkSize);
		byte[] buf = this.tmpByteBuf;
		// detect and skip UTF-8 BOM
		int skip = (readSize > 3 && buf[0] == (byte)0xEF && buf[1] == (byte)0xBB && buf[2] == (byte)0xBF ? 3 : 0);
		// assume ASCII
		float avgBytesPerChar = 1;
		int charsEst = (int)((readSize - skip) * avgBytesPerChar);
		// use shared temp char[] for writes
		CharBuffer outBuf = CharBuffer.wrap(getCharBuf(charsEst > chunkSize ? charsEst : chunkSize));

		CharBuffer resBuf = decode(decoder, ByteBuffer.wrap(buf, skip, readSize - skip), outBuf);

		// save the the write array for future use if decode() expanded it
		if(resBuf != outBuf) {
			if(outBuf.capacity() < resBuf.capacity()) {
				throw new IllegalStateException("decode returned a buffer smaller than the input buffer");
			}
			this.tmpCharBuf = resBuf.array(); // assumes array backed buffer
		}

		return resBuf.limit();
	}


	// ==== read char[]/String ====

	public char[] readChars(Reader reader) throws IOException {
		int readCnt = readReader(reader, defaultChunkSize);
		return copyBuffer(this.tmpCharBuf, readCnt);
	}


	/** Read a {@link Reader} until it is empty with the fewest array allocations possible.
	 * The reader is not closed when the method returns.
	 * The reader is read in blocks and (probably) does not need to be buffered for performance.
	 * @param reader the {@link Reader} to read from
	 * @param chunkSize the number of chars to read per chunk from the underlying
	 * file since the file size is not known
	 * @return the data found in the input stream as a byte array
	 * @throws IOException if there is an error reading the input stream
	 */
	public char[] readChars(Reader reader, int chunkSize) throws IOException {
		int readCnt = readReader(reader, chunkSize);
		return copyBuffer(this.tmpCharBuf, readCnt);
	}


	public String readString(Reader reader) throws IOException {
		int readCnt = readReader(reader, defaultChunkSize);
		return new String(this.tmpCharBuf, 0, readCnt);
	}


	public String readString(Reader reader, int chunkSize) throws IOException {
		int readCnt = readReader(reader, chunkSize);
		return new String(this.tmpCharBuf, 0, readCnt);
	}


	/** Read a {@link Reader} until it is empty with the fewest array allocations possible.
	 * @param reader the reader to read (reader is NOT closed by this method)
	 * @param chunkSize the number of chars to read per chunk from the underlying
	 * file since the file size is not known
	 * @return return number of chars written into {@link #tmpCharBuf}
	 * @throws IOException
	 */
	protected int readReader(Reader reader, int chunkSize) throws IOException {
		if(chunkSize < 2) {
			throw new IllegalArgumentException("chunkSize must be greater than 1");
		}
		int totalSize = 0;
		int variableChunkSize = chunkSize;

		// Read initially available data
		char[] buffer = getCharBuf(chunkSize);
		int readSize = reader.read(buffer, 0, buffer.length);
		totalCharReads++;
		totalSize += readSize;

		int tmpReadNext = -1;
		// read 1 char to see if there is more input (performance optimization before allocating new buffer)
		if((tmpReadNext = reader.read()) > -1) {
			totalCharReads++;
			totalSize++;
			if(totalSize > (int)(buffer.length * 0.8f) - 1) {
				// Create new array large enough to hold the original array and room for more data
				char[] temp = setCharBuf(new char[buffer.length + (totalSize < buffer.length >>> 1 ? 0 : variableChunkSize)]);
				System.arraycopy(buffer, 0, temp, 0, readSize);
				buffer = temp;
			}
			// save the 1 char read
			buffer[readSize] = (char)(tmpReadNext & 0xFFFF); // [readSize] is the index after the last byte from the initial read()

			// Continue to read additional data until no more data is available to read
			while((readSize = reader.read(buffer, totalSize, buffer.length - totalSize)) != -1) {
				totalCharReads++;
				totalSize += readSize;
				if(totalSize > (int)(buffer.length * 0.8f) - 1) {
					// increase chunk size based on algorithm to minimize number of allocation arrays
					variableChunkSize = increaseChunkSize(variableChunkSize, chunkSize);

					// Create larger buffer and move data to larger buffer
					char[] temp = buffer;
					buffer = setCharBuf(new char[buffer.length + variableChunkSize]);
					System.arraycopy(temp, 0, buffer, 0, temp.length);
				}
			}
		}

		return totalSize < 0 ? 0 : totalSize;
	}


	// ==== helpers ====

	public void readTo(InputStream is, StringBuilder dst) throws IOException {
		int readCnt = readText(is, charsetDecoder, defaultChunkSize);
		dst.append(this.tmpCharBuf, 0, readCnt);
	}


	public void readTo(InputStream is, Writer dst) throws IOException {
		int readCnt = readText(is, charsetDecoder, defaultChunkSize);
		dst.write(this.tmpCharBuf, 0, readCnt);
	}


	public void readTo(Reader reader, StringBuilder dst) throws IOException {
		int readCnt = readReader(reader, defaultChunkSize);
		dst.append(this.tmpCharBuf, 0, readCnt);
	}


	public void readTo(Reader reader, Writer dst) throws IOException {
		int readCnt = readReader(reader, defaultChunkSize);
		dst.write(this.tmpCharBuf, 0, readCnt);
	}


	protected byte[] setByteBuf(byte[] ary) {
		byteBufResizeCount++;
		tmpByteBuf = ary;
		return ary;
	}


	protected byte[] getByteBuf(int size) {
		return (tmpByteBuf != null && tmpByteBuf.length >= size) ? tmpByteBuf : setByteBuf(new byte[size]);
	}


	protected char[] setCharBuf(char[] ary) {
		charBufResizeCount++;
		tmpCharBuf = ary;
		return ary;
	}


	protected char[] getCharBuf(int size) {
		return (tmpCharBuf != null && tmpCharBuf.length >= size) ? tmpCharBuf : setCharBuf(new char[size]);
	}


	/** Returns a new byte[] copy of {@code buffer} trimmed to the specified size
	 * Slightly simpler implementation than {@link Arrays#copyOf(byte[], int)}.
	 */
	public static byte[] copyBuffer(byte[] buffer, int totalSize) {
		byte[] temp = new byte[totalSize];
		System.arraycopy(buffer, 0, temp, 0, totalSize);
		return temp;
	}


	/** Returns a new char[] copy of {@code buffer} trimmed to the specified size.
	 * Slightly simpler implementation than {@link Arrays#copyOf(char[], int)}.
	 */
	public static char[] copyBuffer(char[] buffer, int totalSize) {
		char[] temp = new char[totalSize];
		System.arraycopy(buffer, 0, temp, 0, totalSize);
		return temp;
	}


	/** Return a buffer size increased incrementally up to MAX_CHUNK_SIZE
	 */
	public static int increaseChunkSize(int currentChunkSize, int originalChunkSize) {
		if(currentChunkSize >= MAX_CHUNK_SIZE) {
			return currentChunkSize;
		}
		return currentChunkSize << 1; // size * 2
	}


	/** Code extracted from {@link CharsetDecoder} for static conversion of a {@link ByteBuffer} to a {@link CharBuffer}
	 * @param decoder the charset decoder
	 * @param in the input byte[] buffer to decode
	 * @param out the output char[] buffer to write decoded input into
	 * @param avgCharsPerByte average number of characters stored in each {@code in} byte, (e.g. ASCII = 1 char-per-byte, UTF-8 = variable estimate)
	 * @return the {@code in} {@link CharBuffer} or a new larger char buffer containing the decoded {@code in} bytes
	 * @throws CharacterCodingException
	 */
	public static CharBuffer decode(CharsetDecoder decoder, ByteBuffer in, CharBuffer out) throws CharacterCodingException {
		int n = out.remaining();
		out = out != null ? out : CharBuffer.allocate(n);

		if (n == 0 && (in.remaining() == 0)) {
			return out;
		}

		decoder.reset();
		while(true) {
			CoderResult cr = in.hasRemaining() ? decoder.decode(in, out, true) : CoderResult.UNDERFLOW;
			if (cr.isUnderflow()) {
				cr = decoder.flush(out);
			}

			if (cr.isUnderflow()) {
				break;
			}
			if (cr.isOverflow()) {
				n = 2 * n + (n < 16 ? 4 : 0); // +4 to ensure progress when n is small
				CharBuffer outNew = CharBuffer.allocate(n);
				out.flip();
				outNew.put(out);
				out = outNew;
				continue;
			}
			cr.throwException();
		}
		out.flip();
		return out;
	}


	/** Set the initial size of growth chunks when reading buffered input for all {@link #FileReadUtil()} no-arg constructor calls, must be greater than 1.
	 */
	public static void setDefaultChunkSize(int chunkSize) {
		FileReadUtil.staticDefaultChunkSize = chunkSize;
	}


	/** Set the default {@link Charset} text decoding for all {@link #FileReadUtil()} no-arg constructor calls, if null 'UTF-8' is used.
	 */
	public static void setDefaultCharset(Charset cs) {
		FileReadUtil.defaultCharset = cs;
	}


	/** Get a thread local {@link FileReadUtil} instance.
	 * These instances are not thread safe and so should only be used on one thread.
	 */
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
					"byteCache: { size: " + (tmpByteBuf != null ? tmpByteBuf.length : 0) + ", readCount: " + totalByteReads + ", cacheResizes: " + byteBufResizeCount + " }, " +
					"charCache: { size: " + (tmpCharBuf != null ? tmpCharBuf.length : 0) + ", readCount: " + totalCharReads + ", cacheResizes: " + charBufResizeCount + " } }";
		}

	}

}
