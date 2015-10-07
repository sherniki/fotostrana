package fotostrana.ru.gui.logWindow;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.proxy.ProxyManager;
import fotostrana.ru.reports.StatusReportNetworkManager;
import fotostrana.ru.reports.StatusReportProxyManager;
import fotostrana.ru.reports.StatusReportTaskManager;
import fotostrana.ru.task.TaskManager;

public class StatePanel extends JPanel {
	private static final long serialVersionUID = 7396885274509065968L;

	private NetworkStatePanel networkStatePanel = new NetworkStatePanel();;
	private TasksStatePanel tasksStatePanel = new TasksStatePanel();

	public StatePanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(networkStatePanel);
		this.add(tasksStatePanel);
	}

	public void update() {
		networkStatePanel.update();
		tasksStatePanel.update();
	}

}

/**
 * Панель отображает состояние сетевых компонентов
 * 
 */
class NetworkStatePanel extends JPanel {
	private static final long serialVersionUID = -3307622467405724197L;
	public static final String QUEQUE = "Запросов в очереди : ";
	public static final String COUNT_CONNECTIONS = "Количество соединений : ";
	public static final String COUNT_WORK_CONNECTIONS = "Работающих : ";
	public static final String COUNT_SLEEP_CONNECTIONS = "Спящих : ";
	public static final String COUNT_WAITING_RESPONSE = "Ожидающих ответа : ";
	public static final String COUNT_FREE_PROXY = "Резервных : ";
	public static final String COUNT_BANNED_PROXY = "Забаненых : ";
	public static final String NOT_WORKING_PROXY = "Неработающих : ";
	public static final String SPEED_OF_EXECUTION = "Скорость (запросов в секунду) : ";
	public static final String SIZE_QUEUE_CHECKER = "Осталось протестировать : ";

	private JLabel labelSizeQueue = new JLabel(QUEQUE);
	private JLabel labelCountConnection = new JLabel(COUNT_CONNECTIONS);
	private JLabel labelWorkConnection = new JLabel(COUNT_WORK_CONNECTIONS);
	private JLabel labelSleepConnection = new JLabel(COUNT_SLEEP_CONNECTIONS);
	private JLabel labelWaitingResponse = new JLabel(COUNT_WAITING_RESPONSE);
	private JLabel labelFreeProxy = new JLabel(COUNT_FREE_PROXY);
	private JLabel labelBannedProxy = new JLabel(COUNT_BANNED_PROXY);
	private JLabel labelNotWorkingProxy = new JLabel(NOT_WORKING_PROXY);
	private JLabel labelSpeedOfExecution = new JLabel(SPEED_OF_EXECUTION);
	private JLabel labelSizeQueueChecker = new JLabel(SIZE_QUEUE_CHECKER);
	private JLabel labelProxy=new JLabel("Прокси: ");

	public NetworkStatePanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		setMinimumSize(new Dimension(1000, 200));

		line1.add(labelCountConnection);
		line1.add(labelWorkConnection);
		line1.add(labelSleepConnection);
		line1.add(labelWaitingResponse);

		line2.add(labelSizeQueue);
		line2.add(labelSpeedOfExecution);

		line3.add(labelProxy);
		line3.add(labelFreeProxy);
		line3.add(labelBannedProxy);
		line3.add(labelNotWorkingProxy);
		line3.add(labelSizeQueueChecker);

		this.add(line1);
		this.add(line2);
		this.add(line3);

		this.setBorder(new TitledBorder("Состояние сети"));
		update();
	}

	public void update() {
		StatusReportNetworkManager reportNetwork = NetworkManager.NETWORK_MANAGER
				.getReport();
		labelSizeQueue.setText(QUEQUE + reportNetwork.countRequests);
		labelCountConnection.setText(COUNT_CONNECTIONS
				+ reportNetwork.countConnections);
		labelWorkConnection.setText(COUNT_WORK_CONNECTIONS
				+ reportNetwork.countWorkingConnections);
		labelSleepConnection.setText(COUNT_SLEEP_CONNECTIONS
				+ reportNetwork.countSleepingConnections);
		labelWaitingResponse.setText(COUNT_WAITING_RESPONSE
				+ reportNetwork.countWaitingResponse);
		labelSpeedOfExecution.setText(SPEED_OF_EXECUTION
				+ reportNetwork.speedOfExecution);

		StatusReportProxyManager reportProxy = ProxyManager.PROXY_MANAGER
				.getReport();
		labelBannedProxy.setText(COUNT_BANNED_PROXY + reportProxy.countBanned);
		labelFreeProxy.setText(COUNT_FREE_PROXY + reportProxy.countFree);
		labelNotWorkingProxy.setText(NOT_WORKING_PROXY
				+ reportProxy.countNotWorking);
		labelSizeQueueChecker.setText(SIZE_QUEUE_CHECKER
				+ reportProxy.sizeQueueProxyChecker);

	}
}

/**
 * Панель отображает состояние выполнения заданий
 * 
 */
class TasksStatePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final String COUNT_TASKS = "Количество заданий : ";
	public static final String COUNT_COMPLETED = "Выполненых : ";
	public static final String COUNT_RUNNING = "Выполняющихся : ";
	public static final String COUNT_NOT_START = "Незапущненых : ";

	private JLabel labelTasks = new JLabel(COUNT_TASKS);
	private JLabel labelCompleted = new JLabel(COUNT_COMPLETED);
	private JLabel labelRunning = new JLabel(COUNT_RUNNING);
	private JLabel labelNotStart = new JLabel(COUNT_NOT_START);

	public TasksStatePanel() {
		this.setBorder(new TitledBorder("Выполнение заданий"));
		setLayout(new FlowLayout(FlowLayout.LEFT));

		this.add(labelTasks);
		this.add(labelCompleted);
		this.add(labelRunning);
		this.add(labelNotStart);
		update();
	}

	public void update() {
		StatusReportTaskManager report = TaskManager.TASK_MANAGER.getReport();
		labelTasks.setText(COUNT_TASKS + report.countTasks);
		labelCompleted.setText(COUNT_COMPLETED + report.countCompleted);
		labelRunning.setText(COUNT_RUNNING + report.countRunning);
		labelNotStart.setText(COUNT_NOT_START + report.countNotRunning);

	}
}
