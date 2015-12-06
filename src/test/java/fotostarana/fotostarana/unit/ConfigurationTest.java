package fotostarana.fotostarana.unit;

import static org.junit.Assert.*;
import org.junit.Test;

import configuration.ApplicationConfiguration;
import configuration.utils.xml.XMLReader;

public class ConfigurationTest {
  private static String CONFIG_FILE = "test/configuration.xml";

  @Test
  public void testReadConfig() {
    ApplicationConfiguration config = ApplicationConfiguration.INSTANCE;
    XMLReader xmlReader = new XMLReader(config);
    xmlReader.load(CONFIG_FILE);
    assertTrue(config.getValue("test.string.empty").isEmpty());
    assertEquals(config.getValue("test.string.value1"), "value1");
    assertEquals(config.getValue("test.nonexistent.key"), null);
    assertEquals(config.getValue("test.nonexistent.key", "defaultValue"), "defaultValue");
    assertEquals(config.getIntValue("test.int.value1"), new Integer(12));
    assertEquals(config.getIntValue("test.int.empty"), null);
    assertEquals(config.getIntValue("test.int.doublevalue"), null);
    assertEquals(config.getIntValue("test.int.incorrectvalue"), null);
  }
}
