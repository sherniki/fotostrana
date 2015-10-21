package fotostrana.ru;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.xml.parsers.ParserConfigurationException;

import configuration.ApplicationConfiguration;
import configuration.utils.xml.XMLReader;
import configuration.utils.xml.XMLWriter;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.application.EventCloseApplication;
import fotostrana.ru.events.application.NoAvatars;
import fotostrana.ru.events.application.NotLoadUsers;
import fotostrana.ru.events.network.EventNoWorkingConnections;
import fotostrana.ru.gui.TaskWindow.WindowTasks;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.proxy.ProxyManager;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.users.UserManager;

/**
 * Точка входа в приложение, отвечает за инициализацию и завершение приложения
 * 
 */
public enum Application implements EventListener {
	APPLICATION;
	/**
	 * Папка с резервными копиями
	 */
	public static String BACKUP_DIRECTORY = "Резерные копии";
	/**
	 * Файл с конфигурацией
	 */
	public static final String CONFIGURATION_FILE = "configuration.xml";
	public static final int CURRENT_OS = currentOS();
	public static final int MAC_OS = 2;
	public static final int UNIX_OS = 3;
	public static final int UNKNOWN_OS = 0;

	public static final int WINDOWS_OS = 1;

	static public int currentOS() {
		String nameOS = System.getProperty("os.name").toLowerCase();
		if (nameOS.indexOf("win") > -1)
			return WINDOWS_OS;
		if (nameOS.indexOf("mac") > -1)
			return MAC_OS;
		if (nameOS.indexOf("unix") > -1)
			return UNIX_OS;
		return UNKNOWN_OS;
	}

	public static void main(String[] args) {
		// TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		Log.LOGGING.addLog("defaultCharset = " + Charset.defaultCharset());
		// System.out.println(Charset.defaultCharset());

		// DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		// String dateString = format.format(new Date());
		// if (dateString.indexOf("30.09.2014") > -1)
		// return;

		APPLICATION.currentOS = currentOS();
		APPLICATION.loadConfiguration();
		// APPLICATION.startTOR();
		APPLICATION.showGUI();
		APPLICATION.showStandbyWindow();
		APPLICATION.backup();
		APPLICATION.start();
		APPLICATION.closeStandbyWindow();
	}

	private int countShowErrorNotAvatar = 0;

	public int currentOS;
	/**
	 * Период сохранния данных на диск
	 */
	public final int DELAY_INTERMEDIATE_SAVING = 30 * 60 * 1000;
	private boolean isStartedTOR = false;
	private boolean isWithoutSound = false;
	private ActionListener listenerSaving;
	private String PATH_PROGRAM_CHANGE_PROXY = "data" + File.separator
			+ "ChangeSystemProxy.exe";
	private String START_TOR = "1 socks=127.0.0.1:9050";
	private String STOP_TOR = "0 socks=127.0.0.1:9050";

	private Timer timerIntermediateSaving;

	/**
	 * Окно управления заданиями
	 */
	public WindowTasks windowTasks;

	private Application() {
		listenerSaving = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				intermediateSaving();
			}
		};
		timerIntermediateSaving = new Timer(DELAY_INTERMEDIATE_SAVING,
				listenerSaving);
	}

	/**
	 * Выполняет резервное копирование
	 */
	public void backup() {
		setDescriptionWaiting("Выполняется резервное копирование файлов");
		List<String> backupFiles = new LinkedList<String>();
		backupFiles.add(CONFIGURATION_FILE);
		backupFiles.addAll(ProxyManager.PROXY_MANAGER.getBackupFiles());
		backupFiles.addAll(UserManager.USER_MANAGER.getBackupFiles());
		backupFiles.addAll(TaskManager.TASK_MANAGER.getBackupFiles());
		FileManager.backup(BACKUP_DIRECTORY, backupFiles);
	}

	/**
	 * Открывает страницу в браузере поумолчанию
	 * 
	 * @param targetUrl
	 *            адресс страницы
	 */
	public void browseURL(String targetUrl) {
		Desktop desktop = Desktop.getDesktop();
		try {
			URI url = new URI(targetUrl);
			desktop.browse(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Закрытие программы
	 */
	public void close() {
		// stopTOR();
		setDescriptionWaiting("Останавливаются интернет соединения");
		NetworkManager.NETWORK_MANAGER.stop();
		setDescriptionWaiting("Сохранение отчета");
		TaskManager.TASK_MANAGER.saveReport();
		setDescriptionWaiting("Сохранение прокси");
		ProxyManager.PROXY_MANAGER.saveListProxy();
		setDescriptionWaiting("Сохранение анкет");
		UserManager.USER_MANAGER.saveToFile();
		setDescriptionWaiting("Сохранение логов");
		try {
			Log.LOGGING.saveToFile();
		} catch (Exception e) {

			e.printStackTrace();
		}

		XMLWriter xmlWriter = new XMLWriter(ApplicationConfiguration.INSTANCE);
		try {
			xmlWriter.save(CONFIGURATION_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Закрывает окно с ожиданием
	 */
	public void closeStandbyWindow() {
		windowTasks.closeStandbyWindow();
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof EventCloseApplication) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					APPLICATION.close();
					APPLICATION.hideGUI();
					APPLICATION.terminate();
				}
			});
			t.start();

		}
		if (event instanceof EventNoWorkingConnections) {
			showError("Нет работающих интернет соединений.");
		}
		if (event instanceof NotLoadUsers) {
			showError("Незагружено ни одной новой анкеты.");
		}
		if (event instanceof NoAvatars) {
			countShowErrorNotAvatar++;
			if (countShowErrorNotAvatar <= 1)
				showError("Нет файлов с аватарками.");

		}
	}

	public void hideGUI() {
		closeStandbyWindow();
		windowTasks.setVisible(false);
	}

	/**
	 * Промежуточное сохранение данных
	 */
	public void intermediateSaving() {
		Log.LOGGING.saveToFile();
	}

	public boolean isWithoutSound() {
		return isWithoutSound;
	}

	/**
	 * Загрузка конфигурации
	 */
	public void loadConfiguration() {
		XMLReader xmlReader = new XMLReader(ApplicationConfiguration.INSTANCE);
		xmlReader.load(CONFIGURATION_FILE);

		Log.LOGGING.loadConfiguration();

		START_TOR = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.Proxy_TOR", START_TOR);
		PATH_PROGRAM_CHANGE_PROXY = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Network.ProgramChangeProxy",
				PATH_PROGRAM_CHANGE_PROXY);

		ProxyManager.PROXY_MANAGER.loadConfiguration();
		NetworkManager.NETWORK_MANAGER.loadConfiguration();
		UserManager.USER_MANAGER.loadConfiguration();
		TaskManager.TASK_MANAGER.loadConfiguration();
	}

	/**
	 * Воспроизводит аудио из файла, поддерживаемые фотрматы: wav,
	 * 
	 * @param file
	 *            звуковой файл
	 */
	public void playAudio(String file) {
		if (isWithoutSound)
			return;
		try {
			File soundFile = new File(file);
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.setFramePosition(0);
			clip.start();
		} catch (Exception e) {
			Log.LOGGING.addFileLog("Невозможно воспроизести музыкальный файл: "
					+ file, Log.TYPE_NEGATIVE);
			Log.LOGGING.printStackTraceException(e, Log.GROUP_FILE);
		}
	}

	public void setDescriptionWaiting(String descriptionWaiting) {
		windowTasks.setDescriptionWaitingText(descriptionWaiting);
	}

	public void setWithoutSound(boolean value) {
		isWithoutSound = value;
	}

	/**
	 * Показывает диалоговое окно с ошибкой без блокировки вызывавшего потока
	 * 
	 * @param message
	 */
	public void showError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				windowTasks.showError(message);
			}
		});

	}

	/**
	 * 
	 */
	public void showGUI() {
		windowTasks = new WindowTasks(APPLICATION);
		windowTasks.setVisible(true);
	}

	/**
	 * Показывает окно ожидания
	 * 
	 * @param text
	 *            описание ожидания
	 */
	public void showStandbyWindow() {
		windowTasks.showStandbyWindow();
	}

	/**
	 * Начало работы программы
	 */
	public void start() {
		timerIntermediateSaving.start();
		// TaskManager.TASK_MANAGER.loadFiles();
		setDescriptionWaiting("Загружаются прокси");
		ProxyManager.PROXY_MANAGER.loadFromFile();
		setDescriptionWaiting("Запускаются интернет соединения");
		NetworkManager.NETWORK_MANAGER.addTheMaximumNumberOfConnections();
		setDescriptionWaiting("Загружаются анкеты");
		UserManager.USER_MANAGER.readWithFile();
		setDescriptionWaiting("Инициализация завершена");
//		UserManager.USER_MANAGER.aoutoloadUsers();
	}

	public boolean startTOR() {
		if (currentOS == WINDOWS_OS) {
			if (isStartedTOR)
				return true;
			try {
				Runtime.getRuntime().exec(
						PATH_PROGRAM_CHANGE_PROXY + " " + START_TOR);
				System.out.println(PATH_PROGRAM_CHANGE_PROXY + " " + START_TOR);
				Log.LOGGING.addNetworkLog("Запущен TOR", Log.TYPE_POSITIVE);
			} catch (IOException e) {
				Log.LOGGING.addNetworkLog("Ошибка запуска TOR",
						Log.TYPE_NEGATIVE);
				Log.LOGGING.printStackTraceException(e);
				return false;
			}
			isStartedTOR = true;
			return true;
		}
		return false;
	}

	public boolean stopTOR() {
		if (isStartedTOR)
			if (currentOS == WINDOWS_OS) {
				try {
					Runtime.getRuntime().exec(
							PATH_PROGRAM_CHANGE_PROXY + " " + STOP_TOR);
				} catch (IOException e) {
					Log.LOGGING.printStackTraceException(e);
					return false;
				}
				return true;
			}
		return false;
	}

	/**
	 * Прекращает работу программы
	 */
	private void terminate() {
		System.exit(0);
	}

}
