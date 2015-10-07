package fotostrana.ru.network.filters.proxy;

import fotostrana.ru.network.proxy.AddressProxy;

/**
 * Фильтр предназначен для получения списка прокси удовлетворяющего заданым
 * критериям отбора из списка рабочих прокси
 * 
 */
public interface ProxyFilter {

	/**
	 * Проверяет удовлетворяет ли прокси заданый критерий
	 * 
	 * @param proxy
	 *            адресс прокси
	 * @return результат отбора, true - подходит, false - отбрасывается
	 */
	boolean filtrate(AddressProxy proxy);
}
