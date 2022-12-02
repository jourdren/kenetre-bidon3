package fr.ens.biologie.genomique.kenetre.log;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import fr.ens.biologie.genomique.kenetre.KenetreException;

/**
 * This class define a logger using the java.util.logging API that write log in
 * a file.
 * @author Laurent Jourdren
 */
public class FileLogger extends AbstractJavaUtilLoggingLogger {

  @Override
  protected Handler createHandler(Map<String, String> conf)
      throws KenetreException {

    // Get Log path
    String logPath = Objects.toString(conf.get("aozan.log"), "");

    if (logPath.trim().isEmpty()) {
      throw new KenetreException("No log file defined");
    }

    try {

      Handler result = new FileHandler(logPath, true);

      if (conf.containsKey("aozan.log.level")) {
        String logLevelName = conf.get("aozan.log.level");
        Level logLevel = Level.parse(logLevelName.toUpperCase());
        result.setLevel(logLevel);
      }

      return result;
    } catch (SecurityException | IOException e) {
      throw new KenetreException(e);
    }
  }

  //
  // Constructor
  //

  /**
   * Constructor.
   * @param conf configuration
   * @throws Aozan3Exception if an error occurs while creating the logger
   */
  public FileLogger(String loggerName, Map<String, String> conf)
      throws KenetreException {
    super(loggerName, conf);
  }

}
