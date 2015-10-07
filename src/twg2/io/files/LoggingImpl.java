package twg2.io.files;

import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A logging utility for logging message.
 * More controlled than System.out, I haven't quite gotten around to using {@link Logger}
 * @author TeamworkGuy2
 * @since 2013-8-10
 */
public final class LoggingImpl implements Logging {

	public static enum Format {
		NONE,
		LEVEL,
		LEVEL_AND_CLASS,
		DATETIME_LEVEL_AND_CLASS;
	}


	private static final DateTimeFormatter dateFormater = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	private boolean doPrintLog = true;
	private PrintStream output;
	private Format format;
	private PrintStream output2;
	private Format format2;
	private PrintStream output3;
	private Format format3;
	private Level level;
	private int levelValue;
	private ArrayList<Level> levels;
	private ArrayList<Class<?>> classes;
	private ArrayList<String> messages;
	private ArrayList<Throwable> errors;
	private ArrayList<Integer> paramStartIndex;
	private ArrayList<Object> params;


	/**
	 * @param level the {@link Level} of messages to log
	 * @param outputStream the output stream to print messages to
	 */
	public LoggingImpl(Level level, PrintStream outputStream, Format format) {
		this(level, outputStream, format, null, null, null, null);
	}


	public LoggingImpl(Level level, PrintStream outputStream, Format format, PrintStream outputStream2, Format format2) {
		this(level, outputStream, format, outputStream2, format2, null, null);
	}


	public LoggingImpl(Level level, PrintStream outputStream, Format format, PrintStream outputStream2, Format format2, PrintStream outputStream3, Format format3) {
		this.level = level;
		this.output = outputStream;
		this.format = format;
		this.output2 = outputStream2;
		this.format2 = format2;
		this.output3 = outputStream3;
		this.format3 = format3;
		if(outputStream == null) {
			doPrintLog = false;
		}
		this.levelValue = level.intValue();
		levels = new ArrayList<Level>();
		classes = new ArrayList<Class<?>>();
		messages = new ArrayList<String>();
		errors = new ArrayList<Throwable>();
		paramStartIndex = new ArrayList<Integer>();
		params = new ArrayList<Object>();
	}


	@Override
	public LogWrapper createWrapperFor(Class<?> type) {
		return new LogWrapperImpl(this, type);
	}


	@Override
	public Level getLevel() {
		return level;
	}


	@Override
	public int getLevelValue() {
		return levelValue;
	}


	@Override
	public boolean wouldLog(Level level) {
		return level.intValue() >= this.levelValue;
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				paramStartIndex.add(params.size());
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String str, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				paramStartIndex.add(params.size());
				params.add(str);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				paramStartIndex.add(params.size());
				params.add(strA);
				params.add(strB);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				paramStartIndex.add(params.size());
				params.add(strA);
				params.add(strB);
				params.add(strC);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC, String strD,
			Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				paramStartIndex.add(params.size());
				params.add(strA);
				params.add(strB);
				params.add(strC);
				params.add(strD);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String str) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				params.add(str);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String strA, String strB) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(strA);
				parameters.add(strB);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(strA);
				parameters.add(strB);
				parameters.add(strC);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, String strA, String strB, String strC, String strD) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(strA);
				parameters.add(strB);
				parameters.add(strC);
				parameters.add(strD);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object param) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				params.add(param);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(paramA);
				parameters.add(paramB);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(paramA);
				parameters.add(paramB);
				parameters.add(paramC);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(paramA);
				parameters.add(paramB);
				parameters.add(paramC);
				parameters.add(paramD);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD, Object paramE) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				ArrayList<Object> parameters = params;
				parameters.add(paramA);
				parameters.add(paramB);
				parameters.add(paramC);
				parameters.add(paramD);
				parameters.add(paramE);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object paramA, Object paramB, Object paramC,
			Object paramD, Object paramE, Object paramF) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				ArrayList<Object> parameters = params;
				parameters.add(paramA);
				parameters.add(paramB);
				parameters.add(paramC);
				parameters.add(paramD);
				parameters.add(paramE);
				parameters.add(paramF);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object param, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				paramStartIndex.add(params.size());
				params.add(param);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, int a) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				params.add(a);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, int a, int b) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c, int d) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				parameters.add(d);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c, int d, int e) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				parameters.add(d);
				parameters.add(e);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, int a, int b, int c, int d, int e, int f) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				parameters.add(d);
				parameters.add(e);
				parameters.add(f);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, float a) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				paramStartIndex.add(params.size());
				params.add(a);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, float a, float b) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c, float d) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				parameters.add(d);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c, float d, float e) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				parameters.add(d);
				parameters.add(e);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, float a, float b, float c, float d, float e, float f) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				parameters.add(a);
				parameters.add(b);
				parameters.add(c);
				parameters.add(d);
				parameters.add(e);
				parameters.add(f);
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object[] paramAry) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(null);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				for(int i = 0, size = paramAry.length; i < size; i++) {
					parameters.add(paramAry[i]);
				}
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, String msg, Object[] paramAry, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg);
				errors.add(thrown);
				ArrayList<Object> parameters = params;
				paramStartIndex.add(parameters.size());
				for(int i = 0, size = paramAry.length; i < size; i++) {
					parameters.add(paramAry[i]);
				}
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, Supplier<String> msg) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg.get());
				errors.add(null);
				paramStartIndex.add(params.size());
				output();
			}
		}
	}


	@Override
	public void log(Level level, Class<?> clazz, Supplier<String> msg, Throwable thrown) {
		if(level.intValue() >= this.levelValue) {
			synchronized(this) {
				levels.add(level);
				classes.add(clazz);
				messages.add(msg.get());
				errors.add(thrown);
				paramStartIndex.add(params.size());
				output();
			}
		}
	}

	@Override
	public void close() throws IOException {
		doPrintLog = false;
		output.close();
	}


	@Override
	public String toString() {
		return "{ level: " + this.getLevelValue() + ", out: " + output + (output2 != null ? ", output2: " + output2 : "") + (output3 != null ? ", output3: " + output3 : "") + " }";
	}


	private void output() {
		output(output, format);
		if(output2 != null) {
			output(output2, format2);
		}
		if(output3 != null) {
			output(output3, format3);
		}
	}


	private void output(PrintStream out, Format format) {
		if(!doPrintLog) {
			return;
		}

		int index = levels.size() - 1;

		switch(format) {
		case DATETIME_LEVEL_AND_CLASS:
			out.printf("[%s] %d, [%s] ", dateFormater.format(Instant.now()), levels.get(index).intValue(), classes.get(index).getCanonicalName());
			break;
		case LEVEL:
			out.printf("%d, ", levels.get(index).intValue());
			break;
		case LEVEL_AND_CLASS:
			out.printf("%d, [%s] ", levels.get(index).intValue(), classes.get(index).getCanonicalName());
			break;
		case NONE:
			break;
		default:
			throw new IllegalArgumentException("unknown " + Format.class.getSimpleName() + " enum value '" + format + "'");
		}

		int startIndex = paramStartIndex.get(index);
		int paramCount = (index == paramStartIndex.size()-1) ? params.size()-startIndex
				: paramStartIndex.get(index+1)-startIndex;
		Object[] args = new Object[paramCount];
		for(int i = startIndex, a = 0; i < startIndex+paramCount; i++, a++) {
			args[a] = params.get(i);
		}
		out.printf(messages.get(index), args);
		if(errors.get(index) != null) {
			out.printf(", error: ");
			errors.get(index).printStackTrace(out);
		}
		out.println();
	}

}
