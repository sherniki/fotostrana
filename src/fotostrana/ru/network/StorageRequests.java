package fotostrana.ru.network;

import fotostrana.ru.events.EventListener;

/**
 * Интерфейс хранилища запросов; через него соединение получает новые запросы и
 * информирует о своем состоянии
 * 
 */
public interface StorageRequests extends EventListener {

	/**
	 * Возращает следующий запрос
	 * 
	 * @param connection
	 *            соединение которому нужен запрос
	 * @return null если нет запросов для этого соединения
	 */
	public Request getNextRequest(Connection connection);

	/**
	 * Добавляет запрос в источник
	 * 
	 * @param request
	 *            новый запрос
	 */
	public void addRequest(Request request);

	/**
	 * Количестов запроов в хранилище
	 * 
	 * @return
	 */
	public int getCountRequest();

}
