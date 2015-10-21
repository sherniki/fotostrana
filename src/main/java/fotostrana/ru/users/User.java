package fotostrana.ru.users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * Описание пользователя-бота
 * 
 */
public class User implements Comparable<User>, Comparator<User> {
	private static String[] tabulation = { ",", " ", "	", "\n" };
	/**
	 * Обязательные куки-записи
	 */
	public static final String[] mandatoryElements = { "hw", "ref_id",
			"simpletoken", "uea", "uid" };
	// "__utma", "__utmb", "__utmc", "__utmz", "s", "l_source",
	public static int COLUMN_EMAIL = 0;
	public static int COLUMN_PASSWORD_EMAIL = 2;
	public static int COLUMN_PASSWORD_FS = 3;
	public static int COLUMN_URL_AUTOLOGIN = 4;
	public static int COLUMN_ID = 6;
	public static int COLUMN_IS_TOURNAMENT = 7;
	public static int COLUMN_COLOR = 8;
	public static int COLUMN_QUICK_NOMINATION = 9;
	public static int COLUMN_QUICK_TOURNAMENT = 10;
	public static int COLUMN_NAME = 11;
	public static int COLUMN_TOURNAMENT_TOKEN = 22;
	public static int COLUMN_DESCRIPTION = 23;
	public static int MAX_COLUMN = COLUMN_DESCRIPTION + 1;
	public static final String SEPARATOR = "|";

	public static final int GENDER_WOMAN = 0;
	public static final int GENDER_MAN = 1;
	public static final int GENDER_UNKNOW = 2;

	public HttpClientContext httpContext;
	public CookieStore cookie;

	public String id = "";
	public String passwordFS = "";
	public String login = "";
	public String eMailPassword = "";
	public String urlAutoConnection = "";

	public String color = "";
	public String name = "";
	public String description = "";

	public int gender = GENDER_UNKNOW;
	/**
	 * Места и количество голосов в разных номинациях
	 */
	public VotingPositions votingPositions = new VotingPositions();
	/**
	 * Количество голосов в номинациях
	 */
	public AtomicInteger quickNomination = new AtomicInteger(-1);
	/**
	 * Количество голосов в турнире
	 */
	public AtomicInteger quickTournament = new AtomicInteger(-1);
	/**
	 * Действия выполненые пользователем
	 */
	// private Set<UserActions> userActions;
	/**
	 * true если пользователь забанен
	 */
	public boolean isBanned;

	/**
	 * Может ли голосовать в турнире
	 */
	public boolean isCanVoteTournament = false;

	/**
	 * Токен для турнира
	 */
	public String tournamentToken = "";

	public boolean isSave = true;

	public User(String urlAutoConnection) {
		setUrlConnection(urlAutoConnection);
		// login = "";
		// passwordFS = "";
		// eMailPassword = "";
		// id = "";
		cookie = UserManager.USER_MANAGER.getCookie(id);
		// userActions = UserManager.USER_MANAGER.getUserAction(id);
		createContext();
	}

	/**
	 * @param id
	 * @param login
	 * @param password
	 * @param urlConnection
	 * @param color
	 * @param name
	 */
	public User(String id, String login, String passwordFS,
			String urlConnection, String color, String name,
			String passwordEMAIL) {
		this.id = id;
		this.login = login;
		this.passwordFS = passwordFS;
		this.name = name;
		this.color = color;
		this.eMailPassword = passwordEMAIL;
		setUrlConnection(urlConnection);

		cookie = UserManager.USER_MANAGER.getCookie(id);
		// userActions = UserManager.USER_MANAGER.getUserAction(id);
		createContext();
	}

	public void setUrlConnection(String url) {
		urlAutoConnection = url;
		for (String tab : tabulation) {
			int i = urlAutoConnection.indexOf(tab);
			if (i > 0)
				urlAutoConnection = urlAutoConnection.substring(0, i);
		}
	}

	/**
	 * Создает контекст интрернет-запросов
	 */
	private void createContext() {
		cookie.clearExpired(new Date());
		httpContext = HttpClientContext.create();
		httpContext.setCookieStore(cookie);
	}

	// public Set<UserActions> getUsersActions() {
	// return userActions;
	// }
	//
	// public void addACtion(UserActions userAction) {
	// userActions.add(userAction);
	// }
	//
	// public void setUsersActions(ConcurrentSkipListSet<UserActions>
	// usersActions) {
	// this.userActions = usersActions;
	// }

	public void setId(String newId) {
		id = newId;
		UserManager.USER_MANAGER.setCookie(id, cookie);
		// UserManager.USER_MANAGER.setUserActions(id, getUsersActions());
	}

	/**
	 * Проверяет авторизирован ли пользователь,удаляет все куки записи которые
	 * неявляются обязательными
	 * 
	 * @return true если авторизирован
	 */
	public boolean isAutorizted() {
		List<Cookie> listCookie = new ArrayList<Cookie>();
		// System.out.println(cookie.getCookies().size());
		for (Cookie cookieElement : cookie.getCookies()) {
			boolean result = false;
			for (String mandatoryEl : mandatoryElements)
				if (cookieElement.getName().compareTo(mandatoryEl) == 0) {
					result = true;
					break;
				}
			if (result) {
				listCookie.add(cookieElement);
			}
		}
		cookie.clear();
		// System.out.println(cookie.getCookies().size());
		for (Cookie cookieElement : listCookie) {
			cookie.addCookie(cookieElement);
		}
		// System.out.println(cookie.getCookies().size());
		return cookie.getCookies().size() == mandatoryElements.length;
	}

	@Override
	public int compareTo(User arg0) {
		return compare(this, arg0);
	}

	public void setDescription(String description) {
		if (description == null)
			description = "";
		this.description = description;
	}

	@Override
	public int compare(User user1, User user2) {
		if ((user1.id.length() > 5) && (user2.id.length() > 5))
			if (user1.id.compareTo(user2.id) == 0)
				return 0;
		return user1.urlAutoConnection.compareTo(user2.urlAutoConnection);
	}

	public void resetCookie() {
		cookie = new BasicCookieStore();
		UserManager.USER_MANAGER.setCookie(id, cookie);
	}

	/**
	 * Возращает куки-запись по миени
	 * 
	 * @param nameCookie
	 * @return null если нет записи с таким именем
	 */
	public Cookie getCookie(String nameCookie) {
		for (Cookie c : cookie.getCookies()) {
			if (c.getName().compareTo(nameCookie) == 0)
				return c;
		}
		return null;
	}

	/**
	 * Создает пользователя по анкетным данным, не добавляет нового пользователя
	 * МЕНЕДЖЕРУ
	 * 
	 * @param parameters
	 *            анектные данные
	 * @return null, если нельзя создать пользователя
	 */
	public static User createUser(String[] parameters) {
		try {
			User user = null;
			String url = parameters[COLUMN_URL_AUTOLOGIN];
			if (url.indexOf("http://fotostrana.ru/") == -1)
				return null;
			// url = "";
			// int intId = Integer.parseInt(id);
			// if (intId < 1)
			// return null;
			String id = null;
			try {
				id = parameters[COLUMN_ID];
				String login = parameters[COLUMN_EMAIL];
				String passwordEMail = parameters[COLUMN_PASSWORD_EMAIL];
				String passwordFS = parameters[COLUMN_PASSWORD_FS];
				String color = parameters[COLUMN_COLOR];
				String name = parameters[COLUMN_NAME];

				// if (((login.length() < 5) || (passwordFS.length() < 1)))
				// return null;

				user = new User(id, login, passwordFS, url, color, name,
						passwordEMail);
				if (parameters[COLUMN_IS_TOURNAMENT].toUpperCase().compareTo(
						"ДА") == 0)
					user.isCanVoteTournament = true;

				user.tournamentToken = parameters[COLUMN_TOURNAMENT_TOKEN];

				try {
					if (parameters[COLUMN_QUICK_NOMINATION].length() > 0) {
						user.quickNomination.set(Integer
								.parseInt(parameters[COLUMN_QUICK_NOMINATION]));
					}
					if (parameters[COLUMN_QUICK_TOURNAMENT].length() > 0) {
						user.quickTournament.set(Integer
								.parseInt(parameters[COLUMN_QUICK_TOURNAMENT]));
					}
					String[] positions = new String[10];
					for (int i = 0; i < positions.length; i++) {
						positions[i] = parameters[i + COLUMN_NAME + 1];
					}
					user.votingPositions = VotingPositions.create(positions);
				} catch (Exception e2) {

				}

			} catch (Exception e3) {
				e3.printStackTrace();
				if (url.length() < 30)
					return null;
				user = new User(url);
				if (id != null)
					user.setId(id);
			}
			return user;
		} catch (Exception e) {
			return null;
		}
		// return null;
	}

	public static User createUser(String stringValue) {
		if (stringValue == null)
			return null;
		String[] columns = stringValue.split("[,]");
		if (columns.length <= 2) {
			String[] parameters = new String[User.MAX_COLUMN];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = "";
			}
			parameters[COLUMN_URL_AUTOLOGIN] = columns[0].trim();
			if (columns.length == 2)
				parameters[COLUMN_ID] = columns[1].trim();
			return User.createUser(parameters);
		} else
			return null;

	}

	/**
	 * преобразовывает к формату для сохранения в файле
	 * 
	 * @return
	 */
	public String[] toRow() {
		String[] result = new String[MAX_COLUMN];
		result[COLUMN_EMAIL] = this.login;
		result[COLUMN_PASSWORD_EMAIL] = this.eMailPassword;
		result[COLUMN_PASSWORD_FS] = this.passwordFS;
		result[COLUMN_URL_AUTOLOGIN] = this.urlAutoConnection;
		result[COLUMN_ID] = this.id;
		result[COLUMN_COLOR] = this.color;
		result[COLUMN_NAME] = this.name;
		result[COLUMN_DESCRIPTION] = this.description;
		result[COLUMN_TOURNAMENT_TOKEN] = this.tournamentToken;
		if (isCanVoteTournament)
			result[COLUMN_IS_TOURNAMENT] = "ДА";
		else
			result[COLUMN_IS_TOURNAMENT] = "НЕТ";
		result[1] = SEPARATOR;
		result[5] = SEPARATOR;
		if (this.quickNomination.get() > 1) {
			result[COLUMN_QUICK_NOMINATION] = Integer
					.toString(this.quickNomination.get());
		} else
			result[COLUMN_QUICK_NOMINATION] = "";
		if (this.quickTournament.get() > 1) {
			result[COLUMN_QUICK_TOURNAMENT] = Integer
					.toString(this.quickTournament.get());
		} else
			result[COLUMN_QUICK_TOURNAMENT] = "";

		int indexPositeion = COLUMN_NAME + 1;
		for (int i = 0; i < VotingPositions.names.length; i++) {
			if (votingPositions.place[i] > -1)
				result[indexPositeion] = Integer
						.toString(votingPositions.place[i]);
			indexPositeion++;
			if (votingPositions.vote[i] > -1)
				result[indexPositeion] = Integer
						.toString(votingPositions.vote[i]);
			indexPositeion++;
		}
		for (int i = 0; i < result.length; i++) {
			if (result[i] == null)
				result[i] = "";
		}
		return result;
	}
}
