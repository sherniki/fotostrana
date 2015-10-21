package fotostrana.ru.events.groups;

import fotostrana.ru.task.GroupTask;

/**
 * Успешно накручены голоса в группе заданий
 * 
 */
public class SuccessfulVotesInGroup extends EventGroup {
	/**
	 * Количество голосов
	 */
	private int countVotes = 1;

	/**
	 * Успешно накручен 1 голос
	 * 
	 * @param group
	 *            группа в которой произошло событие
	 */
	public SuccessfulVotesInGroup(GroupTask group) {
		this(group, 1);
	}

	/**
	 * Успешно накручен 1 голос
	 * 
	 * @param group
	 *            группа в которой произошло событие
	 * @param countVotes
	 *            количество голосов
	 */
	public SuccessfulVotesInGroup(GroupTask group, int countVotes) {
		super(group);
		if (countVotes > 1)
			this.countVotes = countVotes;

	}

	/**
	 * возращает количество накрученых голосов
	 * 
	 * @return
	 */
	public int getCountVotes() {
		return countVotes;
	}
}
