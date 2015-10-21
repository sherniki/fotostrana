package fotostrana.ru.events.connections;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.network.Connection;

/**
 * Соединение не прошло проверку работоспособности
 * 
 */
public class TestFails extends EventConnection implements FailEvent {

	public TestFails(Connection conection) {
		super(conection);
	}

}
