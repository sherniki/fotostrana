package fotostrana.ru.network.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import configuration.ApplicationConfiguration;
import fotostrana.ru.FileManager;
import fotostrana.ru.events.Event;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.StorageRequests;
import fotostrana.ru.network.TypeProxy;
import fotostrana.ru.network.filters.proxy.ProxyFilter;
import fotostrana.ru.network.storagesRequests.QueueRequests;
import fotostrana.ru.reports.StatusReportProxyManager;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.proxy.TaskDownloadProxy;

/**
 * Управляет адрессами прокси-серверов
 * 
 */
public enum ProxyManager implements StorageRequests {
	PROXY_MANAGER;
	/**
	 * Минимальный интервал времени между двумя обновлениями прокси,в минутах
	 */
	public int DELAY_BETWEEN_UPDATES_PROXY = 5;
	/**
	 * Хранилище запросов
	 */
	private StorageRequests sourceRequests;
	/**
	 * Список всех проски-серверов
	 */
	private List<AddressProxy> listAllProxy;
	/**
	 * Неиспользуемые прокси
	 */
	private List<AddressProxy> listFreeProxy;
	/**
	 * Нерабочие прокси
	 */
	private List<AddressProxy> listNotWorkingProxy;

	private Date timeLastUpdateProxy = new Date(0);

	/**
	 * Забаненые прокси
	 */
	private List<AddressProxy> listBannedProxy;
	private Random random = new Random();;
	private Map<String, Date> timeChecked;

	private int countOfProxyClones = 1;
	/**
	 * Файл с рабочими
	 */
	private String fileCorrectHTTPproxy = "Рабочие HTTP прокси.txt";
	private String fileCorrectSOCKSproxy = "Рабочие SOCKS прокси.txt";
	/**
	 * Файл с нерабочими
	 */
	private String fileUncorrectProxy = "NotWorkingProxy.txt";
	private String fileWithTimeChekedProxy = "data" + File.separator
			+ "TimeCheckedProxy.data";
	/**
	 * логин для сайта обновления прокси
	 */
	private String proxyLogin = "";
	/**
	 * пароль для обновления
	 */
	private String proxyPassword = "";

	/**
	 * Ключ для сайта http://proxyhub.ru/
	 */
	public String proxyhubToken = "";
	private int limitDownloadProxy = 300;
	/**
	 * Прямое соединение
	 */
	private Connection direcionConnection;
	private ProxyCheker proxyCheker;
	/**
	 * Список добавленых адресов
	 */
	private List<AddressProxy> newProxy = new LinkedList<AddressProxy>();

	private ProxyManager() {
		proxyCheker = new ProxyCheker();
		sourceRequests = new QueueRequests(this);
		listAllProxy = new ArrayList<AddressProxy>();
		listFreeProxy = new ArrayList<AddressProxy>();
		listBannedProxy = new LinkedList<AddressProxy>();
		listNotWorkingProxy = new ArrayList<AddressProxy>();
		startDirectConnection();
	}

	/**
	 * Возращает список файлов для резервного копирования
	 * 
	 * @return
	 */
	public List<String> getBackupFiles() {
		List<String> result = new LinkedList<String>();
		result.add(fileCorrectHTTPproxy);
		result.add(fileUncorrectProxy);
		return result;
	}

	/**
	 * Запуск прямого соединения
	 */
	protected void startDirectConnection() {
		direcionConnection = new Connection(AddressProxy.NO_PROXY, this);
		Thread newThread = new Thread(direcionConnection,
				"Thread direction connection");
		newThread.start();
	}

	/**
	 * Устанавливает параметры из файла конфигураций
	 */
	public void loadConfiguration() {
		fileCorrectHTTPproxy = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.FileWithHTTP", fileCorrectHTTPproxy);
		fileCorrectSOCKSproxy = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.FileWithSOCKS", fileCorrectSOCKSproxy);
		fileUncorrectProxy = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.FileWithNotWorkingProxy",
				fileUncorrectProxy);
		fileWithTimeChekedProxy = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.ProxyChecker.FileWithTimeChekedProxy",
				fileWithTimeChekedProxy);

		proxyLogin = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.ProxyLogin", proxyLogin);
		proxyPassword = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.ProxyPassword", proxyPassword);
		proxyhubToken = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.ProxyhubToken", proxyhubToken);
		limitDownloadProxy = ApplicationConfiguration.INSTANCE.getIntValue(
				"configuration.Network.LimitDownloadProxy", limitDownloadProxy);
		countOfProxyClones = ApplicationConfiguration.INSTANCE.getIntValue(
				"configuration.Network.CountCloneProxy", countOfProxyClones);
		DELAY_BETWEEN_UPDATES_PROXY = ApplicationConfiguration.INSTANCE
				.getIntValue("configuration.Network.DelayBeetwenUpdateProxy",
						limitDownloadProxy);
		proxyCheker.loadConfiguration();
	}

	/**
	 * Добавляет один прокси по адрессу заданому строкой
	 * 
	 * @param address
	 * @return результат добавления
	 */
	public boolean addProxy(String address, TypeProxy type) {
		if (address.length() < 9)
			return false;
		AddressProxy newProxy = null;
		try {
			newProxy = new AddressProxy(address, type);
			newProxy.setCountRemainingClones(countOfProxyClones);
		} catch (Exception e) {
			newProxy = null;
		}
		if (newProxy != null)
			if ((!listAllProxy.contains(newProxy) && (!listNotWorkingProxy
					.contains(newProxy)))) {
				listAllProxy.add(newProxy);
				listFreeProxy.add(newProxy);
				this.newProxy.add(newProxy);
				Date time = timeChecked.get(newProxy.ip());
				if (time != null) {
					newProxy.timeLastCheck = time;
				}
				return true;
			}
		return false;
	}

	/**
	 * Добавляет список HTTP адресов
	 * 
	 * @param listAdress
	 *            список адресов
	 * @param createConnections
	 *            флаг добавления соединений после загрузки прокси
	 * @return количество новых адресов
	 */
	public int addListHTTPProxy(String[] listAdress, boolean createConnections) {
		int countNewProxy = 0;
		for (String currentAddress : listAdress) {
			if (addProxy(currentAddress, TypeProxy.HTTP))
				countNewProxy++;
		}
		proxyCheker.check(newProxy);
		newProxy.clear();
		Log.LOGGING.addNetworkLog("Добавлено " + countNewProxy
				+ " новых прокси.", Log.getTypeMessage(countNewProxy));
		if ((createConnections) && (countNewProxy != 0))
			NetworkManager.NETWORK_MANAGER.addTheMaximumNumberOfConnections();
		return countNewProxy;

	}

	/**
	 * Загружает из файла заданого в настройках
	 */
	public void loadFromFile() {
		loadTimeCheked(fileWithTimeChekedProxy);
		loadListProxyFromFile(fileCorrectHTTPproxy, TypeProxy.HTTP);
		loadListProxyFromFile(fileCorrectSOCKSproxy, TypeProxy.SOCKS);
		proxyCheker.start();
	}

	@SuppressWarnings("unchecked")
	private void loadTimeCheked(String file) {
		FileInputStream inFile = null;
		ObjectInputStream objectInputStream = null;
		try {
			inFile = new FileInputStream(new File(file));
			objectInputStream = new ObjectInputStream(inFile);
			try {
				timeChecked = (Map<String, Date>) objectInputStream
						.readObject();
			} catch (ClassNotFoundException e) {
				Log.LOGGING.addFileLog("Ошибка : " + e.getMessage(),
						Log.TYPE_NEGATIVE);
			}
		} catch (IOException e) {
			Log.LOGGING.addFileLog("Ошибка чтения файла: " + file
					+ ". Ошибка : " + e.getMessage(), Log.TYPE_NEGATIVE);
		} finally {
			try {
				if (timeChecked == null)
					timeChecked = new Hashtable<String, Date>();
				if (objectInputStream != null)
					objectInputStream.close();
			} catch (IOException e) {
			}
		}
	}

	private void saveTimeCheked(String file) {
		timeChecked.clear();
		for (AddressProxy proxy : listAllProxy) {
			timeChecked.put(proxy.ip(), proxy.timeLastCheck);
		}

		FileOutputStream outFile = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			outFile = new FileOutputStream(new File(file));
			objectOutputStream = new ObjectOutputStream(outFile);
			objectOutputStream.writeObject(timeChecked);
			objectOutputStream.flush();

		} catch (IOException e) {
			Log.LOGGING.addFileLog("Ошибка записи в файл: " + file
					+ ". Ошибка : " + e.getMessage(), Log.TYPE_NEGATIVE);
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
			}
		}
	}

	// public void loadListProxyFromFile() {
	// List<String> httpProxy = FileManager.readTextFile(fileCorrectHTTPproxy);
	// List<String> socksProxy = FileManager
	// .readTextFile(fileCorrectSOCKSproxy);
	// int countNewProxy = 0;
	// for (String stringProxy : httpProxy) {
	// if (addProxy(stringProxy, TypeProxy.HTTP))
	// countNewProxy++;
	// }
	// for (String stringProxy : socksProxy) {
	// if (addProxy(stringProxy, TypeProxy.SOCKS)) {
	// countNewProxy++;
	//
	// }
	// }
	// Log.LOGGING.addFileLog("Добавлено " + countNewProxy + " адресов.",
	// Log.getTypeMessage(countNewProxy));
	// }

	/**
	 * Загружает из файла список адрессов формат адресса ip:port
	 * 
	 * @param file
	 *            файл с адрессами
	 * @param type
	 *            тип прокси
	 */
	public void loadListProxyFromFile(String file, TypeProxy type) {
		List<String> proxy = FileManager.readTextFile(file);
		int countNewProxy = 0;
		for (String stringProxy : proxy) {
			if (addProxy(stringProxy, type))
				countNewProxy++;
		}
		Log.LOGGING.addFileLog("Добавлено " + countNewProxy + " адресов.",
				Log.getTypeMessage(countNewProxy));
	}

	/**
	 * Сохраняет списки проски-серверов в файл
	 */
	public void saveListProxy() {
//		saveTimeCheked(fileWithTimeChekedProxy);
//		Collections.sort(listAllProxy);
//		List<AddressProxy> httpProxy = new LinkedList<AddressProxy>();
//		List<AddressProxy> socksProxy = new LinkedList<AddressProxy>();
//		for (AddressProxy addressProxy : listAllProxy) {
//			switch (addressProxy.getType()) {
//			case HTTP:
//				httpProxy.add(addressProxy);
//				break;
//			case SOCKS:
//				socksProxy.add(addressProxy);
//				break;
//			}
//		}
//		FileManager.writeTextFile(fileCorrectHTTPproxy, httpProxy, false);
//		FileManager.writeTextFile(fileCorrectSOCKSproxy, socksProxy, false);
//
//		FileManager
//				.writeTextFile(fileUncorrectProxy, listNotWorkingProxy, true);
//
//		Log.LOGGING.addNetworkLog("Работающих прокси : " + listAllProxy.size()
//				+ ", неработающих : " + listNotWorkingProxy.size(),
//				Log.TYPE_NEUTRAL);

	}

	/**
	 * Возращает свободный прокси по индеку,при этом исключается из свободных
	 * 
	 * @param index
	 *            индекс
	 * @return свободный прокси, null если свободных нет
	 */
	public AddressProxy getFreeProxy(int index) {
		if ((listFreeProxy.size() == 0) || (index > listFreeProxy.size())
				|| (index < 0))
			return null;
		AddressProxy result = listFreeProxy.get(index);
		result.setCountRemainingClones(result.getCountRemainingClones() - 1);
		if (result.getCountRemainingClones() == 0)
			listFreeProxy.remove(index);
		return AddressProxy.NO_PROXY;
	}

	/**
	 * Добавляет забаненый прокси
	 * 
	 * @param addressProxy
	 */
	public synchronized void addBannedProxy(AddressProxy addressProxy) {
		listBannedProxy.add(addressProxy);
	}

	/**
	 * Возращает случайный свободный прокси,при этом исключается из свободных
	 * 
	 * @return свободный прокси, null если свободных нет
	 */
	public AddressProxy getRandomFreeProxy() {
		// return AddressProxy.NO_PROXY;
		if (listFreeProxy.size() == 0)
			return null;
		int index = random.nextInt(listFreeProxy.size());
		return getFreeProxy(index);
	}

	/**
	 * Удаляет нерабочий прокси из списков доступных серверов
	 * 
	 * @param notWorkingProxy
	 *            нерабочий прокси
	 */
	public void notWorkingProxy(AddressProxy notWorkingProxy) {
		listNotWorkingProxy.add(notWorkingProxy);
		listAllProxy.remove(notWorkingProxy);
		listFreeProxy.remove(notWorkingProxy);
		// timeChecked.remove(notWorkingProxy.ip());
	}

	/**
	 * Выполняет обновление прокси
	 */
	public void updateProxy() {
		Date currentDate = new Date();
		if ((currentDate.getTime() - timeLastUpdateProxy.getTime()) < DELAY_BETWEEN_UPDATES_PROXY * 60 * 1000) {
			return;
		}
		timeLastUpdateProxy = currentDate;
		TaskDownloadProxy taskDownloadProxy = new TaskDownloadProxy(proxyLogin,
				proxyPassword);
		taskDownloadProxy.limitProxy = limitDownloadProxy;
		taskDownloadProxy.setStorageRequests(this);
		TaskManager.TASK_MANAGER.executeTask(taskDownloadProxy);
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	public Request getNextRequest(Connection connection) {
		return sourceRequests.getNextRequest(connection);
	}

	@Override
	public void addRequest(Request request) {
		sourceRequests.addRequest(request);

	}

	@Override
	public int getCountRequest() {
		return sourceRequests.getCountRequest();
	}

	/**
	 * Возращает отчет о своем состоянии
	 * 
	 * @return
	 */
	public StatusReportProxyManager getReport() {
		StatusReportProxyManager report = new StatusReportProxyManager();
		report.countFree = listFreeProxy.size();
		report.countBanned = listBannedProxy.size();
		report.countNotWorking = listNotWorkingProxy.size();
		report.sizeQueueProxyChecker = proxyCheker.getSizeQueue();
		return report;
	}

	public List<AddressProxy> getProxyByFilter(ProxyFilter filter) {
		List<AddressProxy> result = new LinkedList<AddressProxy>();
		for (AddressProxy addressProxy : listAllProxy)
			if (filter.filtrate(addressProxy)) {
				result.add(addressProxy);
			}
		return result;
	}
}
