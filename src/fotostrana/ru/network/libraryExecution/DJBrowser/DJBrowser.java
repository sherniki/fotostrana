package fotostrana.ru.network.libraryExecution.DJBrowser;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationParameters;
import fotostrana.ru.Application;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.libraryExecution.LibraryNativeBrowser;
import fotostrana.ru.network.requests.RequestDJNativeBrowser;
import fotostrana.ru.users.User;

/**
 * Синглитон, представляющий экземпляр браузера
 * 
 */
public class DJBrowser extends WebBrowserAdapter {
	private static DJBrowser instance;

	/**
	 * Браузер
	 */
	protected JWebBrowser webBrowser;

	/**
	 * Распределитель нагрузки
	 */
	protected LoadBalancerBrowser loadBalancer;
	protected JFrame frame;
	/**
	 * Текущий выполняемы запрос
	 */
	protected Request currentRequest;
	protected String currentURL;
	protected int state = 0;

	/**
	 * Содержание текущей страницы
	 */
	protected String currentHtmlContent;

	// url = request.getAddress();
	// htmlContent = null;

	public synchronized static DJBrowser getInstance() {
		if (instance == null) {
			Application.APPLICATION.startTOR();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			instance = new DJBrowser();
		}
		return instance;

	}

	private DJBrowser() {
		loadBalancer = new LoadBalancerBrowser();
		NativeInterface.open();

		UIUtils.setPreferredLookAndFeel();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setUndecorated(true);
				frame.setSize(0, 0);
				webBrowser = new JWebBrowser();
//				JWebBrowser.clearSessionCookies();
				webBrowser.addWebBrowserListener(DJBrowser.this);

				frame.getContentPane().add(webBrowser, BorderLayout.CENTER);
				frame.setVisible(true);
				frame.setVisible(false);
			}
		});
	}

	/**
	 * Выполняет запрос
	 * 
	 * @param request
	 */
	void executeRequest(Request request) {
		currentHtmlContent = null;
		if (request == null) {
			loadBalancer.executionRequestCompleted();
			return;
		}
		currentRequest = request;
		currentURL = request.getURL();

		readCookie();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				webBrowser.navigate(currentURL, createParameter());
			}
		});

	}

	/**
	 * Добавляет запрос
	 * 
	 * @param request
	 */
	public void addRequest(LibraryNativeBrowser request) {
		loadBalancer.addRequest(request);
	}

	/**
	 * Устанавливает куки запроса
	 */
	protected void readCookie() {
		String message = "Устанавливаемые куки:";
		for (Cookie cookie : currentRequest.getHttpContext().getCookieStore()
				.getCookies()) {
			String c = cookie.getName() + "=" + cookie.getValue();
			message += '\n' + c;
			JWebBrowser.setCookie(currentURL, c);
		}
		Log.LOGGING.addNetworkLog(message, Log.TYPE_NEUTRAL);
		message = "Установленые куки:";
		for (String nameCookie : User.mandatoryElements) {
			String cookieValue = JWebBrowser.getCookie(currentURL, nameCookie);
			message += '\n' + nameCookie + "=" + cookieValue;
		}
		Log.LOGGING.addNetworkLog(message, Log.TYPE_NEUTRAL);

	}

	/**
	 * Записывет новые куки
	 */
	protected void writeNewCookie() {
		CookieStore cookieStore = currentRequest.getHttpContext()
				.getCookieStore();
		cookieStore.clear();
		// Date exDate = new Date();
		// exDate.setTime(exDate.getTime() + 1000000*1000);
		for (String nameCookie : User.mandatoryElements) {
			try {
				String cookieValue = JWebBrowser.getCookie(currentURL,
						nameCookie);
				if (cookieValue != null) {
					BasicClientCookie cookie = new BasicClientCookie(
							nameCookie, cookieValue);
					cookie.setDomain(".fotostrana.ru");
					// cookie.setExpiryDate(exDate);
					cookie.setVersion(0);
					cookie.setPath("/");
					cookieStore.addCookie(cookie);
				}
			} catch (Exception e) {
			}

		}

	}

	@Override
	public void loadingProgressChanged(WebBrowserEvent e) {
		// System.out
		// .println("**********************************************************");
		// System.out.println("loadingProgressChanged");
		// if (webBrowser.getHTMLContent() != null)
		// System.out.println("есть контент");
		// System.out.println("cur location= " +
		// webBrowser.getResourceLocation());
		// System.out.println("status= " + webBrowser.getStatusText());
		// System.out
		// .println("loadingProcess= " + webBrowser.getLoadingProgress());
		if (webBrowser.getLoadingProgress() == 100) {
			if (webBrowser.getResourceLocation().compareTo("about:blank") == 0)
				return;
			executionRequestCompleted();
		}

	}

	/**
	 * Загрузка страницы завершена
	 */
	private void executionRequestCompleted() {
		System.out.println("FINISH");
		System.out.println("cur location= " + webBrowser.getResourceLocation());

		if (currentRequest instanceof RequestDJNativeBrowser) {
			((RequestDJNativeBrowser) currentRequest)
					.сallbackPageFinishedLoading(webBrowser);
		}

		if (!currentRequest.isUnanswered)
			currentHtmlContent = webBrowser.getHTMLContent();
		if (currentHtmlContent != null)
			currentHtmlContent = currentHtmlContent.toLowerCase();
		if (currentHtmlContent == null)
			currentHtmlContent = "";
		writeNewCookie();
		loadBalancer.executionRequestCompleted();

		// webBrowser.
		// isFinish.set(true);
	}

	/**
	 * Создание параметров запроса
	 * 
	 * @return
	 */
	protected WebBrowserNavigationParameters createParameter() {
		WebBrowserNavigationParameters parameters = new WebBrowserNavigationParameters();
		Map<String, String> headersMap;
		headersMap = new HashMap<String, String>();
		// headersMap.put("proxy", adressProxy);
		headersMap.put("User-agent", Connection.USER_AGENT);
		headersMap.put("method", "get");
		return parameters;
	}

	/**
	 * Возращает контент текущей страницы
	 * 
	 * @return
	 */
	public String getHtmlContent() {
		return currentHtmlContent;
	}

	public static void executeJavascript(JWebBrowser browser, String JS) {
		Log.LOGGING.addLog("На странице " + browser.getResourceLocation()
				+ '\n' + "Выполняется JavaScript:" + '\n' + JS);
		browser.executeJavascript(JS);
	}
}
