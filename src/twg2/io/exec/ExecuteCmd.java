package twg2.io.exec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.logging.Level;

import twg2.logging.Logging;

/** A wrapper for a {@link Process}, with fields for
 * accessing the underlying Process instance, the process start time, and the process result.<br>
 * See static helper methods:<br>
 * {@link #execSync(String, Logging)}<br>
 * {@link #execAsync(String, Logging)}
 * @author TeamworkGuy2
 * @since 2015-6-14
 */
public final class ExecuteCmd {

	private static class LazyDefaultRuntime {
		static Runtime defaultRuntime = Runtime.getRuntime();
	}


	/** The result of running a {@link Process}.
	 * This class is immutable and thread safe
	 * @author TeamworkGuy2
	 * @since 2015-10-3
	 */
	public static class Result {
		final boolean completedSuccess;
		final int processTerminationValue;
		final long executionTimeNano;

		public Result(boolean completedSuccess, int processTerminationValue, long executionTimeNano) {
			this.completedSuccess = completedSuccess;
			this.processTerminationValue = processTerminationValue;
			this.executionTimeNano = executionTimeNano;
		}

		public boolean isCompletedSuccess() {
			return completedSuccess;
		}

		public int getProcessTerminationValue() {
			return processTerminationValue;
		}

		public long getExecutionTimeNano() {
			return executionTimeNano;
		}

	}
	



	ReadInputStream inputReader = null;
	ReadInputStream errorReader = null;
	Process process = null;
	boolean startedSuccess = false;
	volatile Result completedResult;
	long startTimeNano;


	public Process getProcess() {
		return process;
	}


	/** @return true if the process has been started, false if not
	 */
	public boolean isStarted() {
		return startedSuccess;
	}


	/** @return null if the process has not yet failed/completed, a {@link Result} object once the process has failed/completed
	 */
	public Result getCompletedResult() {
		return completedResult;
	}


	public long getStartTimeNano() {
		return startTimeNano;
	}


	/**
	 * @see Runtime#exec(String, String[], java.io.File)
	 */
	public Process execRuntimeCommand(String execCommand, Runtime runtime,
			OutputStream outStream, OutputStream errStream, Logging log) {
		Thread inputReaderThread = null;
		Thread errorReaderThread = null;

		try {
			if(Logging.wouldLog(log, Level.FINE)) {
				log.log(Level.FINE, ExecuteCmd.class, "exec( %s )", execCommand);
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
			if(Logging.wouldLog(log, Level.SEVERE)) {
				log.log(Level.SEVERE, ExecuteCmd.class, "Error executing: '%s'", execCommand, e);
			}
			else {
				System.err.println("Error executing: '" + execCommand + "'");
				e.printStackTrace(System.err);
			}
		} catch (Exception e) {
			if(inputReader != null) {
				inputReader.stop();
			}
			if(errorReader != null) {
				errorReader.stop();
			}

			if(Logging.wouldLog(log, Level.SEVERE)) {
				log.log(Level.SEVERE, ExecuteCmd.class, "Error waiting for exec() thread to finish running: '%s'", execCommand, e);
			}
			else {
				System.err.println("Error waiting for exec() thread to finish running: '" + execCommand + "'");
				e.printStackTrace(System.err);
			}
		}

		return this.process;
	}


	/**
	 * @return true if the command executed successfully, false if not
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging)
	 */
	public static final Result execSync(String execCommand, Runtime runtime,
			OutputStream outStream, OutputStream errStream, Logging log) {
		ExecuteCmd exeCmd = execAsync(execCommand, runtime, outStream, errStream, log);

		return finishSync(exeCmd);
	}


	/**
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging)
	 */
	public static final ExecuteCmd execAsync(String execCommand, Runtime runtime,
			OutputStream outStream, OutputStream errStream, Logging log) {
		ExecuteCmd exeCmd = new ExecuteCmd();
		exeCmd.execRuntimeCommand(execCommand, runtime, outStream, errStream, log);
		return exeCmd;
	}


	// helpers
	/**
	 * @return true if the command executed successfully, false if not
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging)
	 */
	public static final Result execSync(String execCommand, Logging log) {
		ProcessIoStreamFactory streamFactory = new ProcessIoStreamFactory.MemoryStreams();
		return execSync(execCommand, streamFactory, log);
	}


	/**
	 * @return true if the command executed successfully, false if not
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging)
	 */
	public static final Result execSync(String execCommand, ProcessIoStreamFactory streamFactory, Logging log) {
		try(OutputStream outStream = streamFactory.openOutputStream();
				OutputStream errStream = streamFactory.openErrorOutputStream()) {
			return execSync(execCommand, getDefaultRuntime(), outStream, errStream, log);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	/**
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging)
	 */
	public static final ExecuteCmd execAsync(String execCommand, Logging log) {
		ProcessIoStreamFactory streamFactory = new ProcessIoStreamFactory.MemoryStreams();
		return execAsync(execCommand, streamFactory, log);
	}


	/**
	 * @see #execRuntimeCommand(String, Runtime, OutputStream, OutputStream, Logging)
	 */
	public static final ExecuteCmd execAsync(String execCommand, ProcessIoStreamFactory streamFactory, Logging log) {
		try(OutputStream outStream = streamFactory.openOutputStream();
				OutputStream errStream = streamFactory.openErrorOutputStream()) {
			return execAsync(execCommand, getDefaultRuntime(), outStream, errStream, log);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	/** Pause the current thread until the command finishes
	 * @param exeCmd the command to execute
	 * @return an entry where the key is true if the command finished without error, false if an error occurred
	 * and the value is the process' return value (0 is normally used to indicate success)
	 */
	public static final Result finishSync(ExecuteCmd exeCmd) {
		int res = -1;
		boolean success = false;
		try {
			res = exeCmd.process.waitFor();

			exeCmd.inputReader.stop();
			exeCmd.errorReader.stop();
			// threads stop when the readers are stopped

			success = true;
		} catch (InterruptedException e) {
			System.err.println("Error waiting for process to finish");
			e.printStackTrace();
		}

		long elapsedNanos = System.nanoTime() - exeCmd.startTimeNano;
		Result result = new Result(success, res, elapsedNanos);

		exeCmd.completedResult = result;

		return result;
	}


	private static Runtime getDefaultRuntime() {
		return LazyDefaultRuntime.defaultRuntime;
	}

}
