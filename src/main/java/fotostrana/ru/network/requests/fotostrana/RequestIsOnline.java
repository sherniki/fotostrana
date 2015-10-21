package fotostrana.ru.network.requests.fotostrana;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;

/**
 * Проверяет находится ли заданый пользователь на сайте, проверка происходит без
 * авторизации на сайте
 * 
 */
public class RequestIsOnline extends RequestFotostrana {
	/**
	 * Состояние пользователя
	 */
	public boolean isOnline;
	/**
	 * проверяемый пользователь
	 */
	public String targetId;

	/**
	 * @param targetId
	 *            проверяемый пользователь
	 * @param eventListener
	 */
	public RequestIsOnline(String targetId, EventListener eventListener) {
		super((RequestFotostrana) null);
		this.targetId = targetId;
		this.eventListener = eventListener;
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/user/" + targetId + "/";
	}

	@Override
	public void setResult(String result) {
		isOnline = false;
		if (result.indexOf("пользователь онлайн") != -1)
			isOnline = true;
		eventListener.handleEvent(new EventRequestExecutedSuccessfully(this));
	}

	@Override
	public HttpClientContext getHttpContext() {
		httpContext = HttpClientContext.create();
		httpContext.setCookieStore(new BasicCookieStore());
		return httpContext;
	}
}
