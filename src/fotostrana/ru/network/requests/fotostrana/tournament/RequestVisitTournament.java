package fotostrana.ru.network.requests.fotostrana.tournament;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.requests.fotostrana.LoginRequest;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.users.User;

/**
 * Открывает страницу с турниром и устанавливает анкете токен
 * 
 */
public class RequestVisitTournament extends RequestFotostrana {
	/**
	 * Цвета команд по номеру команды
	 */
	public static final String[] COLOR_TEAM = { "Красные", "Жёлтые", "Зелёные",
			"Бирюзовые", "Синие", "Фиолетовые", "Салатовые", "Пурпурные" };
	/**
	 * Токен для турнира
	 */
	public String tournamentToken;

	private LoginRequest loginRequest;

	public boolean isPhone = true;

	/**
	 * @param user
	 *            польователь от имени которого окрывается турнир
	 */
	public RequestVisitTournament(User user) {
		super(user);
		if (!user.isAutorizted()) {
			loginRequest = new LoginRequest(this);
			listRequests.add(0, loginRequest);
		}
	}

	/**
	 * @param parrentRequest
	 *            родительский запрос
	 */
	public RequestVisitTournament(RequestFotostrana parrentRequest) {
		super(parrentRequest);

	}

	@Override
	public void setResult(String result) {
		Event event;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				tournamentToken = ParserJSON.getSubstring(result,
						"window.fsft = '", "';");
				if (tournamentToken != null)
					user.tournamentToken = tournamentToken;
				if (result.indexOf("telephone=no") != -1) {
					isPhone = false;
					user.isCanVoteTournament = false;
					user.tournamentToken = "";
				}
				user.name = ParserJSON.getSubstring(result,
						"<span class=\"user-name trebuchet ellipsis\">",
						"</span>");
				Integer roomId = ParserJSON.getInt(result, "roomid");
				if ((roomId != null) && (roomId > 0)
						&& (roomId <= COLOR_TEAM.length)) {
					user.color = COLOR_TEAM[roomId - 1];
				}
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/team/";
	}

}
