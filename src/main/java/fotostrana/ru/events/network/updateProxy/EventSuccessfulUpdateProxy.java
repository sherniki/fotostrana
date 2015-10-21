package fotostrana.ru.events.network.updateProxy;

import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.Request;

/**
 * Успшено загружены новые прокси
 * 
 */
public class EventSuccessfulUpdateProxy extends EventOfNetworkRequests
		implements SuccessfulEvent, SchedulerEvent {

	public EventSuccessfulUpdateProxy(Request request) {
		super(request);
	}

}
