package fotostrana.ru.reports;

/**
 * Отчет о состоянии менеджера сети
 * 
 */
public class StatusReportNetworkManager implements Report {
	/**
	 * Количество запросов в очереди на выполнение
	 */
	public int countRequests;
	/**
	 * Количество соединений
	 */
	public int countConnections;
	/**
	 * Количество работающих соединений
	 */
	public int countWorkingConnections;
	/**
	 * Количество спящих соединений
	 */
	public int countSleepingConnections;
	/**
	 * Количество соединений ожидающих ответа
	 */
	public int countWaitingResponse;

	/**
	 * Количество забаненых прокси
	 */
	public int countBanned;

	/**
	 * Скорость выполнения запросов
	 */
	public int speedOfExecution;
}
