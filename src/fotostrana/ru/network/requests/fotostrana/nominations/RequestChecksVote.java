package fotostrana.ru.network.requests.fotostrana.nominations;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.network.filters.Filter;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.users.User;

/**
 * Запрос проверки возможности проголосовать
 * 
 */
public class RequestChecksVote extends RequestFotostrana {
	public static final String[] KEYWORDS = { "token", "canvotefree",
			"presentvotes" };
	public static final int COUNT_NOMINATION = 6;

	public String targetId = "";
	int nomination;
	public int[] position = new int[COUNT_NOMINATION];
	public int[] votes = new int[COUNT_NOMINATION];
	private int currentVotes = -1;
	private int currentPosition = -1;
	public int countVoteFree = -1;
	public String targetName = "";
	public String token = "";

	/**
	 * @param user
	 *            пользователь от имени которого посылается запрос
	 * @param targetId
	 *            пользователь который проверяется
	 * @param nomination
	 *            номинация
	 */
	public RequestChecksVote(User user, String targetId, int nomination) {
		super(user);
		this.targetId = targetId;
		this.nomination = nomination;
		init();
	}

	public RequestChecksVote(RequestVoteIsNomination parentRequest) {
		super(parentRequest);
		this.targetId = parentRequest.targetId;
		init();
	}

	protected void init() {
		for (int i = 0; i < votes.length; i++) {
			votes[i] = -1;
			position[i] = -1;
		}
	}

	@Override
	public String getURL() {
		return URL_FOTOSTRANA + "contest/ajax/votepopup/?userId=" + targetId
				+ "&mimi=1";
	}

	@Override
	public void setResult(String result) {
		Event event;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				if (Filter.checkingByKeywords(result, KEYWORDS)) {
					token = ParserJSON.getString(result, "token");

					int iNameStart = result.indexOf("viewuser");
					targetName = ParserJSON.getString(result, "name",
							iNameStart);
					// определение голосов и мест до голосования
					int indexPosition = result.indexOf("nominations",
							iNameStart);
					while (true) {
						indexPosition = result.indexOf("id", indexPosition);
						if (indexPosition == -1)
							break;
						int idNom = ParserJSON.getInt(result, "id",
								indexPosition);
						position[idNom] = ParserJSON.getInt(result, "position",
								indexPosition);
						votes[idNom] = ParserJSON.getInt(result, "votes",
								indexPosition);
						indexPosition += "position".length();
					}

					if (nomination == Nomination.NOMINATION.id) {
						choiceNomination();
					}
					currentPosition = position[nomination];
					currentVotes = votes[nomination];

					countVoteFree = ParserJSON.getInt(result, "canvotefree");
					int countPresentVotes = ParserJSON.getInt(result,
							"presentvotes");
					int canvotepayed = ParserJSON
							.getInt(result, "canvotepayed");
					countVoteFree = Math.max(canvotepayed, countVoteFree);
					countVoteFree = Math.max(countPresentVotes, countVoteFree);
					user.quickNomination.set(countVoteFree);
					// if (countVoteFree < 1) {
					// event = new EventCanNotVote(this);
					// } else
					event = new EventRequestExecutedSuccessfully(this);
				} else
					event = new EventCanNotVote(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

	/**
	 * Количество голосов до голосования
	 * 
	 * @return
	 */
	public int getCurrentVotes() {
		return currentVotes;
	}

	/**
	 * Место до голосования
	 * 
	 * @return
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * выбирает номинацию для голосования если она не указана
	 */
	private void choiceNomination() {
		int minPosition = Integer.MAX_VALUE;
		int newNomination = 0;
		for (int i = 0; i < position.length; i++) {
			int intPosition = position[i];
			if ((intPosition > 0) && (intPosition < minPosition)) {
				minPosition = intPosition;
				newNomination = i;
			}
		}
		nomination = newNomination;
	}
}
