package twg2.io.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.junit.Assert;
import org.junit.Test;

import twg2.arrays.ArrayUtil;
import twg2.io.files.FileReadUtil;

/**
 * @author TeamworkGuy2
 * @since 2015-9-19
 */
public class FileReadUtilTest {
	private Charset defaultCharset = Charset.forName("UTF-8");
	private CharsetDecoder defaultCharsetDecoder = defaultCharset.newDecoder()
			.onMalformedInput(CodingErrorAction.REPLACE)
			.onUnmappableCharacter(CodingErrorAction.REPLACE);

	@Test
	public void readBytesTest() throws IOException {
		FileReadUtil inst = new FileReadUtil(null, 64); //FileReadUtil.threadLocalInst(); // can't set cache size

		byte[] bts0 = byteRange(0);
		byte[] bts1 = byteRange(30);
		byte[] bts2 = byteRange(128);

		byte[][] byteArys = {
				bts0,
				bts1,
				bts2,
				ArrayUtil.concat(bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2, bts1, bts2, bts2),
				bts2
		};

		for(byte[] bts : byteArys) {
			byte[] bytes = inst.readBytes(inputStream(bts, 0));
			byte[] bytes2 = inst.readBytes(inputStream(bts, 50), 64);

			Assert.assertNotEquals(getFileReadUtilByteBuf(inst), bytes);
			Assert.assertNotEquals(getFileReadUtilByteBuf(inst), bytes2);

			Assert.assertArrayEquals(bts, bytes);
			Assert.assertArrayEquals(bts, bytes2);
		}

		System.out.println("readBytes:");
		System.out.println(inst.getStats());
		System.out.println("stream array reads " + LimitedByteArrayInputStream.readCnt);
		System.out.println();
		LimitedByteArrayInputStream.readCnt = 0;
	}


	@Test
	public void readCharsTest() throws IOException {
		FileReadUtil inst = new FileReadUtil(null, 64); //FileReadUtil.threadLocalInst(); // can't set cache size

		String str0 = "";
		String str1 = "this string is longer than sixteen characters";
		String str2 = "this string is 128 chars long to match the expected FileReadUtil array/buffer size when initial array/buffer fills and doubles!!";
		String str3 = str1 + str2 + str2;

		String[] strs = {
				str0,
				str1,
				str2,
				str3 + str3 + str3 + str3 + str3 + str3 + str3 + str3 + str3 + str3,
				str2,
		};

		for(String str : strs) {
			char[] chars = inst.readChars(inputStream(str.getBytes(defaultCharset), 0));
			char[] chars2 = inst.readChars(inputStream(str.getBytes(defaultCharset), 50), 64);
			char[] chars3 = inst.readChars(new StringReader(str));
			String strRead = inst.readString(new StringReader(str), 64);
			String strRead2 = inst.readString(inputStream(str.getBytes(defaultCharset), 0), 64);
			String strRead3 = inst.readString(inputStream(str.getBytes(defaultCharset), 50), defaultCharsetDecoder, 64);

			Assert.assertNotEquals(getFileReadUtilCharBuf(inst), chars);
			Assert.assertNotEquals(getFileReadUtilCharBuf(inst), chars2);
			Assert.assertNotEquals(getFileReadUtilCharBuf(inst), chars3);

			Assert.assertEquals(str, new String(chars));
			Assert.assertEquals(str, new String(chars2));
			Assert.assertEquals(str, new String(chars3));
			Assert.assertEquals(str, strRead);
			Assert.assertEquals(str, strRead2);
			Assert.assertEquals(str, strRead3);
		}

		System.out.println("readChars:");
		System.out.println(inst.getStats());
		System.out.println("stream array reads " + LimitedByteArrayInputStream.readCnt);
		System.out.println();
		LimitedByteArrayInputStream.readCnt = 0;
	}


	private static byte[] getFileReadUtilByteBuf(FileReadUtil inst) {
		try {
			Field field = FileReadUtil.class.getDeclaredField("tmpByteBuf");
			field.setAccessible(true);
			return (byte[])field.get(inst);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Error getting FileReadUtil.tmpByteBuff", e);
		}
	}



	private static char[] getFileReadUtilCharBuf(FileReadUtil inst) {
		try {
			Field field = FileReadUtil.class.getDeclaredField("tmpCharBuf");
			field.setAccessible(true);
			return (char[])field.get(inst);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Error getting FileReadUtil.tmpCharBuff", e);
		}
	}


	static ByteArrayInputStream inputStream(byte[] buf, int limit) {
		if(limit > 0) {
			return new LimitedByteArrayInputStream(limit, buf);
		}
		return new ByteArrayInputStream(buf);
	}

	static byte[] byteRange(int size) {
		byte[] bytes = new byte[size];
		for(int i = 0; i < size; i++) {
			bytes[i] = (byte)i;
		}
		return bytes;
	}

}
