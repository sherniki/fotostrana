package fotostrana.ru.network;

/**
 * Интерфейс адаптера для библиотеки выполнения запросов
 * 
 */
public interface LibraryExecutionRequests {

	/**
	 * Выполняет запрос
	 * 
	 * @param request
	 *            запрос который необходимо выполнить
	 * @return результат запроса, null -если неудалось получить результат
	 * @throws Exception
	 *             ошибки возникшие при запросе
	 */
	String executeRequest(Request request) throws Exception;

	/**
	 * Завершает работу с соединением
	 */
	void close();

	/**
	 * Возращает соединение к которому будет относится адаптер
	 * 
	 * @return
	 */
	Connection getConnection();

	/**
	 * Изменяет адресс прокси на тот который установлен у соединения
	 * 
	 * @return true если адресс изменен, false - адресс неудалось изменить
	 *         адресс
	 */
	boolean changeProxy();

}
