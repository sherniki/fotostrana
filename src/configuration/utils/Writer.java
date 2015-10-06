/**
 * Пакет с утилитами
 */
package configuration.utils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import configuration.IConfiguration;

/**
 * Интерфейс для записи конфигурации
 *
 */
public abstract class Writer {

	protected  IConfiguration configuration;

	/** зберігає конфігурацію у файл
	 * @param path адреса файлу
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	abstract public void save(String path) throws IOException, ParserConfigurationException;

	/**встановлює конфігурацію яку необходно зберегти
	 * @param configuration конфігурація
	 */
	public void setConfiguration(IConfiguration configuration) {
		this.configuration = configuration;
	}

}