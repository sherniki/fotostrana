package fotostrana.ru.task.tasks;

import java.util.concurrent.atomic.AtomicInteger;

import fotostrana.ru.network.Request;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.Scheduler;
import fotostrana.ru.task.schedulers.SchedulerQuickTaskWithLimitedThreads;
import fotostrana.ru.users.User;

/**
 * Задание быстрого голосования
 * 
 */
public abstract class TaskQuickVoting extends TaskVoting {

	/**
	 * предполагаемое количество голосов
	 */
	public AtomicInteger expectedCountOfVotes = new AtomicInteger(0);

	public TaskQuickVoting(String targetId, Nomination nomination,
			int countVotes, String targetName,boolean isVisit) {
		super(targetId, nomination, countVotes, targetName,isVisit);
		scheduler = new SchedulerQuickTaskWithLimitedThreads(this);
		scheduler.setCountOfTaskToBePerformed(1);
	}

	@Override
	protected Request createNewRequest() {
		int leftUntilVotes = countVotes - expectedCountOfVotes.get();
		if (leftUntilVotes == 0)
			return null;
		User user = getRandomUser();
		if (user == null)
			return null;

		int count = Math.min(leftUntilVotes, getCountQuickVotes(user));
		if (count == 0)
			return null;
		expectedCountOfVotes.addAndGet(count);

		Request newRequest = createRequest(user, count);
		newRequest.setEventListener(this);
		return newRequest;
	}

	@Override
	public boolean setScheduler(Scheduler scheduler) {
		return true;
	}

	/**
	 * Количество быстрых голосов у пользователя
	 * 
	 * @param user
	 * @return
	 */
	public abstract int getCountQuickVotes(User user);

	/**
	 * Создает запрос
	 * 
	 * @param user
	 *            пользователь
	 * @param count
	 *            количество голосов которое нужно накрутить
	 * @return
	 */
	public abstract Request createRequest(User user, int count);

}
