package fotostrana.ru.task.tasks;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fotostrana.ru.FileManager;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.log.Log;
import fotostrana.ru.users.UserManager;

/**
 * Обьединяет забаненые анкеты в один файл
 * 
 */
public class ProcessingBannedProfiles extends CompositeTask {
	public static final int MAX_COUNT_ROW = 65000;

	private String saveFile = UserManager.USER_MANAGER.folderWithNotWorkingProfiles
			+ "База нерабочих анкет";
	private Set<RecordProfiles> profiles = new TreeSet<RecordProfiles>();
	private int countFiles;
	private int countDownloadedFiles;
	private int countAllProfiles;
	private File[] files;
	private FileFilter filterFiles = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().toLowerCase().endsWith(".xls");
		}
	};

	public ProcessingBannedProfiles() {
		descriptionTask = "Объединение забаненых анкеты";
		name = descriptionTask;
		state = "Не выполняется";

	}

	@Override
	protected void execute() {
		timeStart = new Date();
		Log.LOGGING.addTaskLog("Начато объединение забаненых анкет.",
				Log.TYPE_NEUTRAL);
		searchFiles();
		state = "Необходимо объединить " + files.length + " файлов.";
		Log.LOGGING.addTaskLog(state, Log.getTypeMessage(files.length));
		if (files.length > 0) {
			state = "Загрузка " + countFiles + " файлов";
			scheduler.setCountOfTaskToBePerformed(files.length);
			scheduler.start();
			for (File file : files) {
				LoadFile loadFile = new LoadFile(this, file.getPath());
				Thread t = new Thread(loadFile);
				t.start();
			}
		} else {
			state = "Нет файлов с анкетами.";
			scheduler.stop();
		}

	}

	@Override
	public void finish() {
		timeFinish = new Date();
		state = "Завершено." + state + " Время : " + taskExecutionTime();
		Log.LOGGING.addTaskLog(getDescription() + getTaskState(),
				Log.TYPE_POSITIVE);
		eventListener.handleEvent(new EventCompliteTheTask(this));
	}

	private void searchFiles() {
		File folder = new File(
				UserManager.USER_MANAGER.folderWithNotWorkingProfiles);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		files = folder.listFiles(filterFiles);
		countFiles = files.length;
	}

	/**
	 * Обработка успешно загруженого файла
	 * 
	 * @param loadFile
	 */
	synchronized void successfulLoad(LoadFile loadFile) {
		countAllProfiles += loadFile.countAllLines;
		countDownloadedFiles++;
		state = "Загружено " + countDownloadedFiles + " файлов ("
				+ countAllProfiles + " анкет).";
		Log.LOGGING.addTaskLog("Обрабатывается файл: " + loadFile.file,
				Log.TYPE_NEUTRAL);
		int beforeSize = profiles.size();
		profiles.addAll(loadFile.profiles);
		int afterSize = profiles.size();
		int newProfiles = afterSize - beforeSize;
		Log.LOGGING.addTaskLog("Обработан файл: " + loadFile.file
				+ "; добавлено " + newProfiles + " анкет.", Log.TYPE_POSITIVE);

		if (countDownloadedFiles == countFiles)
			saveToFile();
	}

	/**
	 * Сохраняет обработаные анкеты в файл
	 */
	private void saveToFile() {
		state = "Преобразование " + profiles.size() + " анкет в файлы xls.";
		Log.LOGGING.addTaskLog(state, Log.TYPE_POSITIVE);

		List<LinkedList<String[]>> dataFiles = new ArrayList<LinkedList<String[]>>();
		int countSaveFile = 1 + profiles.size() / MAX_COUNT_ROW;
		for (int i = 0; i < countSaveFile; i++) {
			dataFiles.add(new LinkedList<String[]>());
		}
		int i = 0;
		int j = 0;
		for (RecordProfiles record : profiles) {
			j = i / MAX_COUNT_ROW;
			dataFiles.get(j).add(record.toLine());
			i++;
		}
		state = "Сохранение " + profiles.size() + " анкет в " + countSaveFile
				+ " файлов.";
		Log.LOGGING.addTaskLog(state, Log.TYPE_POSITIVE);
		String[] nameSaveFiles = new String[dataFiles.size()];
		for (int k = 0; k < nameSaveFiles.length; k++) {
			nameSaveFiles[k] = saveFile + " (" + (k + 1) + ").xls";
			FileManager.writeDatabaseBannedProfiles(nameSaveFiles[k],
					dataFiles.get(k));
		}

		state = "Удаление старых файлов.";
		for (File file : files) {
			boolean flagRemove = true;
			for (int k = 0; k < nameSaveFiles.length; k++)
				if (file.getPath().compareTo(nameSaveFiles[k]) == 0) {
					flagRemove = false;
					break;

				}
			if (flagRemove)
				file.delete();
		}

		state = "Разных анкет " + profiles.size() + ".Всего анкет "
				+ countAllProfiles + ".";
		// timeFinish = new Date();
		//
		// state = "Завершено. " + state + ". Время : " + taskExecutionTime();
		// Log.LOGGING.addTaskLog(state, Log.TYPE_POSITIVE);
		finish();
		scheduler.stop();
	}

	@Override
	public String getTaskState() {
		return state;
	}

	@Override
	public int getState() {
		return scheduler.getState();
	}

}

/**
 * Загружает файл с анкетами
 * 
 */
class LoadFile implements Runnable {
	private ProcessingBannedProfiles listener;
	public String file;
	public Set<RecordProfiles> profiles;
	public int countAllLines = -1;

	public LoadFile(ProcessingBannedProfiles listener, String file) {
		this.listener = listener;
		profiles = new TreeSet<RecordProfiles>();
		this.file = file;
	}

	@Override
	public void run() {
		List<String[]> lines = FileManager.readBannedUsers(file);
		countAllLines = lines.size();
		for (String[] line : lines) {
			RecordProfiles record = RecordProfiles.create(line);
			if (profiles != null)
				profiles.add(record);
		}
		listener.successfulLoad(this);
	}

}

class RecordProfiles implements Comparator<RecordProfiles>,
		Comparable<RecordProfiles> {
	public String url;
	public String login;
	public String passwordFS;
	public String passwordMail;
	public String banReason;
	public String isPhone;
	public String quickNomination;
	public String quickTournament;

	public RecordProfiles() {

	}

	public RecordProfiles(String login, String passwordMail, String passwordFS,
			String url, String isPhone, String quickNomination,
			String quickTournament, String banReason) {
		this.url = url;
		this.login = login;
		this.passwordFS = passwordFS;
		this.passwordMail = passwordMail;
		this.banReason = banReason;
		this.isPhone = isPhone;
		this.quickNomination = quickNomination;
		this.quickTournament = quickTournament;
	}

	public String[] toLine() {
		String[] line = new String[10];
		line[0] = login;
		line[1] = "|";
		line[2] = passwordMail;
		line[3] = passwordFS;
		line[4] = url;
		line[5] = "|";
		line[6] = isPhone;
		line[7] = quickNomination;
		line[8] = quickTournament;
		line[9] = banReason;
		return line;
	}

	public static RecordProfiles create(String[] v) {
		if (v.length == 10) {
			return new RecordProfiles(v[0], v[2], v[3], v[4], v[6], v[7], v[8],
					v[9]);
		}
		if (v.length >= 24) {
			return new RecordProfiles(v[0], v[2], v[3], v[4], v[7], v[9],
					v[10], v[23]);
		}
		return null;
	}

	@Override
	public int compare(RecordProfiles arg0, RecordProfiles arg1) {
		return arg0.url.compareTo(arg1.url);
	}

	@Override
	public int compareTo(RecordProfiles arg0) {
		return compare(this, arg0);
	}
}
