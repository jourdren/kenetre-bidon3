package fr.ens.biologie.genomique.kenetre.nanopore.samplesheet.io;

import static java.nio.charset.Charset.defaultCharset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

import fr.ens.biologie.genomique.kenetre.KenetreException;
import fr.ens.biologie.genomique.kenetre.nanopore.samplesheet.SampleSheet;
import fr.ens.biologie.genomique.kenetre.nanopore.samplesheet.SampleSheet.Barcode;

public class SampleSheetCSVReader implements SampleSheetReader, AutoCloseable {

  private static final String PROTOCOL_RUN_ID_FIELDNAME = "protocol_run_id";
  private static final String FLOW_CELL_ID_FIELDNAME = "flow_cell_id";
  private static final String POSITION_ID_FIELDNAME = "position_id";
  private static final String SAMPLE_ID_FIELDNAME = "sample_id";
  private static final String EXPERIMENT_ID_FIELDNAME = "experiment_id";
  private static final String FLOW_CELL_PRODUCT_CODE_FIELDNAME =
      "flow_cell_product_code";
  private static final String KIT_FIELDNAME = "kit";
  private static final String ALIAS_FIELDNAME = "alias";
  private static final String TYPE_FIELDNAME = "type";
  private static final String BARCODE_FIELDNAME = "barcode";
  private static final String INTERNAL_BARCODE_FIELDNAME = "internal_barcode";
  private static final String EXTERNAL_BARCODE_FIELDNAME = "external_barcode";

  private final BufferedReader reader;

  @Override
  public SampleSheet read() throws IOException {

    SampleSheet result = new SampleSheet();

    Map<String, Integer> fieldPositions = new HashMap<>();

    boolean header = true;
    int lineCount = 0;
    String line = null;
    while ((line = this.reader.readLine()) != null) {

      lineCount++;
      if (line.isBlank()) {
        continue;
      }

      if (header) {

        List<String> fields = Splitter.on(',').trimResults().splitToList(line);
        int fieldCount = 0;

        for (String field : fields) {

          switch (field.toLowerCase()) {
          case PROTOCOL_RUN_ID_FIELDNAME:
          case FLOW_CELL_ID_FIELDNAME:
          case POSITION_ID_FIELDNAME:
          case SAMPLE_ID_FIELDNAME:
          case EXPERIMENT_ID_FIELDNAME:
          case FLOW_CELL_PRODUCT_CODE_FIELDNAME:
          case KIT_FIELDNAME:
          case ALIAS_FIELDNAME:
          case TYPE_FIELDNAME:
          case BARCODE_FIELDNAME:
          case INTERNAL_BARCODE_FIELDNAME:
          case EXTERNAL_BARCODE_FIELDNAME:
            break;

          default:
            throw new IOException("Unknown field in sample sheet: " + field);

          }

          if (fieldPositions.containsKey(ALIAS_FIELDNAME)) {

            if (fieldPositions.containsKey(BARCODE_FIELDNAME)
                && (fieldPositions.containsKey(INTERNAL_BARCODE_FIELDNAME)
                    || fieldPositions
                        .containsKey(EXTERNAL_BARCODE_FIELDNAME))) {
              throw new IOException(
                  "A sample sheet cannot handle single and dual barcoding");
            }

            if (!fieldPositions.containsKey(INTERNAL_BARCODE_FIELDNAME)
                && fieldPositions.containsKey(EXTERNAL_BARCODE_FIELDNAME)) {
              throw new IOException(
                  "Internal barcode field is missing in the sample sheet");
            }

            if (fieldPositions.containsKey(INTERNAL_BARCODE_FIELDNAME)
                && !fieldPositions.containsKey(EXTERNAL_BARCODE_FIELDNAME)) {
              throw new IOException(
                  "External barcode field is missing in the sample sheet");
            }

            if (!fieldPositions.containsKey(BARCODE_FIELDNAME)
                && !fieldPositions.containsKey(INTERNAL_BARCODE_FIELDNAME)
                && !fieldPositions.containsKey(EXTERNAL_BARCODE_FIELDNAME)) {
              throw new IOException(
                  "Barcode field(s) are missing in the sample sheet");
            }
          }

          fieldPositions.put(field.toLowerCase(), fieldCount++);
        }

        header = false;
      } else {

        List<String> fields = Splitter.on(',').trimResults().splitToList(line);

        for (String field : new String[] {PROTOCOL_RUN_ID_FIELDNAME,
            FLOW_CELL_ID_FIELDNAME, POSITION_ID_FIELDNAME, SAMPLE_ID_FIELDNAME,
            EXPERIMENT_ID_FIELDNAME, FLOW_CELL_PRODUCT_CODE_FIELDNAME,
            KIT_FIELDNAME}) {

          if (fieldPositions.containsKey(field)) {

            int posField = fieldPositions.get(field);
            if (posField >= fields.size()) {
              continue;
            }
            String newValue = fields.get(posField);
            String oldValue = get(result, field);
            if (oldValue != null && !oldValue.equals(newValue)) {
              throw new IOException(
                  "The field \"" + field + "\" cannot have multiple values");
            }

            set(result, field, newValue);
          }
        }

        // Handle barcodes
        if (fieldPositions.containsKey(ALIAS_FIELDNAME)) {

          String alias =
              getValue(fields, ALIAS_FIELDNAME, fieldPositions, lineCount);

          Barcode b;

          if (fieldPositions.containsKey(BARCODE_FIELDNAME)) {

            String barcode =
                getValue(fields, BARCODE_FIELDNAME, fieldPositions, lineCount);
            b = result.addBarcode(barcode, alias);

          } else {

            String internalBarcode = getValue(fields,
                INTERNAL_BARCODE_FIELDNAME, fieldPositions, lineCount);
            String externalBarcode = getValue(fields,
                EXTERNAL_BARCODE_FIELDNAME, fieldPositions, lineCount);
            b = result.addBarcode(internalBarcode, externalBarcode, alias);
          }

          if (fieldPositions.containsKey(TYPE_FIELDNAME)) {
            String type =
                getValue(fields, TYPE_FIELDNAME, fieldPositions, lineCount);
            if (type != null && !type.isBlank()) {
              b.setType(type);
            }
          }

        }

      }

    }

    try

    {
      result.validate();
    } catch (KenetreException e) {
      throw new IOException(e);
    }

    return result;
  }

  private static String getValue(List<String> fields, String fieldName,
      Map<String, Integer> fieldPositions, int lineCount) throws IOException {

    int posField = fieldPositions.get(fieldName);
    if (posField >= fields.size()) {
      throw new IOException(
          "cannot found " + fieldName + " line: " + lineCount);
    }

    return fields.get(posField);
  }

  @Override
  public void close() throws IOException {

    this.reader.close();
  }

  private static String get(SampleSheet sampleSheet, String fieldName) {

    switch (fieldName) {

    case PROTOCOL_RUN_ID_FIELDNAME:
      return sampleSheet.getProtocolRunId();

    case FLOW_CELL_ID_FIELDNAME:
      return sampleSheet.getFlowCellId();

    case POSITION_ID_FIELDNAME:
      return sampleSheet.getPositionId();

    case SAMPLE_ID_FIELDNAME:
      return sampleSheet.getSampleId();

    case EXPERIMENT_ID_FIELDNAME:
      return sampleSheet.getExperimentId();

    case FLOW_CELL_PRODUCT_CODE_FIELDNAME:
      return sampleSheet.getFlowCellProductCode();

    case KIT_FIELDNAME:
      return sampleSheet.getKit();

    default:
      throw new IllegalArgumentException("Unknown field: " + fieldName);
    }
  }

  private static void set(SampleSheet sampleSheet, String fieldName,
      String value) {

    switch (fieldName) {

    case PROTOCOL_RUN_ID_FIELDNAME:
      sampleSheet.setProtocolRunId(value);
      break;

    case FLOW_CELL_ID_FIELDNAME:
      sampleSheet.setFlowCellId(value);
      break;

    case POSITION_ID_FIELDNAME:
      sampleSheet.setPositionId(value);
      break;

    case SAMPLE_ID_FIELDNAME:
      sampleSheet.setSampleId(value);
      break;

    case EXPERIMENT_ID_FIELDNAME:
      sampleSheet.setExperimentId(value);
      break;

    case FLOW_CELL_PRODUCT_CODE_FIELDNAME:
      sampleSheet.setFlowCellProductCode(value);
      break;

    case KIT_FIELDNAME:
      sampleSheet.setKit(value);
      break;

    default:
      throw new IllegalArgumentException("Unknown field: " + fieldName);
    }
  }

  //
  // Constructors
  //

  /**
   * Public constructor
   * @param is InputStream to use
   */
  public SampleSheetCSVReader(final InputStream is) {

    if (is == null) {
      throw new NullPointerException("InputStream is null");
    }

    this.reader =
        new BufferedReader(new InputStreamReader(is, defaultCharset()));
  }

  /**
   * Public constructor
   * @param file File to use
   * @throws IOException if the file does not exists
   */
  public SampleSheetCSVReader(final File file) throws IOException {

    if (file == null) {
      throw new NullPointerException("File is null");
    }

    if (!file.isFile()) {
      throw new FileNotFoundException(
          "File not found: " + file.getAbsolutePath());
    }

    this.reader = new BufferedReader(new FileReader(file, defaultCharset()));
  }

  /**
   * Public constructor
   * @param path File to use
   * @throws IOException if the file does not exists
   */
  public SampleSheetCSVReader(final Path path) throws IOException {

    this.reader = Files.newBufferedReader(path);
  }

  /**
   * Public constructor
   * @param filename File to use
   * @throws IOException if an error occurs while reading the file
   */
  public SampleSheetCSVReader(final String filename) throws IOException {

    if (filename == null) {
      throw new NullPointerException("Filename is null");
    }

    final File file = new File(filename);

    if (!file.isFile()) {
      throw new FileNotFoundException(
          "File not found: " + file.getAbsolutePath());
    }

    this.reader = new BufferedReader(new FileReader(file, defaultCharset()));
  }

}
