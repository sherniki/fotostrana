package fotostrana.ru.events.tasks;

import fotostrana.ru.events.Event;
import fotostrana.ru.task.AbstractTask;

/**
 * События от заданий
 * 
 */
public class EventTask implements Event {

	protected AbstractTask task;

	/**
	 * Событие от задания
	 * 
	 * @param abstractTask
	 * 
	 */
	public EventTask(AbstractTask abstractTask) {
		this.task = abstractTask;
	}

	public AbstractTask getTask() {
		return task;
	}

}
