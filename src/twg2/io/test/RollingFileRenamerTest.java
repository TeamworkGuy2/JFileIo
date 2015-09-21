package twg2.io.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import stringUtils.template.SingleIntTemplate;
import stringUtils.template.StringTemplateBuilder;
import twg2.io.files.RollingFileRenamer;
import checks.CheckCollections;

/**
 * @author TeamworkGuy2
 * @since 2015-7-28
 */
public class RollingFileRenamerTest {

	@Test
	public void testRollingFileRenamer() {
		{
			List<String> existingFiles = Arrays.asList(
				"file.txt",
				"file.bak1",
				"file.bak2",
				"file.bak3",
				"file.bak4"
			);
			List<String> remaining = Arrays.asList(
				"file.txt",
				"file.bak2",
				"file.bak3",
				"file.bak4"
			);
			List<String> removed = Arrays.asList(
				"file.bak4"
			);
			renameStrings(1, 3, existingFiles, remaining, removed);
		}

		{
			List<String> existingFiles = Arrays.asList(
				"file.txt",
				"file.bak1",
				"file.bak2",
				"file.bak3"
			);
			List<String> remaining = Arrays.asList(
				"file.txt",
				"file.bak1",
				"file.bak2",
				"file.bak3"
			);
			List<String> removed = Arrays.asList(
			);
			renameStrings(0, 1, existingFiles, remaining, removed);
		}

		{
			List<String> existingFiles = Arrays.asList(
				"file.txt",
				"file.bak1",
				"file.bak2",
				"file.bak3"
			);
			List<String> remaining = Arrays.asList(
				"file.txt",
				"file.bak2",
				"file.bak3"
			);
			List<String> removed = Arrays.asList(
				"file.bak2"
			);
			renameStrings(0, 2, existingFiles, remaining, removed);
		}

		{
			List<String> existingFiles = Arrays.asList(
				"file.txt",
				"file.bak1",
				"file.bak2"
			);
			List<String> remaining = Arrays.asList(
				"file.txt",
				"file.bak2",
				"file.bak3"
			);
			List<String> removed = Arrays.asList(
			);
			renameStrings(1, 3, existingFiles, remaining, removed);
		}
	}


	private static void renameStrings(int startIndex, int count, List<String> existingFiles, List<String> expectedRemaining, List<String> expectedRemoved) {
		List<String> remainingFiles = new ArrayList<>(existingFiles);
		List<String> removedFiles = new ArrayList<>();

		RollingFileRenamer<String> renamer = RollingFileRenamer.of(startIndex, count, SingleIntTemplate.of(new StringTemplateBuilder().and("file.bak").andInt()),
				(str) -> str,
				(src, dst) -> {
					//System.out.println("rename: " + src + ", to: " + dst);
					remainingFiles.remove(src);
					remainingFiles.add(dst);
					return true;
				},
				(str) -> remainingFiles.contains(str),
				(str) -> {
					//System.out.println("delete: " + str);
					boolean res = remainingFiles.remove(str);
					if(res) {
						removedFiles.add(str);
					}
					return res;
				});

		renamer.add();

		CheckCollections.assertLooseEquals("removed", expectedRemoved, removedFiles);
		CheckCollections.assertLooseEquals("remaining", expectedRemaining, remainingFiles);
	}

}
