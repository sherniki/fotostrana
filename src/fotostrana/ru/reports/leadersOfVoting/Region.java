package fotostrana.ru.reports.leadersOfVoting;

/**
 * Описание региона
 * 
 */
public class Region {
	public static final Region TEAM_RED = new Region(1, "Красные");
	public static final Region TEAM_YELLOW = new Region(2, "Жёлтые");
	public static final Region TEAM_GREEN = new Region(3, "Зелёные");
	public static final Region TEAM_TURQUOISE = new Region(4, "Бирюзовые");
	public static final Region TEAM_BLUE = new Region(5, "Синие");
	public static final Region TEAM_VIOLET = new Region(6, "Фиолетовые");
	public static final Region TEAM_LIGHT_GREEN = new Region(7, "Салатовые");
	public static final Region TEAM_PURPLE = new Region(8, "Пурпурные");
	public static final Region PERSONAL_VOTING = new Region(0,
			"Личное голосование");
	public static final Region VOTING_FAMILY = new Region(1, "family");
	public static final Region VOTING_CHILDREN = new Region(2, "children");
	public static final Region VOTING_SUMMER = new Region(3, "summer");
	/**
	 * Команды турнира
	 */
	public static final Region[] TEAMS_TOURNAMENT = { TEAM_RED, TEAM_YELLOW,
			TEAM_GREEN, TEAM_TURQUOISE, TEAM_BLUE, TEAM_VIOLET,
			TEAM_LIGHT_GREEN, TEAM_PURPLE };

	public static final Region[] SUBNOMINATION_OF_RATING = { VOTING_FAMILY,
			VOTING_CHILDREN, VOTING_SUMMER };

	public final static String SEPARATOR = "|";
	/**
	 * Номер
	 */
	public int id;
	/**
	 * Название
	 */
	public String name;

	public Region() {
		id = -1;
		name = "";
	}

	public Region(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return id + SEPARATOR + name;
	}

	/**
	 * Создает экземпляр по строке
	 * 
	 * @param source
	 *            входные даные вида id|name
	 * @return null если невозможно создать регион с такими параметрами
	 */
	public static Region create(String source) {
		String[] elements = source.split("[" + SEPARATOR + "]");
		if (elements.length == 2) {
			String id = elements[0].trim();
			String name = elements[1].trim();
			try {
				int intId = Integer.parseInt(id);
				if (intId > 0)
					return new Region(intId, name);
			} catch (Exception e) {
			}
		}
		return null;

	}
}
