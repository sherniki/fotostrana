package fotostrana.ru.events.network.updateProxy;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.Request;
//import fotostrana.ru.network.requests.RequestFotostrana;

/**
 * Неудалось авторизоваться для обновления списка прокси
 * 
 */
public class EventAutorizatinError extends EventOfNetworkRequests implements
		FailEvent,SchedulerEvent {

	public EventAutorizatinError(Request request) {
		super(request);
	}

}
