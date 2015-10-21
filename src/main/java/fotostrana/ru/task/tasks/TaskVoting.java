package fotostrana.ru.task.tasks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.network.votes.SuccessfulVote;
import fotostrana.ru.events.schedulers.FailEventScheduler;
import fotostrana.ru.events.tasks.SuccessfulVotes;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.RequestVote;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;

/**
 * Интерфейс заданий для голосования
 * 
 */
public abstract class TaskVoting extends TaskFotostrana {
	private static FailEventScheduler FAIL_EVENT = new FailEventScheduler();

	public static final String SEPARATOR = "|";

	/**
	 * Минимальное количество голосов, после которого добавляется запись в лог
	 */
	public int COUNT_VOTES_FOR_ADDING_THE_LOG = 10;
	/**
	 * Период подкачки анкет
	 */
	public int PERIOD_SWAPPING_PROFILES = 60 * 1000;

	/**
	 * Минимальное количество голосов для отправки сообщения слушателю о их
	 * накрутке
	 */
	public int COUNT_VOTES_FOR_MESSAGE = 10;
	/**
	 * Голоса до голосования
	 */
	protected int voteBeforeTheVote = -1;
	/**
	 * Мессто до голосования
	 */
	protected int positionBeforeTheVote = -1;
	/**
	 * Голоса после голосования
	 */
	protected int voteAfterTheVote = -1;
	/**
	 * Место после голосвания
	 */
	protected int positionAfterTheVote = -1;

	/**
	 * количество накрученых голосов для добавления записи в лог
	 */
	private int countVoteForLog = 0;

	/**
	 * Количество накрученых голосов для отправки сообщения
	 */
	private int countVoteForMessage = 0;

	/**
	 * Название номинации голосования
	 */
	protected Nomination nomination;

	/**
	 * Заходить на страницу пользователя за которого голосуем после успешного
	 * голоса
	 */
	public boolean isVisit;

	protected Timer timerSwappingProfiles;

	/**
	 * @param targetId
	 *            за кого голосовать
	 * @param nomination
	 *            номинация голосования
	 * @param countVotes
	 *            количество голосов
	 * @param targetName
	 *            имя человека за кого голосуем
	 * @param isVisit
	 *            заходить на страницу после успешного голоса
	 */
	public TaskVoting(String targetId, Nomination nomination, int countVotes,
			String targetName, boolean isVisit) {
		this.nomination = nomination;
		this.targetName = targetName;
		scheduler.setCountOfTaskToBePerformed(countVotes);
		this.countVotes = countVotes;
		this.targetId = targetId;
		this.isVisit = isVisit;
		if (countVotes > 5000)
			COUNT_VOTES_FOR_ADDING_THE_LOG = countVotes / 100;
		descriptionTask = countVotes + " голосов " + targetName + " ("
				+ targetId + ")" + "  в " + nomination.name + ".";

	}

	/**
	 * Обрабатывает добавление голосов
	 * 
	 * @param countVotes
	 *            сколько голосов было добавлено
	 */
	protected void addedToVotes(int countVotes) {
		if (countVotes < 1)
			return;
		countSuccessfulVotes.addAndGet(countVotes);
		countVoteForLog += countVotes;
		countVoteForMessage += countVotes;
		if (countVoteForLog >= COUNT_VOTES_FOR_ADDING_THE_LOG) {
			Log.LOGGING.addTaskLog("Отдано " + countVoteForLog + " голосов "
					+ targetName + " (" + targetId + ").",
					Log.getTypeMessage(countVoteForLog));
			countVoteForLog = 0;
		}
		if (countVoteForMessage >= COUNT_VOTES_FOR_MESSAGE) {
			eventListener.handleEvent(new SuccessfulVotes(this,
					countVoteForMessage));
			countVoteForMessage = 0;
		}
	}

	@Override
	public boolean executeOneSubtask() {
		Request request = createNewRequest();
		if (request != null) {
			scheduler.getStorageRequests().addRequest(request);
		} else
			swappingProfiles();
		return true;
	}

	@Override
	public void finish() {
		stopSwappingProfiles();
		if (countVoteForMessage > 0) {
			eventListener.handleEvent(new SuccessfulVotes(this,
					countVoteForMessage));
			countVoteForMessage = 0;
		}
		timeFinish = new Date();

		state = "Отдано " + countSuccessfulVotes.get() + " из " + countVotes
				+ " голосов.";
		super.finish();
	}

	@Override
	public List<String[]> getReport() {
		List<String[]> report = new LinkedList<String[]>();
		int COLUMN_COUNT = 17;
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String[] result = new String[COLUMN_COUNT];
		result[0] = targetName;
		result[1] = SEPARATOR;
		result[2] = nomination.name;
		result[3] = SEPARATOR;
		result[4] = targetId;
		result[5] = SEPARATOR;
		result[6] = Integer.toString(getCountVotes());
		result[7] = Integer.toString(getCountSuccessfulVotes());
		if (timeStart != null)
			result[8] = dateFormat.format(timeStart);
		else
			result[8] = "Не выполнялось";

		if (timeFinish != null)
			result[9] = dateFormat.format(timeFinish);
		else
			result[9] = "Не завершено";
		result[10] = taskExecutionTime();
		result[11] = Integer.toString(voteBeforeTheVote);
		result[12] = Integer.toString(voteAfterTheVote);
		result[13] = Integer.toString(voteAfterTheVote - voteBeforeTheVote);
		result[14] = Integer.toString(positionBeforeTheVote);
		result[15] = Integer.toString(positionAfterTheVote);
		result[16] = Integer.toString(positionBeforeTheVote
				- positionAfterTheVote);
		report.add(result);
		return report;
	}

	@Override
	protected void handleNetworkEvent(EventOfNetworkRequestsFotostrana event) {
		if (event instanceof SuccessfulVote) {
			addedToVotes(((SuccessfulVote) event).getCountVotes());
		}
		super.handleNetworkEvent(event);
	}

	/**
	 * Обработка успешно выполненого запроса
	 * 
	 * @param request
	 */
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		if (request instanceof RequestVote) {
			RequestVote voteRequest = (RequestVote) request;

			if (voteBeforeTheVote == -1) {
				voteBeforeTheVote = voteRequest.getPointsBeforeVoting();
				positionBeforeTheVote = voteRequest.getPositionBeforeVoting();
			}

			if (voteAfterTheVote < voteRequest.getPointsAfterVoting()) {
				voteAfterTheVote = voteRequest.getPointsAfterVoting();
				positionAfterTheVote = voteRequest.getPositionAfterVoting();
			}
			addedToVotes(voteRequest.countVotesForMessage);
		}
	}

	/**
	 * подкачка анкет
	 */
	protected void swappingProfiles() {
		if (timerSwappingProfiles == null) {
			Log.LOGGING.addTaskLog("Включена подкачка анкет", Log.TYPE_NEUTRAL);
			timerSwappingProfiles = new Timer(
					UserManager.USER_MANAGER.periodAutoloadUsers * 1000,
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							List<User> newUsers = UserManager.USER_MANAGER
									.getUsers(usersFilter);
							newUsers.removeAll(usedUsers);
							listUsers.addAll(newUsers);
//							Log.LOGGING.addTaskLog(
//									"Дозагружено " + listUsers.size()
//											+ " новых анкет.",
//									Log.getTypeMessage(listUsers.size()));
							for (int i = 0; i < listUsers.size(); i++) {
								scheduler.handleEvent(FAIL_EVENT);
							}
						}
					});
			timerSwappingProfiles.start();
		}
	}

	protected void stopSwappingProfiles() {
		if (timerSwappingProfiles != null) {
			timerSwappingProfiles.stop();
		}
	}
}
