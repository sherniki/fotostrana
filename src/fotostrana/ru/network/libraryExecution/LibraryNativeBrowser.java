package fotostrana.ru.network.libraryExecution;

import java.util.concurrent.atomic.AtomicBoolean;

import fotostrana.ru.network.Connection;
import fotostrana.ru.network.LibraryExecutionRequests;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.libraryExecution.DJBrowser.DJBrowser;

/**
 * Адаптер для библиотеки DJ NativeWebBrowser
 * 
 */
public class LibraryNativeBrowser implements LibraryExecutionRequests {
	/**
	 * Период прверки завершения запроса
	 */
	public static int TIME_WAIT = 3 * 1000;
	/**
	 * Соединение к которому относится адаптер
	 */
	protected Connection connection;

	// protected String url;
	protected AtomicBoolean isFinish;

	protected String htmlContent;
	protected Request currentRequest;

	// protected String adressProxy;

	public LibraryNativeBrowser(Connection connection) {
		this.connection = connection;
		isFinish = new AtomicBoolean(false);
		currentRequest = null;
	}

	@Override
	public String executeRequest(Request request) throws Exception {
		currentRequest = request;

		isFinish.set(false);
		htmlContent = null;
		DJBrowser.getInstance().addRequest(this);
		while (!isFinish.get()) {
			Thread.sleep(TIME_WAIT);
		}

		return htmlContent;
	}

	public void finish() {
		isFinish.set(true);
	}

	@Override
	public void close() {

	}

	/**
	 * Задает содержание страницы
	 * 
	 * @param content
	 */
	public void setHtmlContent(String content) {
		htmlContent = content;
	}

	/**
	 * озращает текущий запрос
	 * 
	 * @return
	 */
	public Request getRequest() {
		return currentRequest;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public boolean changeProxy() {
		return true;
	}

}
