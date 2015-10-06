package fotostrana.ru.reports;

public class StatusReportProxyManager implements Report {

	/**
	 * Количество резервных прокси
	 */
	public int countFree;
	/**
	 * Количество забаненых прокси
	 */
	public int countBanned;

	/**
	 * Количество неработающих прокси
	 */
	public int countNotWorking;

	/**
	 * Длина очереди тестировщика прокси
	 */
	public int sizeQueueProxyChecker;

}
