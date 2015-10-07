package twg2.io.files;

import java.io.Closeable;
import java.util.function.Supplier;
import java.util.logging.Level;

/** A simple alternative to the Java {@link java.util.logging} package, useful for small projects
 * @author TeamworkGuy2
 * @since 2015-2-7
 * @see LoggingImpl
 */
public interface Logging extends Closeable {

	/** Create a log wrapper which forwards calls to this logging instance
	 * @param type the class type of the class that will be logging information
	 * @return the new {@link LogWrapperImpl} that forwards logging calls to this logging instance
	 */
	public LogWrapper createWrapperFor(Class<?> type);

	/**
	 * @return the current logging level, values greater than this level are saved,
	 * values less than this level are ignored/dropped.
	 */
	public Level getLevel();

	/**
	 * @return the {@link Level} value associated with this log
	 */
	public int getLevelValue();

	/** Check if the specified value would be logged by this object
	 * @param level the level to compare to this object's log level
	 * @return true if the specified log level is greater than or equal to this
	 * object's log level, false if the specified log level is less than this object's log level
	 */
	public boolean wouldLog(Level level);


	public void log(Level level, Class<?> clazz, String msg);

	public void log(Level level, Class<?> clazz, String msg, Throwable thrown);

	public void log(Level level, Class<?> clazz, String msg, String str, Throwable thrown);

	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, Throwable thrown);

	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC, Throwable thrown);

	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC, String strD, Throwable thrown);

	public void log(Level level, Class<?> clazz, String msg, String str);

	public void log(Level level, Class<?> clazz, String msg, String strA, String strB);

	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC);

	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC, String strD);

	public void log(Level level, Class<?> clazz, String msg, Object param);

	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB);

	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC);

	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC, Object paramD);

	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC, Object paramD, Object paramE);

	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC, Object paramD, Object paramE, Object paramF);

	public void log(Level level, Class<?> clazz, String msg, Object param, Throwable thrown);

	public void log(Level level, Class<?> clazz, String msg, int a);

	public void log(Level level, Class<?> clazz, String msg, int a, int b);

	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c);

	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c, int d);

	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c, int d, int e);

	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c, int d, int e, int f);

	public void log(Level level, Class<?> clazz, String msg, float a);

	public void log(Level level, Class<?> clazz, String msg, float a, float b);

	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c);

	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c, float d);

	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c, float d, float e);

	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c, float d, float e, float f);

	public void log(Level level, Class<?> clazz, String msg, Object[] paramAry);

	public void log(Level level, Class<?> clazz, String msg, Object[] paramAry, Throwable thrown);

	public void log(Level level, Class<?> clazz, Supplier<String> msg);

	public void log(Level level, Class<?> clazz, Supplier<String> msg, Throwable thrown);


	public static boolean wouldLog(Logging log, Level level) {
		return log != null && log.wouldLog(level);
	}


	public static boolean wouldLog(LogWrapper log, Level level) {
		return log != null && log.wouldLog(level);
	}

}