package fotostrana.ru.users;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import configuration.ApplicationConfiguration;
import fotostrana.ru.Application;
import fotostrana.ru.FileManager;
import fotostrana.ru.events.application.NoAvatars;
import fotostrana.ru.log.Log;
import fotostrana.ru.reports.StatusReportUserManager;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.TaskLogin;
import fotostrana.ru.task.tasks.TaskUpdateProfiles;
//import fotostrana.ru.users.actionCriterion.ResetVotingInTheNomination;
import fotostrana.ru.users.filtersUsers.FilterNewUsers;

//import fotostrana.ru.users.filtersUsers.FilterNotLogin;

/**
 * 
 * 
 */
public class UserManager {
	public static UserManager USER_MANAGER;
	static{
		USER_MANAGER=new UserManager();
	}
	class IDComparator implements Comparator<String[]> {
		@Override
		public int compare(String[] arg0, String[] arg1) {
			return arg0[User.COLUMN_ID].compareTo(arg1[COLUMN_ID]);
		}
	}

	public static String SEPARATOR = ";";
	public static int COLUMN_EMAIL = 0;
	public static int COLUMN_PASSWORD_EMAIL = 2;
	public static int COLUMN_PASSWORD_FS = 3;
	public static int COLUMN_URL_AUTOLOGIN = 4;
	public static int COLUMN_ID = 6;
	public static int COLUMN_COLOR = 7;
	public static int COLUMN_NAME = 9 + 1;
	public static int COLUMN_DESCRIPTION = 20 + 1;

//	public static int COUNT__OF_DOWNLOADS_FOR_CLEANING = 10;

	public String folderWithNotWorkingProfiles = "База нерабочих анкет"
			+ File.separator;
	/**
	 * Размер файла с нерабочими анкетами, после которго он будет перенемен в
	 * папку с нерабочими анкетами
	 */
	public final long MAX_SIZE_FILE = 10 * 1000000;// в МБ
	// /**
	// * Список всех пользователей
	// */
	// private ConcurrentSkipListSet<User> allUsers;
	/**
	 * 
	 */
	private List<User> freeUsers;

	/**
	 * Действия пользователей,ключ id - пользователя
	 */
	// private Map<String, Set<UserActions>> usersActions;

	/**
	 * куки-записи для всех пользователей, ключ - id
	 */
	private Map<String, CookieStore> cookieStore;

	/**
	 * Пользователи с рабочими аккаунтами
	 */
	private Set<User> workingUsers;

	/**
	 * Пользователи с забаненым аккаунтом
	 */
	private Set<User> bannedUsers;
	private String fileWithProfiles = "Рабочие анкеты.xls";
	private String fileWithBannedProfiles = "Нерабочие анкеты.xls";
	private String fileWithCookies = "cookies.data";
	// private String fileWithActionsUsers = "data" + File.separator
	// + "actions.data";
	private String folderWithAvatars = "Аватарки";
	private String folderWithUsedAvatars = "Аватарки" + File.separator
			+ "Использованые";
	private FileFilter filterAvatars;
	private Random random;

	/**
	 * Период автозагрузки анкет из текстового файла, в секундах
	 */
	public int periodAutoloadUsers = 5;

	/**
	 * Период очистки файла автозагрузки анкет, в минутах
	 */
	private int periodClearingFileAutoloading = 60;
	/**
	 * Время следующей очистки файла автозагрузки
	 */
	private Date timeNextClearing;

	private UserManager() {
		// allUsers = new ConcurrentSkipListSet<User>();
		freeUsers = new ArrayList<User>();
		workingUsers = new ConcurrentSkipListSet<User>();
		bannedUsers = new ConcurrentSkipListSet<User>();
		cookieStore = new ConcurrentHashMap<String, CookieStore>();
		// usersActions = new ConcurrentHashMap<String, Set<UserActions>>();
		random = new Random();
		filterAvatars = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".jpg");
			}
		};
	}

	// /**
	// * Удаляет действия пользователей которые подходят по критерию
	// *
	// * @param criterion
	// * критерий по которому удаляются записи
	// */
	// public void deleteActionsByCriterion(ActionCriterion criterion) {
	// Set<String> keys = usersActions.keySet();
	// for (String key : keys) {
	// Set<UserActions> userActions = usersActions.get(key);
	// List<UserActions> removeList = new ArrayList<UserActions>();
	// for (UserActions action : userActions) {
	// if (criterion.checkAction(action)) {
	// removeList.add(action);
	// }
	// }
	// userActions.removeAll(removeList);
	// }
	//
	// }

	/**
	 * Добавляет пользователя в бан
	 * 
	 * @param user
	 *            забаненый пользователь
	 */
	public void addToBan(User user) {
		if (user != null) {
			workingUsers.remove(user);
			bannedUsers.add(user);
			cookieStore.remove(user.id);
			// usersActions.remove(user.id);
			user.isBanned = true;

			Log.LOGGING.addUserLog("Пользователь " + user.id + " забанен. "
					+ user.description, Log.TYPE_NEGATIVE);
		}
	}

	/**
	 * Добавляет нового пользователя
	 * 
	 * @param newUser
	 *            может быть null
	 * @return false - если null или такой пользователь уже есть, true - если
	 *         пользователь успешно добавлен
	 */
	private boolean addUser(User newUser) {
		if (newUser == null)
			return false;
		if (bannedUsers.contains(newUser))
			return false;
		return workingUsers.add(newUser);
	}

	public void aoutoloadUsers() {
		Timer timerAutoload = new Timer();
		long periodTimer = periodAutoloadUsers * 1000;
		final String fileWithNewProfiles = ApplicationConfiguration.INSTANCE
				.getValue("configuration.Users.FolderWithTextProfiles");
		timerAutoload.schedule(new TimerTask() {
			@Override
			public void run() {
				readListUsersWithFile(fileWithNewProfiles, false);
				if (timeNextClearing == null)
					timeNextClearing = new Date((new Date()).getTime()
							+ periodClearingFileAutoloading * 60 * 1000);

				if (timeNextClearing.getTime() <= (new Date()).getTime()) {
					FileManager.clearFile(fileWithNewProfiles);
					timeNextClearing = new Date((new Date()).getTime()
							+ periodClearingFileAutoloading * 60 * 1000);
				}
			}
		}, 0, periodTimer);
	}

	/**
	 * Возращает список файлов для резервного копирования
	 * 
	 * @return
	 */
	public List<String> getBackupFiles() {
		List<String> result = new LinkedList<String>();
		result.add(fileWithProfiles);
		result.add(fileWithBannedProfiles);
		result.add(fileWithCookies);
		// result.add(fileWithActionsUsers);
		return result;
	}

	/**
	 * Возращает куки пользователя из хранилища по id, если пользователя нет в
	 * хранилище,то ему создается новая куки запись в хранилище
	 * 
	 * @param id
	 * @return никогда невозращает null
	 */
	public CookieStore getCookie(String id) {
		CookieStore result = cookieStore.get(id);
		if (result == null) {
			result = new BasicCookieStore();
			setCookie(id, result);
		}
		return result;
	}

	/**
	 * Возращает свободного пользователя и удаляет его из списка свободных
	 * 
	 * @param index
	 *            номер в списке
	 * @return свободный пользователь
	 */
	public synchronized User getFreeUser(int index) {
		if (freeUsers.size() == 0)
			return null;
		return freeUsers.remove(index);
	}

	/**
	 * Возращает фал с неиспользованой аватаркой
	 * 
	 * @return файл аватарки, null - если больше нет картинок
	 */
	public File getNextAvatar() {
		File fileAvatar = null;
		File folderAvatars = new File(folderWithAvatars);
		File folderUsedAvatars = new File(folderWithUsedAvatars);
		if (!folderAvatars.exists()) {
			folderAvatars.mkdirs();
			folderUsedAvatars.mkdirs();
			return null;
		}
		if (!folderUsedAvatars.exists()) {
			folderUsedAvatars.mkdirs();
		}
		File[] listAvatars = folderAvatars.listFiles(filterAvatars);
		if (listAvatars != null)
			if (listAvatars.length > 0) {
				File nextAvatar = listAvatars[0];
				String filename = nextAvatar.getName();
				File newFileAvatar = new File(folderUsedAvatars.getPath()
						+ File.separator + filename);
				nextAvatar.renameTo(newFileAvatar);
				fileAvatar = newFileAvatar;
			}
		if (fileAvatar == null) {
			Log.LOGGING
					.addFileLog("Нет файлов с аватарками", Log.TYPE_NEGATIVE);
			Application.APPLICATION.handleEvent(new NoAvatars());
		}
		return fileAvatar;
	}

	/**
	 * @return
	 */
	public synchronized User getRandomFreeUser() {
		int index = random.nextInt(freeUsers.size());
		return getFreeUser(index);
	}

	// /**
	// * Возращает действия пользователя из хранилища по id, если пользователя
	// нет
	// * в хранилище,то ему создается новая запись в хранилище
	// *
	// * @param id
	// * @return никогда невозращает null
	// */
	// public Set<UserActions> getUserAction(String id) {
	// Set<UserActions> actions = usersActions.get(id);
	// if (actions == null) {
	// actions = new ConcurrentSkipListSet<UserActions>();
	// setUserActions(id, actions);
	// }
	// return actions;
	// }

	// /**
	// * Сохраняет выполненые пользователями действия в серриализованом виде
	// *
	// * @param file
	// * файл для сохранения
	// */
	// private void saveUsersActionsToFile(String file) {
	// FileOutputStream outFile = null;
	// ObjectOutputStream objectOutputStream = null;
	// try {
	// outFile = new FileOutputStream(new File(file));
	// objectOutputStream = new ObjectOutputStream(outFile);
	// objectOutputStream.writeObject(usersActions);
	// objectOutputStream.flush();
	//
	// } catch (IOException e) {
	// Log.LOGGING.addFileLog("Ошибка записи в файл: " + file
	// + ". Ошибка : " + e.getMessage(), Log.TYPE_NEGATIVE);
	// } finally {
	// try {
	// objectOutputStream.close();
	// } catch (IOException e) {
	// }
	// }
	// }

	// /**
	// * Загружает выполненые действия пользователями
	// *
	// * @param file
	// * файл с данными
	// */
	// private void readUsersActionsWithFile(String file) {
	// FileInputStream inFile = null;
	// ObjectInputStream objectInputStream = null;
	// try {
	// inFile = new FileInputStream(new File(file));
	// objectInputStream = new ObjectInputStream(inFile);
	// try {
	// usersActions = (ConcurrentMap<String, Set<UserActions>>)
	// objectInputStream
	// .readObject();
	// // Сброс голосований которые были в прошлых сутках
	// ActionCriterion criterion = new ResetVotingInTheNomination();
	// deleteActionsByCriterion(criterion);
	// } catch (ClassNotFoundException e) {
	// Log.LOGGING.addFileLog(
	// "Ошибка чтения действий пользователей. Ошибка : "
	// + e.getMessage(), Log.TYPE_NEGATIVE);
	// }
	//
	// } catch (IOException e) {
	// Log.LOGGING.addFileLog("Ошибка чтения файла: " + file
	// + ". Ошибка : " + e.getMessage(), Log.TYPE_NEGATIVE);
	// } finally {
	// try {
	// if (objectInputStream != null)
	// objectInputStream.close();
	// } catch (IOException e) {
	// }
	// }
	// }

	/**
	 * Преобразовывает пользователей в форму для сохранения
	 * 
	 * @param users
	 *            спсиок пользователей
	 * @return
	 */
	private List<String[]> getSaveData(Set<User> users) {

		List<String[]> result = new LinkedList<String[]>();
		if (users != null)
			for (User user : users)
				if (user.isSave) {
					result.add(user.toRow());
				}
		Collections.sort(result, new IDComparator());
		return result;
	}

	public StatusReportUserManager getStatusReport() {
		return new StatusReportUserManager(workingUsers.size(),
				bannedUsers.size());
	}

	/**
	 * Возращает список пользователь отобраных фильтром
	 * 
	 * @param usersFilter
	 *            фильтр пользователей
	 * @return никогда невозращает null
	 */
	public List<User> getUsers(UsersFilter usersFilter) {
		List<User> result = new LinkedList<User>();
		// System.out.println(workingUsers.);
		for (User user : getWorkingUsers()) {
			if (usersFilter.filtrate(user)) {
				result.add(user);
			}
		}

		return result;
	}
	
	public Collection<User> getWorkingUsers(){
		return workingUsers;
	}

	public void loadConfiguration() {
		fileWithProfiles = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Users.FileWithProfiles", fileWithProfiles);

		fileWithBannedProfiles = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Users.FileWithBannedProfiles",
				fileWithBannedProfiles);

		fileWithCookies = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Users.FileWithCookies", fileWithCookies);

		folderWithAvatars = ApplicationConfiguration.INSTANCE.getValue(
				"configuration.Users.FolderWithAvatars", folderWithAvatars);
		periodAutoloadUsers = ApplicationConfiguration.INSTANCE.getIntValue(
				"configuration.Users.PeriodAutoloadUsers", periodAutoloadUsers);
		periodClearingFileAutoloading = ApplicationConfiguration.INSTANCE
				.getIntValue(
						"configuration.Users.PeriodClearingFileAutoloading",
						periodClearingFileAutoloading);

		folderWithUsedAvatars = folderWithAvatars + File.separator
				+ "Использованые";
	}

	/**
	 * Загружает куки всех пользователей из файла
	 * 
	 * @param file
	 *            файл с куками в серриализованом виде
	 */
	@SuppressWarnings("unchecked")
	private void readCookiesWithFile(String file) {
		FileInputStream inFile = null;
		ObjectInputStream objectInputStream = null;
		try {
			inFile = new FileInputStream(new File(file));
			objectInputStream = new ObjectInputStream(inFile);
			try {
				cookieStore = (ConcurrentMap<String, CookieStore>) objectInputStream
						.readObject();
			} catch (ClassNotFoundException e) {
				Log.LOGGING.addFileLog(
						"Ошибка чтения куки. Ошибка : " + e.getMessage(),
						Log.TYPE_NEGATIVE);
			}

		} catch (IOException e) {
			Log.LOGGING.addFileLog("Ошибка чтения файла: " + file
					+ ". Ошибка : " + e.getMessage(), Log.TYPE_NEGATIVE);
		} finally {
			try {
				if (objectInputStream != null)
					objectInputStream.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Загружает пользователей из файла
	 * 
	 * @param update
	 *            флаг, нужно ли обновлять загруженые анкеты
	 * @return
	 */
	public int readListUsersWithFile(String file, boolean update) {
		int countNewUsers = 0;
		if (file.endsWith(".xls"))
			countNewUsers = readListUserWithXLS(file);
		else if (file.endsWith(".txt"))
			countNewUsers = readListUserWithText(file);
		else {
			File f = new File(file);
			if (f.isDirectory())
				countNewUsers = readListUsersWithFolder(file);
		}

		Log.LOGGING.addUserLog("Загружено " + countNewUsers + " новых анкет.",
				Log.getTypeMessage(countNewUsers));
		if (countNewUsers == 0) {
			// Application.APPLICATION.handleEvent(new NotLoadUsers());
		} else if (update)
			updateNewUsers();
		return countNewUsers;
	}

	private int readListUsersWithFolder(String pathFolder) {
		File folder = new File(pathFolder);
		int countNewUsers = 0;
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				countNewUsers += readListUsersWithFile(file.getPath(), false);
			}
		}
		return countNewUsers;
	}

	/**
	 * Загружает пользователей из текстового файла
	 * 
	 * @param file
	 *            файл
	 * @return количество новых анкет в файле
	 */
	private int readListUserWithText(String file) {
		List<String> data = FileManager.readTextFile(file);
		int countNewUsers = 0;
		for (String line : data) {
			if (addUser(User.createUser(line)))
				countNewUsers++;
		}
		return countNewUsers;
	}

	/**
	 * Загружает пользователей из xls файла
	 * 
	 * @param file
	 *            файл
	 * @return количество новых анкет в файле
	 */
	private int readListUserWithXLS(String file) {
		List<String[]> data = FileManager.readWorkingUsers(file);
		int countNewUsers = 0;
		for (String[] lineData : data) {
			if (addUser(User.createUser(lineData)))
				countNewUsers++;
		}
		return countNewUsers;
	}

	/**
	 * Загружает информацию с файлов указаных в конфигурации
	 */
	public void readWithFile() {
		readCookiesWithFile(fileWithCookies);
		// readUsersActionsWithFile(fileWithActionsUsers);
		readListUsersWithFile(fileWithProfiles, true);
	}

	/**
	 * Созраняет куки в файл
	 * 
	 * @param file
	 *            адресс файла
	 */
	private void saveCookiesToFile(String file) {
		FileOutputStream outFile = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			outFile = new FileOutputStream(new File(file));
			objectOutputStream = new ObjectOutputStream(outFile);
			objectOutputStream.writeObject(cookieStore);
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

	/**
	 * Сохраняет список пользователей в файл
	 * 
	 */
	private void saveListUsersToFile() {
		// Если файл с нерабочими анкетами слишком большой, то он перемещается в
		// папку с нерабочими анкетами
		File fileBadProfiles = new File(fileWithBannedProfiles);
		if (fileBadProfiles.length() > MAX_SIZE_FILE) {
			File folder = new File(folderWithNotWorkingProfiles);
			File newName = new File(folder.getPath() + File.separator
					+ "Нерабочие анкеты (" + folder.list().length + ").xls");
			fileBadProfiles.renameTo(newName);
		}

		FileManager.writeWorkingUsers(fileWithProfiles,
				getSaveData(workingUsers));
		if (bannedUsers.size() > 0)
			FileManager.writeBannedUsers(fileWithBannedProfiles,
					getSaveData(bannedUsers));
	}

	/**
	 * Сохраняет всю связаную с пользователями информацию в файлы указаные в
	 * конфигурации
	 */
	public void saveToFile() {
		saveListUsersToFile();
		saveCookiesToFile(fileWithCookies);
		// saveUsersActionsToFile(fileWithActionsUsers);
	}

	public void setCookie(String id, CookieStore cookie) {
		if (id.compareTo("") != 0)
			cookieStore.put(id, cookie);
	}

	/**
	 * Устанавливает запись действий пользователя в хранилище
	 * 
	 * @param id
	 * @param actions
	 */
	public void setUserActions(String id, Set<UserActions> actions) {
		// if (id.compareTo("") != 0)
		// usersActions.put(id, actions);
	}

	/**
	 * Обновлет анкеты новых пользователей
	 */
	public void updateNewUsers() {
		// Set<User> newUsers = USER_MANAGER.getUsers(new FilterNewUsers());
		// if (newUsers.size() > 0) {
		TaskUpdateProfiles updateTask = new TaskUpdateProfiles();
		updateTask.addFilter(new FilterNewUsers());
		TaskManager.TASK_MANAGER.executeTask(updateTask);
		// }
		// }
		// userAuthorization(notAutorizationUser);
	}

	/**
	 * Авторизовывает заданых пользователей
	 * 
	 * @param notLoginUsers
	 */
	public void userAuthorization() {
		// if (notLoginUsers.size() > 0) {
		TaskLogin updateTask = new TaskLogin();
		TaskManager.TASK_MANAGER.executeTask(updateTask);
		// }
	}
}
