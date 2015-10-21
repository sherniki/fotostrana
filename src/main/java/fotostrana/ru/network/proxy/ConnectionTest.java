package fotostrana.ru.network.proxy;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.connections.EventConnection;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.StorageRequests;
import fotostrana.ru.network.requests.TestConnection;

/**
 * Проверяет соединение на работоспособность (проверяет только те соединения
 * которые находятся в состоянии тестирования)
 * 
 */
public class ConnectionTest implements StorageRequests {
	// private ConcurrentSkipListSet<Connection> connections;

	@Override
	public void handleEvent(Event event) {
		if (event instanceof EventConnection) {
			Connection connection = ((EventConnection) event).getConnection();
			if (connection.getState() == Connection.STATE_STOP)
				return;
			if (event instanceof SuccessfulEvent) {
				ProxyManager.PROXY_MANAGER.addBannedProxy(connection
						.getAddressProxy());
				// connection.state = Connection.STATE_BANNED;
				connection.banned();
				Log.LOGGING.addNetworkLog("Забанен прокси "
						+ connection.getAddressProxy().getProxy(),
						Log.TYPE_NEGATIVE);
			} else {
				// connection.state = Connection.STATE_STOP;
				connection.stopConnection();
				ProxyManager.PROXY_MANAGER.notWorkingProxy(connection
						.getAddressProxy());
				Log.LOGGING.addNetworkLog("Неработает прокси "
						+ connection.getAddressProxy().getProxy(),
						Log.TYPE_NEGATIVE);
			}
			NetworkManager.NETWORK_MANAGER.deleteConnection(connection);
		}
	}

	@Override
	public Request getNextRequest(Connection connection) {
		if (connection.getState() == Connection.STATE_TESTING)
			return new TestConnection(connection);
		else
			return null;
	}

	@Override
	public void addRequest(Request request) {
	}

	@Override
	public int getCountRequest() {
		return 1;
	}

}
