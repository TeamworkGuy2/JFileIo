package twg2.io.test;

import java.util.logging.Level;

import org.junit.Test;

import twg2.io.log.LogWrapperImpl;
import twg2.io.log.LogWrapperMulti;
import twg2.io.log.Logging;
import twg2.io.log.LoggingImpl;
import twg2.io.log.LoggingMulti;

/**
 * @author TeamworkGuy2
 * @since 2015-10-3
 */
public class LogMultiTest {

	@Test
	public void loggingMultiTest() {
		String pre = "logging - ";
		LoggingImpl log1 = new LoggingImpl(Level.FINE, System.out, LoggingImpl.Format.LEVEL_AND_CLASS);
		LoggingImpl log2 = new LoggingImpl(Level.INFO, System.out, LoggingImpl.Format.LEVEL_AND_CLASS);

		LoggingMulti logMulti = new LoggingMulti(new Logging[] { log1, log2 });

		logMulti.log(Level.FINER, LogMultiTest.class, "!! " + pre + "shouldn't see");
		logMulti.log(Level.CONFIG, LogMultiTest.class, pre + "should log once");
		logMulti.log(Level.INFO, LogMultiTest.class, pre + "should log twice");
	}


	@Test
	public void logWrapperMultiTest() {
		String pre = "wrapped - ";
		LoggingImpl logging1 = new LoggingImpl(Level.FINE, System.out, LoggingImpl.Format.LEVEL_AND_CLASS);
		LoggingImpl logging2 = new LoggingImpl(Level.INFO, System.out, LoggingImpl.Format.LEVEL_AND_CLASS);

		LogWrapperImpl log1 = new LogWrapperImpl(logging1, String.class);
		LogWrapperImpl log2 = new LogWrapperImpl(logging2, Number.class);

		LogWrapperMulti logMulti = new LogWrapperMulti(new LogWrapperImpl[] { log1, log2 });

		logMulti.log(Level.FINER, "!! " + pre + "shouldn't see");
		logMulti.log(Level.CONFIG, pre + "should log once");
		logMulti.log(Level.INFO, pre + "should log twice");
	}


}
