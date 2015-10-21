package fotostrana.ru.network.storagesRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.StorageRequests;

/**
 * Хранилище состоящие из нескольких простых очередей
 * 
 */
public class MultiQueueRequests implements StorageRequests {
	/**
	 * Слушатель событий
	 */
	protected EventListener eventListener;
	/**
	 * Список простых очередей
	 */
	protected List<QueueRequests> listQueues;
	protected Random random = new Random();

	/**
	 * Хранилище состоящие из нескольких простых очередей
	 * 
	 * @param countQueque
	 *            количество очередей (минимум 1)
	 * @param eventListener
	 *            слушатель событий
	 */
	public MultiQueueRequests(int countQueque, EventListener eventListener) {
		listQueues = new ArrayList<QueueRequests>();
		this.eventListener = eventListener;
		if (countQueque < 1)
			countQueque = 1;
		for (int i = 0; i < countQueque; i++) {
			listQueues.add(new QueueRequests(this));
		}

	}

	@Override
	public void handleEvent(Event event) {
		eventListener.handleEvent(event);
	}

	@Override
	public Request getNextRequest(Connection connection) {
		return getQueue().getNextRequest(connection);
	}

	@Override
	public void addRequest(Request request) {
		getQueue().addRequest(request);
	}

	@Override
	public int getCountRequest() {
		int count = 0;
		for (QueueRequests queue : listQueues) {
			count += queue.getCountRequest();
		}
		return count;
	}

	/**
	 * Возращает случайную очередь
	 * 
	 * @return
	 */
	protected QueueRequests getQueue() {
		int i = random.nextInt(listQueues.size());
		return listQueues.get(i);
	}

	/**
	 * Количество очередей
	 * 
	 * @return
	 */
	public int getCountQueue() {
		return listQueues.size();
	}

}
