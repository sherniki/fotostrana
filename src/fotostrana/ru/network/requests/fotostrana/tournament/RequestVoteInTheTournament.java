package fotostrana.ru.network.requests.fotostrana.tournament;

import org.apache.http.HttpEntity;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.filters.Filter;
import fotostrana.ru.network.requests.fotostrana.RequestVisitUserPage;
import fotostrana.ru.network.requests.fotostrana.RequestVote;
import fotostrana.ru.users.User;

/**
 * Голосует заданое количество раз в турнире
 * 
 */
public class RequestVoteInTheTournament extends RequestVote {
	/**
	 * Голосует за заданого пользователя
	 */
	public static final int MODE_VOTING_USER = 0;
	/**
	 * Голосует за команду к которой принадлежит заданый пользователь
	 */
	public static final int MODE_VOTING_TEAM = 1;
	/**
	 * Длина токена, по ней определяется нужно ли обновлять его или нет
	 */
	public static int TOKEN_LENGTH = 10;

	private RequestVisitTournament visitTournament;
	/**
	 * Проверка до голосования
	 */
	private RequestCheckTournament checkTournamentBeforeVoting;

	// private InputStream dataVote = null;
	private FiterTournamentVote tournament = new FiterTournamentVote();

	/**
	 * Тип голосования
	 */
	private int modeVoting = MODE_VOTING_USER;

	/**
	 * Голосует заданое количество раз в турнире (режим VOTING_USER)
	 * 
	 * @param user
	 *            анкета с которой будет голосовать
	 * @param targetId
	 *            за кого голосовать
	 * @param countVotes
	 *            количество голосов которое нужно отдать
	 */
	public RequestVoteInTheTournament(User user, String targetId, int countVotes,boolean isVisit) {
		this(user, targetId, countVotes, MODE_VOTING_USER,isVisit);
	}

	/**
	 * Голосует заданое количество раз в турнире
	 * 
	 * @param user
	 *            анкета с которой будет голосовать
	 * @param targetId
	 *            за кого голосовать
	 * @param countVotes
	 *            количество голосов которое нужно отдать
	 * @param mode
	 *            режим голосования
	 */
	public RequestVoteInTheTournament(User user, String targetId,
			int countVotes, int mode,boolean isVisit) {
		super(user, countVotes, targetId,isVisit);
		typeRequest = TYPE_POST;
		listRequests.clear();
		if (user.tournamentToken.length() != TOKEN_LENGTH) {
			visitTournament = new RequestVisitTournament(this);
			listRequests.add(visitTournament);
		}
		if (mode != MODE_VOTING_TEAM)
			checkTournamentBeforeVoting = new RequestCheckTournament(this,
					targetId);
		listRequests.add(checkTournamentBeforeVoting);
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
				if (tournament.filtrate(result)) {

					if (modeVoting == MODE_VOTING_USER) {
						points = ParserJSON.getInt(result, "points");
						position = ParserJSON.getInt(result, "position");
						int presentVotes = ParserJSON.getInt(result,
								"presentvotes");
						user.quickTournament.set(presentVotes);
					}

					addVote();
					// System.out.println(countSuccessfulVotes
					// + ") Успешно проголосовано за " + targetId);
					if ((countSuccessfulVotes == countOfVotesRequired)
							|| (user.quickTournament.get() == 0)) {
						event = new EventRequestExecutedSuccessfully(this);
					} else {
						back();
						// if (modeVoting == MODE_VOTING_TEAM) {
						// targetId = checkTournamentBeforeVoting.nextId;
						// back();
						// }
					}
				} else {
					event = new EventCanNotVote(this);
				}

			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		if (event != null)
			eventListener.handleEvent(event);

	}

	@Override
	public HttpEntity getRequestData() {
		String data = "userId=" + targetId + "&ftoken-all="
				+ user.tournamentToken + "&ajax=1";
		return Request.getPostData_APPLICATION_FORM_URLENCODED(data);

	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/teamajax/votefree/";
	}

	@Override
	public int getPositionBeforeVoting() {
		return checkTournamentBeforeVoting.position;
	}

	@Override
	public int getPointsBeforeVoting() {
		return checkTournamentBeforeVoting.points;
	}

	/**
	 * Возращает тип запроса
	 * 
	 * @return
	 */
	public int getModeVoting() {
		return modeVoting;
	}

	/**
	 * Задает тип глосования
	 * 
	 * @param modeVoting
	 *            тип голосвания
	 */
	public void setModeVoting(int modeVoting) {
		switch (modeVoting) {
		case MODE_VOTING_USER:
			this.modeVoting = modeVoting;
			checkTournamentBeforeVoting
					.setMode(RequestCheckTournament.MODE_CURRENT_USER);
			break;
		case MODE_VOTING_TEAM:
			this.modeVoting = modeVoting;
			checkTournamentBeforeVoting
					.setMode(RequestCheckTournament.MODE_RANDOM_USER);
			break;
		default:
			break;
		}

	}

}

/**
 * Фильтр
 * 
 */
class FiterTournamentVote extends Filter {
	public String error;

	@Override
	public boolean filtrate(String result) {
		int ret = ParserJSON.getInt(result, "ret");
		if (ret < 1) {
			error = ParserJSON.getString(result, "errorcode");
			return false;
		}
		return true;
	}

}
