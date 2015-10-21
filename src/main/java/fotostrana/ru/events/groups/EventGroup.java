package fotostrana.ru.events.groups;

import fotostrana.ru.events.Event;
import fotostrana.ru.task.GroupTask;

/**
 * Событие от группы заданий
 * 
 */
public class EventGroup implements Event {
	/**
	 * Группа от которой исходит событие
	 */
	private GroupTask groupTask;

	public EventGroup(GroupTask groupTask) {
		setGroupTask(groupTask);
	}

	/**
	 * Возращает группу ото которой произошло событие
	 * 
	 * @return
	 */
	public GroupTask getGroupTask() {
		return groupTask;
	}

	/**
	 * Задает группу от которой произошло событие
	 * 
	 * @param groupTask
	 */
	public void setGroupTask(GroupTask groupTask) {
		this.groupTask = groupTask;
	}

}
