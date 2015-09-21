package twg2.io.files;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * @author TeamworkGuy2
 * @since 2014-12-6
 */
public class LogWrapperImpl implements LogWrapper, Closeable {
	private Logging log;
	private Class<?> type;
	private int level;
	private Level levelObj;


	public LogWrapperImpl(Logging log, Class<?> type) {
		this(log, type, log.getLevel(), log.getLevelValue());
	}


	public LogWrapperImpl(Logging log, Class<?> type, Level level) {
		this(log, type, level, level.intValue());
	}


	/**
	 * @param log
	 * @param type
	 * @param level
	 */
	public LogWrapperImpl(Logging log, Class<?> type, Level levelObj, int level) {
		this.log = log;
		this.type = type;
		this.level = level;
		this.levelObj = levelObj;
	}


	@Override
	public Level getLevel() {
		return levelObj;
	}


	@Override
	public boolean wouldLog(Level level) {
		return level.intValue() >= this.level;
	}


	@Override
	public void setLevel(Level level) {
		this.level = level.intValue();
	}


	@Override
	public void log(Level level, String msg) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg);
		}
	}


	@Override
	public void log(Level level, String msg, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, thrown);
		}
	}


	@Override
	public void log(Level level, String msg, String str, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, str, thrown);
		}
	}


	@Override
	public void log(Level level, String msg, String strA, String strB, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, strA, strB, thrown);
		}
	}


	@Override
	public void log(Level level, String msg, String strA, String strB, String strC, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, strA, strB, strC, thrown);
		}
	}


	@Override
	public void log(Level level, String msg, String strA, String strB, String strC, String strD, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, strA, strB, strC, strD, thrown);
		}
	}


	@Override
	public void log(Level level, String msg, String str) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, str);
		}
	}


	@Override
	public void log(Level level, String msg, String strA, String strB) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, strA, strB);
		}
	}


	@Override
	public void log(Level level, String msg, String strA, String strB, String strC) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, strA, strB, strC);
		}
	}


	@Override
	public void log(Level level, String msg, String strA, String strB, String strC, String strD) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, strA, strB, strC, strD);
		}
	}


	@Override
	public void log(Level level, String msg, Object param) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, param);
		}
	}


	@Override
	public void log(Level level, String msg, Object paramA, Object paramB) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramA, paramB);
		}
	}


	@Override
	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramA, paramB, paramC);
		}
	}


	@Override
	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramA, paramB, paramC, paramD);
		}
	}


	@Override
	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD, Object paramE) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramA, paramB, paramC, paramD, paramE);
		}
	}


	@Override
	public void log(Level level, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD, Object paramE, Object paramF) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramA, paramB, paramC, paramD, paramE, paramF);
		}
	}


	@Override
	public void log(Level level, String msg, Object param, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, param, thrown);
		}
	}


	@Override
	public void log(Level level, String msg, int a) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a);
		}
	}


	@Override
	public void log(Level level, String msg, int a, int b) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b);
		}
	}


	@Override
	public void log(Level level, String msg, int a, int b, int c) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c);
		}
	}


	@Override
	public void log(Level level, String msg, int a, int b, int c, int d) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c, d);
		}
	}


	@Override
	public void log(Level level, String msg, int a, int b, int c, int d, int e) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c, d, e);
		}
	}


	@Override
	public void log(Level level, String msg, int a, int b, int c, int d, int e, int f) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c, d, e, f);
		}
	}


	@Override
	public void log(Level level, String msg, float a) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a);
		}
	}


	@Override
	public void log(Level level, String msg, float a, float b) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b);
		}
	}


	@Override
	public void log(Level level, String msg, float a, float b, float c) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c);
		}
	}


	@Override
	public void log(Level level, String msg, float a, float b, float c, float d) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c, d);
		}
	}


	@Override
	public void log(Level level, String msg, float a, float b, float c, float d, float e) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c, d, e);
		}
	}


	@Override
	public void log(Level level, String msg, float a, float b, float c, float d, float e, float f) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, a, b, c, d, e, f);
		}
	}


	@Override
	public void log(Level level, String msg, Object[] paramAry) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramAry);
		}
	}


	@Override
	public void log(Level level, String msg, Object[] paramAry, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, paramAry, thrown);
		}
	}


	@Override
	public void log(Level level, Supplier<String> msg) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg);
		}
	}


	@Override
	public void log(Level level, Supplier<String> msg, Throwable thrown) {
		if(level.intValue() >= this.level) {
			log.log(level, type, msg, thrown);
		}
	}


	/** Closing this log wrapper closes the underlying {@link Logging} instance
	 * which may also close other {@link LogWrapper LogWrappers} associated with
	 * the underlying {@link Logging} instance.
	 */
	@Override
	public void close() throws IOException {
		log.close();
	}

}
