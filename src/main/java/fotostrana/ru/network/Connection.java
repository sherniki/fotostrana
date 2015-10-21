package fotostrana.ru.network;

import java.util.Random;

import fotostrana.ru.events.connections.ConnectionError;
import fotostrana.ru.events.connections.EventCloseConnection;
import fotostrana.ru.events.network.IncorrectResponseException;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.filters.FilterError404;
import fotostrana.ru.network.filters.FilterFotostrana;
import fotostrana.ru.network.libraryExecution.LibraryHTTPClient;
import fotostrana.ru.network.libraryExecution.LibraryNativeBrowser;
import fotostrana.ru.network.proxy.AddressProxy;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Абстрактное соединение c одним прокси-сервером
 * 
 */
public class Connection implements Runnable, Comparable<Connection> {
	/**
	 * Библиотека APACHE HTTPCLIENT
	 */
	public final static int LIBRARY_HTTPCLIENT = 0;
	/**
	 * Библиотека DJ NATIVE BROWSER
	 */
	public final static int LIBRARY_DJNATIVEBROWSER = 1;
	/**
	 * Время остановки при пустой очереди
	 */
	public static int TIME_SLEEP = 30 * 1000;
	/**
	 * время тайм-аута для соединения
	 */
	public static int TIME_OUT = 20 * 1000;

	/**
	 * Время перерыва при выполнении большого количества запросов
	 */
	public static final int TIME_LONG_BREAK = 3 * 60 * 1000;

	/**
	 * Максимальное количество запросов без перерыва
	 */
	public static final int NUMBER_OF_QUERIES_BETWEEN_BREAKS = 10000;

	public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";

	/**
	 * Выполняет действия
	 */
	public static final int STATE_WORK = 1;
	/**
	 * Остановлен на некоторое время
	 */
	public static final int STATE_PAUSE = -1;
	/**
	 * Завершил работу
	 */
	public static final int STATE_STOP = -2;
	/**
	 * Создан, но еще незапущен
	 */
	public static final int STATTE_NOT_STARTED = -3;
	/**
	 * Ожидает ответа
	 */
	public static final int STATE_WAITING_RESPONSE = 2;
	/**
	 * Тестируется на работоспособность
	 */
	public static final int STATE_TESTING = 3;
	/**
	 * Забанен
	 */
	public static final int STATE_BANNED = -4;

	/**
	 * Адрес прокси-сервера
	 */
	protected AddressProxy addressProxy;

	/**
	 * Количество ошибок после которого будет отправлено сообщение слушателю
	 */
	protected int countErrorsFillNotification = 3;

	/**
	 * Состояние в котором находится соединение
	 */
	protected int state;

	/**
	 * Источник запросов
	 */
	protected StorageRequests storageRequests;

	protected FilterError404 filterError404 = new FilterError404();
	protected FilterFotostrana filterFotostrana = new FilterFotostrana();
	protected Random random = new Random();
	/**
	 * Поток в котором выполняется соединение
	 */
	protected Thread currentThread;

	/**
	 * Количество ошибок соединения
	 */
	protected int countErrors = 0;

	/**
	 * Количество выполненых запросов
	 */
	protected int countOfSendRequests = 0;

	/**
	 * Текущий запрос
	 */
	protected Request currentRequest;

	/**
	 * Время затраченое на полное выполение запроса (вместе с обработкой
	 * результата),в милисикундах
	 */
	protected long executionTime = 1;

	/**
	 * Время затраченое только на сетевое соединение
	 */
	protected long timeNetwork = 1;

	/**
	 * Библиотека с попощью которой будет выполнятся запрос
	 */
	protected LibraryHTTPClient libraryHTTPClient;
	protected LibraryNativeBrowser libraryNativeBrowser;

	// private int shortBreak;

	public Connection(AddressProxy addressProxy, StorageRequests sourceRequests) {
		this.storageRequests = sourceRequests;
		this.addressProxy = addressProxy;

		state = STATTE_NOT_STARTED;

	}

	/**
	 * Закрывает все библиотеки
	 */
	protected void closeAllLibrary() {
		if (libraryHTTPClient != null)
			libraryHTTPClient.close();
	}

	@Override
	public int compareTo(Connection o) {
		return this.addressProxy.getProxy()
				.compareTo(o.addressProxy.getProxy());
	}

	/**
	 * Отбрасывет результат если он несоответсвет ожидаемому
	 * 
	 * @param resultResponse
	 * @return
	 */
	protected boolean filtrateResult(String resultResponse) {
		if (filterError404.filtrate(resultResponse)) {
			if (currentRequest instanceof RequestFotostrana) {
				boolean result = filterFotostrana.filtrate(resultResponse);
				if (!result) {
					String message = "Ошибка в запросе:" + '\n'
							+ currentRequest.toString() + '\n' + "Response="
							+ resultResponse;
					Log.LOGGING.addLog(message);
				}
				return result;
			} else
				return true;
		}
		return false;
	}

	public AddressProxy getAddressProxy() {
		return addressProxy;
	}

	/**
	 * Возращает библиотеку с помощью которой будет выполнятся запрос
	 * 
	 * @param request
	 *            запрос который необходимо выполнить
	 * @return
	 */
	protected LibraryExecutionRequests getLibrary(Request request) {
		switch (request.libraryExecution) {
		case LIBRARY_DJNATIVEBROWSER:
			if (libraryNativeBrowser == null)
				libraryNativeBrowser = new LibraryNativeBrowser(this);
			return libraryNativeBrowser;
		default:
			if (libraryHTTPClient == null)
				libraryHTTPClient = new LibraryHTTPClient(this);
			return libraryHTTPClient;
		}
	}

	public StorageRequests getSourceRequests() {
		return storageRequests;
	}

	/**
	 * Возращает состояние в котором нходится соединение
	 * 
	 * @return
	 */
	public int getState() {
		return state;
	}

	/**
	 * Обработка ошибки соединения
	 * 
	 * @param e
	 *            возникшая ошибка
	 * @param currentRequest
	 *            запрос пр котором воззникла ошибка
	 */
	protected void handleError(Exception e) {
		currentRequest.back();
		storageRequests.addRequest(currentRequest);

		String errorMessage = e.getMessage();
		if (errorMessage == null) {
			errorMessage = e.getClass().getName();
		}
		Log.LOGGING.addNetworkLog("Прокси: " + addressProxy.getProxy()
				+ ". Ошибка при соединении." + errorMessage, Log.TYPE_NEGATIVE);
		Log.LOGGING.printStackTraceException(e, Log.GROUP_NETWORK);
		countErrors++;
		if (countErrors >= countErrorsFillNotification) {
			storageRequests.handleEvent(new ConnectionError(this));
		}
	}

	/**
	 * Останавливает поток на заданое время
	 * 
	 * @param timePause
	 *            время паузы
	 * @throws InterruptedException
	 */
	protected void pause(long timePause) throws InterruptedException {
		state = STATE_PAUSE;
		Thread.sleep(timePause);
		state = STATE_WORK;
	}

	public void run() {
		state = STATE_WORK;
		currentThread = Thread.currentThread();
		try {
			while ((!currentThread.isInterrupted()) && (state != STATE_STOP)) {

				// if (countOfSendRequests % NUMBER_OF_QUERIES_BETWEEN_BREAKS ==
				// 0) {
				// pause(TIME_LONG_BREAK);
				// }

				currentRequest = storageRequests.getNextRequest(this);

				if (currentRequest != null) {
					currentRequest = currentRequest.nextRequest();
					boolean stop = false;
					while ((currentRequest != null) && (!stop)) {
						long timeBeforeExecution = System.currentTimeMillis();
						try {
							LibraryExecutionRequests libraryExecutionRequests = getLibrary(currentRequest);
							state = STATE_WAITING_RESPONSE;
							String result = libraryExecutionRequests
									.executeRequest(currentRequest);

							timeNetwork += System.currentTimeMillis()
									- timeBeforeExecution;

							state = STATE_WORK;
							stop = !setResult(result);
						} catch (Exception e) {
							if (!currentRequest.handleExeption(e)) {
								handleError(e);
								stop = true;
							}
						}
						countOfSendRequests++;
						long timeAfterExecution = System.currentTimeMillis();
						executionTime += timeAfterExecution
								- timeBeforeExecution;
						if (!stop)
							currentRequest = currentRequest.nextRequest();

					}

					// shortBreak = random.nextInt(10*1000) + 1000;
					// pause(shortBreak);

					// /****************************************
				} else {
					// очередь запросов пустая
					pause(TIME_SLEEP);
				}
			}
		} catch (InterruptedException e) {
		} finally {
			closeAllLibrary();
			state = STATE_STOP;
			storageRequests.handleEvent(new EventCloseConnection(this));
		}
	}

	/**
	 * Обработка результата запроса
	 * 
	 * @param result
	 *            ответ от сервера
	 * @return false если неудалось обработать результат
	 * @throws Exception
	 */
	protected boolean setResult(String result) throws Exception {
		if (result == null)
			return false;
		if ((currentRequest.isUnanswered)
				|| (result.compareTo("0" + '\n') == 0)) {
			currentRequest.setResult(result);
		} else {
			if (filtrateResult(result))
				currentRequest.setResult(result);
			else {
				Log.LOGGING.addNetworkLog(result, Log.TYPE_NEUTRAL);
				throw new IncorrectResponseException(currentRequest.getURL(),
						result);
			}
		}

		return true;
	}

	public void setSourceRequests(StorageRequests sourceRequests) {
		this.storageRequests = sourceRequests;
	}

	/**
	 * Устанавливает состояние ЗАБАНЕН
	 */
	public void banned() {
		state = STATE_BANNED;
	}

	/**
	 * Скорость выполнения запросов [запрос/секунда]
	 * 
	 * @return 0 если невыполнено ниодного запроса
	 */
	public double speedOfExecution() {
		return (((double) countOfSendRequests) / executionTime) * 1000;
	}

	public void setCountErrorFillNotification(int count) {
		if (count > 0) {
			countErrorsFillNotification = count;
		}
	}

	/**
	 * Останавливает соединение
	 */
	public void stopConnection() {
		// if (currentThread != null)
		// currentThread.interrupt();
		state = STATE_STOP;
	}

	public boolean changeProxy(AddressProxy newAddressProxy) {
		addressProxy = newAddressProxy;
		if (libraryHTTPClient != null)
			libraryHTTPClient.changeProxy();
		if (libraryNativeBrowser != null)
			libraryNativeBrowser.changeProxy();
		countErrors = 0;
		countOfSendRequests = 0;
		timeNetwork = 0;
		countOfSendRequests = 0;
		state = STATE_WORK;
		return true;
	}

}
