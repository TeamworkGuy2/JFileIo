package twg2.io.test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import twg2.io.files.CharsetUtil;
import twg2.io.files.FileRecursion;
import twg2.io.files.Locations;
import twg2.text.stringSearch.StringCompare;
import twg2.text.stringUtils.StringHex;
import twg2.text.stringUtils.StringReplace;

/**
 * @author TeamworkGuy2
 * @since 2014-6-17
 */
public final class IoUtilityTest {


	private static class StringReaderCustom extends Reader {
		private String str;
		private int index;
		private int readLess;


		public StringReaderCustom(String str) {
			this.str = str;
			this.index = 0;
		}


		/** Read less characters than asked when {@link #read(char[], int, int)}
		 * or {@link #read(char[])} is called;
		 * @param readLess the number of characters less than requested to read,
		 * this logic while never caused a {@code read()} method to read less than 1 character 
		 */
		public void setReadLessThanAsked(int readLess) {
			this.readLess = readLess;
		}


		@Override
		public int read() throws IOException {
			if(index == str.length()) { return -1; }
			return str.charAt(index++);
		}


		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			if(index == str.length()) { return -1; }
			len = len-readLess < 1 ? 1 : len-readLess;
			len = len > str.length()-index ? str.length()-index : len;
			if(off+len > cbuf.length || index+len > str.length()) { throw new IndexOutOfBoundsException(); }

			for(int i = 0; i < len; i++) {
				cbuf[off+i] = str.charAt(index+i);
			}
			index += len;
			return len;
		}


		@Override
		public void close() throws IOException {
		}

	}




	private static void testLocations() {
		Locations.setProgramMain(IoUtilityTest.class);
		System.out.println("program file location: " + Locations.getProgramFileLocation());
		System.out.println("program main class name: " + Locations.getProgramMainClassName());
		System.out.println("program main class location: " + Locations.getRelativeClassPath(IoUtilityTest.class));
		System.out.println("program relative file location: " + Locations.getRelativeResourceFile());
	}


	private static void testClosestString() {
		{
			@SuppressWarnings("unchecked")
			Map.Entry<String, String>[] strs = new Map.Entry[] {
				entry("$stuff_with", "things with"),
				entry("$$identifier", "'secret string'"),
				entry("$$id", "42"),
				entry("$stuff", "things"),
				entry("alphacentauri", "galaxy"),
				entry("alpha", ".first"),
				entry("Alpha", ".First"),
				entry("a. ", "A: "),
				entry("al", "-al-"),
				entry("A", "A."),
				entry("all", "-all-"),
				entry("abc", "alphabet"),
				entry(" space", "\" \""),
				entry(" space ", "\"  \""),
				entry("$$a", "\"a\""),
				entry("$$b", "\"b\""),
				entry("	tab", "'	'")};
			String searchString = "al beta $stuff_with other stuff done";

			Arrays.sort(strs, (e1, e2) -> e1.getKey().compareTo(e2.getKey()));

			System.out.println("Sorted: " + Arrays.toString(strs));

			Map.Entry<String, String> closest = StringCompare.closestMatch(searchString, 0, strs, true);
			System.out.println("closest: " + closest);

			String replaced = StringReplace.replaceTokens(searchString, strs, false);
			System.out.println("replaced: " + replaced);
		}

		{
			@SuppressWarnings("unchecked")
			Map.Entry<String, String>[] strs = new Map.Entry[] {
	            entry("$$filesharePageUrl", "www.website.net"),
	            entry("$$fileName", "instructions.txt"),
	            entry("$$fileId", "873A2C8F3C91"),
	            entry("$$contentType", "text/plain"),
	            entry("$$contentLength", "256"),
	            entry("$$uploadTime", "23:40"),
	            entry("$$deleteTime", "22:32"),
	            entry("$$fromAddress", "abc@def.org"),
	            entry("$$toAddress", "ghi@jkl.org"),
	            entry("$$userEmailSubject", "[subject]"),
	            entry("$$userEmailBody", "[body]")
			};

			//Arrays.sort(strs, (e1, e2) -> e1.getKey().compareTo(e2.getKey()));

			String searchString = "File $$fileName from $$fromAddress";
			System.out.println("original: " + searchString);
			String replaced = StringReplace.replaceTokens(searchString, strs, true);
			System.out.println("replaced: " + replaced);
			System.out.println("\n");

			searchString = "File located at: <a href=\"$$filesharePageUrl?id=$$fileId\">$$fileId</a><br/>This file is from $$fromAddress to $$toAddress";
			System.out.println("original: " + searchString);
			replaced = StringReplace.replaceTokens(searchString, strs, true);
			System.out.println("replaced: " + replaced);
		}
	}


	private static void testToFromHex() throws IOException {
		byte[] b = new byte[] { 1, 2, 3, 5, 7, 9, 10, 15, 20, 100, 127, -128, -10, 50};
		StringReaderCustom source = new StringReaderCustom(StringHex.toHexString(b, 0, b.length));
		source.setReadLessThanAsked(1);
		byte[] result = StringHex.decodeHexStream(source);

		if(b.length != result.length) { throw new Error(); }

		for(int i = 0; i < b.length; i++) {
			if(b[i] != result[i]) { throw new Error(); }
		}

		byte[] bytes2 = "testing a sentence of 23, it's \"content\" /contains\\ 'even' _number-of-characters!? A = 65;".getBytes(Charset.forName("US-ASCII"));
		String str2Result = StringHex.toHexString(bytes2);
		byte[] bytes2Result = StringHex.decodeHexString(str2Result);
		if(b.length != result.length) { throw new Error(); }
		for(int i = 0; i < b.length; i++) {
			if(b[i] != result[i]) { throw new Error(); }
		}

		String str3 = "B93AF10D5"; // odd number of digits
		byte[] bytes3 = StringHex.decodeHexString(str3);
		String str3Result = StringHex.toHexString(bytes3);
		if(!str3Result.startsWith(str3)) { throw new Error(); }
	}


	private static void convertFilesFromCp1252ToUtf8() {
		String rootFolder = "C:/Users/TeamworkGuy2/Documents/Java/Projects";
		Charset inCharset = Charset.forName("cp1252");
		Charset outCharset = Charset.forName("UTF-8");

		FileRecursion.forEachFileByFolderRecursively(new File(rootFolder), (dir, file) -> {
			if(!file.getPath().endsWith(".java")) {
				return;
			}
			try {
				List<String> lines = Files.readAllLines(file.toPath(), inCharset);
				if(CharsetUtil.checkForNonAsciiChars(file.getPath(), lines)) {
					System.out.println("rewrite: " + file.getPath());
					Files.write(file.toPath(), lines, outCharset, StandardOpenOption.WRITE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}


	private static final <K, V> Map.Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleImmutableEntry<>(key, value);
	}


	public static void main(String[] args) throws IOException {
		//testLocations();
		//testClosestString();
		//testToFromHex();
	}

}
