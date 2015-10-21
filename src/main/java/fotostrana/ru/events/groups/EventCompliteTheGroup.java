package fotostrana.ru.events.groups;

import fotostrana.ru.task.GroupTask;

/**
 * Выполнение группы заданий завершено
 * 
 */
public class EventCompliteTheGroup extends EventGroup {

	public EventCompliteTheGroup(GroupTask groupTask) {
		super(groupTask);
	}

}
