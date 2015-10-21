package fotostrana.ru.events.connections;

import fotostrana.ru.network.Connection;

/**
 * Событие закрытия соединения
 * 
 */
public class EventCloseConnection extends EventConnection {

	public EventCloseConnection(Connection conection) {
		super(conection);
	}

}
