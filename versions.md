--------
####0.4.1
date: 2016-2-27

commit: ?

* Added twg2.io.write utilities for writing serializable objects to an Appendable destination
* Refactored twg2.io.fileLoading (SourcesFiles and SourceInfo) from JParserTools library into this library
* Use latest version of JTwg2Logging (LoggingPrefixFormat instead of LoggingImpl.PrefixFormat)


--------
####0.4.0
date: 2016-2-24

commit: 65a89848376862c2fc3ce12e1e8e011e8166ae9f

* Move twg2.io.log package to separate [JTwg2Logging] (https://github.com/TeamworkGuy2/JTwg2Logging) library


--------
####0.3.0
date: 2016-2-17

commit: e47f4071f4bb3bf36ee5948e8d73b7b96bbdc1c3

* Moved file/directory recursive traversal from FileUtility to new FileRecursion class
* Updated FileReadUtil to only use thread local caches to prevent bugs when calling these static methods from multiple threads
* Removed some unused CharsetUtil methods 


--------
####0.2.1
date: 2016-1-27

commit: 89ef12b3eddcb0e4b230447cfdeab42d9b9abc69

* Fixed Twg2Logs.defaultInst() not getting set after calling initialize()
* Twg2Logs.createLog() reuses one underlying log


--------
####0.2.0
date: 2016-1-23

commit: a107373c64820f55a31f3d7350934b49c3f5f9d9

* Changed public FileReadUtil.defaultInst field to FileReadUtil.defaultInst() method.
* Added thread local FileReadUtil instances via FileReadUtil.threadLocalInst().
* Added Twg2Logs class with singleton like static methods for initializing a single logging instance for use across an application.
* Added Logging.Formatter interface and renamed LoggingImpl.Format -> LoggingImpl.PrefixFormat.  Used to format logging data into strings.


--------
####0.1.0
date: 2016-1-19

commit: 6c1a6738feea81c5d753ce4fc132610a28aa82fa

* Initial versioning, includes process execution utilities (twg2.io.exec), simple logging (twg2.io.log), general I/O utilities, file filters, a rolling file renamer, and some other miscellaneous utilities (twg.io.files).
* Removed unused dependency on JSimpleTypes
* Removed test-check-util dependency in favor of eclipse project library reference, since it is not required at runtime unless calling methods from twg2.io.test.