JFileIo
==============
version: 0.2.1

Various Utilities for easily writing/reading data from files in Java. Includes:
* Easy builders and utility methods for external process execution (i.e. runtime.exec(...))
* Filtered FileVisitor builders for building FileVisitors (which can be passed to Files.walkFileTree(...))
* Rolling file renamer for creating log files
* Simple logging classes similar to 'java.util.logging' (these were for personal use, you should probably use java.util.logging).  Provides simple message and exception logging to a PrintStream based on a java.util.logging.Level
* File reader utility class for reading files using interal cache which is reused between file reads with smart buffer resizing to minimize garbage generated 
* 'Locations' helpers for discovering the current executing '.class'/'.jar' file location if it is being run independently (i.e. won't work for containers like apache tomcat)

Take a look at the 'twg2.io.test' package for some examples of how the API can be used.
