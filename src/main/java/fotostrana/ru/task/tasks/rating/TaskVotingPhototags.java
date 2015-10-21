package fotostrana.ru.task.tasks.rating;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.phototags.RequestVotePhototags;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.AllUsers;

public class TaskVotingPhototags extends TaskVoting {
	/**
	 * @param targetId за кого голосовать
	 * @param nomination номинация
	 * @param countVotes количество голосов
	 * @param targetName имя за кого голосвать
	 * @param isVisit заходить на страницу после успешного голоса
	 */
	public TaskVotingPhototags(String targetId, Nomination nomination,
			int countVotes, String targetName,boolean isVisit) {
		super(targetId, nomination, countVotes, targetName,isVisit);
		usersFilter.clear();
		usersFilter.addFilter(new AllUsers());
	}

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		Request request = new RequestVotePhototags(user, targetId, nomination,
				this,isVisit);
		return request;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/phototags/" + nomination.alias + "-"
				+ targetId + "/";
	}

}
