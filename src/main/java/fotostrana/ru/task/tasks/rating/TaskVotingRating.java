package fotostrana.ru.task.tasks.rating;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.rating.RequestVoteInRating;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.AllUsers;

/**
 * Задание голосвания в рейтинге
 *
 */
public class TaskVotingRating extends TaskVoting {

	/**
	 * @param targetId за кого голосовать
	 * @param countVotes количество голосов
	 * @param targetName имя за кого голосвать
	 * @param isVisit заходить на страницу после успешного голоса
	 */
	public TaskVotingRating(String targetId, int countVotes, String targetName,boolean isVisit) {
		super(targetId, Nomination.RATING, countVotes, targetName,isVisit);
		usersFilter.clear();
		usersFilter.addFilter(new AllUsers());
	}

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		Request request = new RequestVoteInRating(user, targetId,isVisit);
		request.setEventListener(this);
		return request;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/rating/user/" + targetId + "/";
	}

}
