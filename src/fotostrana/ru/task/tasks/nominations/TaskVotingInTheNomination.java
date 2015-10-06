package fotostrana.ru.task.tasks.nominations;

import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.nominations.RequestVoteIsNomination;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.AllUsers;
//import fotostrana.ru.users.filtersUsers.FilterVotesNomination;
import fotostrana.ru.users.filtersUsers.nominations.NoQuickNominations;

/**
 * Накрутка голосов в номинациях
 * 
 */
public class TaskVotingInTheNomination extends TaskVoting {
	// /**
	// * Номинации голососвания
	// */
	// public static final String[] NOMINATIONS = { "ГОРОД", "ОЧАРОВАНИЕ",
	// "СИМПАТИЯ", "СУПЕР СТАР", "НОМИНАЦИЯ" };

	// protected int nominationNumber;

	/**
	 * Накручивает голоса в номинации
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
	public TaskVotingInTheNomination(String targetId, Nomination nomination,
			int countVotes, String targetName,boolean isVisit) {
		super(targetId, nomination, countVotes, targetName,isVisit);
		usersFilter.clear();
		usersFilter.addFilter(new AllUsers());
		usersFilter.addFilter(new NoQuickNominations());
		// usersFilter.addFilter(new FilterVotesNomination(targetId));
	}

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		RequestVoteIsNomination requestVoteIsNomination = new RequestVoteIsNomination(
				user, targetId, nomination.id,isVisit);
		requestVoteIsNomination.setEventListener(this);
		return requestVoteIsNomination;
	}

	/**
	 * Обработка сетевых событий
	 * 
	 * @param event
	 *            событие при запросе
	 */
	@Override
	protected void handleNetworkEvent(EventOfNetworkRequestsFotostrana event) {
		super.handleNetworkEvent(event);
		RequestFotostrana request = event.getRequest();
		// if (request.getUser().quickNomination.get() == 0) {
		setUserAction(request, nomination.id);
		// }
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/contest/" + targetId;
	}

}
