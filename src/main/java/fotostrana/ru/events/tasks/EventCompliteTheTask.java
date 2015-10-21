package fotostrana.ru.events.tasks;

import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.task.AbstractTask;

/**
 * Событие выполнение задания
 * 
 */
public class EventCompliteTheTask extends EventTask implements SuccessfulEvent,SchedulerEvent {

	/**
	 * Событие выполнение задания
	 * 
	 * @param abstractTask
	 *            выполненое задание
	 */
	public EventCompliteTheTask(AbstractTask abstractTask) {
		super(abstractTask);
	}
}
