package fotostrana.ru.network.libraryExecution.DJBrowser;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import fotostrana.ru.network.libraryExecution.LibraryNativeBrowser;

/**
 * Распределитель нагрузки браузера
 * 
 */
public class LoadBalancerBrowser implements Runnable {
	/**
	 * Перерыв между проверкой выполнения запроса
	 */
	public static int TIME_SHORT__WAIT = 1 * 1000;
	/**
	 * Перерыв между запросами
	 */
	public static int TIME_LONG__WAIT = 2 * 1000;
	/**
	 * Очередь запросов
	 */
	private ConcurrentLinkedQueue<LibraryNativeBrowser> queue;
	/**
	 * Флаг показывающий, завершено ли выполнение запроса
	 */
	private AtomicBoolean isFinish = new AtomicBoolean(false);

	public LoadBalancerBrowser() {
		queue = new ConcurrentLinkedQueue<LibraryNativeBrowser>();
		Thread currentThread = new Thread(this);
		currentThread.start();
	}

	/**
	 * Добавляет запрос
	 * 
	 * @param request
	 */
	public void addRequest(LibraryNativeBrowser request) {
		if (request != null)
			queue.add(request);
	}

	/**
	 * Выполняет запрос
	 * 
	 * @param nextRequest
	 * @throws InterruptedException
	 */
	protected void executeNextRequest(LibraryNativeBrowser nextRequest)
			throws InterruptedException {
		isFinish.set(false);
		DJBrowser.getInstance().executeRequest(nextRequest.getRequest());
		while (!isFinish.get()) {
			Thread.sleep(TIME_SHORT__WAIT);
		}
		nextRequest.setHtmlContent(DJBrowser.getInstance().getHtmlContent());
		nextRequest.finish();
	}

	/**
	 * Оповещает о том, что браузер завершил выполнение текущего запроса
	 */
	void executionRequestCompleted() {
		isFinish.set(true);
	}

	@Override
	public void run() {
		try {
			while (true) {
				LibraryNativeBrowser nextRequest = queue.poll();
				if (nextRequest != null) {
					executeNextRequest(nextRequest);
				}
				Thread.sleep(TIME_LONG__WAIT);
			}
		} catch (InterruptedException e) {

		}

	}

}
