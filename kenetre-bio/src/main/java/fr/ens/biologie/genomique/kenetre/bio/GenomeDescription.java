/*
 *                  Eoulsan development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License version 2.1 or
 * later and CeCILL-C. This should be distributed with the code.
 * If you do not have a copy, see:
 *
 *      http://www.gnu.org/licenses/lgpl-2.1.txt
 *      http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.txt
 *
 * Copyright for this code is held jointly by the Genomic platform
 * of the Institut de Biologie de l'École normale supérieure and
 * the individual authors. These should be listed in @author doc
 * comments.
 *
 * For more information on the Eoulsan project and its aims,
 * or to join the Eoulsan Google group, visit the home page
 * at:
 *
 *      http://outils.genomique.biologie.ens.fr/eoulsan
 *
 */

package fr.ens.biologie.genomique.kenetre.bio;

import static fr.ens.biologie.genomique.kenetre.util.StringUtils.md5DigestToString;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.ens.biologie.genomique.kenetre.bio.io.FastaLineParser;
import fr.ens.biologie.genomique.kenetre.io.FileUtils;
import fr.ens.biologie.genomique.kenetre.util.StringUtils;

/**
 * This class define a genome description.
 * @since 1.0
 * @author Laurent Jourdren
 */
public class GenomeDescription {

  private static final String PREFIX = "genome.";
  private static final String NAME_PREFIX = PREFIX + "name";
  private static final String LENGTH_PREFIX = PREFIX + "length";
  private static final String MD5_PREFIX = PREFIX + "md5";
  private static final String SEQUENCE_PREFIX = PREFIX + "sequence.";
  private static final String SEQUENCES_COUNT_PREFIX = PREFIX + "sequences";

  private String genomeName;
  private final Map<String, Long> sequences = new LinkedHashMap<>();
  private String md5Sum;

  //
  // Setters
  //

  /**
   * Set the genome name.
   * @param genomeName name of the genome
   */
  private void setGenomeName(final String genomeName) {

    this.genomeName = genomeName;
  }

  /**
   * Add a sequence.
   * @param sequenceName name of the sequence
   * @param sequenceLength length of the sequence
   */
  public void addSequence(final String sequenceName,
      final long sequenceLength) {

    this.sequences.put(sequenceName, sequenceLength);
  }

  /**
   * Set the md5 digest of the genome file
   * @param md5Digest the md5 digest
   */
  public void setMD5Sum(final String md5Digest) {

    this.md5Sum = md5Digest;
  }

  //
  // Getters
  //

  /**
   * Get the genome name.
   * @return the genome name
   */
  public String getGenomeName() {

    return this.genomeName;
  }

  /**
   * Get the length of a sequence
   * @param sequenceName name of the sequence
   * @return the length of the sequence or -1 if the sequence does not exists
   */
  public long getSequenceLength(final String sequenceName) {

    if (this.sequences.containsKey(sequenceName)) {

      return this.sequences.get(sequenceName);
    }

    return -1;
  }

  /**
   * Test if the genome description contains a sequence
   * @param sequenceName name of the sequence
   * @return true if the sequence exists in the genome description
   */
  public boolean containsSequence(final String sequenceName) {

    return this.sequences.containsKey(sequenceName);
  }

  /**
   * Get the names of the sequences.
   * @return a set with the name of the sequence
   */
  public List<String> getSequencesNames() {

    return Collections
        .unmodifiableList(new ArrayList<>(this.sequences.keySet()));
  }

  /**
   * Get the md5 sum for the genome.
   * @return the md5 sum
   */
  public String getMD5Sum() {

    return this.md5Sum;
  }

  /**
   * Get the number of sequences in the genome.
   * @return the number of sequences in the genome
   */
  public int getSequenceCount() {

    return this.sequences.size();
  }

  /**
   * Get the genome length;
   * @return the genome length
   */
  public long getGenomeLength() {

    long count = 0;

    for (Map.Entry<String, Long> e : this.sequences.entrySet()) {
      count += e.getValue();
    }

    return count;
  }

  /**
   * Convert Object to a map of chromosome lengths
   * @return a Map object
   */
  public Map<String, Long> toMap() {

    Map<String, Long> result = new LinkedHashMap<>();

    for (Map.Entry<String, Long> e : this.sequences.entrySet()) {
      result.put(e.getKey(), e.getValue());
    }

    return result;
  }

  //
  // Save description
  //

  /**
   * Save genome description.
   * @param os OutputStream to use for genome description writing
   * @throws IOException if an error occurs while saving the genome description
   */
  public void save(final OutputStream os) throws IOException {

    requireNonNull(os, "OutputStream is null");

    final Writer writer = FileUtils.createFastBufferedWriter(os);

    if (this.genomeName != null) {
      writer.write(NAME_PREFIX + "=" + getGenomeName() + '\n');
    }

    if (this.md5Sum != null) {
      writer.write(MD5_PREFIX + "=" + getMD5Sum() + '\n');
    }

    writer.write(SEQUENCES_COUNT_PREFIX + '=' + getSequenceCount() + '\n');

    writer.write(LENGTH_PREFIX + '=' + getGenomeLength() + '\n');

    for (String seqName : getSequencesNames()) {

      writer.write(
          SEQUENCE_PREFIX + seqName + "=" + getSequenceLength(seqName) + "\n");
    }

    writer.close();
  }

  /**
   * Save genome description.
   * @param file output file
   * @throws IOException if an error while writing the genome description
   */
  public void save(final File file) throws IOException {

    requireNonNull(file, "File is null");
    save(FileUtils.createOutputStream(file));
  }

  /**
   * Save genome description in UCSC TSV format.
   * @param os OutputStream to use for TSV file
   * @throws IOException if an error occurs while saving the genome description
   */
  public void saveTSV(final OutputStream os) throws IOException {

    requireNonNull(os, "OutputStream is null");

    try (Writer writer = new OutputStreamWriter(os)) {

      for (Map.Entry<String, Long> e : toMap().entrySet()) {
        writer.write(e.getKey() + '\t' + e.getValue() + '\n');
      }
    }
  }

  /**
   * Save genome description in UCSC TSV format.
   * @param os OutputStream to use for TSV file
   * @throws IOException if an error occurs while saving the genome description
   */
  public void saveTSV(final File file) throws IOException {

    requireNonNull(file, "File is null");
    saveTSV(FileUtils.createOutputStream(file));
  }

  //
  // Load description
  //

  /**
   * Load genome description.
   * @param is InputStream to use
   * @return a genome description object
   * @throws IOException if an error while loading the genome description
   */
  public static GenomeDescription load(final InputStream is)
      throws IOException {

    requireNonNull(is, "InputStream is null");

    final GenomeDescription result = new GenomeDescription();

    final BufferedReader read = FileUtils.createBufferedReader(is);

    String line = null;

    while ((line = read.readLine()) != null) {

      final List<String> fields = asList(line.split("="));

      if (fields.size() > 1) {

        final String key = fields.get(0).trim();

        if (key.startsWith(NAME_PREFIX)) {
          result.setGenomeName(fields.get(1));
        }
        if (key.startsWith(MD5_PREFIX)) {
          result.setMD5Sum(fields.get(1));
        } else {
          try {
            if (key.startsWith(SEQUENCE_PREFIX)) {
              result.addSequence(key.substring(SEQUENCE_PREFIX.length()),
                  Integer.parseInt(fields.get(1)));
            }
          } catch (NumberFormatException e) {

          }
        }
      }
    }

    is.close();

    return result;
  }

  /**
   * Load genome description.
   * @param file File to use
   * @return a genome description object
   * @throws IOException if an error while loading the genome description
   */
  public static GenomeDescription load(final File file) throws IOException {

    requireNonNull(file, "File is null");
    return load(FileUtils.createInputStream(file));
  }

  //
  // Static methods
  //

  /**
   * Create a GenomeDescription object from a Fasta file.
   * @param genomeFastaFile genome fasta file
   * @return a genome description object
   * @throws BadBioEntryException if an error occurs while creating the genome
   *           description
   * @throws IOException if an error occurs while reading the FASTA file
   */
  public static GenomeDescription createGenomeDescFromFasta(
      final File genomeFastaFile) throws BadBioEntryException, IOException {

    requireNonNull(genomeFastaFile, "The genome file is null");

    return createGenomeDescFromFasta(
        FileUtils.createInputStream(genomeFastaFile),
        genomeFastaFile.getName());
  }

  /**
   * Create a GenomeDescription object from a Fasta file.
   * @param genomeFastaIs genome fasta input stream
   * @param filename name of the file of the input stream
   * @return a genome description object
   * @throws BadBioEntryException if an error occurs while creating the genome
   *           description
   * @throws IOException if an error occurs while reading the FASTA file
   */
  public static GenomeDescription createGenomeDescFromFasta(
      final InputStream genomeFastaIs, final String filename)
      throws BadBioEntryException, IOException {

    return createGenomeDesc(genomeFastaIs, filename, false);
  }

  /**
   * Create a GenomeDescription object from a GFF file.
   * @param gffFile genome in GFF file
   * @return a genome description object
   * @throws BadBioEntryException if an error occurs while creating the genome
   *           description
   * @throws IOException if an error occurs while reading the GFF file
   */
  public static GenomeDescription createGenomeDescFromGFF(final File gffFile)
      throws BadBioEntryException, IOException {

    requireNonNull(gffFile, "The genome file is null");

    return createGenomeDescFromGFF(FileUtils.createInputStream(gffFile),
        gffFile.getName());
  }

  /**
   * Create a GenomeDescription object from a GFF file.
   * @param gffFile genome in GFF input stream
   * @param filename name of the file of the input stream
   * @return a genome description object
   * @throws BadBioEntryException if an error occurs while creating the genome
   *           description
   * @throws IOException if an error occurs while reading the GFF file
   */
  public static GenomeDescription createGenomeDescFromGFF(
      final InputStream gffFile, final String filename)
      throws BadBioEntryException, IOException {

    return createGenomeDesc(gffFile, filename, true);
  }

  /**
   * Create a GenomeDescription object from a Fasta file of GFF file.
   * @param genomeFastaIs genome fasta input stream
   * @param filename name of the file of the input stream
   * @param gffFormat the input file is in GFF format
   * @return a genome description object
   * @throws BadBioEntryException if an error occurs while creating the genome
   *           description
   * @throws IOException if an error occurs while reading the GFF file
   */
  public static GenomeDescription createGenomeDesc(
      final InputStream genomeFastaIs, final String filename,
      final boolean gffFormat) throws BadBioEntryException, IOException {

    requireNonNull(genomeFastaIs, "The input stream of the genome is null");

    final GenomeDescription result = new GenomeDescription();
    result.setGenomeName(StringUtils.basename(filename));

    MessageDigest md5Digest;
    try {
      md5Digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      md5Digest = null;
    }

    final FastaLineParser parser =
        new FastaLineParser(genomeFastaIs, gffFormat);

    final Alphabet alphabet = Alphabets.AMBIGUOUS_DNA_ALPHABET;
    String seqName = null;
    String lastSeqName = null;
    String parsedSeqName = null;
    long chrSize = 0;

    while ((seqName = parser.parseNextLineAndGetSequenceName()) != null) {

      if (!seqName.equals(lastSeqName)) {

        // Check if sequence has been found more than one time
        if (result.getSequenceLength(lastSeqName) != -1) {
          throw new BadBioEntryException(
              "Sequence name found twice: " + lastSeqName, lastSeqName);
        }

        // Add sequence
        if (lastSeqName != null) {
          result.addSequence(parsedSeqName, chrSize);
        }

        // Parse chromosome name
        parsedSeqName = parseChromosomeName(seqName);

        if (parsedSeqName == null) {
          throw new IOException("No fasta header found.");
        }

        // Update digest with chromosome name
        if (md5Digest != null) {
          md5Digest.update(parsedSeqName.getBytes(Charset.defaultCharset()));
        }

        lastSeqName = seqName;
        chrSize = 0;
      }

      final String sequence = parser.getSequence();
      if (sequence == null) {
        throw new IOException("No fasta sequence found.");
      }

      // Check the sequence and increment the length of the sequence
      chrSize += checkBases(sequence, lastSeqName, alphabet);

      // Update digest with chromosome sequence
      md5Digest.update(sequence.getBytes(Charset.defaultCharset()));
    }

    // Add the last sequence
    if (lastSeqName != null) {
      result.addSequence(parsedSeqName, chrSize);
    }

    // Compute final MD5 sum
    if (md5Digest != null) {
      result.setMD5Sum(md5DigestToString(md5Digest));
    }

    genomeFastaIs.close();

    return result;
  }

  /**
   * Create a GenomeDescription object from a UCSC TSV file (two fields:
   * chromosome name and chromosome length). No checksum will be computed when
   * using this method.
   * @param tsvFile TSV file
   * @param filename name of the file of the input stream
   * @return a genome description object
   * @throws IOException if an error occurs while reading the GFF file
   */
  public static GenomeDescription createGenomeDescFromTSV(final File tsvFile)
      throws IOException {

    return createGenomeDescFromTSV(FileUtils.createInputStream(tsvFile),
        tsvFile.getName());
  }

  /**
   * Create a GenomeDescription object from a UCSC TSV file(two fields:
   * chromosome name and chromosome length). No checksum will be computed when
   * using this method.
   * @param genomeFastaIs genome fasta input stream
   * @param filename name of the file of the input stream
   * @return a genome description object
   * @throws IOException if an error occurs while reading the GFF file
   */
  public static GenomeDescription createGenomeDescFromTSV(
      final InputStream tsvIs, final String filename) throws IOException {

    requireNonNull(tsvIs, "The input stream of the UCSC TSV file is null");

    final GenomeDescription result = new GenomeDescription();
    result.setGenomeName(StringUtils.basename(filename));

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(tsvIs))) {

      for (String line : (Iterable<String>) reader.lines()::iterator) {
        String[] fields = line.trim().split("\t");
        if (fields.length != 2) {
          continue;
        }

        result.addSequence(fields[0].trim(), Long.parseLong(fields[1]));
      }

    }

    return result;
  }

  private static String parseChromosomeName(final String fastaHeader)
      throws BadBioEntryException {

    if (fastaHeader == null) {
      return null;
    }

    if ("".equals(fastaHeader.trim())) {
      throw new BadBioEntryException("Sequence header is empty",
          ">" + fastaHeader);
    }

    if (fastaHeader.startsWith(" ")) {
      throw new BadBioEntryException(
          "A whitespace was found at the beginning of the sequence name",
          ">" + fastaHeader);
    }

    final String s = fastaHeader.trim();
    String[] fields = s.split("\\s");

    if (fields == null || fields.length == 0) {
      throw new BadBioEntryException("Invalid sequence header",
          ">" + fastaHeader);
    }

    return fields[0];
  }

  private static long checkBases(final String sequence,
      final String sequenceName, final Alphabet alphabet)
      throws BadBioEntryException {

    final char[] array = sequence.toCharArray();

    for (final char c : array) {
      if (!alphabet.isLetterValid(c)) {
        throw new BadBioEntryException("Invalid base in genome: " + c,
            sequenceName);
      }
    }

    return sequence.length();
  }

  //
  // Other methods
  //

  @Override
  public String toString() {

    return this.getClass().getSimpleName()
        + "{genomeName=" + this.genomeName + ", sequencesCount="
        + this.sequences.size() + ", md5Sum=" + this.md5Sum + ", sequences="
        + this.sequences + "}";
  }
}
