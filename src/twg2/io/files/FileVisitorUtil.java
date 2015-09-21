package twg2.io.files;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author TeamworkGuy2
 * @since 2015-9-19
 */
public final class FileVisitorUtil {


	/** A container for a {@link FileVisitor} and {@link FileFilterUtil.Cache} filters for
	 * filtering and tracking visited directories and files
	 */
	public static final class Cache {
		FileVisitor<Path> fileVisitor;
		FileFilterUtil.Cache preVisitDirFilter;
		FileFilterUtil.Cache visitFileFilter;


		public Cache(FileVisitor<Path> fileVisitor, FileFilterUtil.Cache preVisitDirFilter, FileFilterUtil.Cache visitFileFilter) {
			this.fileVisitor = fileVisitor;
			this.preVisitDirFilter = preVisitDirFilter;
			this.visitFileFilter = visitFileFilter;
		}


		public FileFilterUtil.Cache getPreVisitDirectoryFilterCache() {
			return preVisitDirFilter;
		}


		public FileFilterUtil.Cache getVisitFileFilterCache() {
			return visitFileFilter;
		}


		public FileVisitor<Path> getFileVisitor() {
			return fileVisitor;
		}

	}




	/** A {@link FileVisitorUtil.Cache} builder
	 */
	public static final class Builder {
		private FileVisitResult defaultPreVisitDirRes = FileVisitResult.CONTINUE;
		private BiFunction<Path, BasicFileAttributes, FileVisitResult> preVisitDirFunc;
		private FileFilterUtil.Builder preVisitDirFilterBldr;

		private FileVisitResult defaultVisitFileRes = FileVisitResult.CONTINUE;
		private BiFunction<Path, BasicFileAttributes, FileVisitResult> visitFileFunc;
		private FileFilterUtil.Builder visitFileFilterBldr;

		private FileVisitResult defaultVisitFileFailedRes = FileVisitResult.CONTINUE;
		private BiFunction<Path, IOException, FileVisitResult> visitFileFailedFunc;

		private FileVisitResult defaultVisitDirFailedRes = FileVisitResult.CONTINUE;
		private BiFunction<Path, IOException, FileVisitResult> postVisitDirFunc;


		public Builder() {
		}


		public FileFilterUtil.Builder getPreVisitDirectoryFilter() {
			if(preVisitDirFilterBldr == null) {
				preVisitDirFilterBldr = new FileFilterUtil.Builder();
			}
			return preVisitDirFilterBldr;
		}


		public FileFilterUtil.Builder getVisitFileFilter() {
			if(visitFileFilterBldr == null) {
				visitFileFilterBldr = new FileFilterUtil.Builder();
			}
			return visitFileFilterBldr;
		}


		public FileVisitResult getPreVisitDirectoryDefaultResult() {
			return defaultPreVisitDirRes;
		}


		public void setPreVisitDirectoryDefaultResult(FileVisitResult defaultPreVisitDirRes) {
			this.defaultPreVisitDirRes = defaultPreVisitDirRes;
		}


		public FileVisitResult getVisitFileDefaultResult() {
			return defaultVisitFileRes;
		}


		public void setVisitFileDefaultResult(FileVisitResult defaultVisitFileRes) {
			this.defaultVisitFileRes = defaultVisitFileRes;
		}


		public FileVisitResult getVisitFileFailedDefaultResult() {
			return defaultVisitFileFailedRes;
		}


		public void setVisitFileFailedDefaultResult(FileVisitResult defaultVisitFileFailedRes) {
			this.defaultVisitFileFailedRes = defaultVisitFileFailedRes;
		}


		public FileVisitResult getVisitDirectoryFailedDefaultResult() {
			return defaultVisitDirFailedRes;
		}


		public void setVisitDirectoryFailedDefaultResult(FileVisitResult defaultVisitDirFailedRes) {
			this.defaultVisitDirFailedRes = defaultVisitDirFailedRes;
		}


		// ==== build ====
		public FileVisitorUtil.Cache build() {
			FileFilterUtil.Cache preDirFilterCache = preVisitDirFilterBldr != null ? preVisitDirFilterBldr.buildOrNullIfNoFilters() : null;
			Predicate<Path> preDirFilter = preDirFilterCache != null ? preDirFilterCache.getFileFilter() : null;

			FileFilterUtil.Cache fileFilterCache = visitFileFilterBldr != null ? visitFileFilterBldr.buildOrNullIfNoFilters() : null;
			Predicate<Path> fileFilter = fileFilterCache != null ? fileFilterCache.getFileFilter() : null;
			FileVisitor<Path> visitor;

			if(preVisitDirFunc == null && visitFileFunc == null && visitFileFailedFunc == null && postVisitDirFunc == null) {
				visitor = buildWithDefaultFileVisitResult(preDirFilter, fileFilter);
			}
			else {
				visitor = buildWithFuncs(preDirFilter, fileFilter);
			}

			FileVisitorUtil.Cache inst = new FileVisitorUtil.Cache(visitor, preDirFilterCache, fileFilterCache);
			return inst;
		}


		FileVisitor<Path> buildWithDefaultFileVisitResult(Predicate<Path> preDirFilter, Predicate<Path> fileFilter) {
			return new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if(preDirFilter != null) {
						boolean res = preDirFilter.test(dir);
						return (res ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE);
					}
					return defaultPreVisitDirRes;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(fileFilter != null) {
						fileFilter.test(file);
					}
					return defaultVisitFileRes;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return defaultVisitFileFailedRes;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return defaultVisitDirFailedRes;
				}
			};
		}


		FileVisitor<Path> buildWithFuncs(Predicate<Path> preDirFilter, Predicate<Path> fileFilter) {
			return new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if(preDirFilter != null) {
						boolean res = preDirFilter.test(dir);
						return preVisitDirFunc != null ? preVisitDirFunc.apply(dir, attrs) : (res ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE);
					}
					return preVisitDirFunc != null ? preVisitDirFunc.apply(dir, attrs) : defaultPreVisitDirRes;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(fileFilter != null) {
						fileFilter.test(file);
					}
					return visitFileFunc != null ? visitFileFunc.apply(file, attrs) : defaultVisitFileRes;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return visitFileFailedFunc != null ? visitFileFailedFunc.apply(file, exc) : defaultVisitFileFailedRes;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return postVisitDirFunc != null ? postVisitDirFunc.apply(dir, exc) : defaultVisitDirFailedRes;
				}
			};
		}


		// ==== static helpers ====
		/** @return a {@link FileVisitor} function which always returns {@link FileVisitResult#CONTINUE}
		 */
		public static BiFunction<Path, BasicFileAttributes, FileVisitResult> createVisitContinueFunc() {
			return (file, attrs) -> FileVisitResult.CONTINUE;
		}


		/** @return a {@link FileVisitor} failure function which always returns {@link FileVisitResult#CONTINUE}
		 */
		public static BiFunction<Path, IOException, FileVisitResult> createVisitFailedContinueFunc() {
			return (file, exc) -> FileVisitResult.CONTINUE;
		}

	}

}
