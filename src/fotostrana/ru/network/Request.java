package fotostrana.ru.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.log.Log;
import fotostrana.ru.task.TaskManager;

/**
 * 
 * Интерфейс сетевых запросов
 */
public abstract class Request implements EventListener, Comparable<Request> {
	public final static int TYPE_GET = 0;
	public final static int TYPE_POST = 1;
	public final static String[] TYPE_NAME = { "GET", "POST" };

	public final static String URL_FOTOSTRANA = "http://fotostrana.ru/";

	public static void addErrorLogRecord(Request request) {
		String url = request.getURL();
		Map<String, String> header = request.headers();
		String value = "Нет данных.";
		if (request.getRequestData() != null) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(request.getRequestData().getContent());
				while (scanner.hasNext()) {
					value += scanner.nextLine() + '\n';
				}
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			} finally {
				if (scanner != null)
					scanner.close();
			}
		}
		Log.LOGGING.addNetworkLog("Ошибка в запросе:" + url + '\n'
				+ "Заголовок: " + header.toString() + '\n' + "Данные:" + value,
				Log.TYPE_NEGATIVE);
	}

	/**
	 * Преобразовывает строку в данные типа APPLICATION_FORM_URLENCODED
	 * 
	 * @param value
	 *            строка с данными
	 * @return
	 */
	public static HttpEntity getPostData_APPLICATION_FORM_URLENCODED(
			String value) {
		// InputStreamEntity requestData = null;
		StringEntity requestData = null;
		try {
			// InputStream data = new
			// ByteArrayInputStream(value.getBytes("UTF-8"));
			// requestData = new InputStreamEntity(data,
			// ContentType.APPLICATION_FORM_URLENCODED);
			requestData = new StringEntity(value);
			requestData.setChunked(false);
			requestData.setContentType("application/x-www-form-urlencoded");
		} catch (UnsupportedEncodingException e) {
		}
		return requestData;
	}

	/**
	 * Тип запроса
	 */
	protected int typeRequest = TYPE_GET;
	/**
	 * Библиотека с помощью которой будет выполнятся запрос
	 */
	protected int libraryExecution = Connection.LIBRARY_HTTPCLIENT;

	/**
	 * Список из запросов,относящихся к одному заданию
	 */
	protected List<Request> listRequests;

	/**
	 * индекс следующего запроса
	 */
	protected int indexNextRequest = 0;

	/**
	 * Ссылка на родительский запрос
	 */
	protected Request parentRequest = null;

	/**
	 * слушатель событий
	 */
	protected EventListener eventListener;

	protected HttpClientContext httpContext;

	public boolean isContextUsed = true;

	/**
	 * true если запрос без ответа
	 */
	public boolean isUnanswered = false;

	public Request() {
		setParentRequest(null);
	}

	/**
	 * Создание запроса с родителем
	 * 
	 * @param parentRequest
	 *            родительский запрос
	 */
	public Request(Request parentRequest) {
		setParentRequest(parentRequest);
	}

	/**
	 * Устанавливает родителя
	 * 
	 * @param request
	 */
	public void addChildrenRequest(Request request) {
		listRequests.add(request);
		request.setParentRequest(this);
	}

	/**
	 * Выполяет переход к превидущему запросу
	 */
	public void back() {
		if (parentRequest == null)
			indexNextRequest--;
		else
			parentRequest.back();
	}

	@Override
	public int compareTo(Request arg0) {
		return -1;
	}

	/**
	 * Выполняет переход к следующему запросу
	 */
	public void forward() {
		if (parentRequest == null)
			indexNextRequest++;
		else
			parentRequest.forward();
	}

	/**
	 * Возращает адрес страницы запроса
	 * 
	 * @return строка с адресом запроса
	 */
	public abstract String getURL();

	/**
	 * Возращает кодировку ответа
	 * 
	 * @return
	 */
	public String getCharsetResponse() {
		return "Cp1251";
	}

	public EventListener getEventListener() {
		return eventListener;
	}

	/**
	 * Возращает первый запрос,устанавливает счетчик запросов в 0
	 * 
	 * @return
	 */
	public Request getFirstRequest() {
		indexNextRequest = 0;
		return nextRequest();
	}

	/**
	 * Возращает контекст запроса
	 * 
	 * @return
	 */
	public HttpClientContext getHttpContext() {
		if (httpContext == null) {
			httpContext = HttpClientContext.create();
			httpContext.setCookieStore(new BasicCookieStore());
		}
		return httpContext;
	}

	public Request getParentRequest() {
		return parentRequest;
	}

	/**
	 * Данные запроса
	 * 
	 * @return
	 */
	public HttpEntity getRequestData() {
		return null;
	}

	/**
	 * Возращает тип запроса
	 * 
	 * @return
	 */
	public int getType() {
		return typeRequest;
	}

	/**
	 * Обработчик событий макро-запроса
	 * 
	 * @param event
	 *            событие от дочерних запросов
	 */
	public void handleEvent(Event event) {
		if (event instanceof EventRequestExecutedSuccessfully) {
			return;
		}
		if (event instanceof EventOfNetworkRequests) {
			((EventOfNetworkRequests) event).setRequest(this);
		}
		eventListener.handleEvent(event);
	}

	/**
	 * Обрабатывает ошибки возникшие вовремя выполнения запроса
	 * 
	 * @param e
	 *            возникшая ошибка
	 * @return true если ошибка была обработана и дальше ее обрабатывать не
	 *         нужно,false - если запрос несмог обработать ошибку и ее
	 *         необходимо обработать дальше
	 */
	public boolean handleExeption(Exception e) {
		return false;
	}

	/**
	 * Дополнительные поля заголовка запроса
	 * 
	 * @return null -если нет дополнительных полей
	 */
	public Map<String, String> headers() {
		return null;
	}

	/**
	 * Возращает следующий запрос
	 * 
	 * @return следующий запрос, null - если больше запросов нет
	 */
	public Request nextRequest() {
		if (parentRequest == null) {
			if ((listRequests == null)
					|| (indexNextRequest >= listRequests.size())
					|| (indexNextRequest < 0)) {
				return null;
			} else {
				Request next = listRequests.get(indexNextRequest);
				forward();
				return next;
			}
		} else
			return parentRequest.nextRequest();
	}

	public void setEventListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}

	public void setParentRequest(Request parentRequest) {
		this.parentRequest = parentRequest;
		if (parentRequest != null) {
			listRequests = null;
			eventListener = parentRequest;
		} else {
			listRequests = new ArrayList<Request>();
			eventListener = TaskManager.TASK_MANAGER;
			listRequests.add(this);
		}
	}

	/**
	 * Обработчик результата выполнения запроса
	 * 
	 * @param result
	 */
	public abstract void setResult(String result);

	/**
	 * останавливает выполение запроса
	 */
	public void stop() {
		if (parentRequest == null) {
			indexNextRequest = -1;
		} else
			parentRequest.stop();
	}

	@Override
	public String toString() {
		String type = "TYPE=" + TYPE_NAME[typeRequest] + ";";
		String url = "URL=" + getURL() + ";";

		String header = "HEADER=";
		Map<String, String> h = headers();
		if (h != null)
			header += h.toString();
		header += ";";
		String body = "BODY=" + getRequestData() + ";";
		return type + '\n' + url + '\n' + header + '\n' + body;
	}

	/**
	 * Преобразует строку в которой есть символы вида \\uXXXX в строку с обычным
	 * представление
	 * 
	 * @param value
	 * @return
	 */
	public static String uXXXX(String value) {
		Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
		Matcher m = p.matcher(value);
		StringBuffer buf = new StringBuffer(value.length());
		while (m.find()) {
			String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
			m.appendReplacement(buf, Matcher.quoteReplacement(ch));
		}
		return buf.toString();
	}
}
