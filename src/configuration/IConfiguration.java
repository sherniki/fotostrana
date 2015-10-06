package configuration;

import java.util.List;

/**
 * Интерфейс конфигурации
 */
public interface IConfiguration {

	/**
	 * Возвращает зачение по ключу
	 * 
	 * @param key
	 *            -ключ
	 * @param Value
	 *            -значение
	 */
	public void setValue(String key, String Value);

	/**
	 * Возращает значение по ключу
	 * 
	 * @param key
	 *            -ключ
	 * @return - значение
	 */
	public String getValue(String key);

	/**
	 * Возращает все ключи
	 * 
	 * @return - все ключи
	 */
	public List<String> getAllKey();

}
