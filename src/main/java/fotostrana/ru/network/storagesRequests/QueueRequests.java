package fotostrana.ru.network.storagesRequests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.StorageRequests;

/**
 * Хранилище запросов реализованое на основе простой потокобезопасной очереди
 * 
 */
public class QueueRequests implements StorageRequests {
	protected EventListener eventListener;
	/**
	 * очередь запросов
	 */
	protected BlockingQueue<Request> queueRequest;

	public QueueRequests(EventListener eventListener) {
		queueRequest = new LinkedBlockingQueue<Request>();
		setEventListener(eventListener);
	}

	@Override
	public void handleEvent(Event event) {
		eventListener.handleEvent(event);
	}

	@Override
	public Request getNextRequest(Connection connection) {
		return queueRequest.poll();
	}

	public EventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}

	@Override
	public void addRequest(Request newRequest) {
		if (newRequest != null) 
			queueRequest.add(newRequest);
	}

	@Override
	public int getCountRequest() {
		return queueRequest.size();
	}

}
