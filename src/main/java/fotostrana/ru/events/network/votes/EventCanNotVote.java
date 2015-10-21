package fotostrana.ru.events.network.votes;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Событие возникает, когда нельзя проголосовать 
 *
 */
public class EventCanNotVote extends EventOfNetworkRequestsFotostrana implements FailEvent,SchedulerEvent{

	public EventCanNotVote(RequestFotostrana request) {
		super(request);
	}

}
