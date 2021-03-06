package twg2.io.fileLoading;

import java.util.Collections;
import java.util.List;

import twg2.text.stringUtils.StringSplit;

/** A file system filter containing a path, max child directory depth to search, and list of valid file extensions.
 * Parse from a string using {@link #parseFromArgs(String, String) parseFromArgs(...)}
 * @author TeamworkGuy2
 * @since 2016-1-9
 */
public class DirectorySearchInfo {
	private static int DEFAULT_MAX_RECURSIVE_DEPTH = 20;
	String path;
	int maxRecursiveDepth;
	String[] validFileExtensions;
	final List<String> validFileExtensionList;


	/**
	 * @param path
	 * @param maxRecursiveDepth
	 * @param validFileExtensions
	 */
	public DirectorySearchInfo(String path, int maxRecursiveDepth, List<String> validFileExtensions) {
		this.path = path;
		this.maxRecursiveDepth = maxRecursiveDepth;
		this.validFileExtensions = validFileExtensions.toArray(new String[validFileExtensions.size()]);
		this.validFileExtensionList = validFileExtensions;
	}


	public String getPath() {
		return path;
	}


	public int getMaxRecursiveDepth() {
		return maxRecursiveDepth;
	}


	public List<String> getValidFileExtensions() {
		return validFileExtensionList;
	}


	@Override
	public String toString() {
		return path + "=" + maxRecursiveDepth + (validFileExtensions.length > 0 ? ",[" + String.join(",", validFileExtensions) + "]" : "");
	}


	/** Parses a string into a {@link DirectorySearchInfo} object using any of the following string formats:<br>
	 * {@code 'path=depth,[fileExt,fileExt,...]'}<br>
	 * {@code 'path=depth'}<br>
	 * {@code 'path'}<br>
	 * Note: the brackets around '[fileExt]' are literal.<br>
	 * Examples:<br>
	 * {@code '../app/src/widgets=3,[java,cs,stg]'}<br>
	 * {@code 'src/images/=1'}<br>
	 * {@code '../resources'}
	 * @param str the arguments string to parse
	 * @param argName the name of the argument
	 * @return {@link DirectorySearchInfo} parsed from the arguments string
	 */
	public static final DirectorySearchInfo parseFromArgs(String str, String argName) {
		String[] values = StringSplit.split(str, "=", 2);

		if(values[0] == null) {
			throw new IllegalArgumentException("argument '" + argName + "' should contain an argument value");
		}

		List<String> validFileExtensions = Collections.emptyList();
		int maxRecursiveDepth = DEFAULT_MAX_RECURSIVE_DEPTH;
		String path = values[0];

		if(values[1] != null) {
			String[] depthAndExtensions = StringSplit.split(values[1], ',', new String[2]);
			int idx = 0;
			if(depthAndExtensions[0] != null) {
				int depth = Integer.parseInt(depthAndExtensions[0]);
				maxRecursiveDepth = depth;
				idx++;
			}

			String extensionsAryStr = depthAndExtensions[idx];
			if(extensionsAryStr != null) {
				if(!extensionsAryStr.startsWith("[") || !extensionsAryStr.endsWith("]")) {
					throw new IllegalArgumentException("second portion of '" + argName + "' value should be a '[file_extension_string,..]'");
				}
				List<String> extensions = StringSplit.split(extensionsAryStr.substring(1, extensionsAryStr.length() - 1), ',');
				validFileExtensions = extensions;
			}
		}

		DirectorySearchInfo srcInfo = new DirectorySearchInfo(path, maxRecursiveDepth, validFileExtensions);

		return srcInfo;
	}

}