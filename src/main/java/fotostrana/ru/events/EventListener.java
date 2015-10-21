package fotostrana.ru.events;

/**
 * Интерфейс обработчика событий
 * 
 */
public interface EventListener {
	/**
	 * Обрабатывает событие
	 * 
	 * @param event
	 *            событие
	 */
	void handleEvent(Event event);
}
