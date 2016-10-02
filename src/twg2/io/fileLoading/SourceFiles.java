package twg2.io.fileLoading;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Level;

import twg2.io.files.FileVisitorUtil;
import twg2.io.json.stringify.JsonStringify;
import twg2.logging.Logging;

/** A container for a group of {@link DirectorySearchInfo} instances associated with their paths
 * @author TeamworkGuy2
 * @since 2016-2-27
 */
//@Immutable
public class SourceFiles {
	static String newline = System.lineSeparator();

	private final List<Entry<DirectorySearchInfo, List<Path>>> sources;


	/** Create a list of source file groups.
	 * @param sources Note: this list is not copied, modify at your own risk.
	 */
	public SourceFiles(List<Entry<DirectorySearchInfo, List<Path>>> sources) {
		this.sources = sources;
	}


	public List<Entry<DirectorySearchInfo, List<Path>>> getSources() {
		return sources;
	}


	/** Log all of these source files to a logging instance
	 * @param log the logging instance to log the information to
	 * @param level the level of this logging action, the message is only generated if the logging instance will accept it
	 * @param includeHeader whether to include a short description on the first line
	 */
	public void log(Logging log, Level level, boolean includeHeader) {
		if(Logging.wouldLog(log, level)) {
			StringBuilder sb = new StringBuilder();
			if(includeHeader) {
				sb.append(newline);
				sb.append("files by source:");
				sb.append(newline);
			}
			for(Entry<DirectorySearchInfo, List<Path>> src : sources) {
				sb.append(newline);
				sb.append(src.getKey());
				sb.append(newline);
				JsonStringify.joinEscape(src.getValue(), newline, sb, (f) -> f.toString());
				sb.append(newline);
			}

			log.log(level, this.getClass(), sb.toString());
		}
	}


	/** Create an instance of this class from a list of {@link DirectorySearchInfo} objects and using {@link Paths#get(String, String...) Paths.get(...)}
	 */
	public static final SourceFiles load(List<DirectorySearchInfo> directorySearchInfos) throws IOException {
		return load(directorySearchInfos, Paths::get);
	}


	/** Create an instance of this class from a list of {@link DirectorySearchInfo} objects and a custom path resolver
	 * @param directorySearchInfos the source info objects to use
	 * @param pathResolver convert a {@link DirectorySearchInfo#path} to a {@link Path}
	 */
	public static final SourceFiles load(List<DirectorySearchInfo> directorySearchInfos, Function<String, Path> pathResolver) throws IOException {
		List<Entry<DirectorySearchInfo, List<Path>>> allFiles = new ArrayList<>();

		for(DirectorySearchInfo srcInfo : directorySearchInfos) {
			List<Path> fileSet = getFilesByExtension(pathResolver.apply(srcInfo.path), srcInfo.maxRecursiveDepth, srcInfo.validFileExtensions);
			allFiles.add(entry(srcInfo, fileSet));
		}

		return new SourceFiles(Collections.unmodifiableList(allFiles));
	}


	/** Helper method to extract files matching given file extensions from a directory including child directories down to a certain depth
	 */
	public static final List<Path> getFilesByExtension(Path fileOrDir, int depth, String... extensions) throws IOException {
		FileVisitorUtil.Builder filterBldr = new FileVisitorUtil.Builder();
		if(extensions.length > 0) {
			filterBldr.getVisitFileFilter().addFileExtensionFilters(true, extensions);
		}
		filterBldr.getVisitFileFilter().setTrackMatches(true);
		FileVisitorUtil.Cache fileFilter = filterBldr.build();
		Files.walkFileTree(fileOrDir, EnumSet.noneOf(FileVisitOption.class), depth, fileFilter.getFileVisitor());
		List<Path> files = fileFilter.getVisitFileFilterCache().getMatches();
		return files;
	}


	private static <K, V> Map.Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
	}
}