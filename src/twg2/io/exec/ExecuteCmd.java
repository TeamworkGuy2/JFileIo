package twg2.io.exec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import twg2.io.files.Logging;

/** An instance of a running process
 * @author TeamworkGuy2
 * @since 2015-6-14
 */
public final class ExecuteCmd {
	private static class LazyDefaultRuntime {
		static Runtime defaultRuntime = Runtime.getRuntime();
	}


	ReadInputStream inputReader = null;
	ReadInputStream errorReader = null;
	Process process = null;
	boolean startedSuccess = false;
	boolean completedSuccess = false;
	long startTimeNano;


	public Process getProcess() {
		return process;
	}


	public long getStartTimeNano() {
		return startTimeNano;
	}


	/**
	 * @see Runtime#exec(String, String[], java.io.File)
	 */
	public Process execRuntimeCommand(String execCommand, Runtime runtime,
			OutputStream outStream, OutputStream errStream, Logging log, boolean debug) {
		Thread inputReaderThread = null;
		Thread errorReaderThread = null;

		try {
			if(debug) {
				System.out.println("exec( " + execCommand + ")");
			}
			this.process = runtime.exec(execCommand, null, null);
			this.startTimeNano = System.nanoTime();

			this.inputReader = new ReadInputStream(this.process.getInputStream(), outStream, log);
			this.errorReader = new ReadInputStream(this.process.getErrorStream(), errStream, log);
			inputReaderThread = new Thread(inputReader, "ReadInput");
			errorReaderThread = new Thread(errorReader, "ReadError");
			inputReaderThread.start();
			errorReaderThread.start();

			this.startedSuccess = true;

		} catch (IOException e) {
			System.err.println("Error executing: '" + execCommand + "'");
			e.printStackTrace();
		} catch (Exception e) {
			inputReader.stop();
			errorReader.stop();
			System.err.println("Error waiting for exec() thread to finish running: '" + execCommand + "'");
			e.printStackTrace();
		}

		return this.process;
	}


	/**
	 * @return true if the command executed successfully, false if not
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging, boolean)
	 */
	public static final boolean execSync(String execCommand, Runtime runtime,
			OutputStream outStream, OutputStream errStream, Logging log, boolean debug) {
		ExecuteCmd exeCmd = execAsync(execCommand, runtime, outStream, errStream, log, debug);

		return finishSync(exeCmd, debug);
	}


	/**
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging, boolean)
	 */
	public static final ExecuteCmd execAsync(String execCommand, Runtime runtime,
			OutputStream outStream, OutputStream errStream, Logging log, boolean debug) {
		ExecuteCmd exeCmd = new ExecuteCmd();
		exeCmd.execRuntimeCommand(execCommand, runtime, outStream, errStream, log, debug);
		return exeCmd;
	}


	// helpers
	/**
	 * @return true if the command executed successfully, false if not
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging, boolean)
	 */
	public static final boolean execSync(String execCommand, Logging log, boolean debug) {
		ProcessIoStreamFactory streamFactory = new ProcessIoStreamFactory.MemoryStreams();
		return execSync(execCommand, streamFactory, log, debug);
	}


	/**
	 * @return true if the command executed successfully, false if not
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging, boolean)
	 */
	public static final boolean execSync(String execCommand, ProcessIoStreamFactory streamFactory, Logging log, boolean debug) {
		try(OutputStream outStream = streamFactory.openOutputStream();
				OutputStream errStream = streamFactory.openErrorOutputStream()) {
			return execSync(execCommand, getDefaultRuntime(), outStream, errStream, log, debug);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	/**
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging, boolean)
	 */
	public static final ExecuteCmd execAsync(String execCommand, Logging log, boolean debug) {
		ProcessIoStreamFactory streamFactory = new ProcessIoStreamFactory.MemoryStreams();
		return execAsync(execCommand, streamFactory, log, debug);
	}


	/**
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging, boolean)
	 */
	public static final ExecuteCmd execAsync(String execCommand, ProcessIoStreamFactory streamFactory, Logging log, boolean debug) {
		try(OutputStream outStream = streamFactory.openOutputStream();
				OutputStream errStream = streamFactory.openErrorOutputStream()) {
			return execAsync(execCommand, getDefaultRuntime(), outStream, errStream, log, debug);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	/** Pause the current thread until the command finishes
	 * @param exeCmd the command to execute
	 * @param debug true to print additional information logging messages to {@link System#out}
	 * @return true if the command finished without error, false if an error occurred
	 */
	public static final boolean finishSync(ExecuteCmd exeCmd, boolean debug) {
		try {
			int res = exeCmd.process.waitFor();

			if(debug) {
				System.out.println("completed, result=" + res);
			}

			exeCmd.inputReader.stop();
			exeCmd.errorReader.stop();
			// threads stop when the readers are stopped

			exeCmd.completedSuccess = true;
		} catch (InterruptedException e) {
			System.err.println("Error waiting for process to finish");
			e.printStackTrace();
		}

		return exeCmd.completedSuccess;
	}


	private static Runtime getDefaultRuntime() {
		return LazyDefaultRuntime.defaultRuntime;
	}

}
