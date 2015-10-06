package fotostrana.ru.task.schedulers;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.StorageRequests;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.Scheduler;

/**
 * Абстрактный планировщик
 * 
 */
public abstract class AbstractScheduler implements Scheduler, EventListener {

	/**
	 * Источник в который будут помещятся запросы для выполнения
	 */
	protected StorageRequests storageRequests = NetworkManager.NETWORK_MANAGER
			.getStorageRequests();
	/**
	 * Состояние планировщика
	 */
	protected volatile int state;

	/**
	 * Щадание планирование которого планируется
	 */
	protected AbstractTask abstractTask;

	/**
	 * Количество выполненых заданий
	 */
	protected int countCompletedTask;

	/**
	 * Количество задач которое необходимо выполнить
	 */
	protected int countOfTaskToBePerformed;

	/**
	 * количество выполненных заданий, после которого выполнение планируемое
	 * задания будет считаться завершенным
	 */
	protected int countOfExecutedTasksToComplete;

	public AbstractScheduler(AbstractTask abstractTask) {
		this.abstractTask = abstractTask;
		state = STATE_NOT_STARTED;
		countOfTaskToBePerformed = 0;
		countCompletedTask = 0;
		countOfExecutedTasksToComplete = 0;
	}

	/**
	 * Выполняет одно задание,
	 * 
	 * @return результат выполнения, false -если неудалось выполнить,true - если
	 *         успешно,при этом увеличивает количество выполняемых заданий
	 */
	protected boolean executeOneTask() {
		return abstractTask.executeOneSubtask();
	}

	/**
	 * Проверяет, завершилось выполнение или нет
	 * 
	 * @return true, если выполнение завершилось
	 */
	protected boolean isFinish() {
		return countCompletedTask == countOfExecutedTasksToComplete;
	}

	@Override
	public void handleEvent(Event event) {
		if (!(event instanceof SchedulerEvent)) {
			return;
		}

		if (event instanceof SuccessfulEvent) {
			countCompletedTask++;
		}

		if (state == STATE_RUN)
			if (isPossibleToExecuteANewTask()) {
				if (!executeOneTask()) {
					countOfExecutedTasksToComplete--;
				}
			}
		if (isFinish())
			stop();
	}

	/**
	 * Проверяет можно ли выполнить еще одну задачу
	 * 
	 * @return true если можно выполнить
	 */
	protected abstract boolean isPossibleToExecuteANewTask();

	/**
	 * Останавливает выполнение с возможностью продолжить методом
	 * continueExecution
	 */
	public void pauseExecution() {
		state = STATE_PAUSE;
	};

	/**
	 * Действия выполняемые при завершении выполнения задания
	 */
	public void stop() {
		if (state != STATE_COMPLETED) {
			state = STATE_COMPLETED;
			abstractTask.finish();
		}
	}

	/**
	 * Возращает сосстояние в котором находится планировщик
	 * 
	 * @return
	 */
	public int getState() {
		return state;
	}

	/**
	 * Количество заданий которое необходимо выполнить
	 * 
	 * @return
	 */
	public int getCountOfTaskToBePerformed() {
		return countOfTaskToBePerformed++;
	}

	/**
	 * Задает количество заданий которое необходимо выполнить
	 * 
	 * @param countOfTaskToBePerformed
	 */
	public void setCountOfTaskToBePerformed(int countOfTaskToBePerformed) {
		if (state == STATE_NOT_STARTED) {
			this.countOfTaskToBePerformed = countOfTaskToBePerformed;
			countOfExecutedTasksToComplete = countOfTaskToBePerformed;
		}
	}

	/**
	 * Количество выполненых заданий
	 * 
	 * @return
	 */
	public int getCountCompletedTask() {
		return countCompletedTask;
	}

	@Override
	public void setStorageRequests(StorageRequests sourceRequests) {
		if (sourceRequests != null) {
			this.storageRequests = sourceRequests;
		}

	}

	@Override
	public StorageRequests getStorageRequests() {
		return storageRequests;
	}

	/**
	 * Возобновляет работу после вызова pauseExecution
	 * 
	 * @return true если планирощик успешно возобновил работу
	 */
	public boolean continueExecution() {
		if (state == STATE_PAUSE) {
			state = STATE_RUN;
			return true;
		} else
			return false;
	};

	/**
	 * Запускает планировщик
	 */
	public void start() {
		state = STATE_RUN;
	}

	public void setTask(AbstractTask task) {
		this.abstractTask = task;
	}

	public AbstractTask getTask() {
		return abstractTask;
	}

}
