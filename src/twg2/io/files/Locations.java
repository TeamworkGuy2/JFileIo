package twg2.io.files;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import twg2.io.log.LogWrapperImpl;

/** This class is static. This is an unfortunate side affect of needing to
 * use this class' methods from nearly anywhere in a project.
 * Before calling any methods in the class, call {@link #setProgramMain(Class)} with the
 * class that will be used as the main class for these utility methods.
 * @author TeamworkGuy2
 * @since 2014-4-9
 * @see LogWrapperImpl
 */
public final class Locations {
	/** The string path of the base folder of the currently executing application ending with an '/' */
	private static final Object lock = new Object();
	private static Class<?> programMainClass;

	/**
	 * @author TeamworkGuy2
	 * @since 2014-4-9
	 */
	private enum Data {
		;

		private static final DataInst inst;

		private static class DataInst {
			private final File resourceLocation;
			private final Path resourceLocationPath;
			private final String resourceLocationStr;
			private final File codeLocation;
			private final String codeLocationStr;
			private final boolean isJar;
			@SuppressWarnings("unused")
			private final Class<?> mainClass;
			private final String mainClassName;

			public DataInst(Class<?> clazz) {
				mainClass = clazz;
				mainClassName = clazz.getCanonicalName();
				// Determine the location of the class' code
				File tempFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsoluteFile();
				isJar = tempFile.isFile() && tempFile.getName().toLowerCase().endsWith(".jar");
				// If the code is a class file in a directory, this gets the parent directory which makes sense since
				// most projects that use class files store them in a classes directory
				// If the code is in a JAR file, this gets the directory that the JAR is located in
				codeLocation = tempFile;
				codeLocationStr = checkFileString(tempFile.getPath());
				resourceLocation = tempFile.getParentFile();
				resourceLocationPath = tempFile.getParentFile().toPath();
				resourceLocationStr = checkFileString(tempFile.getParent());
			}
		}

		static {
			// Ensure that the locations' main class reference has been initialized by setProgramMain()
			Class<?> clazz = null;
			boolean error = false;
			synchronized(Locations.lock) {
				clazz = Locations.programMainClass;
				if(clazz == null) {
					error = true;
				}
			}
			if(error) {
				// This error is unrecoverable, because if setProgramMain() is called after this error
				// it will try to access this class' instance variables and will throw another
				// error (NoClassDefFound) because this class could not be initialized properly
				throw new LinkageError("locations initialized prematurely, call setProgramMain() first");
			}

			inst = new DataInst(clazz);
		}


		private static final String checkFileString(String filePath) {
			// Ensure the path ends with a forward slash '/' so that relative paths
			// that are appended to this path are separated by a forward slash '/'
			if(File.separatorChar != '/' && filePath.indexOf(File.separatorChar) > -1) {
				filePath = filePath.replace(File.separatorChar, '/');
			}
			if(filePath.charAt(filePath.length()-1) != '/') {
				filePath = filePath + '/';
			}
			return filePath;
		}

	}


	private Locations() { throw new AssertionError("cannot instantiate Locations"); }


	/** Set the full qualifying Java name of the class that contains {@code main()}.
	 * For example: "java.io.File".
	 * @param clazz the class containing {@code main()}
	 */
	public static final void setProgramMain(Class<?> clazz) {
		boolean alreadySet = false;
		synchronized(lock) {
			if(programMainClass == null) {
				programMainClass = clazz;
				// Initialize the inner class that builds the location information
				@SuppressWarnings("unused")
				String temp = Data.inst.resourceLocationStr;
			}
			else {
				alreadySet = true;
			}
		}
		if(alreadySet) {
			throw new IllegalStateException("program main name already set, cannot set it again");
		}
	}


	/**
	 * @return true if this program is located in a JAR file, false if it is not
	 */
	public static final boolean isProgramInJar() {
		return Data.inst.isJar;
	}


	/** Get the full path of a relative class path based on this program's location
	 * @param programClass the class file to convert to an absolute path
	 * @return the absolute path of the specified relative file path based on this
	 * program's current location as specified when calling {@link #setProgramMain(Class)}
	 */
	public static final File getRelativeClassFile(Class<?> programClass) {
		if(programClass == null) { return Data.inst.codeLocation; }
		return new File(Data.inst.codeLocation, programClass.getCanonicalName().replace('.', File.separatorChar));
	}


	/** Get the full path of a relative class path based on this program's location
	 * @param programClass the class file to convert to an absolute path
	 * @return the absolute path of the specified relative file path based on this
	 * program's current location as specified when calling {@link #setProgramMain(Class)}
	 */
	public static final Path getRelativeClassPath(Class<?> programClass) {
		if(programClass == null) { return Data.inst.codeLocation.toPath(); }
		return Paths.get(Data.inst.codeLocationStr, programClass.getCanonicalName().replace('.', File.separatorChar));
	}


	/** Get this program's resource relative location
	 * @return the absolute path of the program's current location as
	 * specified when calling {@link #setProgramMain(Class)}
	 */
	public static final File getRelativeResourceFile() {
		return Data.inst.resourceLocation;
	}


	/** Get the full path of a relative file path based on this program's location
	 * @param relativeFile the relative path to convert to an absolute path
	 * @return the absolute path of the specified relative file path based on this
	 * program's current location as specified when calling {@link #setProgramMain(Class)}
	 */
	public static final File getRelativeResourceFile(String relativeFile) {
		if(relativeFile == null) { return Data.inst.resourceLocation; }
		return new File(Data.inst.resourceLocation, relativeFile);
	}


	/** Get the full path of a relative file path based on this program's location
	 * @param relativePath the relative path to convert to an absolute path
	 * @return the absolute path of the specified relative file path based on this
	 * program's current location as specified when calling {@link #setProgramMain(Class)}
	 */
	public static final Path getRelativeResourcePath(String relativePath) {
		if(relativePath == null) { return Data.inst.resourceLocationPath; }
		return Paths.get(Data.inst.resourceLocationStr, relativePath);
	}


	/** Get the absolute path to this program.
	 * For example: {@code "/home/username/JavaApplication/data"}, or {@code "C:\Program Files\application\libraries"}.<br/>
	 * In combination with {@link #getProgramMainClassName()}, the following should be a valid shell
	 * command to run this application:<br/>
	 * {@code "java -cp " + }{@link #getProgramFileLocation()} {@code + " " + }{@link #getProgramMainClassName()}.<br/>
	 * Note: required libraries should be added to the class path.
	 * @return the path to this program
	 * @see #getProgramMainClassName()
	 */
	public static final File getProgramFileLocation() {
		return Data.inst.codeLocation;
	}


	/** Get the full qualifying Java name of the class that contains {@code main()}
	 * A fully qualifying Java name is something like "java.io.File" or "com.project.MainClass".<br/>
	 * In combination with {@link #getProgramMainClassName()}, the following should be a valid shell
	 * command to run this application:<br/>
	 * {@code "java -cp " + }{@link #getProgramFileLocation()} {@code + " " + }{@link #getProgramMainClassName()}.<br/>
	 * Note: required libraries should be added to the class path.
	 * @return the fully qualifying name of the class containing {@code main()}
	 * @see #getProgramFileLocation()
	 */
	public static final String getProgramMainClassName() {
		return Data.inst.mainClassName;
	}

}
