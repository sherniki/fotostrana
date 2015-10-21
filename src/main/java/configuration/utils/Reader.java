package configuration.utils;

import configuration.IConfiguration;

/**
 * Интерфейс для заполнения конфигурации
 * 
 */
abstract public class Reader {
	protected IConfiguration configuration;

	/**
	 * читає конфігурацію
	 * 
	 * @param path
	 *            адреса файлу з конфігурацією
	 */
	abstract public void load(String path);

	/**
	 * встановлює обьект конфігурації в який буде відбуватися читання
	 * 
	 * @param configuration
	 */
	public void setConfiguration(IConfiguration configuration) {
		this.configuration = configuration;
	}

}
