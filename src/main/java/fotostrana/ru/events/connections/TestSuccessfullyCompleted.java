package fotostrana.ru.events.connections;

import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.network.Connection;

/**
 * Соединение успшено прошло проверку
 * 
 */
public class TestSuccessfullyCompleted extends EventConnection implements
		SuccessfulEvent {

	public TestSuccessfullyCompleted(Connection conection) {
		super(conection);
	}

}
