package twg2.io.exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TeamworkGuy2
 * @since 2015-5-2
 */
public interface ProcessIoStreamFactory {

	public OutputStream openOutputStream() throws IOException;

	public OutputStream openErrorOutputStream() throws IOException;




	/**
	 * @author TeamworkGuy2
	 * @since 2015-5-2
	 */
	public static class Files implements ProcessIoStreamFactory {
		private File outputFile;
		private File errorFile;


		public Files(File outputFile, File errorFile) {
			this.outputFile = outputFile;
			this.errorFile = errorFile;
		}


		@Override
		public OutputStream openOutputStream() throws FileNotFoundException {
			return new FileOutputStream(outputFile);
		}


		@Override
		public OutputStream openErrorOutputStream() throws FileNotFoundException {
			return new FileOutputStream(errorFile);
		}
		
	}




	/**
	 * @author TeamworkGuy2
	 * @since 2015-5-2
	 */
	public static class MemoryStreams implements ProcessIoStreamFactory {
		private List<ByteArrayOutputStream> outputStreams;
		private List<ByteArrayOutputStream> errorStreams;


		public MemoryStreams() {
			this.outputStreams = new ArrayList<>();
			this.errorStreams = new ArrayList<>();
		}


		public List<ByteArrayOutputStream> getOutputStreams() {
			return outputStreams;
		}


		public List<ByteArrayOutputStream> getErrorStreams() {
			return errorStreams;
		}


		@Override
		public OutputStream openOutputStream() throws FileNotFoundException {
			ByteArrayOutputStream newOutputStream = new ByteArrayOutputStream();
			this.outputStreams.add(newOutputStream);
			return newOutputStream;
		}


		@Override
		public OutputStream openErrorOutputStream() throws FileNotFoundException {
			ByteArrayOutputStream newErrorStream = new ByteArrayOutputStream();
			this.errorStreams.add(newErrorStream);
			return newErrorStream;
		}
		
	}

}
