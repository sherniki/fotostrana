package fotostrana.ru.events.network;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.network.Request;

/**
 * Событие возникает, если есть не указаный параметр запроса
 */
public class EventNoParametersAreSpecified extends EventOfNetworkRequests implements FailEvent {
	public String nameParameter;

	/**
	 * Возникает, если есть не указаный параметр запроса
	 * 
	 * @param request
	 *            запрос
	 * @param nameParameter
	 *            название параметра
	 */
	public EventNoParametersAreSpecified(Request request, String nameParameter) {
		super(request);
		this.nameParameter = nameParameter;
	}

}
