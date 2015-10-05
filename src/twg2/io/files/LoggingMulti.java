package twg2.io.files;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * @author TeamworkGuy2
 * @since 2014-12-6
 */
public class LoggingMulti implements Logging, Closeable {
	private Logging[] logs;
	private int[] levels;
	private Level[] levelObjs;
	private int leastLevel;
	private Level leastLevelObj;


	/**
	 * @param logsAry the set of logs create a logging wrapper for
	 */
	@SafeVarargs
	public LoggingMulti(Logging... logsAry) {
		int logCount = logsAry.length;

		this.logs = new Logging[logCount];
		this.levels = new int[logCount];
		this.levelObjs = new Level[logCount];

		for(int i = 0; i < logCount; i++) {
			Logging logI = logsAry[i];
			this.logs[i] = logI;
			this.levelObjs[i] = logI.getLevel();
			this.levels[i] = logI.getLevelValue();
		}

		Arrays.sort(logsAry, (l1, l2) -> {
			int v1 = l1.getLevelValue();
			int v2 = l2.getLevelValue();
			return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
		});
		Arrays.sort(levelObjs, (l1, l2) -> {
			int v1 = l1.intValue();
			int v2 = l2.intValue();
			return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
		});
		Arrays.sort(levels);

		this.leastLevel = levels[0];
		this.leastLevelObj = levelObjs[0];
	}


	@Override
	public Level getLevel() {
		return leastLevelObj;
	}


	@Override
	public boolean wouldLog(Level level) {
		return level.intValue() >= this.leastLevel;
	}


	@Override
	public LogWrapper createWrapperFor(Class<?> type) {
		Class<?>[] types = new Class[this.logs.length];
		Arrays.fill(types, type);
		return new LogWrapperMulti(types, this.logs);
	}


	@Override
	public int getLevelValue() {
		return leastLevel;
	}


	@Override
	public void log(Level level, Class<?> type, String msg) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String str, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, str, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String strA, String strB, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, strA, strB, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String strA, String strB, String strC, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, strA, strB, strC, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String strA, String strB, String strC, String strD, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, strA, strB, strC, strD, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String str) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, str);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String strA, String strB) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, strA, strB);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String strA, String strB, String strC) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, strA, strB, strC);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, String strA, String strB, String strC, String strD) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, strA, strB, strC, strD);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object param) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, param);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object paramA, Object paramB) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramA, paramB);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object paramA, Object paramB, Object paramC) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramA, paramB, paramC);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramA, paramB, paramC, paramD);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD, Object paramE) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramA, paramB, paramC, paramD, paramE);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD, Object paramE, Object paramF) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramA, paramB, paramC, paramD, paramE, paramF);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object param, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, param, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, int a) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, int a, int b) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, int a, int b, int c) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, int a, int b, int c, int d) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c, d);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, int a, int b, int c, int d, int e) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c, d, e);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, int a, int b, int c, int d, int e, int f) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c, d, e, f);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, float a) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, float a, float b) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, float a, float b, float c) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, float a, float b, float c, float d) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c, d);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, float a, float b, float c, float d, float e) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c, d, e);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, float a, float b, float c, float d, float e, float f) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, a, b, c, d, e, f);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object[] paramAry) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramAry);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, String msg, Object[] paramAry, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, paramAry, thrown);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, Supplier<String> msg) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg);
			}
		}
	}


	@Override
	public void log(Level level, Class<?> type, Supplier<String> msg, Throwable thrown) {
		int levelVal = level.intValue();
		if(levelVal >= this.leastLevel) {
			for(int i = 0, size = logs.length; i < size; i++) {
				if(levels[i] > levelVal) {
					break;
				}
				logs[i].log(level, type, msg, thrown);
			}
		}
	}


	/** Closing this log wrapper closes the underlying {@link Logging} instance
	 * which may also close other {@link LogWrapper LogWrappers} associated with
	 * the underlying {@link Logging} instance.
	 */
	@Override
	public void close() throws IOException {
		for(int i = 0, size = logs.length; i < size; i++) {
			logs[i].close();
		}
	}


	@Override
	public String toString() {
		return "{ level: " + this.getLevelValue() + ", loggers: " + Arrays.toString(this.logs) + " }";
	}

}
