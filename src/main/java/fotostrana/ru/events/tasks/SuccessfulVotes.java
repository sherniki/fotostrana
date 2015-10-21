package fotostrana.ru.events.tasks;

import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.task.AbstractTask;

/**
 * Успешно накручены голоса в задании (минимум 1 голос)
 * 
 */
public class SuccessfulVotes extends EventTask implements SuccessfulEvent {

	protected int countVotes = 1;

	/**
	 * Успешно накручен 1 голос
	 * 
	 * @param task
	 *            задание
	 */
	public SuccessfulVotes(AbstractTask task) {
		this(task, 1);
	}

	/**
	 * Успешно накручено несколько голосов
	 * 
	 * @param task
	 *            задание
	 * @param countVotes
	 *            количество голосов
	 */
	public SuccessfulVotes(AbstractTask task, int countVotes) {
		super(task);
		if (countVotes > 1)
			this.countVotes = countVotes;
	}

	public int getCountVotes() {
		return countVotes;
	}

}
