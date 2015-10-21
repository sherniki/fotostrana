package fotostrana.ru.events.connections;

import fotostrana.ru.events.Event;
import fotostrana.ru.network.Connection;

/**
 * События от соединений
 * 
 */
public class EventConnection implements Event {

	/**
	 * Соединение которое вызвало событие
	 */
	protected Connection connection;

	public EventConnection(Connection conection) {
		setConnection(conection);
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
