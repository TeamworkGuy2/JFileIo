package twg2.io.files;

import java.util.function.Supplier;
import java.util.logging.Level;

/** A {@link Logging} wrapper for a specific class
 * @author TeamworkGuy2
 * @since 2015-2-7
 */
public interface LogWrapper {

	/**
	 * @return the current logging level of this wrapper, values greater than this level
	 * are saved, values less than this level are ignored/dropped.
	 */
	public Level getLevel();

	/** Check if the specified value would be logged by this log wrapper
	 * @param level the level to compare to this object's log level
	 * @return true if the specified log level is greater than or equal to this
	 * object's log level, false if the specified log level is less than this object's log level
	 */
	public boolean wouldLog(Level level);

	/** Set only the log level for this log wrapper, values greater than the specified log level are saved,
	 * values less than the specified log level are ignored/dropped.
	 * @param level the level of logging to set this object to
	 */
	public void setLevel(Level level);

	public void log(Level level, String msg);

	public void log(Level level, String msg, Throwable thrown);

	public void log(Level level, String msg, String str, Throwable thrown);

	public void log(Level level, String msg, String strA, String strB, Throwable thrown);

	public void log(Level level, String msg, String strA, String strB, String strC, Throwable thrown);

	public void log(Level level, String msg, String strA, String strB, String strC, String strD, Throwable thrown);

	public void log(Level level, String msg, String str);

	public void log(Level level, String msg, String strA, String strB);

	public void log(Level level, String msg, String strA, String strB, String strC);

	public void log(Level level, String msg, String strA, String strB, String strC, String strD);

	public void log(Level level, String msg, Object param);

	public void log(Level level, String msg, Object paramA, Object paramB);

	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC);

	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC, Object paramD);

	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC, Object paramD, Object paramE);

	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC, Object paramD, Object paramE, Object paramF);

	public void log(Level level, String msg, Object param, Throwable thrown);

	public void log(Level level, String msg, int a);

	public void log(Level level, String msg, int a, int b);

	public void log(Level level, String msg, int a, int b, int c);

	public void log(Level level, String msg, int a, int b, int c, int d);

	public void log(Level level, String msg, int a, int b, int c, int d, int e);

	public void log(Level level, String msg, int a, int b, int c, int d, int e, int f);

	public void log(Level level, String msg, float a);

	public void log(Level level, String msg, float a, float b);

	public void log(Level level, String msg, float a, float b, float c);

	public void log(Level level, String msg, float a, float b, float c, float d);

	public void log(Level level, String msg, float a, float b, float c, float d, float e);

	public void log(Level level, String msg, float a, float b, float c, float d, float e, float f);

	public void log(Level level, String msg, Object[] paramAry);

	public void log(Level level, String msg, Object[] paramAry, Throwable thrown);

	public void log(Level level, Supplier<String> msg);

	public void log(Level level, Supplier<String> msg, Throwable thrown);

}