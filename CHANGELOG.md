# Changelog

## 0.35.0 (2025-03-20)


### Features

* In release-please.yml, now upload artifact. ([#28](https://github.com/jourdren/kenetre-bidon3/issues/28)) ([8f5cd04](https://github.com/jourdren/kenetre-bidon3/commit/8f5cd04e2424d07764a05a8754ab722b94e27153))
* In release-please.yml, now upload artifact. ([#29](https://github.com/jourdren/kenetre-bidon3/issues/29)) ([cc2542b](https://github.com/jourdren/kenetre-bidon3/commit/cc2542bd6a34d8ba733a64cd5ae2f2c4b1778b55))


### Bug Fixes

* Fix ambiguous castings in VersionTest test class. ([64f4e8a](https://github.com/jourdren/kenetre-bidon3/commit/64f4e8a0da0f37e97dff0696beb52141b15c815b))
* Fix code line removed by mistake in FileGenomeMapperIndexer. ([ae025b1](https://github.com/jourdren/kenetre-bidon3/commit/ae025b1c6938c9ca6a16820b5602167ac0220983))
* Fix dependency issue with jhdf5. ([bf01f86](https://github.com/jourdren/kenetre-bidon3/commit/bf01f8627a81ebb8a427659e8bcff83d17420ffa))
* Fix META-INF/services file names and class names. ([9ede964](https://github.com/jourdren/kenetre-bidon3/commit/9ede9640427768c27d282cb94fc69b6b2c4a258d))
* For compatibility with previous serialization of expected results, the EnhancedBloomFilter move to its original package. ([bb6b2f2](https://github.com/jourdren/kenetre-bidon3/commit/bb6b2f24a99282430d84afbdcdb6ac226d80dfdb))
* In AbstractFileGenomeIndexStorage, sequence count and genome length were not read when load the genomes_index_storage.txt file. ([265e7a3](https://github.com/jourdren/kenetre-bidon3/commit/265e7a3aaa23f91919e6d5fe940fcb3136db6704))
* In BinariesInstaller, application version was not set in constructor. ([0382a3a](https://github.com/jourdren/kenetre-bidon3/commit/0382a3ac9dadac72c64be2b994f5429120d2a2f9))
* In BundledMapperExecutor constructor, logger was not used. ([e250a89](https://github.com/jourdren/kenetre-bidon3/commit/e250a89c9e0f1869038cccc58a194293802f2a32))
* In ErrorMetricsReader.getExpectedRecordSize(), result for version 6 was not implemented. ([3d450cd](https://github.com/jourdren/kenetre-bidon3/commit/3d450cd095598cc9f793c49e780c21c845696cb6))
* In FallBackDockerImageInstance.start(), fix filtering of null file used values. ([a163599](https://github.com/jourdren/kenetre-bidon3/commit/a1635993e3f3a386b725da6dec8aa2eb575eecd4))
* In FileDataPath.symlinkOrCopy(), link was not created. ([4ac5ad9](https://github.com/jourdren/kenetre-bidon3/commit/4ac5ad922ff3ecbd7c50a9ff8716178bfd079ccf))
* In FileGenomeMapperIndexer constructor, handle the case were no genome index storage is set. ([cab92cf](https://github.com/jourdren/kenetre-bidon3/commit/cab92cf1cb9989edb6c1f4d6c1d1e152cd10267e))
* In FileLogger and StandardErrorLogger, remove aozan string from logger configuration keys. ([a721404](https://github.com/jourdren/kenetre-bidon3/commit/a7214045a0a02295f7f579758cb4acdfa0ac942d))
* In kenetre-illumina, add missing commons-csv dependency for parsing PrimaryAnalysisMetrics.csv files. ([c0dc413](https://github.com/jourdren/kenetre-bidon3/commit/c0dc4134a7a66e7db9f42b8dc3f93ce1fdc36e43))
* In Mapper constructor, application version was not correctly set. ([e250a89](https://github.com/jourdren/kenetre-bidon3/commit/e250a89c9e0f1869038cccc58a194293802f2a32))
* In MapperBuilder.build(), now return null if mapper is unknown instead of a new instance of Mapper that will produce an exception in the constructor. ([f2cde7e](https://github.com/jourdren/kenetre-bidon3/commit/f2cde7e359e59f40db0940b53feaa429ecae0bb3))
* In MapperIndex.unzipArchiveIndexFile(), a log message showed a reference. ([e250a89](https://github.com/jourdren/kenetre-bidon3/commit/e250a89c9e0f1869038cccc58a194293802f2a32))
* In MapperInstanceBuilder, withMapperVersion() and withMapperFlavor() methods now allow null parameters to keep compatibility with Eoulsan code. ([d7a89ce](https://github.com/jourdren/kenetre-bidon3/commit/d7a89ce2f68b4db4634d55fc27e4d7af224b7916))
* in maven.yml fix branch name. ([#3](https://github.com/jourdren/kenetre-bidon3/issues/3)) ([1515664](https://github.com/jourdren/kenetre-bidon3/commit/1515664edf5ef53d2bc2b2daf1fdf9860da1f3eb))
* In Nanopore SampleSheet class, non canonical field were not protected if value had comma. ([3013918](https://github.com/jourdren/kenetre-bidon3/commit/3013918f39e1d959c0d1c84fe3ab8ef36f0905a4))
* In pom.xml for kenetre-extra, service directory in META-INF of the jar file was not generated. ([eee74b6](https://github.com/jourdren/kenetre-bidon3/commit/eee74b6dc3f29438b9402369d2726a1ef78e8b2c))
* In PropertySection.containsKey(), fix infinite recursion. ([ca2d79f](https://github.com/jourdren/kenetre-bidon3/commit/ca2d79f06dcf19799508ef692f9d8aa8e8118d2a))
* In PseudoMapReduce.sort(), now handle the case where there is no file to sort. ([4ccebc6](https://github.com/jourdren/kenetre-bidon3/commit/4ccebc6a2f04ee2ec5a24416aacb8cbc76b16c2e))
* In SampleSheetCSVReader, now remove BOM character at the beginning of the file if exists. ([c2953a6](https://github.com/jourdren/kenetre-bidon3/commit/c2953a6b05b54e1542f02a6e1740bcfca90958e4))
* In SampleSheetParser, the "sample_ref" field is not barcode field. ([81fb214](https://github.com/jourdren/kenetre-bidon3/commit/81fb21483bbf402cfd2c556b5fc78c4bd2211927))
* In SampleSheetV2Parser.parseLine(), do not add empty fields in samples. ([4677ecd](https://github.com/jourdren/kenetre-bidon3/commit/4677ecdff9f848dbf522382116ba40c7b4eed438))
* In SAMUtils.parseIntervals() now handle '=' and 'X' Cigar operations. ([18d1c8f](https://github.com/jourdren/kenetre-bidon3/commit/18d1c8fc703b9d7de67da2d79be75f3570c7ec4d))
* In ServiceNameLoader, now use the same class loader to load the class and to read the service file in META-INF. ([48552d2](https://github.com/jourdren/kenetre-bidon3/commit/48552d2ba052b72391d8705355c9dfa54b5f1d2a))
* In ServiceNameLoader, now use this.getClass().getClassLoader() instead of Thread.currentThread().getContextClassLoader() as ClassLoader. ([48552d2](https://github.com/jourdren/kenetre-bidon3/commit/48552d2ba052b72391d8705355c9dfa54b5f1d2a))
* In ServiceNameLoader, the reload() method is now synchronized. ([48552d2](https://github.com/jourdren/kenetre-bidon3/commit/48552d2ba052b72391d8705355c9dfa54b5f1d2a))
* Rename maven.yml ([#2](https://github.com/jourdren/kenetre-bidon3/issues/2)) ([b580cde](https://github.com/jourdren/kenetre-bidon3/commit/b580cde406f34e31aeca231b6863dc0f0a709c04))
* Rename unit test SampleSheetCSVReader to SampleSheetCVSReaderWriterTest to be handled by Maven. ([8dd4eac](https://github.com/jourdren/kenetre-bidon3/commit/8dd4eac2bca568854480fb562c54adbc6bc3d61a))
* Some fixes for using Kenetre in Eoulsan. ([ec33b65](https://github.com/jourdren/kenetre-bidon3/commit/ec33b65d9c13454acbbcf41645b1e8110f4b87c6))


### Miscellaneous Chores

* release 0.35.0 ([#31](https://github.com/jourdren/kenetre-bidon3/issues/31)) ([aae400c](https://github.com/jourdren/kenetre-bidon3/commit/aae400ccceab0533cc1d3a41c415850fc94d9029))
