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

/** A utility class for basic file I/O tasks such as reading the contents of an input stream
 * or recursively find all files in a directory, or shorthand methods for created buffered
 * input/output streams or readers/writers. 
 * @author TeamworkGuy2
 * @since 2014-1-1
 */
public final class FileUtility {
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
		FileRecursion.loadFilesRecursively(dirFile, (f) -> fileNameSuffix == null || f.getName().endsWith(fileNameSuffix), recursively ? Integer.MAX_VALUE : 1, files);

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
			byte[] fileContents = FileReadUtil.threadLocalInst().readBytes(input);
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
