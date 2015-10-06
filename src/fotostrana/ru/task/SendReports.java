package fotostrana.ru.task;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import configuration.ApplicationConfiguration;
import fotostrana.ru.FileManager;
import fotostrana.ru.log.Log;
import fotostrana.ru.reports.sendMessages.Message;
import fotostrana.ru.users.User;

/**
 * Рассылка сообщений клиентам о выполнении задания
 * 
 */
public class SendReports {
	private static String folder = "Отчитывалка" + File.separator;
	private String configuration = "configuration.Task.SendReports.";
	public String fileWithRecords = folder + "Отчитывалка.txt";
	public String fileWithReport = folder + "Отчет о рассылке.xls";
	public String fileWithUsers = folder + "Анкета.txt";

	public boolean isUsed = false;
	
	public List<Message> messages;

	public final String SEPARATOR = "|";
	/**
	 * анкеты с которых будут отправляться сообщения
	 */
	public TreeSet<User> users;

	public SendReports() {
		users = new TreeSet<User>();
		// sentMessages = new LinkedList<Message>();
	}

	private boolean checkID(String id) {
		try {
			int intID = Integer.parseInt(id);
			if (intID > 0)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	private int getInt(String value) {
		Scanner scanner = new Scanner(value);
		try {
			if (scanner.hasNextInt())
				return scanner.nextInt();
			else
				return Integer.MAX_VALUE;
		} catch (Exception e) {
			return Integer.MAX_VALUE;
		} finally {
			scanner.close();
		}
	}

	private boolean isNumber(String value) {
		Scanner scanner = new Scanner(value);
		try {
			if (scanner.hasNextInt())
				return true;
		} catch (Exception e) {
		} finally {
			scanner.close();
		}
		return false;
	}

	public void loadConfiguration() {
		fileWithReport = ApplicationConfiguration.INSTANCE.getValue(
				configuration + "FileWithReport", fileWithReport);
		fileWithUsers = ApplicationConfiguration.INSTANCE.getValue(
				configuration + "FileWithUsers", fileWithUsers);
		fileWithRecords = ApplicationConfiguration.INSTANCE.getValue(
				configuration + "DefaultFile", fileWithRecords);
	}

	public void loadFiles() {
		loadFiles(fileWithUsers, fileWithReport);
	}

	public void loadFiles(String fileProfiles, String fileReport) {
		loadUsers(fileProfiles);
		loadReports(fileReport);
	}

	private void loadReports(String file) {
		List<String> lines = FileManager.readTextFile(file);
		messages = new LinkedList<Message>();
		List<String> errorLines = new LinkedList<String>();
		for (String line : lines) {
			String[] columns = line.split("[" + SEPARATOR + "]");
			if (columns.length == 10) {
				String namePayer = columns[0].trim();
				String idPayer = columns[1].trim();
				String idVote = columns[6].trim();
				String nameVote = columns[4].trim();
				String moneyRub = columns[7].trim();
				String countVotes = columns[8].trim();
				String moneyOtherCurrency = columns[9].trim();
				String nomination = columns[5].trim();

				String idRecipient = null;
				String nameRecipient = null;
				if (checkID(idPayer)) {
					idRecipient = idPayer;
					nameRecipient = namePayer;
				} else {
					if (checkID(idVote)) {
						idRecipient = idVote;
						nameRecipient = nameVote;
					} else {
						errorLines.add(line);
						continue;
					}
				}

				int intCountVotes = Integer.MIN_VALUE;
				if (isNumber(countVotes)) {
					intCountVotes = Math.abs(Integer.parseInt(countVotes));
				} else {
					errorLines.add(line);
					continue;
				}

				int intMoney = Integer.MAX_VALUE;
				if (isNumber(moneyOtherCurrency)) {
					intMoney = getInt(moneyOtherCurrency);
				} else {
					if (moneyRub.indexOf(" ") != -1) {
						moneyRub = moneyRub.substring(0, moneyRub.indexOf(" "));
					}
					if (isNumber(moneyRub)) {
						intMoney = getInt(moneyRub);
					} else {
						errorLines.add(line);
						continue;
					}
				}
				String textMessage = nameRecipient + ", привет" + '\n'
						+ "Задание выполнено, ";
				if (intMoney < 0) {
					textMessage += "долг " + Math.abs(intMoney) + " за "
							+ intCountVotes + " голосов." + '\n'
							+ "Пополнишь копилку, сообщи.";
				} else {
					textMessage += "остаток " + intMoney + " на "
							+ intCountVotes + " голосов.";
				}
				Message message = new Message(idRecipient, textMessage);
				messages.add(message);
				message.description = nomination;
			} else
				errorLines.add(line);
		}
		if (errorLines.size() > 0) {
			Log.LOGGING.addTaskLog("Нераспознанные строки:", Log.TYPE_NEGATIVE);
			for (String errorLine : errorLines) {
				Log.LOGGING.addTaskLog(errorLine, Log.TYPE_NEGATIVE);
			}

		}
	}

	private void loadUsers(String file) {
		List<String> strings = FileManager.readTextFile(file);
		for (String urlAutoLogin : strings) {
			User user = new User(urlAutoLogin);
			users.add(user);
		}
	}

	public void saveFiles() {
		if (isUsed) {
			saveUsers();
			saveReport();
		}
	}

	private void saveReport() {
		List<String[]> report = new LinkedList<String[]>();
		DateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
		for (Message message : messages)
			if (message.isSent) {
				String[] line = new String[6];
				line[0] = message.sender.urlAutoConnection;
				line[1] = message.sender.id;
				line[2] = message.targetId;
				line[3] = message.description;
				line[4] = format.format(message.timeSend);
				line[5] = message.message;
				report.add(line);
			}
		if (report.size() > 0)
			FileManager.writeSendingReport(fileWithReport, report);

	}

	private void saveUsers() {
		List<String> lines = new LinkedList<String>();
		for (User user : users)
			if (!user.isBanned) {
				lines.add(user.urlAutoConnection);
			}
		FileManager.writeTextFile(fileWithUsers, lines, false);
	}

}
