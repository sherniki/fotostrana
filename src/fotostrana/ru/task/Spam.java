package fotostrana.ru.task;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import configuration.ApplicationConfiguration;
import fotostrana.ru.FileManager;
import fotostrana.ru.log.Log;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.reports.sendMessages.Message;
import fotostrana.ru.users.User;

public class Spam {
	public static final String ConfigurationKey = "configuration.Task.Spam.";
	public static final String SEPARATOR = "|";
	private String folderWithSpam = "Спам" + File.separator;
	private String fileNominationRegions = folderWithSpam
			+ "Регионы в номинациях.txt";
	private String fileWithRatingRegions = folderWithSpam
			+ "Регионы в рейтинге.txt";
	public String fileManProfiles = folderWithSpam + "Мужские анкеты.txt";
	public String fileWithWomanProfiles = folderWithSpam + "Женские анкеты.txt";
	private String fileWithGreetings = folderWithSpam + "Приветствия.txt";
	private String fileWithMainText = folderWithSpam + "Основная часть.txt";
	public String fileWithSpamReport = folderWithSpam + "Отчет о спаме.xls";

	/**
	 * Флаг показывающий использовался ли класс
	 */
	private boolean isUsed = false;
	public List<Region> regions = new ArrayList<Region>();
	/**
	 * пользователи от имени которых будет отправляться спам
	 */
	public Set<User> spamUsers = new TreeSet<User>();
	/**
	 * приветствия
	 */
	public List<String> greetings = new ArrayList<String>();
	/**
	 * Основной текст
	 */
	public List<String> mainText = new ArrayList<String>();

	public List<Region> regionsRating = new ArrayList<Region>();

	/**
	 * Пользователи которым уже отправлялся спам
	 */
	public List<RecordReport> alreadySent = new ArrayList<RecordReport>();

	public List<Message> sentMessages = new LinkedList<Message>();

	public void loadConfiguration() {
		fileNominationRegions = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileNominationRegions",
				fileNominationRegions);
		fileManProfiles = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileWithManProfile", fileManProfiles);
		fileWithWomanProfiles = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileWithWomanProfile",
				fileWithWomanProfiles);
		fileWithGreetings = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileWithGreeting", fileWithGreetings);
		fileWithMainText = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileWithMainText", fileWithMainText);
		fileWithSpamReport = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileWithSpamReport", fileWithSpamReport);
		fileWithRatingRegions = ApplicationConfiguration.INSTANCE.getValue(
				ConfigurationKey + "FileRatingRegions", fileWithRatingRegions);
	}

	public void loadFile() {
		isUsed = true;
		loadSpamUsers();
		loadRegions();
		loadSpamText();
		loadSentMessages();
	}

	public void saveFile() {
		if (!isUsed)
			return;
		saveSpamUsers();
		saveReport();
	}

	private void loadRegions() {
		List<String> r = FileManager.readTextFile(fileNominationRegions);
		for (String string : r) {
			Region region = Region.create(string);
			if (region != null)
				regions.add(region);
		}
		Log.LOGGING
				.addFileLog("Загружено " + regions.size()
						+ " регионов в НОМИНАЦИЯХ.",
						Log.getTypeMessage(regions.size()));

		List<String> ratingRegions = FileManager
				.readTextFile(fileWithRatingRegions);
		for (String string : ratingRegions) {
			Region region = Region.create(string);
			if (region != null)
				regionsRating.add(region);
		}
		Log.LOGGING.addFileLog("Загружено " + regionsRating.size()
				+ " регионов в РЕЙТИНГ.",
				Log.getTypeMessage(regionsRating.size()));
	}

	private void loadSpamUsers() {
		spamUsers.clear();
		List<String> manUsers = FileManager.readTextFile(fileManProfiles);
		for (String urlAutoLogin : manUsers) {
			User user = new User(urlAutoLogin);
			user.gender = User.GENDER_MAN;
			spamUsers.add(user);
		}

		List<String> womanUsers = FileManager
				.readTextFile(fileWithWomanProfiles);
		for (String urlAutoLogin : womanUsers) {
			User user = new User(urlAutoLogin);
			user.gender = User.GENDER_WOMAN;
			spamUsers.add(user);
		}
		Log.LOGGING.addFileLog("Загружено " + spamUsers.size()
				+ " анкет для рассылки спама.",
				Log.getTypeMessage(spamUsers.size()));
	}

	private void loadSpamText() {
		greetings = FileManager.readTextFile(fileWithGreetings);
		Log.LOGGING.addFileLog("Загружено " + greetings.size()
				+ " приветствий.", Log.getTypeMessage(greetings.size()));
		mainText.clear();
		List<String> lines = FileManager.readTextFile(fileWithMainText);
		lines.add(SEPARATOR);
		String currentMainText = "";
		for (String currentLine : lines) {
			if (currentLine.compareTo(SEPARATOR) == 0) {
				if (currentMainText.length() > 5)
					mainText.add(currentMainText);
				currentMainText = "";
			} else
				currentMainText += currentLine + '\n';
		}
		Log.LOGGING.addFileLog("Загружено " + mainText.size()
				+ " текстов спама.", Log.getTypeMessage(mainText.size()));
	}

	private void loadSentMessages() {
		alreadySent.clear();
		List<String[]> spamReport = FileManager
				.readSpamReport(fileWithSpamReport);
		for (String[] line : spamReport) {
			if (line.length >= 4) {
				String id = line[3];
				try {
					RecordReport record = new RecordReport(Integer.parseInt(id));
					alreadySent.add(record);
				} catch (Exception e) {
				}
			}
		}
	}

	private void saveSpamUsers() {
		List<String> man = new LinkedList<String>();
		List<String> woman = new LinkedList<String>();
		for (User user : spamUsers) {
			switch (user.gender) {
			case User.GENDER_MAN:
				man.add(user.urlAutoConnection);
				break;
			case User.GENDER_WOMAN:
				woman.add(user.urlAutoConnection);
				break;
			}
		}
		FileManager.writeTextFile(fileManProfiles, man, false);
		FileManager.writeTextFile(fileWithWomanProfiles, woman, false);
	}

	private void saveReport() {
		List<String[]> dataReport = new LinkedList<String[]>();
		DateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
		for (Message message : sentMessages) {
			String[] line = new String[6];
			line[0] = message.sender.urlAutoConnection;
			line[1] = message.sender.id;
			line[2] = message.sender.name;
			line[3] = message.targetId;
			line[4] = message.description;
			line[5] = format.format(message.timeSend);
			dataReport.add(line);
		}
		if (dataReport.size() > 0)
			FileManager.writeSpamReport(fileWithSpamReport, dataReport);
	}

	/**
	 * Добавляет отправленые сообщения в отчет
	 * 
	 * @param messages
	 */
	public void addSentMessages(Collection<Message> messages) {
		sentMessages.addAll(messages);
	}

}
