package fotostrana.ru.network.requests.updateProxy;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.Request;

/**
 * Запрос для загрузки списка прокси с сайта
 * 
 */
public abstract class RequestUpdateProxy extends Request {
	public String siteName;
	public String[] downloadProxy;

	public RequestUpdateProxy(EventListener listener) {
		eventListener = listener;
	}
}
