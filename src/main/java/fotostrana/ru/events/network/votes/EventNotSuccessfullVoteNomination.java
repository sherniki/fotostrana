package fotostrana.ru.events.network.votes;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Возникает,когда после голосования голос незащитан
 * 
 */
public class EventNotSuccessfullVoteNomination extends EventOfNetworkRequestsFotostrana
		implements FailEvent,SchedulerEvent {

	public EventNotSuccessfullVoteNomination(RequestFotostrana request) {
		super(request);
	}

}
