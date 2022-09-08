package fr.ens.biologie.genomique.kenetre.bio.readfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.ens.biologie.genomique.kenetre.KenetreException;
import fr.ens.biologie.genomique.kenetre.bio.ReadSequence;
import fr.ens.biologie.genomique.kenetre.bio.readfilter.ReadFilter;
import fr.ens.biologie.genomique.kenetre.bio.readfilter.TrailingTrimmerReadFilter;

public class TrailingTrimmerReadFilterTest {
  @Test
  public void TrailingTirmmertest() throws KenetreException {
    ReadFilter filter = new TrailingTrimmerReadFilter();

    filter.setParameter("arguments", "33");
    filter.init();
    assertFalse(filter.accept(null));
    ReadSequence read = new ReadSequence("read1", "AGG", "CBA");
    assertTrue(filter.accept(read));
    assertEquals("read1", read.getName());
    assertEquals("AG", read.getSequence());
    assertEquals("CB", read.getQuality());

    filter = new TrailingTrimmerReadFilter();
    filter.setParameter("arguments", "33");
    filter.init();
    read = new ReadSequence("read2", "AAGGCTT", "CABA;:9");
    assertTrue(filter.accept(read));
    assertEquals("read2", read.getName());
    assertEquals("AAG", read.getSequence());
    assertEquals("CAB", read.getQuality());
    assertFalse(filter.accept(null));
  }
}
