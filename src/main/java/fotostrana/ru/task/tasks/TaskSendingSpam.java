package fotostrana.ru.task.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.reports.leadersOfVoting.ReportLeadersOfVoting;
import fotostrana.ru.reports.sendMessages.Message;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.Spam;
import fotostrana.ru.task.groups.GroupLeadersOfVoting;
import fotostrana.ru.task.groups.GroupSendingMessages;
import fotostrana.ru.users.User;

/**
 * Рассылает спам лидерам голосований в разных номинациях и конкурсах
 * 
 */
public class TaskSendingSpam extends CompositeTask {
	/**
	 * Группа заданий для получения лидеров голосования
	 */
	protected GroupLeadersOfVoting groupLeadersOfVoting;

	protected GroupSendingMessages groupSendingMessages;
	/**
	 * найденые лидеры голосваний
	 */
	protected ReportLeadersOfVoting report;

	protected Random random = new Random();
	/**
	 * Распределение сообщений между отправителями спама
	 */
	protected Map<User, List<Message>> distributionMessages;

	/**
	 * Мужские анкеты отправителей
	 */
	protected List<User> man;
	/**
	 * Женские анкеты отправителей
	 */
	protected List<User> woman;

	/**
	 * Количество сообщений у каждой анкеты
	 */
	protected int[][] countMessages;

	private Spam spam;

	/**
	 * Максимальное количество сообщений с одной анкеты
	 */
	protected int maxCountMessages;

	/**
	 * Если установлен сооющения будут отправляться только тем кто онлайн
	 */
	private boolean isOnline;

	private TaskCheckOnline taskCheckOnline;

	/**
	 * @param spam
	 *            парамерты рассылки спама
	 * @param nominations
	 *            номинации в которых будут браться люди
	 * @param maxCountmessages
	 *            максимальное количество сообщений с одной анкеты
	 * @param minDelay
	 *            минимальная задержка между сообщениями (в секундах)
	 * @param maxDelay
	 *            максимальная задержка между сообщениями (в секундах)
	 */
	public TaskSendingSpam(Spam spam, List<Nomination> nominations,
			int maxCountmessages, boolean isOnline, int minDelay, int maxDelay) {
		this.descriptionTask = "Рассылка спама.";
		this.name = descriptionTask;
		this.spam = spam;
		this.isOnline = isOnline;
		this.distributionMessages = new HashMap<User, List<Message>>();
		this.maxCountMessages = maxCountmessages;
		groupLeadersOfVoting = new GroupLeadersOfVoting(this, spam.spamUsers);
		leaders(nominations, spam.regions);
		groupSendingMessages = new GroupSendingMessages(this, minDelay,
				maxDelay);
		groupSendingMessages.messages = spam.sentMessages;
		currentTask = groupLeadersOfVoting;

	}

	/**
	 * Добавляет к заданию поиск лидеров в заданых номинациях
	 * 
	 * @param nominations
	 *            список номинаций
	 * @param regionsNomination
	 *            регионы в номинациях
	 */
	public void leaders(List<Nomination> nominations,
			List<Region> regionsNomination) {
		for (Nomination nomination : nominations) {
			if ((nomination == Nomination.CITY)
					|| (nomination == Nomination.CHARM)
					|| (nomination == Nomination.SYMPATHY)
					|| (nomination == Nomination.SUPERSTAR))
				groupLeadersOfVoting.leadersOfNomination(nomination,
						regionsNomination);
			if (nomination == Nomination.TOURNAMENT)
				groupLeadersOfVoting.leadersOfTournament();
			if (nomination == Nomination.RATING) {
				groupLeadersOfVoting.leadersOfRating(spam.regionsRating);
			}
		}

	}

	@Override
	public int getCountOfTaskToBePerformed() {
		System.out.println("getCountOfTaskToBePerformed");
		System.out.println((isOnline == true) ? 3 : 2);
		return (isOnline) ? 3 : 2;
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (event instanceof EventCompliteTheTask) {
			handleCompliteTheGroup(((EventCompliteTheTask) event).getTask());
		}
	}

	protected void handleCompliteTheGroup(AbstractTask groupTask) {
		if (groupTask == groupLeadersOfVoting) {
			report = groupLeadersOfVoting.report;
			initializationSenders();
			if (spam.spamUsers.size() == 0) {
				state = "Нет анкет для отправки сообщений.";
				scheduler.stop();
			} else {
				if (isOnline) {
					Map<String, Boolean> mapUsers = new HashMap<String, Boolean>();
					for (RecordReport record : report.getListRecord()) {
						mapUsers.put(Integer.toString(record.id), null);
					}
					descriptionTask += " Проверка на online.";
					taskCheckOnline = new TaskCheckOnline(mapUsers, this);
					currentTask = taskCheckOnline;
				} else {
					generateSpam();
					currentTask = groupSendingMessages;
				}
				currentTask.start();
			}
		}
		if (groupTask == taskCheckOnline) {
			Map<String, Boolean> mapUsers = taskCheckOnline.users;
			Iterator<RecordReport> iterator = report.getListRecord().iterator();
			while (iterator.hasNext()) {
				RecordReport record = iterator.next();
				Boolean isOnlineCureentUsers = mapUsers.get(Integer
						.toString(record.id));
				if ((isOnlineCureentUsers == null)
						|| (isOnlineCureentUsers == false)) {
					iterator.remove();
				}
			}
			generateSpam();
			currentTask = groupSendingMessages;
			currentTask.start();
		}
		if (groupTask == groupSendingMessages) {
			state = groupSendingMessages.getTaskState();
			scheduler.stop();
		}
	}

	protected void generateSpam() {
		filtrateTargetUsers();
		for (RecordReport currentRecord : report.getListRecord()) {
			User user = getUser(currentRecord.gender);
			if (user != null) {
				Message message = getMessage(currentRecord);
				message.description = currentRecord.nomination.name;
				distributionMessages.get(user).add(message);
			} else
				continue;
		}
		for (User user : spam.spamUsers) {
			List<Message> messages = distributionMessages.get(user);
			if (messages.size() > 0) {
				groupSendingMessages.addMailing(user, messages);
			}
		}

	}

	/**
	 * Находит пользователя для отправки сообщения, по заданому полу получателя
	 * 
	 * @param targetGender
	 *            пол получателя
	 * @return null если нет пользователя отправителя для заданого пола
	 */
	private User getUser(int targetGender) {
		if (targetGender == User.GENDER_UNKNOW)
			targetGender = random.nextInt(2);
		targetGender = (targetGender + 1) % 2;
		if (countMessages[targetGender].length == 0)
			return null;
		int index = getIndexMinValue(countMessages[targetGender]);
		if (countMessages[targetGender][index] < maxCountMessages) {
			countMessages[targetGender][index]++;
			if (targetGender == User.GENDER_MAN)
				return man.get(index);
			else
				return woman.get(index);
		} else
			return null;
	}

	private int getIndexMinValue(int[] array) {
		int minValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < minValue) {
				minValue = array[i];
			}
		}
		List<Integer> minIndexes = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++)
			if (array[i] == minValue) {
				minIndexes.add(i);
			}
		int i = random.nextInt(minIndexes.size());
		return minIndexes.get(i);
	}

	/**
	 * Подготовка анкет отправителей
	 */
	private void initializationSenders() {
		deleteBanUsers();
		man = new ArrayList<User>();
		woman = new ArrayList<User>();
		for (User currentSender : spam.spamUsers) {
			if (currentSender.gender == User.GENDER_MAN)
				man.add(currentSender);
			if (currentSender.gender == User.GENDER_WOMAN)
				woman.add(currentSender);
			distributionMessages.put(currentSender, new ArrayList<Message>());
		}
		countMessages = new int[2][];
		countMessages[User.GENDER_WOMAN] = new int[woman.size()];
		countMessages[User.GENDER_MAN] = new int[man.size()];
	}

	/**
	 * Создает сообщение для найденого пользователя
	 * 
	 * @param targetSpam
	 *            пользовател которому будет отправляен спам
	 * @return
	 */
	private Message getMessage(RecordReport targetSpam) {
		int indexGreeting = random.nextInt(spam.greetings.size());
		String greeting = spam.greetings.get(indexGreeting);
		int indexMainText = random.nextInt(spam.mainText.size());
		String maintext = spam.mainText.get(indexMainText);
		String message = targetSpam.name.toUpperCase() + " " + greeting + '\n'
				+ maintext;
		return new Message(Integer.toString(targetSpam.id), message);
	}

	/**
	 * Удаляет из рассылки людей которым уже отправлялись письма
	 */
	private void filtrateTargetUsers() {
		report.getListRecord().removeAll(spam.alreadySent);
	}

	/**
	 * Удаляет забаненых пользователей
	 */
	private void deleteBanUsers() {
		Iterator<User> iterator = spam.spamUsers.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (user.isBanned)
				iterator.remove();
		}
	}

}
