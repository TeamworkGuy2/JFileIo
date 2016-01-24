package twg2.io.log;

import java.io.PrintStream;
import java.util.logging.Level;

import twg2.io.log.Logging.Formatter;

/**
 * @author TeamworkGuy2
 * @since 2016-1-23
 */
public final class Twg2Logs {
	private static volatile Twg2Logs defaultInst;
	private static final Object lock = new Object();

	private Level level;
	private PrintStream outputStream;
	private Logging.Formatter format;


	public Twg2Logs(Level level, PrintStream outputStream, Formatter format) {
		this.level = level;
		this.outputStream = outputStream;
		this.format = format;
	}


	public Logging createLog() {
		return new LoggingImpl(level, outputStream, format);
	}


	public LogWrapperImpl createLog(Class<?> cls) {
		return new LogWrapperImpl(createLog(), cls);
	}


	public static final Twg2Logs initialize(Level level, PrintStream outputStream, Logging.Formatter format) {
		synchronized (lock) {
			if(defaultInst != null) {
				throw new IllegalStateException("cannot create default instance, initialize() has already been called");
			}
			Twg2Logs inst = new Twg2Logs(level, outputStream, format);
			return inst;
		}
	}


	public static final Twg2Logs defaultInst() {
		synchronized (lock) {
			if(defaultInst == null) {
				throw new IllegalStateException("cannot access default instance, initialize() has not been called");
			}
			return defaultInst;
		}
	}

}
