package fotostrana.ru.task.tasks.leadersOfVoting;

import java.util.List;

import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.ratings.LeadersOfCoverFace;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.ratings.LeadersOfPhotoTags;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.task.schedulers.SchedulerWithLimitedThreads;

public class TaskLeadersOfRating extends TaskLeadersOfVoting {
	protected List<Region> regions;

	public TaskLeadersOfRating(List<Region> regions) {
		super(Nomination.RATING);
		this.regions = regions;
		if (scheduler instanceof SchedulerWithLimitedThreads)
			((SchedulerWithLimitedThreads) scheduler)
					.setMaximumCountConcurentTask(1);
	}

	@Override
	protected void fillingTheRequestQueue() {
		for (Region subnomination : Region.SUBNOMINATION_OF_RATING) {
			queueRequests
					.add(new LeadersOfPhotoTags(this, user, subnomination));
		}
		
		for (Region region : regions) {
			queueRequests.add(new LeadersOfCoverFace(this, user, region));
		}

	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/rating/top/";
	}

}
