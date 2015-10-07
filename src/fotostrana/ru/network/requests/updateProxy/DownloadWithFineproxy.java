package fotostrana.ru.network.requests.updateProxy;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.updateProxy.EventAutorizatinError;
import fotostrana.ru.events.network.updateProxy.EventFrequentUpdate;
import fotostrana.ru.events.network.updateProxy.EventSuccessfulUpdateProxy;

/**
 * Получение новых платных прокси с сайта fineproxy.org
 * 
 */
public class DownloadWithFineproxy extends RequestUpdateProxy {
	public static String ERROR = "AUTH ERROR".toLowerCase();
	private String login;
	private String password;

	public int countRepeat = 0;
	private String jschl_vc;
	private String jschl_answer;

	private int state;

	public DownloadWithFineproxy(String login, String password,
			EventListener eventListener) {
		super(eventListener);
		this.login = login;
		this.password = password;
		siteName = "fineproxy.org";
		state = 0;
	}

	@Override
	public void setResult(String result) {
		if (result.indexOf(ERROR) == -1) {
			switch (state) {
			// обработка отправки формы защиты
			case 1:
				state = 0;
				indexNextRequest = 0;
				pause(10 * 1000);
				break;

			// Обработка запроса получения списка
			default: {
				if (result.indexOf("checking your browser before accessing") == -1) {
					downloadProxy = result.split("[\n]");
					// countNewProxy = ProxyManager.PROXY_MANAGER
					// .addListProxy(address);
					// NetworkManager.NETWORK_MANAGER
					// .addTheMaximumNumberOfConnections();
					eventListener.handleEvent(new EventSuccessfulUpdateProxy(
							this));
				} else {
					repeatRequest(result);
				}
			}
				break;
			}

		} else
			eventListener.handleEvent(new EventAutorizatinError(this));
	}

	protected void repeatRequest(String result) {
		countRepeat++;
		if (countRepeat < 3) {
			indexNextRequest = 0;
			jschl_vc = ParserJSON.getSubstring(result, "jschl_vc\" value=\"",
					"\"/>");
			jschl_answer = ParserJSON.getSubstring(result,
					"jschl_answer\" value=\"", "\"/>");
			if ((jschl_answer != null) && (jschl_vc != null)) {
				state = 1;
			}
			pause(10 * 1000);
		} else
			eventListener.handleEvent(new EventFrequentUpdate(this));
	}

	protected void pause(int timePause) {
		try {
			Thread.sleep(timePause);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public String getURL() {
		switch (state) {
		case 1:
			return "http://account.fineproxy.org/cdn-cgi/l/chk_jschl?jschl_vc="
					+ jschl_vc + "&jschl_answer=" + jschl_answer;
		default:
			return "http://account.fineproxy.org/api/getproxy/?format=txt&type=httpip&login="
					+ login + "&password=" + password;
		}

	}

}
