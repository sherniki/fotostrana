package fotostrana.ru.task;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.schedulers.EventComplitedScheduler;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.Request;

/**
 * интерфейс задания
 * 
 */
public abstract class Task extends AbstractTask {
	public int id = -1;

	@Override
	public boolean executeOneSubtask() {
		Request request = createNewRequest();
		if (request != null) {
			scheduler.getStorageRequests().addRequest(request);
			return true;
		} else
			return false;
	}

	/**
	 * Создает новый запрос
	 * 
	 * @return null если нельзя создать новый запрос
	 */
	protected abstract Request createNewRequest();

	@Override
	public synchronized void handleEvent(Event event) {
		if (event instanceof EventOfNetworkRequests) {
			handleNetworkEvent((EventOfNetworkRequests) event);
		}
		if (event instanceof EventComplitedScheduler) {
			finish();
		}
		if (event instanceof SchedulerEvent)
			scheduler.handleEvent(event);
	}

	/**
	 * Обработка сетевых событий
	 * 
	 * @param event
	 *            событие при запросе
	 */
	protected abstract void handleNetworkEvent(EventOfNetworkRequests event);

	/**
	 * Обработка успешно выполненого запроса
	 * 
	 * @param request
	 */
	protected abstract void handleSuccessfullRequest(Request request);

	// @Override
	// public int compareTo(AbstractTask arg0) {
	// int result = this.id - arg0.id;
	// if (result == 0)
	// result = this.descriptionTask.compareTo(arg0.descriptionTask);
	// return result;
	// }

}
