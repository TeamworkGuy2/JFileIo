JFileIo
==============
version: 0.6.0

Utilities for reading/writing data from/to files in Java. Includes:
* Builders and utility methods for external process execution (i.e. runtime.exec(...))
* Filtered FileVisitor builders which can be passed to Files.walkFileTree(...)
* Rolling file renamer for creating log files or file snapshots/backups
* File reader utility class for reading files using interal cache which is reused between file reads with smart buffer resizing to minimize garbage generated 
* 'Locations' helper for discovering the current executing '.class'/'.jar' file location if it is being run independently (i.e. won't work for containers like apache tomcat)

Take a look at the 'twg2.io.test' package for some examples of how the API can be used.


--------
File Filtering/Recursive Loading:

Example - recursively load files filtered by directory and file extension:
```Java
public static List<Path> listFiles(Path projectDir) throws IOException {
	FileVisitorUtil.Builder visitorBldr = new FileVisitorUtil.Builder();
	visitorBldr.getPreVisitDirectoryFilter()
		.addDirectoryNameFilters(false, "/debug", "/tasks", "/src", "/tests");
	visitorBldr.getVisitFileFilter()
		.addFileExtensionFilters(true, new String[] { ".java", ".txt", ".properties" })
		.setTrackMatches(true);

	FileVisitorUtil.Cache visitorCache = visitorBldr.build();

	Files.walkFileTree(projectDir, visitorCache.getFileVisitor());

	return visitorCache.getVisitFileFilterCache().getMatches();
}
```


--------
Process Execution:

Example - run a process asynchronously:
```Java
public static void runCommand() throws IOException {
	Runtime runtime = Runtime.getRuntime();
	Logging log = new LoggingImpl(Level.ALL, System.out, LoggingImpl.PrefixFormat.NONE);
	ProcessIoStreamFactory streamFactory = new ProcessIoStreamFactory.MemoryStreams();
	OutputStream outStream = streamFactory.openOutputStream();
	OutputStream errStream = streamFactory.openErrorOutputStream();

	ExecuteCmd exeCmd = ExecuteCmd.execAsync("...", runtime, outStream, errStream, log);

	// do cool stuff with the resulting output and error streams
	// or wait for the task to complete and get the result
	ExecuteCmd.Result result = ExecuteCmd.finishSync(exeCmd);
}
```