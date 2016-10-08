# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).


--------
###[0.7.1](N/A) - 2016-10-08
#### Changed
* Updated dependency json-stringify to 0.2.0 latest version (use new instance based JsonStringify class)


--------
###[0.7.0](https://github.com/TeamworkGuy2/JFileIo/commit/b3bd7bcb3014eaf0b6cc3c55d40a4409a525c1ca) - 2016-10-01
#### Added
* Added json-stringify dependency (since JsonWrite was moved from this project to json-stringify)

#### Changed
* Updated dependency paths

#### Removed
* Removed twg2.io.write.JsonWrite, moved to new [json-stringify](https://github.com/TeamworkGuy2/JsonStringify) library


--------
###[0.6.4](https://github.com/TeamworkGuy2/JFileIo/commit/a5a463d84d774a4983e78e0bb3a3c4fff33bab07) - 2016-08-21
#### Changed
* Updated jcollection-util dependency to latest 0.7.0 version


--------
###[0.6.3](https://github.com/TeamworkGuy2/JFileIo/commit/5f3c0d03e6e603a65e85c9377ffc6b93fc2b6b0e) - 2016-08-18
#### Added
* Matches interface with getMatches() and getFailedMatches() (implemented by FileFilterUtil.Cache)
* Additional comments/documentation


--------
###[0.6.2](https://github.com/TeamworkGuy2/JFileIo/commit/4682a1fd40d7b3364aea8f21742f5848ba52fbab) - 2016-08-14
#### Added
* ExecuteCmd getInputReader() and getErrorReader() and marked the input and error reader fields volatile

#### Changed
* compiled jar file path from package-lib.json now matches where it's stored (in the /bin/ directory)


--------
###[0.6.1](https://github.com/TeamworkGuy2/JFileIo/commit/f3856c35f0e68d6efadcd14caabb9f476497dcdc) - 2016-08-07
#### Added
* twg2.io.fileLoading.ValidInvalid


--------
###[0.6.0](https://github.com/TeamworkGuy2/JFileIo/commit/3278593274cd92de0586a7a92623d041a4a9600c) - 2016-08-07
#### Added
* FileFilterUtil
  * standardizePathName() for file separator standardization
  * Builder.addFilter() for adding custom filters (previous add*() options were limited to certain string filters such as addFileExtensionFilter())

#### Changed
* Renamed SourceInfo -> DirectorySearchInfo


--------
###[0.5.0](https://github.com/TeamworkGuy2/JFileIo/commit/add649122931b516bb946e5a86f796083ef9665f) - 2016-06-26
#### Added
* FileUtil.getFileNameWithoutExtension()

#### Changed
* Renamed FileUtility -> FileUtil
* Renamed FileUtil methods:
  * toURL() -> toUrl()
  * removeFileExtension() -> getFileWithoutExtension()
* Added JCollectionInterfaces dependency and updated JCollectionFiller dependency to latest 0.5.x version

#### Fixed
* FileUtil getFileExtension(String), getFileWithoutExtension(String), getFileNameWithoutExtension(String) methods so that they handle paths correctly, not just file names


--------
###[0.4.3](https://github.com/TeamworkGuy2/JFileIo/commit/0dec5e2cac40ab32d010e4dd2b79af0c02c81000) - 2016-06-21
#### Added
* Additional twg2.io.fileLoading documentation
* compiled jar file to bin/

#### Changed
* Upgraded versions.md to CHANGELOG.md format (see http://keepachangelog.com/)


--------
###[0.4.2](https://github.com/TeamworkGuy2/JFileIo/commit/bfc9cb65a0570fdf9ade55a32f8b994a9632d692) - 2016-04-06
#### Added
* FileUtility getFileExtension() and removeFileExtension()
* FileFormatException constructors with fileName argument


--------
###[0.4.1](https://github.com/TeamworkGuy2/JFileIo/commit/bef63be56f1f43edaad6f58fb0c484fb7254452c) - 2016-02-27
#### Added
* twg2.io.write utilities for writing serializable objects to an Appendable destination
* Refactored twg2.io.fileLoading (SourceFiles and SourceInfo) from JParserTools library into this library

#### Changed
* Use latest version of JTwg2Logging (LoggingPrefixFormat instead of LoggingImpl.PrefixFormat)


--------
###[0.4.0](https://github.com/TeamworkGuy2/JFileIo/commit/65a89848376862c2fc3ce12e1e8e011e8166ae9f) - 2016-02-24
#### Changed
* Move twg2.io.log package to separate [JTwg2Logging] (https://github.com/TeamworkGuy2/JTwg2Logging) library


--------
###[0.3.0](https://github.com/TeamworkGuy2/JFileIo/commit/e47f4071f4bb3bf36ee5948e8d73b7b96bbdc1c3) - 2016-02-17
#### Changed
* Moved file/directory recursive traversal from FileUtility to new FileRecursion class
* Updated FileReadUtil to only use thread local caches to prevent bugs when calling these static methods from multiple threads

#### Removed
* Removed some unused CharsetUtil methods 


--------
###[0.2.1](https://github.com/TeamworkGuy2/JFileIo/commit/89ef12b3eddcb0e4b230447cfdeab42d9b9abc69) - 2016-01-27
#### Changed
* Twg2Logs.createLog() reuses one underlying log

#### Fixed
* Fixed Twg2Logs.defaultInst() not getting set after calling initialize()


--------
###[0.2.0](https://github.com/TeamworkGuy2/JFileIo/commit/a107373c64820f55a31f3d7350934b49c3f5f9d9) - 2016-01-23
#### Added
* Added thread local FileReadUtil instances via FileReadUtil.threadLocalInst().
* Added Twg2Logs class with singleton like static methods for initializing a single logging instance for use across an application.
* Added Logging.Formatter interface and renamed LoggingImpl.Format -> LoggingImpl.PrefixFormat.  Used to format logging data into strings.

#### Changed
* Changed public FileReadUtil.defaultInst field to FileReadUtil.defaultInst() method.


--------
###[0.1.0](https://github.com/TeamworkGuy2/JFileIo/commit/6c1a6738feea81c5d753ce4fc132610a28aa82fa) - 2016-01-19
#### Added
* Initial versioning, includes process execution utilities (twg2.io.exec), simple logging (twg2.io.log), general I/O utilities, file filters, a rolling file renamer, and some other miscellaneous utilities (twg.io.files).
* Removed unused dependency on JSimpleTypes
* Removed test-check-util dependency in favor of eclipse project library reference, since it is not required at runtime unless calling methods from twg2.io.test.
