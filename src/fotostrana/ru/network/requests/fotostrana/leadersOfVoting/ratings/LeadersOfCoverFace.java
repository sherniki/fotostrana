package fotostrana.ru.network.requests.fotostrana.leadersOfVoting.ratings;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.AbstractRequestLeadersOfRating;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.RequestLeadersOfVoting;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.users.User;

public class LeadersOfCoverFace extends AbstractRequestLeadersOfRating {

	public LeadersOfCoverFace(EventListener eventListener, User user,
			Region region) {
		super(eventListener, user, region);
		RequestSetRatingRegion requestSetRatingRegion=new RequestSetRatingRegion(this, region);
		listRequests.add(listRequests.size()-1,requestSetRatingRegion);
	}

	public LeadersOfCoverFace(EventListener eventListener, User user,
			Region region, int limit) {
		this(eventListener, user, region);
		this.limit = limit;
	}

	@Override
	public RequestLeadersOfVoting clone() {
		return new LeadersOfCoverFace(eventListener, user, region);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/rating/top";
	}

}
