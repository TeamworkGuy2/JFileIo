package twg2.io.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** A utility class for basic file I/O tasks such as reading the contents of an input stream
 * or recursively find all files in a directory, or shorthand methods for created buffered
 * input/output streams or readers/writers. 
 * @author TeamworkGuy2
 * @since 2014-1-1
 */
public final class FileUtility {
	private static final Predicate<Object> identityPredicate = (obj) -> true;
	@SuppressWarnings("unchecked")
	private static final <T> Predicate<T> getIdentityPredicate() { return (Predicate<T>)identityPredicate; }

	private static final String fileProtocolPrefix = "file:///";
	//private static final String fileProtocolName = "file";
	// 1 GB
	private static final long MAX_FILE_SIZE = 1073741824;


	private FileUtility() { throw new AssertionError("cannot instantiate static class FileUtility"); }


	/** Overwrite the destination file with the source file
	 * @param newFilePath the source file to use
	 * @param oldFilePath the destination file to overwrite with the contents of the source file
	 * @throws IOException if there is an error opening either of the files or copying the source file's
	 * contents into the destination file
	 */
	public static void overwriteFile(final String newFilePath, final String oldFilePath) throws IOException {
		FileChannel src = null;
		FileChannel dest = null;
		try {
			File oldFile = new File(oldFilePath).getCanonicalFile();
			File newFile = new File(newFilePath).getCanonicalFile();
			//System.out.println("Overwriting: " + oldFile.getAbsolutePath() + ", with: " + newFile.getAbsolutePath());
			if(!oldFile.isFile()) {
				throw new IOException("old file path does not exist (" + oldFilePath + ")");
			}
			if(!newFile.isFile()) {
				throw new IOException("new file path does not exist (" + newFilePath + ")");
			}
			src = new FileInputStream(newFile).getChannel();
			dest = new FileOutputStream(oldFile).getChannel();
			long transferCount = 0;
			long size = src.size();
			do {
				transferCount += dest.transferFrom(src, transferCount, size-transferCount);
			} while(transferCount < size);
		} catch (IOException e) {
			throw e;
		}
		finally {
			try {
				if(src != null) {
					src.close();
				}
				if(dest != null) {
					dest.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


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
		forFilesByFolderRecursively(file, FileUtility.<File>getIdentityPredicate(), Integer.MAX_VALUE, consumer, new ArrayList<>(), new ArrayList<>());
	}


	public static final <R> void forFilesByFolderRecursively(final File file, final int maxDepth, final BiConsumer<File, List<File>> consumer) {
		forFilesByFolderRecursively(file, FileUtility.<File>getIdentityPredicate(), Integer.MAX_VALUE, consumer, new ArrayList<>(), new ArrayList<>());
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


	/** Convert a local file path to a {@link URL}
	 * @param path the local file path to parse into a URL
	 * @return the URL corresponding to the specified file/folder path
	 * @throws IOException if there is an error creating the URL
	 */
	public static final URL toURL(final String path) throws IOException {
		URL url = null;
		if(path.startsWith(fileProtocolPrefix)) {
			url = new URL(path);
		}
		else {
			// Convert the path to a URL
			try {
				url = new URL(path);
			} catch(Exception e) {
				// If the path cannot be directly converted to a URL
				// convert it to a file first and then convert it to a URL
				File file = new File(path).getCanonicalFile();
				url = file.toURI().toURL();
			}
		}
		return url;
	}


	/** Open a buffered {@link Writer}, equivalent to:<br/>
	 * {@code new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));}
	 * @param file the file to open
	 * @param charset the charset to decode the file contents
	 * @return the buffered reader from the file
	 * @throws FileNotFoundException if the file could not be opened
	 */
	public static final BufferedWriter writerBuffered(final File file, final Charset charset)
			throws FileNotFoundException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
		return writer;
	}


	/** Open a buffered {@link Reader}, equivalent to:<br/>
	 * {@code new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));}
	 * @param file the file to open
	 * @param charset the charset to decode the file contents
	 * @return the buffered reader from the file
	 * @throws FileNotFoundException if the file could not be opened
	 */
	public static final BufferedReader readerBuffered(final File file, final Charset charset)
			throws FileNotFoundException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		return reader;
	}


	/** Open a buffered {@link OutputStream}, equivalent to:<br/>
	 * {@code new BufferedOutputStream(new FileOutputStream(file));}
	 * @param file the file to open
	 * @return the buffered output stream from the file
	 * @throws FileNotFoundException if the file could not be opened
	 */
	public static final BufferedOutputStream outputStreamBuffered(final File file) throws FileNotFoundException {
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
		return output;
	}


	/** Open a buffered {@link InputStream}, equivalent to:<br/>
	 * {@code new BufferedInputStream(new FileInputStream(file));}
	 * @param file the file to open
	 * @return the buffered input stream from the file
	 * @throws FileNotFoundException if the file could not be opened
	 */
	public static final BufferedInputStream inputStreamBuffered(final File file) throws FileNotFoundException {
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
		return input;
	}


	/** Append the specified string to the beginning or end of all files in the specified directory
	 * @param dirFile the file or directory of files to append {@code contents} to.
	 * If it is a directory, then append the string to all files in the directory and to all files
	 * in all sub-directories recursively
	 * @param content the string to append to the file
	 * @param charset the charset to use when convert {@code content} to bytes
	 * @param beginning true to append {@code content} to the beginning of the file,
	 * false to append to the end of the file
	 * @param recursively if {@code dirFile} is a directory, true recursively appends the string content to
	 * all sub files, false appends content to only the files in the specified directory
	 */
	public static final void appendToFiles(final File dirFile, final String fileNameSuffix, final String content, final Charset charset,
			final boolean beginning, final boolean recursively) {
		byte[] contentBytes = content.getBytes(charset);
		ByteBuffer newContent = null;
		if(!beginning) {
			newContent = ByteBuffer.allocate(contentBytes.length);
			newContent.put(contentBytes);
			newContent.rewind();
		}

		List<File> files = new ArrayList<File>();
		FileUtility.loadFilesRecursively(dirFile, (f) -> fileNameSuffix == null || f.getName().endsWith(fileNameSuffix), recursively ? Integer.MAX_VALUE : 1, files);

		for(File file : files) {
			long fileSize = file.length();
			if(fileSize > MAX_FILE_SIZE) {
				throw new IllegalStateException(file + " larger than max file size of " + MAX_FILE_SIZE + ", cannot modify file");
			}
			if(beginning) {
				System.out.println("prepend: " + file);
				appendToBeginning(file, contentBytes, 0, contentBytes.length);
			}
			else {
				appendToEnd(file, newContent);
			}
		}
	}


	/** Append the specified byte buffer to the end of the specified file
	 * @param file the file to append the byte buffer to
	 * @param contents the byte buffer append to the end of the file.
	 * pre-condition: the byte buffer is at its start position
	 * post-condition: the byte buffer has been rewound
	 */
	private static final void appendToEnd(final File file, final ByteBuffer contents) {
		FileChannel fileChannel = null;
		try {
			fileChannel = new FileOutputStream(file, true).getChannel();
			// Set the file channel's position to the beginning or end depending on the specified parameter
			fileChannel.write(contents);
			contents.rewind();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(fileChannel != null) {
				try {
					fileChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/** Append the specified bytes to the beginning of the specified file
	 * @param file the file to append the bytes to
	 * @param contents the bytes to write to the file
	 * @param offset the offset into the array at which to start writing
	 * copying data to the end of the file
	 * @param length the number of bytes to write to the file
	 */
	private static final void appendToBeginning(final File file, final byte[] contents,
			final int offset, final int length) {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(file);
			byte[] fileContents = FileReadUtil.defaultInst.readBytes(input);
			input.close();

			if(fileContents.length < 1) {
				return;
			}

			output = new FileOutputStream(file);
			output.write(contents, offset, length);
			output.write(fileContents);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
