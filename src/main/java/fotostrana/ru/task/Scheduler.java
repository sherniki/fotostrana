package fotostrana.ru.task;

import fotostrana.ru.events.Event;
import fotostrana.ru.network.StorageRequests;

/**
 * Планировщик выполнения
 * 
 */
public interface Scheduler {
	final static int STATE_RUN = 0;
	final static int STATE_PAUSE = 1;
	final static int STATE_NOT_STARTED = 2;
	final static int STATE_COMPLETED = 3;

	/**
	 * Запускает планировщик
	 */
	void start();

	/**
	 * Возобновляет работу после вызова pauseExecution
	 * 
	 * @return true если планирощик успешно возобновил работу
	 */
	boolean continueExecution();

	/**
	 * Останавливает выполнение с возможностью продолжить методом
	 * continueExecution
	 */
	void pauseExecution();

	/**
	 * Останавливает выполнения без возможности продолжения
	 */
	void stop();

	/**
	 * Возращает сосстояние в котором находится планировщик
	 * 
	 * @return
	 */
	int getState();

	/**
	 * Количество выполненых заданий
	 * 
	 * @return
	 */
	int getCountCompletedTask();

	/**
	 * Количество заданий которое необходимо выполнить
	 * 
	 * @return
	 */
	int getCountOfTaskToBePerformed();

	/**
	 * Задает количсетво заданий которое необходимо выполнить
	 * 
	 * @param countOfTaskToBePerformed
	 */
	void setCountOfTaskToBePerformed(int countOfTaskToBePerformed);

	/**
	 * Задает источник в который будут помещяться запросы для выполнения
	 * 
	 * @param sourceRequests
	 *            источник запросов
	 */
	void setStorageRequests(StorageRequests sourceRequests);

	/**
	 * Возращает источник запросов
	 * 
	 * @return
	 */
	StorageRequests getStorageRequests();

	Scheduler cloneScheduler();

	void handleEvent(Event event);

}
