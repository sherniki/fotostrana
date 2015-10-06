package fotostrana.ru.task.tasks.proxy;

import java.util.Date;

import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.network.updateProxy.EventAutorizatinError;
import fotostrana.ru.events.network.updateProxy.EventFrequentUpdate;
import fotostrana.ru.events.network.updateProxy.EventSuccessfulUpdateProxy;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.StorageRequests;
import fotostrana.ru.network.proxy.ProxyManager;
import fotostrana.ru.network.requests.updateProxy.DownloadWithProxyHubRu;
import fotostrana.ru.network.requests.updateProxy.RequestUpdateProxy;
import fotostrana.ru.task.Task;

/**
 * Задание обновления прокси c сайта fineproxy.org
 * 
 */
public class TaskDownloadProxy extends Task {

	public String login = "";
	public String password = "";
	/**
	 * Хранилище запросов в которое будет помещен запрос
	 */
	public StorageRequests sourceRequests;
	// private RequestUpdateProxy requestUpdateProxy;
	private RequestUpdateProxy requestUpdateProxy;
	private String[] downloadProxy = null;
	public int countNewProxy;
	public int limitProxy = 10000;

	public TaskDownloadProxy(String login, String password) {
		this.login = login;
		this.password = password;
		requestUpdateProxy = new DownloadWithProxyHubRu(
				ProxyManager.PROXY_MANAGER.proxyhubToken, this);
		descriptionTask = "Загрузка прокси с сайта "
				+ requestUpdateProxy.siteName + ".";
		scheduler.setCountOfTaskToBePerformed(1);
	}

	@Override
	public int getCountOfTaskToBePerformed() {
		return 1;
	}

	@Override
	protected void execute() {
		timeStart = new Date();

		Log.LOGGING.addTaskLog("Новое задание : " + descriptionTask,
				Log.TYPE_NEUTRAL);
		state = "Выполняется";
		scheduler.start();
		// sourceRequests.addRequest(createNewRequest());
	}

	@Override
	protected Request createNewRequest() {
		// requestUpdateProxy = new RequestUpdateProxy(login, password, this);

		countNewProxy = 0;
		return requestUpdateProxy;
	}

	@Override
	public void finish() {
		timeFinish = new Date();

		state = "Завершено. " + state + ". Время : " + taskExecutionTime();

		Log.LOGGING.addTaskLog(getDescription() + getTaskState(),
				Log.getTypeMessage(downloadProxy.length));

		eventListener.handleEvent(new EventCompliteTheTask(this));
	}

	@Override
	protected void handleNetworkEvent(EventOfNetworkRequests event) {
		if (event instanceof EventSuccessfulUpdateProxy) {
			handleSuccessfullRequest(requestUpdateProxy);
		}
		if (event instanceof EventAutorizatinError) {
			state = "Неудалось обновить прокси: Неверный логин или пароль.";
			scheduler.stop();
		}
		if (event instanceof EventFrequentUpdate) {
			state = "Неудалось обновить прокси: сервер неотвечает";
			scheduler.stop();
		}
	}

	@Override
	protected void handleSuccessfullRequest(Request request) {
		downloadProxy = requestUpdateProxy.downloadProxy;
		if (downloadProxy.length < limitProxy) {
			countNewProxy = ProxyManager.PROXY_MANAGER.addListHTTPProxy(
					downloadProxy, true);
			state = "Загружено " + downloadProxy.length + " прокси из них "
					+ countNewProxy + " новых.";
		} else {
			downloadProxy = new String[limitProxy];
			for (int i = 0; i < limitProxy; i++) {
				downloadProxy[i] = requestUpdateProxy.downloadProxy[i];
			}
			countNewProxy = ProxyManager.PROXY_MANAGER.addListHTTPProxy(
					downloadProxy, true);
			state = "Загружено " + requestUpdateProxy.downloadProxy.length
					+ " прокси; взято " + downloadProxy.length + " из них "
					+ countNewProxy + " новых.";
		}
	}

	@Override
	public String getTaskState() {
		return state;
	}

	@Override
	public String getTargetUrl() {
		return requestUpdateProxy.getURL();
	}

}
