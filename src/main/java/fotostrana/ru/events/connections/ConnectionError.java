package fotostrana.ru.events.connections;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.network.Connection;

/**
 * Событие возникает когда неработает соединение
 * 
 */
public class ConnectionError extends EventConnection implements FailEvent {

	/**
	 * Соединение неработает
	 * 
	 * @param conection
	 *            нерабочее соединение
	 */
	public ConnectionError(Connection conection) {
		super(conection);
	}

}
