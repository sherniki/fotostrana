package fotostrana.ru.reports.leadersOfVoting;

import java.util.ArrayList;
import java.util.List;

/**
 * Номинация голосований
 * 
 */
public class Nomination implements Comparable<Nomination> {
	public static final String SEPARATOR = "|";
	public static final Nomination CITY = new Nomination(0, "ГОРОД");
	public static final Nomination CHARM = new Nomination(1, "ОЧАРОВАНИЕ");
	public static final Nomination SYMPATHY = new Nomination(2, "СИМПАТИЯ");
	public static final Nomination SUPERSTAR = new Nomination(3, "СУПЕР СТАР");
	public static final Nomination PREMIER_LEAGUE = new Nomination(4,
			"ПРЕМЬЕР ЛИГА");
	public static final Nomination ELITE = new Nomination(5, "ЭЛИТА");
	public static final Nomination NOMINATION = new Nomination(6, "НОМИНАЦИЯ");

	public static final Nomination TOURNAMENT = new Nomination(7, "ТУРНИР");
	public static final Nomination RATING = new Nomination(8, "РЕЙТИНГ");
	public static final Nomination FAMILY = new Nomination(9, "ФОТО1", "family");
	public static final Nomination CHILDREN = new Nomination(10, "ФОТО2",
			"children");
	public static final Nomination WOW = new Nomination(11, "ФОТО3", "wow");
	public static final Nomination ALL_NOMINATIONS = new Nomination(-1,
			"ВСЕ НОМИНАЦИИ");

	/**
	 * Номинации голосования
	 */
	public static Nomination[] VOTING_NOMINATION = new Nomination[] { CITY,
			CHARM, SYMPATHY, SUPERSTAR, ELITE, NOMINATION };

	/**
	 * 
	 */
	public static Nomination[] PHOTO_TAGS = new Nomination[] { FAMILY,
			CHILDREN, WOW };

	public static List<Nomination> LIST_ALL_NOMINATIONS;
	static {
		LIST_ALL_NOMINATIONS = new ArrayList<Nomination>();
		for (int i = 0; i < VOTING_NOMINATION.length; i++) {
			LIST_ALL_NOMINATIONS.add(VOTING_NOMINATION[i]);
		}
		for (int i = 0; i < PHOTO_TAGS.length; i++) {
			LIST_ALL_NOMINATIONS.add(PHOTO_TAGS[i]);
		}
		LIST_ALL_NOMINATIONS.add(RATING);
		LIST_ALL_NOMINATIONS.add(TOURNAMENT);
//		System.out.println("ВСего номинаций = " + LIST_ALL_NOMINATIONS.size());
	}

	/**
	 * Проверяет принадлежит ли номинация к голосованию в НОМИНАЦИЯХ
	 * 
	 * @param nomination
	 * @return
	 */
	public static boolean isNominationVoting(Nomination nomination) {
		for (Nomination currentNomination : VOTING_NOMINATION)
			if (nomination == currentNomination) {
				return true;
			}
		return false;
	}
	/**
	 * Проверяет принадлежит ли номинация к голосованию в PhotoTags
	 * 
	 * @param nomination
	 * @return
	 */
	public static boolean isPhotoTagsNomination(Nomination nomination) {
		for (Nomination currentNomination : PHOTO_TAGS)
			if (nomination == currentNomination) {
				return true;
			}
		return false;
	}

	public final int id;
	public final String name;
	public final String alias;

	public Nomination() {
		id = -1;
		name = "";
		alias = "";
	}

	public Nomination(int id, String name) {
		this.id = id;
		this.name = name;
		alias = "";
	}

	public Nomination(int id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	@Override
	public String toString() {
		return id + SEPARATOR + name;
	}

	@Override
	public int compareTo(Nomination arg0) {
		int result = id - arg0.id;
		if (result == 0)
			result = name.compareTo(arg0.name);
		return result;
	}
}
