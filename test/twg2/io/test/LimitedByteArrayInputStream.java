package twg2.io.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/** A special {@link ByteArrayInputStream} which limits the number of characters read even if more are available, useful for testing input stream read() loops.
 * @author TeamworkGuy2
 * @since 2019-3-28
 */
public class LimitedByteArrayInputStream extends ByteArrayInputStream {
	public static int readCnt = 0;
	private int limit;


	public LimitedByteArrayInputStream(int limit, byte[] buf) {
		super(buf);
		this.limit = limit;
	}


	public LimitedByteArrayInputStream(int limit, byte[] buf, int offset, int length) {
		super(buf, offset, length);
		this.limit = limit;
	}


	@Override
	public int read(byte[] b) throws IOException {
		return super.read(b, 0, getLen(b, 0, b.length));
	}


	@Override
	public synchronized int read(byte[] b, int off, int len) {
		return super.read(b, off, getLen(b, off, len));
	}


	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		return super.readNBytes(b, off, getLen(b, off, len));
	}


	/** Generate a len for a (byte[], off, len) tuple that is shorter than the original length and but greater than 0 and using 'limit', if possible
	 */
	private int getLen(byte[] buf, int off, int len) {
		readCnt++;

		if(len < 2 || buf.length < 2) {
			return len;
		}
		else if(limit < len) {
			return limit;
		}
		else {
			return Math.min(Math.max((int)(len * 0.75f), 1), len); // shorten to 75% between [1, len]
		}
	}

}
