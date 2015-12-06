package configuration;

import configuration.Configuration;
import configuration.IConfiguration;
import configuration.utils.xml.XMLReader;

import java.util.List;

import fotostrana.ru.log.Log;

/**
 * Конфигурация приложения
 * 
 */
public enum ApplicationConfiguration implements IConfiguration {
  INSTANCE;

  private Configuration configuration;

  private ApplicationConfiguration() {
    configuration = new Configuration();
  }

  public void setValue(String key, String value) {
    configuration.setValue(key, value);
  }

  public String getValue(String key) {
    String value = configuration.getValue(key);
    if (value != null)
      value = value.trim();
    return value;
  }

  public List<String> getAllKey() {
    return configuration.getAllKey();
  }

  /**
   * Возращает числовое значение
   * 
   * @param key
   * @return null - если нет значения
   */
  public Integer getIntValue(String key) {
    String sValue = getValue(key);
    Integer result = null;
    try {
      result = new Integer(sValue);
    } catch (Exception e) {
      Log.LOGGING.addLog("В настройках неверный параметр : " + key);
    }
    return result;
  }

  /**
   * Возращает числовое значение, если такого значения нет или значение <=0,
   * возращается значение поумолчанию
   * 
   * @param key
   *          ключ
   * @param defaultValue
   *          значение по умолчанию
   * @return
   */
  public Integer getIntValue(String key, int defaultValue) {
    String sValue = getValue(key);
    Integer result = null;
    try {
      Integer configValue = new Integer(sValue);
      if (configValue > 0)
        result = configValue;
    } catch (Exception e) {
      Log.LOGGING.addLog("В настройках неверный параметр : " + key);
    }
    if (result == null) {
      result = defaultValue;
      setValue(key, result.toString());
    }
    return result;
  }

  /**
   * Возращает значение из файла конфигурации, если даного значения нет, то
   * возращает значение по умолчанию
   * 
   * @param key
   *          ключ
   * @param defaultValue
   *          значение по умолчанию
   * @return
   */
  public String getValue(String key, String defaultValue) {
    String configurationValue = ApplicationConfiguration.INSTANCE.getValue(key);
    if (configurationValue != null) {
      return configurationValue;
    } else {
      setValue(key, defaultValue);
      return defaultValue;
    }
  }

  public ApplicationConfiguration readFile(String file) {
    if (file.endsWith("xml")) {
      XMLReader xmlReader = new XMLReader(this);
      xmlReader.load(file);
    }
    return this;
  }

}