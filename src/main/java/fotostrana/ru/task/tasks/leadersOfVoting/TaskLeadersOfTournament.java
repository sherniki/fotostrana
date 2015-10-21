package fotostrana.ru.task.tasks.leadersOfVoting;

import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.RequestLeadresOfTournament;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.Region;

/**
 * Получает список лидеров турнире
 * 
 */
public class TaskLeadersOfTournament extends TaskLeadersOfVoting {

	/**
	 * Получает список лидеров турнире
	 * 
	 */
	public TaskLeadersOfTournament() {
		super(Nomination.TOURNAMENT);
	}

	@Override
	protected void fillingTheRequestQueue() {
		for (Region team : Region.TEAMS_TOURNAMENT) {
			RequestLeadresOfTournament request = new RequestLeadresOfTournament(
					this, user, team);
			queueRequests.add(request);
		}

		// RequestTopOfTournament request = new RequestTopOfTournament(user);
		// request.setEventListener(this);
		// queueRequests.add(request);
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/contest/team/";
	}

}
