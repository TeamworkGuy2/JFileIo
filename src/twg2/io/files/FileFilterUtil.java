package twg2.io.files;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import twg2.text.stringSearch.StringCompare;

/** Contains {@link Cache} and {@link Builder} helpers for filtering files and directories
 * @author TeamworkGuy2
 * @since 2015-9-19
 */
public final class FileFilterUtil {

	private FileFilterUtil() { throw new AssertionError("cannot instantiate static class FileFilterUtil"); }


	/** Replace filesystem specific separators in a string with the current {@link File#separatorChar}
	 */
	public static String standardSeparator(String path) {
		return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
	}


	/** Replace filesystem specific separators in a string with the 'separator'
	 */
	public static String standardSeparator(String path, char separator) {
		return path.replace('\\', separator).replace('/', separator);
	}




	/** A compound {@link FileFilter} that also supports tracking matches and failed matches from
	 * each call to its {@link #getFileFilter() filter}
	 */
	public static final class Cache implements Matches<Path> {
		@SuppressWarnings("unused")
		private Predicate<Path>[] filters;
		private Predicate<Path> compoundFilter;
		private List<Path> matches;
		private List<Path> failedMatches;
		@SuppressWarnings("unused")
		private boolean trackMatches;
		@SuppressWarnings("unused")
		private boolean trackFailedMatches;


		public Cache(Collection<Predicate<Path>> filters, boolean trackMatches, boolean trackFailedMatches) {
			@SuppressWarnings("unchecked")
			Predicate<Path>[] filterAry = new Predicate[filters.size()];
			this.filters = filterAry;
			this.trackMatches = trackMatches;
			this.trackFailedMatches = trackFailedMatches;

			int i = 0;
			for(Predicate<Path> filter : filters) {
				filterAry[i] = filter;
				i++;
			}

			this.compoundFilter = (pathname) -> {
				boolean res = true;
				for(Predicate<Path> filter : filterAry) {
					res &= filter.test(pathname);
					if(!res) {
						break;
					}
				}

				if(!res && trackFailedMatches) {
					this.failedMatches.add(pathname);
				}
				else if(res && trackMatches) {
					this.matches.add(pathname);
				}

				return res;
			};

			if(trackMatches) {
				this.matches = new ArrayList<>();
			}
			if(trackFailedMatches) {
				this.failedMatches = new ArrayList<>();
			}
		}


		public Predicate<Path> getFileFilter() {
			return compoundFilter;
		}


		@Override
		public List<Path> getMatches() {
			return matches;
		}


		@Override
		public List<Path> getFailedMatches() {
			return failedMatches;
		}

	}




	/** {@link FileFilter} builder
	 */
	public static final class Builder {
		private List<Predicate<Path>> filters = new ArrayList<>();
		private boolean trackMatches = true;
		private boolean trackFailedMatches = false;


		public Builder addFilter(Predicate<Path> filter) {
			this.filters.add(filter);
			return this;
		}


		public Builder addDirectoryNameFilter(String dirName, boolean allow) {
			String name = dirName.replace('\\', File.separatorChar).replace('/', File.separatorChar);
			this.filters.add((pathname) -> {
				boolean res = pathname.toString().contains(name) == allow;
				return res;
			});
			return this;
		}


		public Builder addDirectoryNameFilters(boolean allow, String... dirNames) {
			String[] names = new String[dirNames.length];
			int i = 0;
			for(String dirName : dirNames) {
				names[i] = dirName.replace('\\', File.separatorChar).replace('/', File.separatorChar);
				i++;
			}

			this.filters.add((pathname) -> {
				boolean res = StringCompare.containsAny(pathname.toString(), names) == allow;
				return res;
			});
			return this;
		}


		public Builder addFileExtensionFilter(String extension, boolean allow) {
			this.filters.add((pathname) -> {
				boolean res = pathname.toString().endsWith(extension) == allow;
				return res;
			});
			return this;
		}


		public Builder addFileExtensionFilters(boolean allow, String... extension) {
			this.filters.add((pathname) -> {
				boolean res = StringCompare.endsWithAny(pathname.toString(), extension) == allow;
				return res;
			});
			return this;
		}


		public boolean isTrackMatches() {
			return trackMatches;
		}


		/** Used by {@link Cache}, if true, objects which pass this filter, are added to {@link Cache#getMatches()}. If false, passing objects are not tracked.
		 */
		public Builder setTrackMatches(boolean trackMatches) {
			this.trackMatches = trackMatches;
			return this;
		}


		public boolean isTrackFailedMatches() {
			return trackFailedMatches;
		}


		/** Used by {@link Cache}, if true, objects which fail this filter, are added to {@link Cache#getFailedMatches()}. If false, failed objects are not tracked.
		 */
		public Builder setTrackFailedMatches(boolean trackFailedMatches) {
			this.trackFailedMatches = trackFailedMatches;
			return this;
		}


		public Cache build() {
			return build(this.trackMatches, this.trackFailedMatches);
		}


		public Cache build(boolean trackMatches, boolean trackFailedMatches) {
			return new Cache(filters, trackMatches, trackFailedMatches);
		}


		public Cache buildOrNullIfNoFilters() {
			return buildOrNullIfNoFilters(this.trackMatches, this.trackFailedMatches);
		}


		public Cache buildOrNullIfNoFilters(boolean trackMatches, boolean trackFailedMatches) {
			return (filters != null && filters.size() > 0) ? new Cache(filters, trackMatches, trackFailedMatches) : null;
		}

	}


}
