package fotostrana.ru.task;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import configuration.ApplicationConfiguration;
import fotostrana.ru.Application;
import fotostrana.ru.FileManager;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.tasks.EventTask;
import fotostrana.ru.log.Log;
import fotostrana.ru.reports.StatusReportTaskManager;
import fotostrana.ru.task.tasks.TaskUpdateProfiles;
import fotostrana.ru.task.tasks.TaskVoting;
import fotostrana.ru.task.tasks.proxy.TaskDownloadProxy;

/**
 * Управляет задачами
 * 
 */
/**
 * @author Nikita
 * 
 */
public class TaskManager implements EventListener {
	public static TaskManager TASK_MANAGER;
	static {
		TASK_MANAGER = new TaskManager();
	}

	public int TIMER_DELAY = 2 * 1000;
	/**
	 * Все задания
	 */
	private List<AbstractTask> tasks;
	/**
	 * Группы заданий
	 */
	private List<GroupTask> groupsTasks;
	private int MIN_COUNT_VOTES_FOR_THE_REPORT = 5;
	/**
	 * Задания выполняемые по запланироавному времени
	 */
	// private ConcurrentSkipListSet<Task> tasksExecutedByTime;
	private String fileWithReport = "Отчет.xls";
	private String fileWithTasks = "Задания.txt";
	public String fileWithFilterProfiles = "Фильтр анкет.txt";
	private String fileAudio = "data" + File.separator
			+ "Задание выполнено.wav";
	public Spam spam = new Spam();
	public SendReports sendRepots = new SendReports();
	private Timer timerExecutionPlanningTasks;
	private AbstractAction listenerExecuted;
	public boolean isSendingSpam = false;
	private int nextTaskId = 0;

	private TaskManager() {
		tasks = new ArrayList<AbstractTask>();
		groupsTasks = new ArrayList<GroupTask>();

		listenerExecuted = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Date currentTime = new Date();
				for (AbstractTask currentTask : tasks) {
					if (currentTask.getState() == Scheduler.STATE_NOT_STARTED)
						if (currentTask.getScheduledTimeStart() != null)
							if (currentTask.getScheduledTimeStart().getTime() < currentTime
									.getTime()) {
								executeTask(currentTask);
							}
				}
			}
		};

		timerExecutionPlanningTasks = new Timer(TIMER_DELAY, listenerExecuted);
		timerExecutionPlanningTasks.start();
	}

	public boolean addGroupTask(GroupTask group) {
		if (groupsTasks.add(group)) {
			for (AbstractTask task : group.getTasks()) {
				if (task instanceof Task)
					addTask((Task) task);
			}
			return true;
		}
		return false;
	}

	/**
	 * Добавляет задачу в список
	 * 
	 * @param task
	 */
	public void addTask(AbstractTask task) {
		if (task != null) {
			if (tasks.contains(task) == false) {
				tasks.add(task);
			}
		}
	}

	/**
	 * Количество групп
	 * 
	 * @return
	 */
	public int countGroups() {
		return groupsTasks.size();
	}

	/**
	 * Количество заданий
	 * 
	 * @return
	 */
	public int countTasks() {
		return tasks.size();
	}

	/**
	 * Запустить все задачи на выполнение
	 */
	public void executeAll() {
		for (AbstractTask task : tasks) {
			executeTask(task);
		}
	}

	/**
	 * Запускает заданую задачу на выполнение
	 * 
	 * @param task
	 *            задача
	 */
	public void executeTask(AbstractTask task) {
		addTask(task);
		task.start();
	}

	/**
	 * Возращает список файлов для резервного копирования
	 * 
	 * @return
	 */
	public List<String> getBackupFiles() {
		List<String> result = new LinkedList<String>();
		result.add(fileWithReport);
		result.add(spam.fileManProfiles);
		result.add(spam.fileWithWomanProfiles);
		result.add(spam.fileWithSpamReport);
		result.add(sendRepots.fileWithReport);
		return result;
	}

	/**
	 * Возращает следующий id
	 * 
	 * @return
	 */
	public int getNextTaskId() {
		nextTaskId++;
		return nextTaskId;
	}

	/**
	 * Возращает отчет о своем состоянии
	 */
	public StatusReportTaskManager getReport() {
		StatusReportTaskManager report = new StatusReportTaskManager();
		report.countTasks = tasks.size();
		for (AbstractTask task : tasks) {
			switch (task.getState()) {
			case Scheduler.STATE_COMPLETED:
				report.countCompleted++;
				break;
			case Scheduler.STATE_RUN:
				report.countRunning++;
				break;
			case Scheduler.STATE_NOT_STARTED:
			case Scheduler.STATE_PAUSE:
				report.countNotRunning++;
			default:
				break;
			}
		}
		return report;
	}

	/**
	 * Возращает все задания
	 * 
	 * @return
	 */
	public List<AbstractTask> getTasks() {
		return tasks;
	}

	/**
	 * Обрабатывает событие
	 * 
	 * @param event
	 *            событие
	 */
	@Override
	public void handleEvent(Event event) {
		if (event instanceof EventTask) {
			handleEventComliteTask((EventTask) event);
		}
	}

	/**
	 * Обработка событий от заданий
	 * 
	 * @param event
	 */
	private void handleEventComliteTask(EventTask event) {
		if ((event.getTask() instanceof TaskUpdateProfiles)
				|| (event.getTask() instanceof TaskDownloadProxy))
			return;
		Application.APPLICATION.playAudio(fileAudio);
		// UserManager.USER_MANAGER.saveToFile();
	}

	/**
	 * Устанавливает значения из конфигурации
	 */
	public void loadConfiguration() {
		fileWithReport = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Task.FileWithReport", fileWithReport);

		fileWithTasks = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Task.DefaultFileWithTask", fileWithTasks);
		fileWithFilterProfiles = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Task.DefultFileWithFiltersProfiles",
				fileWithFilterProfiles);
		fileAudio = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Task.AudioFile", fileAudio);
		// DefultFileWithFiltersProfiles
		spam.loadConfiguration();
	}

	/**
	 * Загружает файлы необходимые для рассылки клинетам отчетов о выполнении
	 * заданий
	 */
	public void loadFilesSendReports() {
		sendRepots.loadFiles();
	}

	/**
	 * Загружает файлы необходимые для рассылки спама
	 */
	public void loadSpamFiles() {
		spam.loadFile();
	}

	/**
	 * Загружает задачи
	 */
	public void loadTask() {
		Set<Task> newTask = loadTaskWithFile(fileWithTasks);
		for (Task task : newTask) {
			addTask(task);
		}
	}

	/**
	 * Загружает задания из файла
	 * 
	 * @param file
	 *            файл с заданием
	 * @return список загруженых заданий
	 */
	public Set<Task> loadTaskWithFile(String file) {
		Set<Task> result = new ConcurrentSkipListSet<Task>();
		List<String> errorLine = new LinkedList<String>();

		List<String> dataFile = FileManager.readTextFile(file);
		for (String line : dataFile) {
			String lineUTF8 = Log.toUTF8(line);
			if (lineUTF8.length() <= 1)
				continue;
			Task newTask = FactoryTasks.createTask(lineUTF8);
			if (newTask != null) {
				result.add(newTask);
			} else {
				errorLine.add(lineUTF8);
			}
		}
		String message = "Загружено " + result.size() + " заданий.";
		if (errorLine.size() > 0) {
			message = message + '\n' + "Не распознано " + errorLine.size()
					+ " заданий:" + '\n';
			for (String string : errorLine) {
				message = message + string + '\n';
			}
		}
		Log.LOGGING.addTaskLog(message, Log.TYPE_NEUTRAL);

		return result;
	}

	/**
	 * Сохраняет отчет выполнения задач которые были запущены
	 */
	public void saveReport() {
		saveReportToFile(fileWithReport);
		spam.saveFile();
		sendRepots.saveFiles();
	}

	/**
	 * Сохраняет отчет в файл
	 * 
	 * @param file
	 */
	private void saveReportToFile(String file) {
		List<String[]> data = new LinkedList<String[]>();
		for (AbstractTask task : tasks)
			if (task instanceof TaskVoting) {
				if (task.isStarted()) {
					TaskVoting taskVoting = (TaskVoting) task;
					if ((taskVoting.getCountVotes() >= MIN_COUNT_VOTES_FOR_THE_REPORT)
							&& (taskVoting.getCountCompletedTask() >= MIN_COUNT_VOTES_FOR_THE_REPORT))
						data.addAll(task.getReport());
				}
			}
		FileManager.writeTaskReport(fileWithReport, data);
	}

}
