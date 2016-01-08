package twg2.io.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import twg2.text.stringTemplate.SingleIntTemplate;

/**
 * @author TeamworkGuy2
 * @since 2015-7-27
 */
public class RollingFileRenamer<T> {

	private static class Impl<S> {
		private SingleIntTemplate fileNameTmpl;
		private Function<String, S> toValue;
		private Function<S, Boolean> existsFunc;
		private BiPredicate<S, S> renameFunc;
		private Predicate<S> deleteFunc;

		public Impl(SingleIntTemplate fileNameTmpl, Function<String, S> toValue, BiPredicate<S, S> renameFunc, Function<S, Boolean> existsFunc, Predicate<S> deleteFunc) {
			this.toValue = toValue;
			this.renameFunc = renameFunc;
			this.existsFunc = existsFunc;
			this.deleteFunc = deleteFunc;
			this.fileNameTmpl = fileNameTmpl;
		}

	}


	private Impl<T> data;
	private int startingIndex;
	private int count;


	public void add() {
		for(int i = startingIndex + count - 1; i >= startingIndex; i--) {
			String name = data.fileNameTmpl.compile(i);
			T value = data.toValue.apply(name);

			if(value != null && data.existsFunc.apply(value)) {
				String nextName = data.fileNameTmpl.compile(i + 1);
				T nextValue = data.toValue.apply(nextName);

				if(nextValue != null && data.existsFunc.apply(nextValue)) {
					// TODO returns failure flag, should handle
					data.deleteFunc.test(nextValue);
				}
			
				if(value != null) {
					// TODO could allow failure flag to be returned, should implement
					data.renameFunc.test(value, nextValue);
				}
			}
		}
	}


	/**
	 * @see #of(int, int, SingleIntTemplate, Function, BiPredicate, Function, Predicate)
	 */
	public static RollingFileRenamer<Path> ofPath(int startingIndex, int count, SingleIntTemplate fileNameImpl) {
		Impl<Path> data = new Impl<>(fileNameImpl, (str) -> Paths.get(str), (src, dst) -> {
			try {
				Files.move(src, dst);
			} catch (Exception e) {
				return false;
			}
			return true;
		}, (Path p) -> Files.exists(p), (Path p) -> {
			try {
				return Files.deleteIfExists(p);
			} catch (IOException ioe) {
				throwUnchecked(ioe);
			}
			return false;
		});

		RollingFileRenamer<Path> renamer = new RollingFileRenamer<>();
		renamer.data = data;
		renamer.startingIndex = startingIndex;
		renamer.count = count;
		return renamer;
	}


	/**
	 * @see #of(int, int, SingleIntTemplate, Function, BiPredicate, Function, Predicate)
	 */
	public static RollingFileRenamer<File> ofFile(int startingIndex, int count, SingleIntTemplate fileNameImpl) {
		Impl<File> data = new Impl<>(fileNameImpl, (str) -> new File(str), (File src, File dst) -> src.renameTo(dst), (File f) -> f.exists(), (File f) -> f.delete());

		RollingFileRenamer<File> renamer = new RollingFileRenamer<>();
		renamer.data = data;
		renamer.startingIndex = startingIndex;
		renamer.count = count;
		return renamer;
	}


	/**
	 * NOTE: count + 1 values are saved, since the last/max value is renamed to last/max + 1, not deleted
	 * @param startingIndex the initial index to start checking and renaming from
	 * @param count the number of values to count through when renaming (see note above, count + 1 values end up being saved)
	 * @param fileNameImpl the string template that defines how the file names are formatted and where the numeric 'rolling' portion of the name is
	 * @param toValue a function that converts a string (i.e. file name) to a value (e.g. {@link File} or {@link Path}
	 * @param renameFunc a function to rename the first parameter to the second parameter
	 * @param existsFunc a function to check if a value exists
	 * @param deleteFunc a function to delete a value
	 * @return a rolling file renamer that provides an {@link #add()} method to add values to the rolling list of file names
	 */
	public static <R> RollingFileRenamer<R> of(int startingIndex, int count, SingleIntTemplate fileNameImpl,
			Function<String, R> toValue, BiPredicate<R, R> renameFunc, Function<R, Boolean> existsFunc, Predicate<R> deleteFunc) {
		Impl<R> data = new Impl<>(fileNameImpl, toValue, renameFunc, existsFunc, deleteFunc);
		RollingFileRenamer<R> renamer = new RollingFileRenamer<>();
		renamer.data = data;
		renamer.startingIndex = startingIndex;
		renamer.count = count;
		return renamer;
	}


	private static RuntimeException throwUnchecked(Object obj) {
		return (RuntimeException)obj;
	}

}
