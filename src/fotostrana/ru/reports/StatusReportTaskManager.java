package fotostrana.ru.reports;

public class StatusReportTaskManager implements Report {
	/**
	 * Количество всех заданий
	 */
	public int countTasks;
	/**
	 * Количество выполненых заданий
	 */
	public int countCompleted;
	/**
	 * Количество выполняющихся заданий
	 */
	public int countRunning;
	/**
	 * Количество незапущеных заданий
	 */
	public int countNotRunning;
}
