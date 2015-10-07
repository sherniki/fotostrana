package fotostrana.ru.network.requests.fotostrana;

import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.IncorrectResponseException;
import fotostrana.ru.log.Log;
import fotostrana.ru.users.User;

/**
 * Авторизация по ссылке-автологина, устанавливает пользователю его Id
 * 
 */
public class LoginRequest extends RequestFotostrana {
	;

	/**
	 * Создание запроса с родительским запросом, который становится слушателем
	 * событий,и используются его параметры
	 * 
	 * @param parrentRequest
	 *            родительский запрос
	 */
	public LoginRequest(RequestFotostrana parrentRequest) {
		super(parrentRequest);
	}

	public LoginRequest(User user) {
		super(user);
	}

	@Override
	public String getURL() {
		return user.urlAutoConnection;
	}

	@Override
	public void setResult(String result) {
		Cookie cookieID = user.getCookie("uid");
		Event event;
		if (loginFilter.filtrate(result) && (cookieID != null)) {
			if (bannedFilter.filtrate(result)) {
				user.name = ParserJSON.getSubstring(result,
						"<span class=\"user-name trebuchet ellipsis\">",
						"</span>");
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
			if ((cookieID == null) || (cookieID.getValue().length() < 3))
				event = new EventBan(this);
			user.setId(cookieID.getValue());
		} else {
			event = new EventIsNotAuthorization(this);
			Log.LOGGING.addLog("Ответ при авторизации:" + "\n" + result);
		}
		eventListener.handleEvent(event);
	}

	@Override
	public boolean handleExeption(Exception e) {
		boolean isBanned = false;
		if ((e instanceof ClientProtocolException)
				|| (e instanceof URISyntaxException)
				|| (e instanceof IllegalArgumentException)) {
			isBanned = true;
			Log.LOGGING.addLog("Ошибка:" + e.getClass().getName() + ": "
					+ e.getMessage());
		}
		if (e instanceof IncorrectResponseException) {
			if ((((IncorrectResponseException) e).resultResponse.compareTo("") == 0)) {
				Log.LOGGING.addLog("Ошибка:" + e.getClass().getName() + ": "
						+ e.getMessage());
				isBanned = true;
			}
		}
		if (isBanned) {
			EventBan event = new EventBan(this);
			event.setReason("Невозможно авторизоваться");
			eventListener.handleEvent(event);
			return true;
		}
		return super.handleExeption(e);
	}

}
