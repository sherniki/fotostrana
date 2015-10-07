package fotostrana.ru.network.requests.fotostrana;

import fotostrana.ru.events.network.votes.SuccessfulVote;
import fotostrana.ru.users.User;

/**
 * Запрос голосования
 * 
 */
public abstract class RequestVote extends RequestFotostrana {
	/**
	 * Количество голосов, после которого отправляет сообщение о их накрутки
	 */
	public int COUNT_VOTES_FOR_SEND_MESSAGE = 3;

	/**
	 * количество накрученых голосов для оповещения
	 */
	public int countVotesForMessage = 0;

	/**
	 * Количество голосов которое должен накрутить пользователь
	 */
	public int countOfVotesRequired = 1;

	/**
	 * Успешно накрученых голосов
	 */
	public int countSuccessfulVotes = 0;
	/**
	 * Ид за кого голосовать
	 */
	public String targetId;

	/**
	 * Количество голосов вовремя голосования
	 */
	protected int points = -1;
	/**
	 * Место вовремя голосования
	 */
	protected int position = -1;

	/**
	 * Количество голосов до голосования
	 */
	protected int pointsBeforeVoting = -1;

	/**
	 * Место до голосования
	 */
	protected int positionBeforeVoting = -1;
	protected boolean isVisit;

	public RequestVote(RequestFotostrana parentRequest, int countVotes,
			String targetId, boolean isVisit) {
		super(parentRequest);
		this.targetId = targetId;
		this.isVisit = isVisit;
		setCountVotes(countVotes);
	}

	public RequestVote(User user, int countVotes, String targetId, boolean isVisit) {
		super(user);
		this.isVisit = isVisit;
		setCountVotes(countVotes);
		this.targetId = targetId;

	}

	public void setCountVotes(int countVotes) {
		this.countOfVotesRequired = countVotes;
		if ((countVotes / 50) > COUNT_VOTES_FOR_SEND_MESSAGE)
			COUNT_VOTES_FOR_SEND_MESSAGE = countVotes / 50;
	}

	/**
	 * Количество голосов до голосования
	 * 
	 * @return -1 если неизвестно
	 */
	public int getPointsBeforeVoting() {
		return pointsBeforeVoting;
	}

	/**
	 * Количество голосов после голосования
	 * 
	 * @return -1 если неизвестно
	 */
	public int getPointsAfterVoting() {
		return points;
	}

	/**
	 * Место до голосования
	 * 
	 * @return -1 если неизвестно
	 */
	public int getPositionBeforeVoting() {
		return positionBeforeVoting;
	}

	/**
	 * Место после голосования
	 * 
	 * @return -1 если неизвестно
	 */
	public int getPositionAfterVoting() {
		return position;
	}

	/**
	 * Обрабатывает успшено накрученый голос
	 */
	protected void addVote() {
		countSuccessfulVotes++;
		countVotesForMessage++;
		if (countVotesForMessage >= COUNT_VOTES_FOR_SEND_MESSAGE) {
			eventListener.handleEvent(new SuccessfulVote(this,
					countVotesForMessage));
			countVotesForMessage = 0;
		}
	}
}
