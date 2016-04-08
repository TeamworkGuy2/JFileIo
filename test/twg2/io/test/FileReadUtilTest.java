package twg2.io.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import twg2.arrays.ArrayUtil;
import twg2.io.files.FileReadUtil;

/**
 * @author TeamworkGuy2
 * @since 2015-9-19
 */
public class FileReadUtilTest {

	@Test
	public void readBytesTest() throws IOException {
		setupFileReadUtilChunkSize(64);

		byte[] bts1 = byteRange(20);
		byte[] bts2 = byteRange(115);

		byte[][] byteArys = {
				bts1,
				bts2,
				ArrayUtil.concat(bts1, bts2, bts1, bts2, bts1, bts2, bts1, bts2, bts1, bts2, bts1, bts2, bts1, bts2, bts1, bts2),
				bts2
		};

		for(byte[] bts : byteArys) {
			byte[] bytes = FileReadUtil.threadLocalInst().readBytes(new ByteArrayInputStream(bts));

			Assert.assertArrayEquals(bts, bytes);
			System.out.println();
		}
	}


	@Test
	public void readCharsTest() throws IOException {
		setupFileReadUtilChunkSize(64);

		String str1 = "this string is longer than sixteen characters";
		String str2 = "this string is much longer than the expected doubling of FileReadUtil array/buffer size when initial array/buffer is filled";

		String[] strs = {
				str1,
				str2,
				str1 + str2 + str1 + str2 + str1 + str2 + str1 + str2 + str1 + str2 + str1 + str2 + str1 + str2 + str1 + str2,
				str2,
		};

		for(String str : strs) {
			char[] chars = FileReadUtil.threadLocalInst().readChars(new StringReader(str));

			Assert.assertEquals(str, new String(chars));
			System.out.println();
		}
	}


	private static void setupFileReadUtilChunkSize(int size) {
		try {
			Field field = FileReadUtil.class.getDeclaredField("defaultChunkSize");
			field.setAccessible(true);
			field.setInt(FileReadUtil.threadLocalInst(), size);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Error setting FileReadUtil.defaultInst.defaultChunkSize to " + size, e);
		}
	}


	static byte[] byteRange(int size) {
		byte[] bytes = new byte[size];
		for(int i = 0; i < size; i++) {
			bytes[i] = (byte)i;
		}
		return bytes;
	}


	public static void main(String[] args) throws IOException {
		new FileReadUtilTest().readBytesTest();
		new FileReadUtilTest().readCharsTest();
	}

}