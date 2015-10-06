package fotostrana.ru.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.tasks.EventTask;

/**
 * Обьединяет задачи в группы, позволяет планировать групповое выполнение задач
 * 
 */
public class GroupTask extends AbstractTask {
	public static final String DEFAULT_NAME = "Группа";

	/**
	 * Имя группы
	 */
	protected String name;
	/**
	 * Список заданий
	 */
	protected List<AbstractTask> tasks;

	public GroupTask() {
		super();
		setName(DEFAULT_NAME);
		tasks = new ArrayList<AbstractTask>();
	}

	public GroupTask(String name) {
		this();
		this.name = name;
	}

	/**
	 * Добавляет задание к группе
	 * 
	 * @param task
	 * @return false если задание уже есть в группе или уже выполнено
	 */
	public boolean addTask(AbstractTask task) {
		if ((task == null) || (task.isFinished()))
			return false;
		if (TaskManager.TASK_MANAGER.getTasks().contains(task))
			return false;
		boolean result = tasks.add(task);
		if (result) {
			task.setEventListener(this);
			// task.setScheduler(new SchedulerTaskWithLimitedThreads(task, 1));
			scheduler.setCountOfTaskToBePerformed(tasks.size());
		}
		return result;
	}

	/**
	 * Добавлет к группе список заданий
	 * 
	 * @param addTasks
	 *            список заданий
	 */
	public void addTasks(Collection<AbstractTask> addTasks) {
		if (addTasks != null) {
			Collection<AbstractTask> removeTask = new LinkedList<AbstractTask>();
			for (AbstractTask task : addTasks) {
				if (!addTask(task))
					removeTask.add(task);
			}
			addTasks.removeAll(removeTask);
		}
	}

	@Override
	protected void execute() {
		scheduler.start();
	}

	@Override
	public boolean executeOneSubtask() {
		AbstractTask executeTask = null;
		for (AbstractTask task : getTasks())
			if (task.getState() == STATE_NOT_STARTED) {
				executeTask = task;
				break;
			}
		if (executeTask != null) {
			// currentСount.incrementAndGet();
			executeTask.start();
			return true;
		} else
			return false;
	}

	@Override
	public void finish() {
		state = name + ".";
		super.finish();
	}

	@Override
	public int getCountOfTaskToBePerformed() {
		return scheduler.getCountOfTaskToBePerformed();
	}

	public String getName() {
		return name;
	}

	@Override
	public String getTargetUrl() {
		return "";
	}

	/**
	 * Возращает задание по идексу
	 * 
	 * @param index
	 * @return null, если неверный индекс
	 */
	public AbstractTask getTask(int index) {
		if ((index > -1) && (index < tasks.size())) {
			int i = 0;
			for (AbstractTask task : tasks) {
				if (index == i)
					return task;
				i++;
			}
		}
		return null;
	}

	public List<AbstractTask> getTasks() {
		return tasks;
	}

	// @Override
	// public int compareTo(GroupTask o) {
	// return name.compareTo(o.getName());
	// }

	@Override
	public String getTaskState() {
		return state;
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (event instanceof EventTask) {
			handleEventTask((EventTask) event);
		}
		scheduler.handleEvent(event);
	}

	/**
	 * Обрабатывает события от заданий
	 * 
	 * @param event
	 */
	protected void handleEventTask(EventTask event) {

	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setScheduledTimeStart(Date scheduledTimeStart) {
		super.setScheduledTimeStart(scheduledTimeStart);
		for (AbstractTask task : tasks) {
			task.setScheduledTimeStart(scheduledTimeStart);
		}
	}

	public void setTasksScheduler(Scheduler taskScheduler) {
		if (taskScheduler == null)
			return;
		for (AbstractTask task : tasks) {
			Scheduler clone = taskScheduler.cloneScheduler();
			task.setScheduler(clone);
		}
	}

}
