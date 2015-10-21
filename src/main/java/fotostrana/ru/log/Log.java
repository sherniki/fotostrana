package fotostrana.ru.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import configuration.ApplicationConfiguration;

/**
 * Логирование событий
 * 
 */
public enum Log {
	LOGGING;

	public static final int GROUP_ALL = 0;
	public static final int GROUP_TASKS = 1;
	public static final int GROUP_NETWORK = 2;
	public static final int GROUP_FILE = 3;
	public static final int GROUP_USER = 4;
	public static final int COUNT_GROUP = 5;

	public static final int TYPE_POSITIVE = 10;
	public static final int TYPE_NEUTRAL = 0;
	public static final int TYPE_NEGATIVE = -10;

	public final static String START_MESSAGE = ""
			+ '\n'
			+ "**************************** ПРОГРАММА ЗАПУЩЕНА ****************************"
			+ '\n';
	public final static String FINISH_MESSAGE = ""
			+ '\n'
			+ "**************************** ПРОГРАММА ЗАКРЫТА  ****************************"
			+ '\n';

	public static String cp1251ToUTF8(String value) {
		try {
			return new String(value.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return "Ошибка преобразования в UTF-8";
	}

	/**
	 * Возращет тип сообения по значению
	 * 
	 * @param value
	 *            значение
	 * @return TYPE_POSITIVE если занчение больше 0, в другом случае
	 *         TYPE_NEGATIVE
	 */
	public static int getTypeMessage(int value) {
		if (value > 0)
			return TYPE_POSITIVE;
		else
			return TYPE_NEGATIVE;
	}

	public static String toUTF8(String value) {
		try {
			return new String(value.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return "Ошибка преобразования в UTF-8";
	}

	// private ConcurrentSkipListSet<String> listLog;
	private List<List<LogRecord>> logs;
	/**
	 * Время запуска пограммы
	 */
	private Date timeStartProgram;

	// /**
	// * Последняя
	// */
	// private LogRecord lastSaveRecord;

	/**
	 * файл для записи логов
	 */
	private String file;

	/**
	 * папка с логами
	 */
	private String logFolder = "Логи";

	/**
	 * Период хранения логов, в днях
	 */
	private int storagePeriod = 14;

	private Log() {
		// listLog = new ConcurrentSkipListSet<String>();
		timeStartProgram = new Date();
		logs = new ArrayList<List<LogRecord>>();

		for (int i = 0; i < COUNT_GROUP; i++) {
			logs.add(new LinkedList<LogRecord>());
		}
		// listLog.add(START_MESSAGE);
		// setLogFile();

	}

	/**
	 * Добавляет лог от файлов
	 * 
	 * @param message
	 *            текст сообщения
	 * @param type
	 *            тип события
	 */
	public void addFileLog(String message, int type) {
		addLog(message, GROUP_FILE, type);
	}

	/**
	 * добавить лог
	 * 
	 * @param message
	 *            текст сообщения
	 */
	public void addLog(String message) {
		addLog(message, GROUP_ALL);
	}

	/**
	 * Добавляет лог заданого типа
	 * 
	 * @param message
	 *            текст сообщения
	 * @param groupMessage
	 *            группа события
	 * 
	 */
	public void addLog(String message, int groupMessage) {
		addLog(message, groupMessage, TYPE_NEUTRAL);
	}

	/**
	 * Добавляет лог заданого типа
	 * 
	 * @param message
	 *            текст сообщения
	 * @param groupMessage
	 *            группа события
	 * @param type
	 *            тип события
	 */
	public void addLog(String message, int group, int type) {
		LogRecord log = new LogRecord(message, group, type);
		addLogRecord(log);
	}

	/**
	 * Добавляет запись лога, сортируя записи по типу события
	 * 
	 * @param logRecord
	 */
	private void addLogRecord(LogRecord logRecord) {
		logs.get(GROUP_ALL).add(logRecord);
		if (logRecord.group != GROUP_ALL) {
			if (logRecord.group < logs.size())
				logs.get(logRecord.group).add(logRecord);
		}
	}

	/**
	 * Добавляет лог от сети
	 * 
	 * @param message
	 *            текст сообщения
	 * @param type
	 *            тип события
	 */
	public void addNetworkLog(String message, int type) {
		addLog(message, GROUP_NETWORK, type);
	}

	/**
	 * Добавляет лог от заданий
	 * 
	 * @param message
	 *            текст сообщения
	 * @param type
	 *            тип события
	 */
	public void addTaskLog(String message, int type) {
		addLog(message, GROUP_TASKS, type);
	}

	/**
	 * Добавляет лог от пользователей
	 * 
	 * @param message
	 *            текст сообщения
	 * @param type
	 *            тип события
	 */
	public void addUserLog(String message, int type) {
		addLog(message, GROUP_USER, type);
	}

	/**
	 * Удаляет файлы логов которые старше чем период хранения, удаление
	 * происходит в отдельном потоке
	 */
	private void deleteOldFiles() {
		Thread deleteThread = new Thread(new Runnable() {
			@Override
			public void run() {
				File folder = new File(logFolder);
				Date currentDate = new Date();
				int countDeletedFiles = 0;
				for (File currentFile : folder.listFiles()) {
					long fileTime = currentDate.getTime()
							- currentFile.lastModified();
					if (fileTime > storagePeriod * 24 * 60 * 60 * 1000) {
						if (currentFile.delete())
							countDeletedFiles++;
					}
				}
				Log.LOGGING.addFileLog("Удалено " + countDeletedFiles
						+ " файлов логов страше " + storagePeriod + " дней.",
						Log.TYPE_NEUTRAL);
			}
		});
		deleteThread.start();
	}

	/**
	 * Возращает группу записей
	 * 
	 * @param group
	 *            группа
	 * @return если нет такой группы возрщает пустой список
	 */
	public List<LogRecord> getGroup(int group) {
		if ((group > -1) && (group < logs.size()))
			return logs.get(group);
		else
			return new LinkedList<LogRecord>();
	}

	/**
	 * Устанавливает параметры из файла конфигураций
	 */
	public void loadConfiguration() {
		logFolder = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Log.FolderWithLogs", logFolder);
		storagePeriod = ApplicationConfiguration.INSTANCE.getIntValue(
				"configuration.Log.StoragePeriod", storagePeriod);
		setLogFile();
		deleteOldFiles();
	}

	/**
	 * Выводит в лог стек ошибки
	 * 
	 * @param e
	 *            ошибка
	 */
	public void printStackTraceException(Exception e) {
		printStackTraceException(e, GROUP_ALL);
	}

	/**
	 * Выводит в лог стек ошибки
	 * 
	 * @param e
	 *            ошибка
	 * @param group
	 *            группа логов
	 */
	public void printStackTraceException(Exception e, int group) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		String s = writer.toString();
		addLog(s, group);
	}

	/**
	 * Сохранение логов в файл
	 * 
	 * @param file
	 *            файл для сохранения
	 * @return результат сохранения, false если запись прошла неудачно
	 */
	public boolean saveToFile() {
		if (file == null)
			setLogFile();
		PrintWriter out = null;
		boolean result = false;
		try {
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					file), "UTF-8"));
			List<LogRecord> allLogs = logs.get(GROUP_ALL);
			if (allLogs != null)
				for (LogRecord currentLog : allLogs)
					if (currentLog != null) {
						out.write(currentLog.toString() + "\r\n");
					}
			result = true;
		} catch (Exception e) {
		} finally {
			if (out != null)
				out.close();
		}
		return result;
	}

	/**
	 * Устанавливает файл для записи логов
	 */
	private void setLogFile() {
		// DateFormat format = new SimpleDateFormat("hh.mm dd.MM.yyyy");
		DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH.mm");
		file = logFolder + File.separator + "Логи за "
				+ format.format(timeStartProgram).toString() + ".txt";
		File folder = new File(logFolder);
		if (!folder.exists())
			folder.mkdirs();
		File logFile = new File(file);
		try {
			logFile.createNewFile();
		} catch (IOException e) {
		}

	}
}
