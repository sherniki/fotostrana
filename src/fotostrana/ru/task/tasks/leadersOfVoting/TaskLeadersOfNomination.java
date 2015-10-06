package fotostrana.ru.task.tasks.leadersOfVoting;

import java.util.LinkedList;
import java.util.List;

import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.RequestLeadersInNominations;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.Region;

/**
 * Получает лидеров голосования в номинациях
 * 
 */
public class TaskLeadersOfNomination extends TaskLeadersOfVoting {
	/**
	 * Список регионов
	 */
	private List<Region> regions;

	public TaskLeadersOfNomination(Nomination nomination, List<Region> regions) {
		super(nomination);
		if (regions != null)
			this.regions = regions;
		else
			this.regions = new LinkedList<Region>();
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/contest/";
	}

	@Override
	protected void fillingTheRequestQueue() {
		for (Region region : regions) {
			RequestLeadersInNominations request = new RequestLeadersInNominations(this,
					user, 1, nomination, region);
			queueRequests.add(request);
		}
	}
}
