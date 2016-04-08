package twg2.io.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import twg2.io.files.FileUtility;
import checks.CheckTask;

/**
 * @author TeamworkGuy2
 * @since 2016-3-5
 */
public class FileUtilityTest {

	@Test
	public void removeFileExtensionTest() {
		List<String> inputs = Arrays.asList(
			"123.",
			"a.b",
			"123.abc",
			"123.123.abc"
		);

		List<String> expected = Arrays.asList(
			"123",
			"a",
			"123",
			"123.123"
		);

		CheckTask.assertTests(inputs, expected, (s) -> FileUtility.removeFileExtension(s));
	}



	@Test
	public void getFileExtensionTest() {
		List<String> inputs = Arrays.asList(
			"123.",
			"a.b",
			"123.abc",
			"123.123.abc"
		);

		List<String> expected = Arrays.asList(
			"",
			"b",
			"abc",
			"abc"
		);

		CheckTask.assertTests(inputs, expected, (s) -> FileUtility.getFileExtension(s));
	}

}
