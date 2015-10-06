package fotostrana.ru.task.schedulers;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.Scheduler;

/**
 * Планировщик с ограниченным количеством потоков выполнения
 * 
 */
public class SchedulerWithLimitedThreads extends AbstractScheduler {

	/**
	 * Максимальное количество одновременных запущенных заданий
	 */
	protected int maximumCount;
	/**
	 * Количество запущеных задач на текущий момент
	 */
	protected int currentСount;

	public SchedulerWithLimitedThreads(AbstractTask task) {
		super(task);
		maximumCount = 1000000;
		currentСount = 0;
	}

	/**
	 * @param maximumCount
	 *            количество максимально запущеных заданий
	 */
	public SchedulerWithLimitedThreads(AbstractTask task, int maximumCount) {
		this(task);
		setMaximumCountConcurentTask(maximumCount);
	}

	/**
	 * @param maximumCount
	 *            количество максимально запущеных заданий
	 * @param countTask
	 *            количество заданий которое необходимо выполнить
	 */
	public SchedulerWithLimitedThreads(AbstractTask task, int maximumCount,
			int countTask) {
		this(task, maximumCount);
		setCountOfTaskToBePerformed(countTask);
	}

	// public SchedulerWithLimitedThreads(AbstractTask task, int maximumCount) {
	// this(maximumCount);
	// setTask(task);
	// }

	@Override
	public void start() {
		super.start();
		increaseToMaximumAmount();
		if (isFinish())
			stop();
	}

	@Override
	public boolean continueExecution() {
		if (super.continueExecution()) {
			increaseToMaximumAmount();
			return true;
		} else
			return false;
	};

	/**
	 * Возращает количество одновременно выполняемых заданий на текущий
	 * момент,не устанавливается в 0 сразу после паузы
	 * 
	 * @return
	 */
	public int getCurrentCount() {
		return currentСount;
	}

	/**
	 * Возращает максимальное количество одновременно выполняемых заданий
	 * 
	 * @return
	 */
	public int getMaximumCountConcurentTask() {
		return maximumCount;
	}

	/**
	 * Устанавливает максимальное количество одновременно выполняющихся заданий
	 * 
	 * @param maximumCount
	 *            недолжно быть меньшим 1, для остановки выполнения использовать
	 *            pauseExecution()
	 */
	public synchronized void setMaximumCountConcurentTask(int maximumCount) {
		if (maximumCount > 0) {
			this.maximumCount = maximumCount;
			increaseToMaximumAmount();
		}
	}

	/**
	 * Увеличивает количество запущеных задач до максимума
	 */
	protected synchronized void increaseToMaximumAmount() {
		if (state == STATE_RUN)
			while (isPossibleToExecuteANewTask()) {
				if (!executeOneTask()) {
					countOfExecutedTasksToComplete = currentСount;
					break;
				}
			}
	}

	@Override
	protected synchronized boolean executeOneTask() {
		currentСount++;
		boolean result = abstractTask.executeOneSubtask();
		if (!result) {
			currentСount--;
		}
		return result;
	}

	/**
	 * Проверяет можно ли выполнить еще одну задачу
	 * 
	 * @return true если можно выполнить
	 */
	@Override
	protected boolean isPossibleToExecuteANewTask() {
		if (currentСount < maximumCount)
			if (currentСount + countCompletedTask < countOfExecutedTasksToComplete)
				return true;
		return false;
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (!(event instanceof SchedulerEvent)) {
			return;
		}
		currentСount--;
		super.handleEvent(event);
	}

	@Override
	public Scheduler cloneScheduler() {
		SchedulerWithLimitedThreads clone = new SchedulerWithLimitedThreads(
				abstractTask, maximumCount, this.getCountOfTaskToBePerformed());
		clone.storageRequests = this.storageRequests;
		return clone;
	}

}
