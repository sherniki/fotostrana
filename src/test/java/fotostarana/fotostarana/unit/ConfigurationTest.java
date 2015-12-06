package fotostarana.fotostarana.unit;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import configuration.ApplicationConfiguration;

public class ConfigurationTest {
  private static String CONFIG_FILE = "test/configuration.xml";

  @Test
  public void testReadConfig() {
    File file=new File(CONFIG_FILE);
    System.out.println("file:"+file.getAbsolutePath());
    ApplicationConfiguration config = ApplicationConfiguration.INSTANCE.readFile(CONFIG_FILE);
    assertEquals(config.getValue("configuration.test.string.empty"),null);
    assertEquals(config.getValue("configuration.test.string.value1"), "value1");
    assertEquals(config.getValue("configuration.test.nonexistent.key"), null);
    assertEquals(config.getValue("configuration.test.nonexistent.key", "defaultValue"), "defaultValue");
    assertEquals(config.getIntValue("configuration.test.int.value1"), new Integer(12));
    assertEquals(config.getIntValue("configuration.test.int.empty"), null);
    assertEquals(config.getIntValue("configuration.test.int.doublevalue"), null);
    assertEquals(config.getIntValue("configuration.test.int.incorrectvalue"), null);
  }
}
