package fr.ens.biologie.genomique.kenetre.bio.readsmappers;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import fr.ens.biologie.genomique.kenetre.log.DummyLogger;
import fr.ens.biologie.genomique.kenetre.log.GenericLogger;

/**
 * This class define a builder for a mapper instance.
 * @author Laurent Jourdren
 * @since 2.6
 */
public class MapperInstanceBuilder {

  private final String mapperName;
  private String mapperVersion;
  private String mapperFlavor;
  private GenericLogger logger = new DummyLogger();
  private boolean useBundledBinaries;
  private String applicationName = "kenetre";
  private String applicationVersion = "unknown";
  private File tempDir = new File(System.getProperty("java.io.tmpdir"));
  private File executablesTempDir =
      new File(System.getProperty("java.io.tmpdir"));
  private String dockerImage;

  /**
   * Set the mapper version.
   * @param mapperVersion the mapperVersion to set
   */
  public MapperInstanceBuilder withMapperVersion(String mapperVersion) {

    requireNonNull(mapperVersion);

    this.mapperVersion = mapperVersion;
    return this;
  }

  /**
   * Set the flavor of the mapper.
   * @param mapperFlavor the mapperFlavor to set
   */
  public MapperInstanceBuilder withMapperFlavor(String mapperFlavor) {

    requireNonNull(mapperFlavor);

    this.mapperFlavor = mapperFlavor;

    return this;
  }

  /**
   * Set the logger.
   * @param logger the logger to set
   */
  public MapperInstanceBuilder withLogger(GenericLogger logger) {

    requireNonNull(logger);

    this.logger = logger;
    return this;
  }

  /**
   * Set if bundled binaries must be used.
   * @param useBundledBinaries the useBundledBinaries to set
   */
  public MapperInstanceBuilder withUseBundledBinaries(boolean useBundledBinaries) {

    this.useBundledBinaries = useBundledBinaries;
    return this;
  }

  /**
   * Set the application name.
   * @param applicationName the applicationName to set
   */
  public MapperInstanceBuilder withApplicationName(String applicationName) {

    requireNonNull(applicationName);

    this.applicationName = applicationName;
    return this;
  }

  /**
   * Set the application version.
   * @param applicationVersion the applicationVersion to set
   */
  public MapperInstanceBuilder withApplicationVersion(String applicationVersion) {

    requireNonNull(applicationVersion);

    this.applicationVersion = applicationVersion;
    return this;
  }

  /**
   * Set the temporary directory.
   * @param tempDir the temporary directory to set
   */
  public MapperInstanceBuilder withTempDir(File tempDir) {

    requireNonNull(tempDir);

    this.tempDir = tempDir;
    return this;
  }

  /**
   * Set the temporary directory for executables.
   * @param executablesTempDir the temporary directory for executables to set
   */
  public MapperInstanceBuilder withExecutablesTempDir(File executablesTempDir) {

    requireNonNull(executablesTempDir);

    this.executablesTempDir = executablesTempDir;
    return this;
  }

  /**
   * Set the docker image to use.
   * @param dockerImage the docker image to set
   */
  public MapperInstanceBuilder withDockerImage(String dockerImage) {

    requireNonNull(dockerImage);

    this.dockerImage = dockerImage;
    return this;
  }

  //
  // Build method
  //

  /**
   * Build the mapper instance.
   * @return a mapper instance
   * @throws IOException if an error occurs while creating the mapper instance
   */
  public MapperInstance build() throws IOException {

    MapperProvider provider =
        MapperProviderService.getInstance().newService(mapperName);

    Mapper mapper = new Mapper(provider, this.tempDir, this.executablesTempDir,
        this.logger, this.applicationName, this.applicationVersion);

    MapperInstance result = mapper.newMapperInstance(this.mapperVersion,
        this.mapperFlavor, this.useBundledBinaries, this.dockerImage);

    return result;
  }

  //
  // Constructor
  //

  public MapperInstanceBuilder(String mapperName) {

    Objects.requireNonNull(mapperName);
    this.mapperName = mapperName;
  }

}
