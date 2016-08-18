package twg2.io.files;

import java.io.File;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** For the pre Java 1.7 days, most of this is obsolete now with {@link FileVisitor} and {@link Files#walkFileTree(Path, FileVisitor)}
 * @author TeamworkGuy2
 * @since 2014-1-1
 */
public class FileRecursion {
	private static final Predicate<Object> identityPredicate = (obj) -> true;
	@SuppressWarnings("unchecked")
	private static final <T> Predicate<T> getIdentityPredicate() { return (Predicate<T>)identityPredicate; }


	/** Recursively load all files and sub files in a directory.
	 * @param file the directory or file to search for sub-files
	 * @param dst the destination to store the list of found files in
	 * Each map entry represents key=file-size and value=last-modified
	 */
	public static final void loadFilesRecursively(final File file, final List<File> dst) {
		loadFilesRecursively(file, Integer.MAX_VALUE, dst);
	}


	public static final void loadFilesRecursively(final File file, Predicate<File> filter, final List<File> dst) {
		loadFilesRecursively(file, filter, Integer.MAX_VALUE, dst);
	}


	/** Recursively load all files and sub files in a directory.
	 * @param file the directory or file to search for sub-files
	 * @param dst the destination to store the list of found files in
	 * Each map entry represents key=file-size and value=last-modified
	 */
	public static final void loadFilesRecursively(final File file, final int maxDepth, final List<File> dst) {
		loadFilesRecursively(file, getIdentityPredicate(), maxDepth, dst);
	}


	/** Recursively load all files in a specific directory and all child directories up
	 * to {@code maxDepth} levels deep.
	 * @param file the directory or file to search for sub-files
	 * @param filter a filter to determine if files are added to {@code dst}
	 * @param maxDepth the maximum depth of the recursive search for files.
	 * negative values store nothing.<br>
	 * {@code 0} stores {@code file} if file is a file, not a directory.<br>
	 * {@code 1} stores all files in the directory.<br>
	 * {@code 2} stores all the files in this directory and all the files in this directory's sub directories.<br>
	 * {@code N + 1} iterates over directories one level deeper than {@code N}.
	 * @param dst the destination to add found files to
	 */
	public static final void loadFilesRecursively(final File file, Predicate<File> filter, final int maxDepth, final List<File> dst) {
		if(maxDepth < 0) { return; }
		// If the file is a file, count the lines in the file
		if(file.isFile() && filter.test(file)) {
			dst.add(file);
		}
		// If the file is directory, call the directory's sub files and directories recursively
		else if(file.isDirectory()) {
			if(maxDepth < 1) { return; }
			for(File subFile : file.listFiles()) {
				loadFilesRecursively(subFile, filter, maxDepth - 1, dst);
			}
		}
	}


	/** Recursively load all files in a specific directory and all child directories.
	 * @param file the directory or file to search for sub-files
	 * @param dst the destination to store the list of found files in
	 * Each map entry represents key=folder and value=files-in-folder
	 */
	public static final <R> void loadFilesByFolderRecursively(final File file, Function<File, R> fileProcessor,
			Supplier<List<R>> listFactory, final Map<File, List<R>> dst) {
		loadFilesByFolderRecursively(file, Integer.MAX_VALUE, fileProcessor, listFactory, dst);
	}


	public static final <R> void loadFilesByFolderRecursively(final File file, final int maxDepth,
			Function<File, R> fileProcessor, Supplier<List<R>> listFactory, final Map<File, List<R>> dst) {
		loadFilesByFolderRecursively(file, getIdentityPredicate(), maxDepth, fileProcessor, listFactory, dst);
	}


	public static final <R> void loadFilesByFolderRecursively(final File file, final Predicate<File> filter,
			Function<File, R> fileProcessor, Supplier<List<R>> listFactory, final Map<File, List<R>> dst) {
		loadFilesByFolderRecursively(file, filter, Integer.MAX_VALUE, fileProcessor, listFactory, dst);
	}


	/** Recursively load all files in a specific directory and all child directories up
	 * to {@code maxDepth} levels deep.
	 * @param file the directory or file to search for sub-files
	 * @param maxDepth the maximum depth of the recursive search for files.
	 * negative values store nothing.<br>
	 * {@code 0} stores {@code file} if file is a file, not a directory.<br>
	 * {@code 1} stores all files in the directory.<br>
	 * {@code 2} stores all the files in this directory and all the files in this directory's sub directories.<br>
	 * {@code N + 1} iterates over directories one level deeper than {@code N}.
	 * @param dst the destination to add found files to
	 */
	public static final <R> void loadFilesByFolderRecursively(final File file, final Predicate<File> filter, final int maxDepth,
			Function<File, R> fileProcessor, Supplier<List<R>> listFactory, final Map<File, List<R>> dst) {
		if(maxDepth < 0) { return; }
		// If the file is directory, call the directory's sub files and directories recursively
		else if(file.isDirectory()) {
			if(maxDepth < 1) { return; }

			List<R> fileList = dst.get(file);
			if(fileList == null) {
				fileList = listFactory.get();
				dst.put(file, fileList);
			}

			for(File subFile : file.listFiles()) {
				if(subFile.isDirectory()) {
					loadFilesByFolderRecursively(subFile, maxDepth - 1, fileProcessor, listFactory, dst);
				}
				else if(subFile.isFile() && filter.test(subFile)) {
					fileList.add(fileProcessor.apply(subFile));
				}
			}
		}
	}


	/** Get all files in a folder and all sub-folders of infinite depth
	 * @see #forEachFileByFolderRecursively(File, Predicate, int, BiConsumer)
	 */
	public static final <R> void forEachFileByFolderRecursively(final File file, final BiConsumer<File, File> consumer) {
		forEachFileByFolderRecursively(file, getIdentityPredicate(), Integer.MAX_VALUE, consumer);
	}


	/** Get all files in a folder and all sub-folders of infinite depth
	 * @see #forEachFileByFolderRecursively(File, Predicate, int, BiConsumer)
	 */
	public static final <R> void forEachFileByFolderRecursively(final File file, final int maxDepth, final BiConsumer<File, File> consumer) {
		forEachFileByFolderRecursively(file, getIdentityPredicate(), maxDepth, consumer);
	}


	/** Recursively loop all files in a specific directory and all child directories.
	 * @param file the directory or file to search for sub-files
	 * @param maxDepth the maximum depth of the recursive search for files.
	 * negative values iterate over nothing.<br>
	 * {@code 0} iterates over {@code file} if file is a file, not a directory.<br>
	 * {@code 1} iterates over all files in the directory.<br>
	 * {@code 2} iterates over all the files in the directory and all the files in the directory's immediate sub directories.<br>
	 * {@code N + 1} iterates over directories one level deeper than {@code N}.
	 * @param consumer the consumer is called with each folder as the first and each sub-file as the second argument
	 */
	public static final <R> void forEachFileByFolderRecursively(final File file, final Predicate<File> filter, final int maxDepth, final BiConsumer<File, File> consumer) {
		if(maxDepth < 0) { return; }
		// If the file is directory, call the directory's sub files and directories recursively
		if(file.isDirectory()) {
			for(File subFile : file.listFiles()) {
				if(subFile.isDirectory()) {
					if(maxDepth < 1) { return; }
					forEachFileByFolderRecursively(subFile, filter, maxDepth - 1, consumer);
				}
				else if(subFile.isFile() && filter.test(subFile)) {
					consumer.accept(file, subFile);
				}
			}
		}
	}


	public static final <R> void forFilesByFolderRecursively(final File file, final BiConsumer<File, List<File>> consumer) {
		forFilesByFolderRecursively(file, FileRecursion.<File>getIdentityPredicate(), Integer.MAX_VALUE, consumer, new ArrayList<>(), new ArrayList<>());
	}


	public static final <R> void forFilesByFolderRecursively(final File file, final int maxDepth, final BiConsumer<File, List<File>> consumer) {
		forFilesByFolderRecursively(file, FileRecursion.<File>getIdentityPredicate(), Integer.MAX_VALUE, consumer, new ArrayList<>(), new ArrayList<>());
	}


	public static final <R> void forFilesByFolderRecursively(final File file, final Predicate<File> filter, final int maxDepth, final BiConsumer<File, List<File>> consumer) {
		forFilesByFolderRecursively(file, filter, Integer.MAX_VALUE, consumer, new ArrayList<>(), new ArrayList<>());
	}


	private static final <R> void forFilesByFolderRecursively(final File file, final Predicate<File> filter, final int maxDepth, final BiConsumer<File, List<File>> consumer,
			List<File> tmpFileCache, List<File> tmpDirCache) {
		if(maxDepth < 0) { return; }
		// If the file is directory, call the directory's sub files and directories recursively
		tmpFileCache.clear();
		tmpDirCache.clear();
		if(file.isDirectory()) {
			for(File subFile : file.listFiles()) {
				if(subFile.isDirectory()) {
					if(maxDepth < 1) { continue; }
					tmpDirCache.add(subFile);
				}
				else if(subFile.isFile() && filter.test(subFile)) {
					tmpFileCache.add(subFile);
				}
			}
			if(tmpFileCache.size() > 0) {
				consumer.accept(file, tmpFileCache);
			}
			if(tmpDirCache.size() > 0) {
				List<File> dirCacheCopy = new ArrayList<>(tmpDirCache);
				for(int i = 0, size = dirCacheCopy.size(); i < size; i++) {
					forFilesByFolderRecursively(dirCacheCopy.get(i), filter, maxDepth - 1, consumer, tmpFileCache, tmpDirCache);
				}
			}
		}
	}

}
