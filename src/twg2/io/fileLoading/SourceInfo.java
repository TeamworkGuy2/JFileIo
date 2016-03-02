package twg2.io.fileLoading;

import java.util.Collections;
import java.util.List;

import twg2.text.stringUtils.StringSplit;

/**
 * @author TeamworkGuy2
 * @since 2016-1-9
 */
public class SourceInfo {
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
	public SourceInfo(String path, int maxRecursiveDepth, List<String> validFileExtensions) {
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


	/** Parses an argument string in the format:<br>
	 * {@code argName='path,[fileExt,fileExt,...];path,[fileExt,fileExt,...];...'}
	 * @param str the arguments string to parse
	 * @param argName the name of the argument
	 * @return {@link SourceInfo} parsed from the arguments string
	 */
	public static final SourceInfo parseFromArgs(String str, String argName) {
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

		SourceInfo srcInfo = new SourceInfo(path, maxRecursiveDepth, validFileExtensions);

		return srcInfo;
	}

}