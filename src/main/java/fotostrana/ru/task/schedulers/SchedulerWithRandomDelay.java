package fotostrana.ru.task.schedulers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.schedulers.EventTimeoutExpired;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.Scheduler;

/**
 * Планировщик со случайной задержкой между выполнением заданий
 * 
 */
public class SchedulerWithRandomDelay extends AbstractScheduler {

	/**
	 * Минимальное время задержки, в секундах
	 */
	protected int minDelay;
	/**
	 * Максимально время задержи в секундах
	 */
	protected int maxDelay;

	/**
	 * Флаг разрешения от таймера
	 */
	protected boolean timerFlag;
	/**
	 * Флаг разрешения от задания
	 */
	protected boolean taskFlag;
	protected Random random = new Random();
	protected Timer timer;

	/**
	 * @param minDelay
	 *            минимальная задержка
	 * @param maxDelay
	 *            максимальная задержка
	 */
	public SchedulerWithRandomDelay(AbstractTask task, int minDelay,
			int maxDelay) {
		super(task);
		timerFlag = false;
		taskFlag = true;
		this.maxDelay = maxDelay;
		this.minDelay = minDelay;
		if (minDelay >= maxDelay)
			this.maxDelay = minDelay + 1;
		ActionListener timerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				timerFlag = true;
				handleEvent(new EventTimeoutExpired());
				timer.setDelay(getSleepInterval());
			}
		};

		timer = new Timer(getSleepInterval(), timerListener);

	}

	@Override
	protected boolean isPossibleToExecuteANewTask() {
		if (timerFlag && taskFlag)
			if (countCompletedTask < countOfExecutedTasksToComplete)
				return true;
		return false;
	}

	@Override
	public void start() {
		super.start();
		timer.start();
	}

	/**
	 * Возращает интервал ожидания
	 * 
	 * @return
	 */
	protected int getSleepInterval() {
		return minDelay * 1000 + random.nextInt((maxDelay - minDelay) * 1000);
	}

	@Override
	protected boolean executeOneTask() {
		timerFlag = false;
		boolean r = abstractTask.executeOneSubtask();
		taskFlag = !r;
		return r;
	}

	@Override
	public void stop() {
		super.stop();
		timer.stop();
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (event instanceof EventTimeoutExpired)
			timerFlag = true;
		if ((event instanceof EventOfNetworkRequests)
				|| (event instanceof EventCompliteTheTask))
			taskFlag = true;
		super.handleEvent(event);
	}

	@Override
	public Scheduler cloneScheduler() {
		SchedulerWithRandomDelay clone = new SchedulerWithRandomDelay(
				abstractTask, minDelay, maxDelay);
		clone.countOfTaskToBePerformed = this.getCountOfTaskToBePerformed();
		clone.storageRequests = this.storageRequests;
		return clone;
	}

}
