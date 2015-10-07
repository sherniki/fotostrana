package fotostrana.ru.network.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Timer;

import configuration.ApplicationConfiguration;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.connections.EventConnection;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.StorageRequests;
import fotostrana.ru.network.filters.proxy.FilterByTimeLaskChecking;
import fotostrana.ru.network.filters.proxy.ProxyFilter;
import fotostrana.ru.network.requests.TestConnection;

public class ProxyCheker implements StorageRequests {
	// PROXY_CHECKER;
	private static String conf = "configuration.Network.ProxyChecker.";
	private LinkedBlockingQueue<AddressProxy> queueProxy;
	private List<Connection> listConnections;
	private int countThread = 0;
	private int timeInterval = 60;
	private int periodChecked = 10;
	private Timer timer;
	private ProxyFilter filter = new FilterByTimeLaskChecking(timeInterval);

	public ProxyCheker() {
		listConnections = new ArrayList<Connection>();
		queueProxy = new LinkedBlockingQueue<AddressProxy>();
		timer = new Timer();
	}

	public void loadConfiguration() {
		countThread = ApplicationConfiguration.INSTANCE.getIntValue(conf
				+ "CountThreadProxyChecker", countThread);
		
		timeInterval = ApplicationConfiguration.INSTANCE.getIntValue(conf
				+ "TimeInterval", timeInterval);
		periodChecked = ApplicationConfiguration.INSTANCE.getIntValue(conf
				+ "PeriodChecked", periodChecked);
		//Чтобы проеврка не работала
		timeInterval=100000;
		periodChecked=100000;
		countThread=0;
		init();
	}

	private void init() {
		listConnections.clear();
		for (int i = 0; i < countThread; i++) {
			Connection currentConnection = new Connection(
					AddressProxy.NO_PROXY, this);
			currentConnection.setCountErrorFillNotification(1);
			listConnections.add(currentConnection);
		}
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (event instanceof EventConnection) {
			AddressProxy proxy = ((EventConnection) event).getConnection()
					.getAddressProxy();
			if (event instanceof SuccessfulEvent) {
				proxy.timeLastCheck = new Date();
			} else {
				ProxyManager.PROXY_MANAGER.notWorkingProxy(proxy);
				Log.LOGGING.addNetworkLog(
						"Неработает прокси " + proxy.getProxy(),
						Log.TYPE_NEGATIVE);
			}
		}
	}

	@Override
	public synchronized Request getNextRequest(Connection connection) {
		AddressProxy proxy = queueProxy.poll();
		if (proxy != null) {
			connection.changeProxy(proxy);
			return new TestConnection(connection);
		}
		return null;
	}

	@Override
	public void addRequest(Request request) {
	}

	@Override
	public int getCountRequest() {
		return 1;
	}

	public void start() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				List<AddressProxy> list = ProxyManager.PROXY_MANAGER
						.getProxyByFilter(filter);
				Log.LOGGING.addNetworkLog("Начата проверка работоспособности "
						+ list.size() + " прокси.", Log.TYPE_NEUTRAL);
				queueProxy.addAll(list);
			}
		}, 1000, periodChecked * 60 * 1000);

		int i = 0;
		for (Connection connection : listConnections) {
			i++;
			Thread t = new Thread(connection, "Thread proxy checker " + i);
			t.start();
		}
	}

	public void check(List<AddressProxy> listCheckedProxy) {
		for (AddressProxy addressProxy : listCheckedProxy) {
			queueProxy.add(addressProxy);
		}
	}

	public int getSizeQueue() {
		return queueProxy.size();
	}
}
