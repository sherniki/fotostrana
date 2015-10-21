package fotostrana.ru.network;

import java.util.ArrayList;
import java.util.List;

import configuration.ApplicationConfiguration;
import fotostrana.ru.Application;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.connections.ConnectionError;
import fotostrana.ru.events.connections.EventCloseConnection;
import fotostrana.ru.events.connections.EventConnection;
import fotostrana.ru.events.network.EventNoWorkingConnections;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.proxy.AddressProxy;
import fotostrana.ru.network.proxy.ConnectionTest;
import fotostrana.ru.network.proxy.ProxyManager;
import fotostrana.ru.network.storagesRequests.MultiQueueRequests;
import fotostrana.ru.reports.StatusReportNetworkManager;

public enum NetworkManager implements EventListener {
	NETWORK_MANAGER;

	/**
	 * Максимальное количество одновременоо работающих соединений
	 */
	public int MAX_COUNT_CONNECTION = 100;
	public int NUMBER_OF_PROXY_ON_ONE_QUEUE = 15;

	/**
	 * Хранилище запросов
	 */
	private StorageRequests storageRequests;

	/**
	 * список соединений
	 */
	private List<Connection> listConnections;
	/**
	 * Флаг поазывающий остановлен ли менеджер
	 */
	private volatile boolean stop = false;

	/**
	 * проверяет соединение на работоспособность
	 */
	private ConnectionTest connectionTest;
	private int numberThreadConnections = 0;

	private NetworkManager() {
		// storageRequests = new QueueRequests(this);
		storageRequests = new MultiQueueRequests(5, this);
		listConnections = new ArrayList<Connection>();
		connectionTest = new ConnectionTest();
	}

	// /**
	// * Добавляет соединение без прокси
	// *
	// * @return false если уже было запущено
	// */
	// public boolean addConnectionNoProxy() {
	// if (connectionNoProxy == null) {
	// connectionNoProxy = new ConnectionNoProxy(this);
	// listConnections.add(connectionNoProxy);
	// Thread newThread = new Thread(connectionNoProxy);
	// listThreads.put(connectionNoProxy, newThread);
	// newThread.start();
	// return true;
	// } else
	// return false;
	// }

	public void loadConfiguration() {
		Integer configurationValue = null;
		configurationValue = ApplicationConfiguration.INSTANCE
				.getIntValue("configuration.Network.TimeOut");
		if ((configurationValue != null) && (configurationValue.intValue() > 0))
			Connection.TIME_OUT = configurationValue.intValue() * 1000;

		configurationValue = ApplicationConfiguration.INSTANCE
				.getIntValue("configuration.Network.TimeSleep");
		if ((configurationValue != null) && (configurationValue.intValue() > 0))
			Connection.TIME_SLEEP = configurationValue.intValue() * 1000;

		configurationValue = ApplicationConfiguration.INSTANCE
				.getIntValue("configuration.Network.MaxCountConnection");
		if ((configurationValue != null) && (configurationValue.intValue() > 0)) {
			MAX_COUNT_CONNECTION = configurationValue.intValue();
			int countQueue = MAX_COUNT_CONNECTION
					/ NUMBER_OF_PROXY_ON_ONE_QUEUE;
			storageRequests = new MultiQueueRequests(countQueue, this);
		}

	}

	/**
	 * Добавляет новое соединение с прокси-сервером
	 * 
	 * @return результат добавления,false - если небыло добавлено
	 */
	public boolean addNewConnection() {
		AddressProxy proxy = ProxyManager.PROXY_MANAGER.getRandomFreeProxy();
		if (proxy != null) {
			Connection connection = new Connection(proxy, storageRequests);
			listConnections.add(connection);
			numberThreadConnections++;
			String nameThread = "Thread connection " + numberThreadConnections;
			Thread newThread = new Thread(connection, nameThread);
			newThread.start();

			// Log.LOGGING.addLog("Создано новое соединение. Прокси-сервер: "
			// + proxy.getProxy());
			return true;
		} else
			ProxyManager.PROXY_MANAGER.updateProxy();
		// Log.LOGGING
		// .addLog("Неудалось создать новое соединение. Нет свободных прокси.");
		return false;
	}

	/**
	 * Создает максимально возможное, на даный момент, количество соединений
	 * 
	 * @return количество новых соединений
	 */
	public int addTheMaximumNumberOfConnections() {
		int result = 0;
		// if (addConnectionNoProxy()) {
		// result++;
		// }
		if (listConnections.size() < MAX_COUNT_CONNECTION) {
			while (addNewConnection()) {
				result++;
				if (listConnections.size() == MAX_COUNT_CONNECTION)
					break;
			}
			Log.LOGGING.addNetworkLog("Создано " + result
					+ " новых соединений.", Log.getTypeMessage(result));
		}
		if (listConnections.size() == 0) {
			Application.APPLICATION
					.handleEvent(new EventNoWorkingConnections());
		}
		return result;
	}

	/**
	 * Удаляет соединение, если Менеджер находится не в состоянии STOP пытается
	 * добавить нобавить новое соединение
	 * 
	 * @param connection
	 *            соединение
	 */
	public void deleteConnection(Connection connection) {
		listConnections.remove(connection);
		connection.stopConnection();
		if (!stop) {
			addNewConnection();
		}
		if (listConnections.size() == 0) {
			Log.LOGGING.addNetworkLog("Все соединения закрыты",
					Log.TYPE_NEGATIVE);
			Application.APPLICATION
					.handleEvent(new EventNoWorkingConnections());
		}
	}

	/**
	 * останавливает все соединения, возращает управление после завершения всех
	 * потоков соединений
	 */
	public void stop() {
		stop = true;
		for (Connection connection : listConnections) {
			// Thread currentThread = listThreads.get(connection);
			connection.stopConnection();
			// if (currentThread != null) {
			// try {
			// currentThread.join();
			// } catch (InterruptedException e) {
			// }
			// }
		}
	}

	/**
	 * Возращает отчет о состоянии менеджера
	 */
	public StatusReportNetworkManager getReport() {
		StatusReportNetworkManager report = new StatusReportNetworkManager();
		report.countRequests = storageRequests.getCountRequest();
		report.countConnections = listConnections.size();
		double speedExecution = 0;
		for (Connection connection : listConnections) {
			speedExecution += connection.speedOfExecution();
			switch (connection.getState()) {
			case Connection.STATE_WORK:
				report.countWorkingConnections++;
				break;
			case Connection.STATE_PAUSE:
				report.countSleepingConnections++;
				break;
			case Connection.STATE_WAITING_RESPONSE:
				report.countWaitingResponse++;
				report.countWorkingConnections++;
				break;
			case Connection.STATE_BANNED:
				report.countBanned++;
			default:
				break;
			}
		}
		report.speedOfExecution = (int) speedExecution;
		return report;
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof EventConnection) {
			handleEventConnection((EventConnection) event);
		}
	}

	protected void handleEventConnection(EventConnection event) {
		Connection connection = event.getConnection();
		if (event instanceof ConnectionError) {
			// listConnections.remove(connection);
			checkConnection(connection);
		}
		if (event instanceof EventCloseConnection) {
			deleteConnection(connection);
		}
	}

	/**
	 * Проверяет соединение на работоспособность
	 * 
	 * @param connection
	 */
	public void checkConnection(Connection connection) {
		connection.state = Connection.STATE_TESTING;
		connection.setSourceRequests(connectionTest);
	}

	public StorageRequests getStorageRequests() {
		return storageRequests;
	}
}
