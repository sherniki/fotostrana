package fotostrana.ru.events.network;

import fotostrana.ru.events.Event;
import fotostrana.ru.network.Request;

/**
 * 
 * Интерфейс событий проиходящих при сетевом запросе
 */
public abstract class EventOfNetworkRequests implements Event {
	protected Request request;

	/**
	 * @param request
	 *            запрос при котором произошло событие
	 */
	public EventOfNetworkRequests(Request request) {
		this.request = request;
	}

	/**
	 * Возращает запрос при котором произошло событие
	 * 
	 * @return
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * @param newRequest
	 *            новый запрос
	 */
	public void setRequest(Request newRequest) {
		request = newRequest;
	}
}
