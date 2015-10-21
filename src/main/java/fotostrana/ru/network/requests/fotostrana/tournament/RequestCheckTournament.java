package fotostrana.ru.network.requests.fotostrana.tournament;

//import java.util.Random;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.network.filters.Filter;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Запрос проверки голосования в турнире,устанавливает может ли пользователь
 * голосовать в турнире и изменяет количество быстрых голосов у анкеты
 * 
 */
public class RequestCheckTournament extends RequestFotostrana {
	public final static int MAX_COUNT_ERROR = 15;
	/**
	 * Получает информацию о пользователе с TargetId
	 */
	public static final int MODE_CURRENT_USER = 0;
	/**
	 * Получает информацию о случайном пользователе из комнады к которой
	 * принадлежит TargetId
	 */
	public static final int MODE_RANDOM_USER = 1;
	public static final String[] COLORS_RU = { "Желтый", "Красный", "Зеленый",
			"Бирюзовый", "Синий", "Фиолетовый", "Салатовый", "Пурпурный" };
	public static final String[] COLORS_EN = { "yellow", "red", "green",
			"turquoise", "blue", "violet", "salat", "purpur" };
	/**
	 * Может ли пользователь голосовать
	 */
	public boolean canVote = true;
	/**
	 * Ид пользователя за которого будет голосовать
	 */
	public String targetId = "-1";
	/**
	 * Цвет пользователя за кого голосовать
	 */
	public String targetColor = "";
	/**
	 * Количество свободных голосов у пользователя который голосует
	 */
	public int presentVotes = -1;

	/**
	 * Если пользователь непринадлежит никакой команде, то значение true
	 */
	public boolean noTeam = false;
	/**
	 * Тип запроса
	 */
	private int mode = MODE_CURRENT_USER;

	public int position = -1;
	public int points = -1;

	public String error;
	public String nextId = "";

	/**
	 * Количество подрят запросов на которые нельзя голосовать (для случайного
	 * режима)
	 */
	public int countError = 0;

	private FilterCheckTournament checkTournament = new FilterCheckTournament(
			this);

	// private Random random = new Random();
	// int count = 0;

	public RequestCheckTournament(RequestFotostrana parentRequest,
			String targetId) {
		super(parentRequest);
		this.targetId = targetId;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				if (checkTournament.filtrate(result)) {
					presentVotes = ParserJSON.getInt(result, "presentvotes");
					if (presentVotes == Integer.MIN_VALUE)
						presentVotes = 0;

					user.quickTournament.set(presentVotes);
					int iStartPosition = result.indexOf("position");
					position = ParserJSON.getInt(result, "position",
							iStartPosition);

					points = ParserJSON
							.getInt(result, "points", iStartPosition);
					if (event == null)
						event = new EventRequestExecutedSuccessfully(this);
				} else {
					event = new EventCanNotVote(this);
				}
				user.isCanVoteTournament = canVote;
				// if (targetColor.length() < 2) {
				String color = ParserJSON.getString(result, "color");
				if (color != null)
					targetColor = translateColor(color);
				else
					targetColor = "Неизвестно";
				// }
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);

		if (event != null)
			eventListener.handleEvent(event);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/teamajax/votepopup/?_ajax=1&userId="
				+ targetId + "&random=" + mode;
	}

	/**
	 * Переводит цвет на русский
	 * 
	 * @param engColor
	 *            цвет на английском
	 * @return если такого слова нет, то возращает исходное слово
	 */
	public static String translateColor(String engColor) {
		for (int i = 0; i < COLORS_EN.length; i++) {
			if (COLORS_EN[i].compareTo(engColor) == 0)
				return COLORS_RU[i];
		}
		return engColor;
	}

	/**
	 * Возращает тип запроса
	 * 
	 * @return
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Задает тип посылаемого запроса
	 * 
	 * @param mode
	 *            тип запроса (если имеет неверное значение,то небудет изменен)
	 */
	public void setMode(int mode) {
		if ((mode == MODE_CURRENT_USER) || (mode == MODE_RANDOM_USER))
			this.mode = mode;
	}
}

/**
 * Фильтр проверки голосования в турнире
 * 
 */
class FilterCheckTournament extends Filter {
	private RequestCheckTournament requestCheckTournament;

	public FilterCheckTournament(RequestCheckTournament request) {
		requestCheckTournament = request;
	}

	/*
	 * Показывает можно ли проголосовать за заданого пользователя; true - можно
	 * голосовать
	 */
	@Override
	public boolean filtrate(String result) {
		requestCheckTournament.noTeam = false;
		if (result.indexOf("hiddenuseraction") > -1)
			return false;

		boolean resultFilter = false;
		Boolean ret = ParserJSON.getBoolean(result, "ret", 0);
		if (ret != null) {
			resultFilter = ret;
			// requestCheckTournament.canVote = ret;
		}
		String error = ParserJSON.getString(result, "errorcode");

		if (error != null) {
			if (error.compareTo("points day limit no phone") == 0) {
				requestCheckTournament.canVote = false;
				resultFilter = false;
			}
			if (error.compareTo("to user noteam") == 0) {
				requestCheckTournament.canVote = false;
				requestCheckTournament.noTeam = true;
				resultFilter = false;
			}

			if (error.compareTo("to user day limit") == 0) {
				// resultFilter = false;
			}
			requestCheckTournament.error = error;
		} else {
			error = ParserJSON.getString(result, "error");
			if (error != null) {
				resultFilter = false;
				requestCheckTournament.canVote = false;
				requestCheckTournament.error = error;
			}
		}
		return resultFilter;
	}
}
