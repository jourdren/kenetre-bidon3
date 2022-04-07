package fr.ens.biologie.genomique.kenetre.illumina;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

public class PrimaryAnalysisMetricsTest {

  private static PrimaryAnalysisMetrics metrics;

  @BeforeClass
  public static void load() throws IOException {

    InputStream in = RunInfoTest.class.getClassLoader()
        .getResourceAsStream("PrimaryAnalysisMetrics.csv");

    metrics = new PrimaryAnalysisMetrics(in);
  }

  @Test
  public void testGetAverageQ30() {
    assertEquals(0.931, metrics.getAverageQ30(), 0.001);
  }

  @Test
  public void testGetTotalYield() {
    assertEquals(72.45e9, metrics.getTotalYield(), 0.01e9);
  }

  @Test
  public void testGetTotalReads() {
    assertEquals(536.70e6, metrics.getTotalReadsPF(), 0.01e6);
  }

  @Test
  public void testGetLoadingConcentrationPercent() {
    assertEquals(0.998, metrics.getLoadingConcentrationPercent(), 0.001);
  }

}
