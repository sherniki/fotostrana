package fotostrana.ru.network.requests.fotostrana;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.users.User;

public class RequestOpenUserPage extends RequestFotostrana {
	public RequestOpenUserPage(User user) {
		super(user);
	}

	@Override
	public void setResult(String result) {
		Event event;
		if (result.indexOf("страница пользователя скрыта") > -1) {
			event = new EventBan(this, "страница пользователя скрыта");
		} else
			event = new EventRequestExecutedSuccessfully(this);
		eventListener.handleEvent(event);

	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/user/" + user.id;
	}
	
	@Override
	public HttpClientContext getHttpContext() {
		if (httpContext == null) {
			httpContext = HttpClientContext.create();
			httpContext.setCookieStore(new BasicCookieStore());
		}
		return httpContext;
	}

}
