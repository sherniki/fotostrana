package fotostrana.ru.network.requests.fotostrana.nominations;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventNotSuccessfullVoteNomination;
import fotostrana.ru.network.filters.VoteNominationFilter;
import fotostrana.ru.network.requests.fotostrana.RequestVisitUserPage;
import fotostrana.ru.network.requests.fotostrana.RequestVote;
import fotostrana.ru.users.User;

/**
 * голосует в номиинации заданое количество раз (поумолчанию 1)
 * 
 */
public class RequestVoteIsNomination extends RequestVote {
	public final static String KEY_TARGET_ID = "targetId";
	private VoteNominationFilter voteNominationFilter = new VoteNominationFilter();;
	private RequestChecksVote requestChecksVote;

	/**
	 * Накручивает один голос
	 * 
	 * @param user
	 *            пользователь который будет голосовать
	 * @param targetId
	 *            за кого голосовать
	 * @param nomination
	 *            номинация голосования
	 */
	public RequestVoteIsNomination(User user, String targetId, int nomination,boolean isVisit) {
		this(user, targetId, nomination, 1,isVisit);
	}

	/**
	 * Накручивает заданое количество голосов
	 * 
	 * @param user
	 *            пользователь который будет голосовать
	 * @param targetId
	 *            за кого голосовать
	 * @param nomination
	 *            количество голсов которое должен отдатьо пользователь
	 *            номинация голосования
	 * @param countVotes
	 */
	public RequestVoteIsNomination(User user, String targetId, int nomination,
			int countVotes,boolean isVisit) {
		super(user, countVotes, targetId,isVisit);
		listRequests.clear();
		requestChecksVote = new RequestChecksVote(this);
		requestChecksVote.nomination = nomination;
		listRequests.add(new RequestVisitNomination(this));
		listRequests.add(requestChecksVote);
		listRequests.add(this);
		if (isVisit)
			listRequests.add(new RequestVisitUserPage(this, targetId));
		isLogin();
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				if (voteNominationFilter.filtrate(result)) {

					points = ParserJSON.getInt(result, "points");
					position = ParserJSON.getInt(result, "position");

					user.quickNomination.decrementAndGet();
					addVote();
					if ((countSuccessfulVotes == countOfVotesRequired)
							|| (user.quickNomination.get() == 0)) {
						event = new EventRequestExecutedSuccessfully(this);
					} else {
						indexNextRequest--;
					}
				} else
					event = new EventNotSuccessfullVoteNomination(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		if (event != null)
			eventListener.handleEvent(event);
	}

	public int getNomination() {
		return requestChecksVote.nomination;
	}

	@Override
	public int getPointsBeforeVoting() {
		return requestChecksVote.getCurrentVotes();
	}

	@Override
	public int getPositionBeforeVoting() {
		return requestChecksVote.getCurrentPosition();
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/ajax/votefree/?userId=" + targetId
				+ "&nominationId=" + getNomination() + "&token="
				+ requestChecksVote.token;
	}

}
