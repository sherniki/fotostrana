package fotostrana.ru.task.tasks.tournament;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.tournament.RequestVoteInTheTournament;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskQuickVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.tournament.FilterTournament;

/**
 * Задание голосования за команду заданого цвета
 * 
 */
public class TaskTeamVoting extends TaskQuickVoting {
	/**
	 * Лимит голосов в турнире
	 */
	public static final int LIMIT_VOTES = 600;
	static Queue<String> queue;
	{
		queue = new LinkedBlockingDeque<String>();
//		queue.add("77614738");
//		queue.add("77614884");
		
		queue.add("66534758");
		queue.add("64717875");
	}

	/**
	 * Цвет команды хранится в targetName
	 */

	/**
	 * @param colorTeam
	 *            цвет команды
	 * @param countVotes
	 *            количество голосов
	 */
	public TaskTeamVoting(String colorTeam, int countVotes) {
		this(colorTeam, countVotes, "");
	}

	/**
	 * @param colorTeam
	 *            цвет команды
	 * @param countVotes
	 *            количество голосов
	 * @param idTargetUser
	 *            пользователь за команду которого будет происходить голосование
	 */
	public TaskTeamVoting(String colorTeam, int countVotes, String idTargetUser) {
		super(idTargetUser, Nomination.TOURNAMENT, countVotes, colorTeam,false);
		usersFilter.filters.clear();
		usersFilter.addFilter(new FilterTournament());
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/contest/team/";
	}

	@Override
	public int getCountQuickVotes(User user) {
		return LIMIT_VOTES;
	}

	@Override
	public Request createRequest(User user, int count) {
		String tId = queue.poll();
		RequestVoteInTheTournament request = new RequestVoteInTheTournament(
				user, tId, count,false);
		request.setModeVoting(RequestVoteInTheTournament.MODE_VOTING_TEAM);
		return request;
	}

}
