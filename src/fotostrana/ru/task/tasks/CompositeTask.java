package fotostrana.ru.task.tasks;

import java.util.Date;

import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.GroupTask;
import fotostrana.ru.task.Scheduler;

/**
 * Задание состоящее из других заданий, одновреммено может выполнятся только
 * одно задание; Изменение планировщика для этого задания приводит к изменнию
 * планировщика каждого подзадания
 * 
 */
public abstract class CompositeTask extends GroupTask {
	protected AbstractTask currentTask;

	@Override
	protected void execute() {
		timeStart = new Date();
		currentTask.start();
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/";
	}

	@Override
	public String getTaskState() {
		if (this.getState() == STATE_COMPLETED)
			return state;
		else
			return currentTask.getTaskState();
	}

	@Override
	public boolean setScheduler(Scheduler scheduler) {
		setTasksScheduler(scheduler);
		return true;
	}

	@Override
	public int getState() {
		return currentTask.getState();
	}
}
