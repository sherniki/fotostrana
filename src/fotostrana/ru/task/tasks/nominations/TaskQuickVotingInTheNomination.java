package fotostrana.ru.task.tasks.nominations;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.nominations.RequestVoteIsNomination;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskQuickVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.nominations.FilterQuickNominations;

/**
 * Задание для быстрого голосования
 * 
 */
public class TaskQuickVotingInTheNomination extends TaskQuickVoting {

	protected int nominationNumber;

	/**
	 * Быстрая накрутка голосов в номинации
	 * 
	 * @param targetId
	 *            пользователь которому нужно накрутить голоса
	 * @param nomination
	 *            номинация голосования
	 * @param count
	 *            количество голосов которое нужно накрутить
	 * @param targetName
	 *            имя пользователя которому будут накручиваться голоса
	 */
	public TaskQuickVotingInTheNomination(String targetId, Nomination nomination,
			int countVotes, String name,boolean isVisit) {
		super(targetId, nomination, countVotes, name,isVisit);
		nominationNumber = nomination.id;
		usersFilter.filters.clear();
		usersFilter.addFilter(new FilterQuickNominations());
//		usersFilter.addFilter(new FilterVotesNomination(targetId));
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/contest/" + targetId;
	}

	@Override
	public int getCountQuickVotes(User user) {
		return user.quickNomination.get();
	}

	@Override
	public Request createRequest(User user, int count) {
		return new RequestVoteIsNomination(user, targetId, nominationNumber,
				count,isVisit);
	}

}
