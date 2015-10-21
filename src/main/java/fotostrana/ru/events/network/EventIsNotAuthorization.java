package fotostrana.ru.events.network;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Событие возникает при выполнении запроса неавторизированым пользователем
 * 
 */
public class EventIsNotAuthorization extends EventOfNetworkRequestsFotostrana implements
		FailEvent,SchedulerEvent {

	public EventIsNotAuthorization(RequestFotostrana request) {
		super(request);
	}

}
