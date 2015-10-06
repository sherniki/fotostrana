package fotostrana.ru.task.tasks.tournament;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.tournament.RequestVoteInTheTournament;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskQuickVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.tournament.FilterQuickTournament;

/**
 * Голосвание в турнире быстрыми голосами
 *
 */
public class TaskQuickVotingTournament extends TaskQuickVoting {

	public TaskQuickVotingTournament(String targetId, Nomination nomination,
			int countVotes, String targetName,boolean isVisit) {
		super(targetId, nomination, countVotes, targetName,isVisit);
		usersFilter.filters.clear();
		usersFilter.addFilter(new FilterQuickTournament());
	}

	@Override
	public int getCountQuickVotes(User user) {
		return user.quickTournament.get();
	}

	@Override
	public Request createRequest(User user, int count) {
		RequestVoteInTheTournament requestVoteIsTheTournament = new RequestVoteInTheTournament(
				user, targetId, count,isVisit);
		return requestVoteIsTheTournament;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/user/" + targetId + "/teamcontest";
	}

}
