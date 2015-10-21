package fotostrana.ru.events.network;

import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Запрос успешно выполнен
 * 
 */
public class EventRequestExecutedSuccessfully extends
		EventOfNetworkRequestsFotostrana implements SuccessfulEvent,
		SchedulerEvent {

	public EventRequestExecutedSuccessfully(RequestFotostrana request) {
		super(request);
	}

}
