package fotostrana.ru.task.tasks.tournament;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.tournament.RequestVoteInTheTournament;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.tournament.FilterTournament;

/**
 * Простое голосование в ТУРНИРЕ
 * 
 */
public class TaskVotingTournament extends TaskVoting {

	/**
	/**
	 * @param targetId за кого голосовать
	 * @param countVotes количество голосов
	 * @param targetName имя за кого голосвать
	 * @param isVisit заходить на страницу после успешного голоса
	 */
	public TaskVotingTournament(String targetId, int countVotes, String name,boolean isVisit) {
		super(targetId, Nomination.TOURNAMENT, countVotes, name,isVisit);
		this.targetId = targetId;
		this.countVotes = countVotes;
		usersFilter.addFilter(new FilterTournament());
		targetName = name;
		descriptionTask = countVotes + " голосов " + targetName + " ("
				+ targetId + ")" + "  в ТУРНИР.";
	}

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		RequestVoteInTheTournament requestVoteIsTheTournament = new RequestVoteInTheTournament(
				user, targetId, 1,isVisit);
		requestVoteIsTheTournament.setEventListener(this);
		return requestVoteIsTheTournament;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/user/" + targetId + "/teamcontest";
	}

}
