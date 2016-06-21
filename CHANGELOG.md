# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).

--------
###[0.4.3](N/A) - 2016-06-21
####Added
* Additional twg2.io.fileLoading documentation
* compiled jar file to bin/

####Changed
* Upgraded versions.md to CHANGELOG.md format (see http://keepachangelog.com/)


--------
###[0.4.2](https://github.com/TeamworkGuy2/JFileIo/commit/bfc9cb65a0570fdf9ade55a32f8b994a9632d692) - 2016-04-06
####Added
* FileUtility getFileExtension() and removeFileExtension()
* FileFormatException constructors with fileName argument


--------
###[0.4.1](https://github.com/TeamworkGuy2/JFileIo/commit/bef63be56f1f43edaad6f58fb0c484fb7254452c) - 2016-02-27
####Added
* twg2.io.write utilities for writing serializable objects to an Appendable destination
* Refactored twg2.io.fileLoading (SourceFiles and SourceInfo) from JParserTools library into this library

####Changed
* Use latest version of JTwg2Logging (LoggingPrefixFormat instead of LoggingImpl.PrefixFormat)


--------
####[0.4.0](https://github.com/TeamworkGuy2/JFileIo/commit/65a89848376862c2fc3ce12e1e8e011e8166ae9f) - 2016-02-24
####Changed
* Move twg2.io.log package to separate [JTwg2Logging] (https://github.com/TeamworkGuy2/JTwg2Logging) library


--------
####[0.3.0](https://github.com/TeamworkGuy2/JFileIo/commit/e47f4071f4bb3bf36ee5948e8d73b7b96bbdc1c3) - 2016-02-17
####Changed
* Moved file/directory recursive traversal from FileUtility to new FileRecursion class
* Updated FileReadUtil to only use thread local caches to prevent bugs when calling these static methods from multiple threads

####Removed
* Removed some unused CharsetUtil methods 


--------
####[0.2.1](https://github.com/TeamworkGuy2/JFileIo/commit/89ef12b3eddcb0e4b230447cfdeab42d9b9abc69) - 2016-01-27
####Changed
* Twg2Logs.createLog() reuses one underlying log

####Fixed
* Fixed Twg2Logs.defaultInst() not getting set after calling initialize()


--------
####[0.2.0](https://github.com/TeamworkGuy2/JFileIo/commit/a107373c64820f55a31f3d7350934b49c3f5f9d9) - 2016-01-23
####Added
* Added thread local FileReadUtil instances via FileReadUtil.threadLocalInst().
* Added Twg2Logs class with singleton like static methods for initializing a single logging instance for use across an application.
* Added Logging.Formatter interface and renamed LoggingImpl.Format -> LoggingImpl.PrefixFormat.  Used to format logging data into strings.

####Changed
* Changed public FileReadUtil.defaultInst field to FileReadUtil.defaultInst() method.


--------
####[0.1.0](https://github.com/TeamworkGuy2/JFileIo/commit/6c1a6738feea81c5d753ce4fc132610a28aa82fa) - 2016-01-19
####Added
* Initial versioning, includes process execution utilities (twg2.io.exec), simple logging (twg2.io.log), general I/O utilities, file filters, a rolling file renamer, and some other miscellaneous utilities (twg.io.files).
* Removed unused dependency on JSimpleTypes
* Removed test-check-util dependency in favor of eclipse project library reference, since it is not required at runtime unless calling methods from twg2.io.test.