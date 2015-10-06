package fotostrana.ru.task.groups;

import fotostrana.ru.events.groups.SuccessfulVotesInGroup;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.events.tasks.EventTask;
import fotostrana.ru.events.tasks.SuccessfulVotes;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.GroupTask;
import fotostrana.ru.task.tasks.TaskVoting;

/**
 * Группа заданий голосования
 * 
 */
public class GroupTasksVoting extends GroupTask {
	/**
	 * Количество голосов для отпрвки сообщения о их накрутке
	 */
	public int COUNT_VOTES_FOR_MESSAGE = 1000;

	/**
	 * Количество успешно накрученых голосов
	 */
	private int countSuccessfulVotes = 0;

	/**
	 * Количество голосов для отпраки сообщения
	 */
	private int countVotesForMessage = 0;
	/**
	 * Количество голосов в которых были ошибки
	 */
	private int countErrorVotes = 0;

	@Override
	protected void handleEventTask(EventTask event) {
		AbstractTask task = event.getTask();
		if (event instanceof SuccessfulVotes) {
			int count = ((SuccessfulVotes) event).getCountVotes();
			// countSuccessfulVotes += count;
			countVotesForMessage += count;
			if (countVotesForMessage >= COUNT_VOTES_FOR_MESSAGE) {
				eventListener.handleEvent(new SuccessfulVotesInGroup(this,
						countVotesForMessage));
				countVotesForMessage = 0;
			}
		}
		if (event instanceof EventCompliteTheTask)
			if (task instanceof TaskVoting) {
				TaskVoting taskVoting = (TaskVoting) task;
				countErrorVotes = taskVoting.getCountVotes()
						- taskVoting.getCountSuccessfulVotes();
			}
		super.handleEventTask(event);
	}

	/**
	 * Количество успешно накрученых голосов
	 * 
	 * @return
	 */
	public int getCountSuccessfulVotes() {
		return countSuccessfulVotes;
	}

	/**
	 * Количество незащитаных голосов
	 * 
	 * @return
	 */
	public int getCountErrorVotes() {
		return countErrorVotes;
	}

	/**
	 * @return
	 */
	public int getCountVotesForMessage() {
		return countVotesForMessage;
	}
}
