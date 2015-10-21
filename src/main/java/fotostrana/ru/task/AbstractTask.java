package fotostrana.ru.task;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.StorageRequests;
import fotostrana.ru.task.schedulers.AbstractScheduler;
import fotostrana.ru.task.schedulers.SchedulerWithLimitedThreads;

public abstract class AbstractTask implements Scheduler, EventListener,
		Comparable<AbstractTask> {
	public final int id;
	protected EventListener eventListener = TaskManager.TASK_MANAGER;
	/**
	 * Планировщик выполнения
	 */
	protected Scheduler scheduler;

	/**
	 * Время запланировано запуска
	 */
	protected Date scheduledTimeStart;

	/**
	 * периодичность выполнения
	 */
	protected Date executionPeriodicity;

	/**
	 * Время начала выполнения,null если задание небыло запущено
	 */
	protected Date timeStart;

	/**
	 * Время окончания выполнения,null если задание небыло завершено
	 */
	protected Date timeFinish;

	/**
	 * Описание задания
	 */
	protected String descriptionTask = "";
	/**
	 * Состояние выполнения задания
	 */
	protected String state = "Не выполняется";

	public AbstractTask() {
		id = TaskManager.TASK_MANAGER.getNextTaskId();
		scheduler = new SchedulerWithLimitedThreads(this);
	}

	@Override
	public Scheduler cloneScheduler() {
		return scheduler.cloneScheduler();
	}

	@Override
	public int compareTo(AbstractTask arg0) {
		return id - arg0.id;
	}

	@Override
	public boolean continueExecution() {
		return scheduler.continueExecution();
	};

	/**
	 * Выполняет задание, помещая запросы на выполнение
	 */
	protected abstract void execute();

	/**
	 * Выполняет одно подзадание
	 * 
	 * @return результат добавления, true если добавлен новый запрос, false
	 *         -если нельзя добавить еще один запрос
	 */
	public abstract boolean executeOneSubtask();

	/**
	 * Действия выполняемые после завершения выполнения
	 */
	public void finish() {
		timeFinish = new Date();

		state = "Завершено." + state + " Время : " + taskExecutionTime();
		Log.LOGGING.addTaskLog(getDescription() + getTaskState(),
				Log.TYPE_POSITIVE);
		eventListener.handleEvent(new EventCompliteTheTask(this));
	}

	@Override
	public int getCountCompletedTask() {
		return scheduler.getCountCompletedTask();
	}

	/**
	 * Описание задания в текстовом виде
	 * 
	 * @return
	 */
	public String getDescription() {
		return descriptionTask;
	}

	/**
	 * Возраащет слушателя событий задания
	 * 
	 * @return
	 */
	public EventListener getEventListener() {
		return eventListener;
	}

	/**
	 * периодичность выполнения
	 * 
	 * @return null если задание неприодично
	 */
	public Date getExecutionPeriodicity() {
		return executionPeriodicity;
	}

	/**
	 * Возращает отчет о задаче
	 * 
	 * @return
	 */
	public List<String[]> getReport() {
		return new LinkedList<String[]>();
	}

	/**
	 * Время запланировано запуска
	 * 
	 * @return null если задание неприодично
	 */
	public Date getScheduledTimeStart() {
		return scheduledTimeStart;
	}

	/**
	 * Возращает планировщика
	 * 
	 * @return
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public int getState() {
		return scheduler.getState();
	}

	@Override
	public StorageRequests getStorageRequests() {
		return scheduler.getStorageRequests();
	}

	/**
	 * Адресс по которому можно просмотреть выполнение задания
	 * 
	 * @return
	 */
	public abstract String getTargetUrl();

	/**
	 * Состояние выполнения задания в текстовом виде
	 * 
	 * @return
	 */
	public abstract String getTaskState();

	/**
	 * Проверяет закончилось ли выполнение задачи
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return (timeFinish != null);
	}

	/**
	 * Проверяет выполняется ли задача
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return (timeStart != null);
	}

	@Override
	public void pauseExecution() {
		scheduler.pauseExecution();

	}

	@Override
	public void setCountOfTaskToBePerformed(int countOfTaskToBePerformed) {
		scheduler.setCountOfTaskToBePerformed(countOfTaskToBePerformed);
	}

	/**
	 * Устанавливает слушателя событий
	 * 
	 * @param eventListener
	 */
	public void setEventListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}

	/**
	 * Задет периодичность выполнения задания
	 * 
	 * @param executionPeriodicity
	 *            null если задание неприодично
	 */
	public void setExecutionPeriodicity(Date executionPeriodicity) {
		this.executionPeriodicity = executionPeriodicity;
	}

	/**
	 * Задет время запланированого запуска
	 * 
	 * @param scheduledTimeStart
	 *            null -если нужно убрать время запуска
	 */
	public void setScheduledTimeStart(Date scheduledTimeStart) {
		this.scheduledTimeStart = scheduledTimeStart;
	}

	/**
	 * Устанавливает планировщик выполнения, устанавливать нужно до запуска
	 * выполнения
	 * 
	 * @param scheduler
	 * @return false - если неудалось установить новый планировщик
	 */
	public boolean setScheduler(Scheduler scheduler) {
		if (scheduler.getState() == STATE_NOT_STARTED) {
			scheduler.setCountOfTaskToBePerformed(this.scheduler
					.getCountOfTaskToBePerformed());
			((AbstractScheduler) scheduler).setTask(this);
			this.scheduler = scheduler;
			return true;
		}
		return false;
	}

	@Override
	public void setStorageRequests(StorageRequests sourceRequests) {
		if (sourceRequests != null) {
			scheduler.setStorageRequests(sourceRequests);
		}

	}

	@Override
	public void start() {
		timeStart = new Date();
		execute();
	}

	@Override
	public void stop() {
		scheduler.stop();
	}

	/**
	 * Возращает время выполнения задачи, если задача еще невыполнена, то время
	 * от начала выполения до сейчас
	 * 
	 * @return пустая строка, если задача еще не начала выполнятся
	 * 
	 */
	public String taskExecutionTime() {
		if ((timeStart == null))
			return "";
		Date f = timeFinish;
		if (f == null)
			f = new Date();
		int sec = (int) (f.getTime() - timeStart.getTime()) / 1000;
		if (sec < 1) {
			return "1 секунда";
		}
		int minute = sec / 60;
		sec = sec % 60;
		int hour = minute / 60;
		minute = minute % 60;
		String result = "";
		if (hour != 0)
			result = result + hour + " часов ";
		if (minute != 0)
			result = result + minute + " минут ";
		if (sec != 0) {
			result = result + sec + " секудн";
		}

		return result;
	}

}
