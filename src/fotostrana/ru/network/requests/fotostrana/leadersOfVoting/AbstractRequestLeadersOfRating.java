package fotostrana.ru.network.requests.fotostrana.leadersOfVoting;

import java.util.List;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.users.User;

public abstract class AbstractRequestLeadersOfRating extends
		RequestLeadersOfVoting {
	protected Region region;
	public int limit = 5;

	public AbstractRequestLeadersOfRating(EventListener eventListener,
			User user, Region region) {
		super(eventListener, user, Nomination.RATING);
		this.region = region;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				List<Integer> listId = ParserJSON.getIntegersOfSubstrings(
						result, "ownerid: ", ",");
				if (listId != null) {
					int position = 1;
					for (Integer id : listId) {
						RecordReport record = new RecordReport(id,
								User.GENDER_UNKNOW, nomination, region);
						record.setPositionInYourRegion(position);
						records.add(record);
						position++;
						if (position > limit)
							break;
					}
				}
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		if (event != null)
			eventListener.handleEvent(event);
	}

}
