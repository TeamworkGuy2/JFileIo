package twg2.io.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.logging.Level;

import org.junit.Test;

import twg2.io.exec.ExecuteCmd;
import twg2.io.exec.ProcessIoStreamFactory;
import twg2.logging.LoggingImpl;
import twg2.logging.LoggingPrefixFormat;

/**
 * @author TeamworkGuy2
 * @since 2015-9-11
 */
public class ExecuteCmdTest {

	@Test
	public void testExecuteCmd() {
		String javaHomeEnvVar = System.getProperty("java.home");
		String javaExeHelpCmd = "\"" + javaHomeEnvVar + "\\bin\\java.exe\" -version";
		//String javaExeHelpCmd = "\"C:\\Users\\TeamworkGuy2\\Documents\\Cpp Programs\\Runtime Parameters\\input_parameters.exe\" -help";

		System.out.println("running: '" + javaExeHelpCmd + "'");

		LoggingImpl log = new LoggingImpl(Level.ALL, System.out, LoggingPrefixFormat.LEVEL_AND_CLASS);

		ProcessIoStreamFactory.MemoryStreams streamFactory = new ProcessIoStreamFactory.MemoryStreams();
		try(OutputStream outStream = streamFactory.openOutputStream();
				OutputStream errStream = streamFactory.openErrorOutputStream()) {

			ExecuteCmd.execSync(javaExeHelpCmd, log);

			System.out.println("\n==== output streams ====");
			streamFactory.getOutputStreams().forEach((stream) -> {
				System.out.println(new String(stream.toByteArray()));
			});

			System.out.println("\n==== error streams ====");
			streamFactory.getErrorStreams().forEach((stream) -> {
				System.out.println(new String(stream.toByteArray()));
			});
			System.out.println();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
