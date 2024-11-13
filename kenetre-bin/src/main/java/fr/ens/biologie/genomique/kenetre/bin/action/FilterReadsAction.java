package fr.ens.biologie.genomique.kenetre.bin.action;

import static fr.ens.biologie.genomique.kenetre.io.CompressionType.create;
import static fr.ens.biologie.genomique.kenetre.io.CompressionType.open;
import static org.apache.commons.cli.Option.builder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ens.biologie.genomique.kenetre.KenetreException;
import fr.ens.biologie.genomique.kenetre.bio.BadBioEntryException;
import fr.ens.biologie.genomique.kenetre.bio.FastqFormat;
import fr.ens.biologie.genomique.kenetre.bio.ReadSequence;
import fr.ens.biologie.genomique.kenetre.bio.io.FastqReader;
import fr.ens.biologie.genomique.kenetre.bio.io.FastqWriter;
import fr.ens.biologie.genomique.kenetre.bio.readfilter.MultiReadFilterBuilder;
import fr.ens.biologie.genomique.kenetre.bio.readfilter.ReadFilter;

/**
 * This program allow to filter FASTQ files.
 * @author Laurent Jourdren
 * @since 0.29
 */
public class FilterReadsAction implements Action {

  private File inputFile;
  private File inputFile1;
  private File inputFile2;
  private File outputFile;
  private File outputFile1;
  private File outputFile2;

  @Override
  public String getName() {

    return "filterreads";
  }

  @Override
  public String getDescription() {

    return "This program filters reads";
  }

  /**
   * Create options for command line
   * @return an Options object
   */
  @SuppressWarnings("static-access")
  private static Options makeOptions() {

    // create Options object
    final Options options = new Options();

    options.addOption(builder("i").longOpt("input").hasArg().argName("file")
        .desc("single read input").build());

    options.addOption(builder("i1").longOpt("input1").hasArg().argName("file")
        .desc("paired-end input 1").build());

    options.addOption(builder("i2").longOpt("input2").hasArg().argName("file")
        .desc("paired-end input 2").build());

    options.addOption(builder("o").longOpt("output").hasArg().argName("file")
        .desc("single read output").build());

    options.addOption(builder("o1").longOpt("output1").hasArg().argName("file")
        .desc("paired-end output 1").build());

    options.addOption(builder("o2").longOpt("output2").hasArg().argName("file")
        .desc("paired-end output 2").build());

    options.addOption(builder("f").longOpt("flow-cell-type").hasArg()
        .argName("type").desc("flow cell type").build());

    options.addOption("s", "stdin", false, "stdin input");
    options.addOption("t", "stdout", false, "stdout output");

    // Help option
    options.addOption("h", "help", false, "display this help");

    return options;
  }

  @Override
  public void action(List<String> arguments) {

    try {
      MultiReadFilterBuilder filterBuilder = new MultiReadFilterBuilder();
      filterBuilder.addParameters(parseOptions(arguments));
      ReadFilter filter = filterBuilder.getReadFilter();

      if (this.inputFile != null) {
        filterFile(this.inputFile, this.outputFile, filter,
            FastqFormat.FASTQ_SANGER);
      } else {
        filterFile(this.inputFile1, this.inputFile2, this.outputFile1,
            this.outputFile2, filter, FastqFormat.FASTQ_SANGER);
      }

    } catch (KenetreException | IOException e) {
      error("Error: " + e.getMessage());
    }

  }

  private static void filterFile(final File inFile, final File outFile,
      final ReadFilter filter, final FastqFormat fastqFormat)
      throws IOException {

    try (FastqReader reader = new FastqReader(open(inFile));
        FastqWriter writer = new FastqWriter(create(outFile))) {

      for (final ReadSequence read : reader) {

        // Set Fastq format
        read.setFastqFormat(fastqFormat);

        if (filter.accept(read)) {
          writer.write(read);
        }

      }
      reader.throwException();

    } catch (BadBioEntryException e) {

      throw new IOException("Invalid Fastq format: "
          + e.getMessage() + " File: " + inFile + " Entry: " + e.getEntry());

    }
  }

  private static void filterFile(final File inFile1, final File inFile2,
      final File outFile1, final File outFile2, final ReadFilter filter,
      final FastqFormat fastqFormat) throws IOException {

    try (FastqReader reader2 = new FastqReader(open(inFile2));
        FastqWriter writer1 = new FastqWriter(create(outFile1));
        FastqWriter writer2 = new FastqWriter(create(outFile2));
        FastqReader reader1 = new FastqReader(open(inFile1))) {
      for (final ReadSequence read1 : reader1) {

        // Test if the second read exists
        if (!reader2.hasNext()) {
          reader2.throwException();
          throw new IOException("Unexcepted end of the second read file. "
              + inFile1.getName() + " and " + inFile2.getName()
              + " must have the same number of entries/lines.");
        }

        // Get the second read
        final ReadSequence read2 = reader2.next();

        // Set fastq format
        read1.setFastqFormat(fastqFormat);
        read2.setFastqFormat(fastqFormat);

        if (filter.accept(read1, read2)) {
          writer1.write(read1);
          writer2.write(read2);
        }
      }
      reader1.throwException();
      reader2.throwException();

      if (reader2.hasNext()) {
        throw new IOException("Unexcepted end of the first read file. "
            + inFile1.getName() + " and " + inFile2.getName()
            + " must have the same number of entries/lines.");
      }

    } catch (BadBioEntryException e) {

      throw new IOException("Invalid Fastq format: "
          + e.getMessage() + " File 1: " + inFile1 + " File2:" + inFile2
          + " Entry: " + e.getEntry());
    }

  }

  @Override
  public boolean isHidden() {

    return false;
  }

  private Map<String, String> parseOptions(List<String> arguments) {

    final Options options = makeOptions();
    final CommandLineParser parser = new DefaultParser();

    Map<String, String> result = new HashMap<>();

    try {

      // parse the command line arguments
      final CommandLine line =
          parser.parse(options, arguments.toArray(new String[0]), true);

      // Help option
      if (line.hasOption("help")) {
        help(options);
      }

      if (line.hasOption("s")) {
        this.inputFile = new File("/dev/stdin");
      }

      if (line.hasOption("t")) {
        this.outputFile = new File("/dev/stdout");
      }

      if (line.hasOption("i")) {
        this.inputFile = new File(line.getOptionValue("input"));
      }

      if (line.hasOption("i1")) {
        this.inputFile1 = new File(line.getOptionValue("input1"));
      }

      if (line.hasOption("i2")) {
        this.inputFile1 = new File(line.getOptionValue("input2"));
      }

      if (line.hasOption("o")) {
        this.inputFile = new File(line.getOptionValue("input"));
      }

      if (line.hasOption("o1")) {
        this.inputFile1 = new File(line.getOptionValue("output1"));
      }

      if (line.hasOption("o2")) {
        this.inputFile1 = new File(line.getOptionValue("output2"));
      }

      for (String arg : line.getArgList()) {

        int pos = arg.indexOf('=');
        if (pos == -1) {
          continue;
        }

        result.put(arg.substring(0, pos), arg.substring(pos + 1));
      }

    } catch (ParseException e) {
      error("Error while parsing command line arguments: " + e.getMessage());
    }

    // Check paired-end mode
    if (this.inputFile1 != null
        || this.inputFile2 != null || outputFile1 != null
        || outputFile2 != null) {

      if (this.inputFile1 == null) {
        error("Error: input file 1 is not defined");
      }

      if (this.inputFile2 == null) {
        error("Error: input file 2 is not defined");
      }

      if (this.outputFile1 == null) {
        error("Error: output file 1 is not defined");
      }

      if (this.outputFile2 == null) {
        error("Error: output file 2 is not defined");
      }
    } else {

      if (this.inputFile == null) {
        error("Error: input file  is not defined");
      }

      if (this.outputFile == null) {
        error("Error: output file is not defined");
      }

    }

    return result;
  }

  /**
   * Show command line help.
   * @param options Options of the software
   */
  private static void help(final Options options) {

    // Show help message
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("filterreads [options] parameters", options);

    System.exit(0);
  }

  private static void error(String msg) {

    System.err.println(msg);
    System.exit(1);
  }

}
