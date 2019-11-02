package twg2.io.files;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/** Utility class for getting and finding specific charsets from the system's supported charsets
 */
public final class CharsetUtil {

	public static void printCharsets() {
		SortedMap<String, Charset> charset = Charset.availableCharsets();
		System.out.println("Found " + charset.size() + " charsets");
		Set<String> keys = charset.keySet();
		Collection<Charset> values = charset.values();
		Iterator<Charset> valueI = values.iterator();
		Charset currentValue = null;
		if(valueI.hasNext()) {
			currentValue = valueI.next();
		}
		int i = 0;
		for(String key : keys) {
			System.out.println("Charset " + i + ": " + key + " " + currentValue);
			if(valueI.hasNext()) {
				valueI.next();
			}
			i++;
		}
	}


	/**
	 * @param resourceName
	 * @param strs
	 * @return true if the {@code strs} contained non-ASCII characters, false if all characters were ASCII
	 */
	public static boolean checkForNonAsciiChars(String resourceName, List<String> strs) {
		boolean res = false;
		for(String str : strs) {
			for(int i = 0, size = str.length(); i < size; i ++) {
				if(str.charAt(i) > 127) {
					System.err.println(resourceName +  ": " + i + "'" + str.charAt(i) + "' " + str.substring(Math.max(0, i - 10), Math.min(str.length(), i + 10)));
					res = true;
				}
			}
		}
		return res;
	}

}
