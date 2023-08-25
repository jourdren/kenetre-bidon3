package fr.ens.biologie.genomique.kenetre.nanopore.samplesheet.io;

import java.io.IOException;

import fr.ens.biologie.genomique.kenetre.nanopore.samplesheet.SampleSheet;

/**
 * This interface define a reader for Nanopore software sample sheets.
 * @since 0.20
 * @author Laurent Jourdren
 */
public interface SampleSheetReader extends AutoCloseable {

  /**
   * Read a sample sheet.
   * @return a SampleSheet object
   * @throws IOException if an error occurs while reading the sample sheet
   */
  SampleSheet read() throws IOException;

  @Override
  void close() throws IOException;
}