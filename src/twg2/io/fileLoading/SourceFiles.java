package twg2.io.fileLoading;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import twg2.collections.tuple.Tuples;
import twg2.io.files.FileVisitorUtil;
import twg2.io.write.JsonWrite;
import twg2.logging.Logging;

//@Immutable
public class SourceFiles {
	static String newline = System.lineSeparator();

	private final List<Entry<SourceInfo, List<Path>>> sources;


	/** Create a list of source file groups
	 * @param sources
	 */
	public SourceFiles(List<Entry<SourceInfo, List<Path>>> sources) {
		this.sources = sources;
	}


	public List<Entry<SourceInfo, List<Path>>> getSources() {
		return sources;
	}


	public void log(Logging log, Level level, boolean includeHeader) {
		if(Logging.wouldLog(log, level)) {
			StringBuilder sb = new StringBuilder();
			if(includeHeader) {
				sb.append(newline);
				sb.append("files by source:");
				sb.append(newline);
			}
			for(Entry<SourceInfo, List<Path>> src : sources) {
				sb.append(newline);
				sb.append(src.getKey());
				sb.append(newline);
				JsonWrite.joinStr(src.getValue(), newline, sb, (f) -> f.toString());
				sb.append(newline);
			}

			log.log(level, this.getClass(), sb.toString());
		}
	}


	public static final SourceFiles load(List<SourceInfo> sourceInfos) throws IOException {
		List<Entry<SourceInfo, List<Path>>> allFiles = new ArrayList<>();

		for(SourceInfo srcInfo : sourceInfos) {
			List<Path> fileSet = getFilesByExtension(Paths.get(srcInfo.path), srcInfo.maxRecursiveDepth, srcInfo.validFileExtensions);
			allFiles.add(Tuples.of(srcInfo, fileSet));
		}

		return new SourceFiles(Collections.unmodifiableList(allFiles));
	}


	public static final List<Path> getFilesByExtension(Path fileOrDir, int depth, String... extensions) throws IOException {
		FileVisitorUtil.Builder fileFilterBldr = new FileVisitorUtil.Builder();
		if(extensions.length > 0) {
			fileFilterBldr.getVisitFileFilter().addFileExtensionFilters(true, extensions);
		}
		fileFilterBldr.getVisitFileFilter().setTrackMatches(true);
		FileVisitorUtil.Cache fileFilterCache = fileFilterBldr.build();
		Files.walkFileTree(fileOrDir, EnumSet.noneOf(FileVisitOption.class), depth, fileFilterCache.getFileVisitor());
		List<Path> files = fileFilterCache.getVisitFileFilterCache().getMatches();
		return files;
	}

}