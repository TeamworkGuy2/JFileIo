package twg2.io.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import twg2.io.files.FileUtil;
import checks.CheckTask;

/**
 * @author TeamworkGuy2
 * @since 2016-3-5
 */
public class FileUtilTest {

	List<String> fileExtInputs = Arrays.asList(
		"root\\123.",
		"root/a.b",
		"_1_/_2_/123.abc",
		"_1_\\_2_\\123.123.abc",
		"123.123/abc",
		"123/123.abc"
	);

	List<String> fileNoExtExpected = Arrays.asList(
		"root\\123",
		"root/a",
		"_1_/_2_/123",
		"_1_\\_2_\\123.123",
		"123.123/abc",
		"123/123"
	);

	List<String> fileNameNoExtExpected = Arrays.asList(
		"123",
		"a",
		"123",
		"123.123",
		"abc",
		"123"
	);

	List<String> fileExtExpected = Arrays.asList(
		"",
		"b",
		"abc",
		"abc",
		"",
		"abc"
	);


	@Test
	public void removeFileExtensionTest() {
		CheckTask.assertTests(fileExtInputs, fileNoExtExpected, (s) -> FileUtil.getFileWithoutExtension(s));
	}



	@Test
	public void getFileExtensionTest() {
		CheckTask.assertTests(fileExtInputs, fileExtExpected, (s) -> FileUtil.getFileExtension(s));
	}


	@Test
	public void getFileNameWithoutExtensionText() {
		CheckTask.assertTests(fileExtInputs, fileNameNoExtExpected, (s) -> FileUtil.getFileNameWithoutExtension(s));
	}

}
