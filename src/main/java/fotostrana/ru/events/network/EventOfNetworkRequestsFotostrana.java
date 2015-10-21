package fotostrana.ru.events.network;

import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

public class EventOfNetworkRequestsFotostrana extends EventOfNetworkRequests {
	// protected RequestFotostrana request;

	/**
	 * @param request
	 *            запрос при котором произошло событие
	 */
	public EventOfNetworkRequestsFotostrana(RequestFotostrana request) {
		super(request);
		// this.request = request;
	}

	/**
	 * Возращает запрос при котором произошло событие
	 * 
	 * @return
	 */
	public RequestFotostrana getRequest() {
		return (RequestFotostrana) request;
	}

	/**
	 * @param newRequest
	 *            новый запрос
	 */
	public void setRequest(RequestFotostrana newRequest) {
		request = newRequest;
	}
}
