package fotostrana.ru.events.network.updateProxy;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.Request;

/**
 * Частое обновление прокси
 * 
 */
public class EventFrequentUpdate extends EventOfNetworkRequests implements
		FailEvent, SchedulerEvent {

	public EventFrequentUpdate(Request request) {
		super(request);
	}

}
