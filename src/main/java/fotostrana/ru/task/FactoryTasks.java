package fotostrana.ru.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fotostrana.ru.FileManager;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.tasks.TaskCheckOnline;
import fotostrana.ru.task.tasks.TaskUpdateProfiles;
import fotostrana.ru.task.tasks.nominations.TaskQuickVotingInTheNomination;
import fotostrana.ru.task.tasks.nominations.TaskVotingInTheNomination;
import fotostrana.ru.task.tasks.rating.TaskVotingPhototags;
import fotostrana.ru.task.tasks.rating.TaskVotingRating;
import fotostrana.ru.task.tasks.tournament.TaskQuickVotingTournament;
import fotostrana.ru.task.tasks.tournament.TaskVotingTournament;

/**
 * Создает задания по строке
 * 
 */
public class FactoryTasks {
	/**
	 * Номинации голосования
	 */
	public static String[] NOMINATIONS = { "ГОРОД", "ОЧАРОВАНИЕ", "СИМПАТИЯ",
			"СУПЕРСТАР", "НОМИНАЦИЯ", "ТУРНИР" };

	/**
	 * Содает задание по строке
	 * 
	 * @param source
	 *            строка с заданием
	 * @return null если нельзя создать задание по входным данным
	 */
	public static Task createTask(String source) {
		if ((source == null) || (source.isEmpty()))
			return null;
		Task task = null;
		if (task == null)
			task = createTestTask(source);
		if (task == null)
			task = createNominationTask(source);
		if (task == null)
			task = createUpdateQuestionnaires(source);

		return task;
	}

	// private static Task createTaskLogin(String source) {
	// String[] lines = source.split("[;]");
	// if (lines.length == 2) {
	// try {
	// // int count = Integer.parseInt(lines[0], 10);
	// String url = lines[1];
	// return new TaskLogin(url, 1000);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }

	private static Task createTestTask(String source) {
		if (source.compareTo("TestTask") == 0) {
			// return new TestTask();
			List<String> id = FileManager.readTextFile("id.txt");
			Map<String, Boolean> users = new HashMap<String, Boolean>();
			// users.put("34309939", null);
			// users.put("56853813", null);
			// users.put("37767481", null);
			for (String string : id) {
				users.put(string, null);
			}
			System.out.println(users.size());
			return new TaskCheckOnline(users);
		}
		return null;
	}

	// /**
	// * Создает задание открывания страницы турнира
	// *
	// * @param source
	// * @return
	// */
	// private static Task createTaskVisitTornament(String source) {
	// if (source.indexOf("открыть страницу турнира") > -1)
	// return new SignIntoTournament();
	// else
	// return null;
	// }

	/**
	 * Создает задание обновления анкет
	 * 
	 * @param source
	 * @return
	 */
	private static Task createUpdateQuestionnaires(String source) {
		if (source.indexOf("обновить анкеты") > -1)
			return new TaskUpdateProfiles();
		else
			return null;
	}

	/**
	 * Создает задание голосования в номинации; формат входных данных :
	 * ИМЯ|ИД|НОМИНАЦИЯ|КОЛИЧЕСТВО ГОЛОСОВ
	 * 
	 * @param source
	 * @return
	 */
	private static Task createNominationTask(String source) {
		String[] elements = source.split("[" + "|" + "]");
		if (elements.length == 4) {
			String targetName = removeSpace(elements[0]);
			String nomination = elements[1];
			String targetId = removeSpace(elements[2]);
			String countVotes = removeSpace(elements[3]);

			if (removeSpace(nomination).compareTo("") == 0)
				nomination = "номинация";
			Nomination nomi = getNominationByName(nomination);
			if (nomi == null)
				return null;

			boolean quick = nomination.toLowerCase().indexOf("быстр") > -1;
			try {
				long intId = Long.parseLong(targetId);
				if (intId < 1)
					return null;
				int intCountVotes = Integer.parseInt(countVotes);
				if (intCountVotes < 1)
					return null;
				// номинации
				if (Nomination.isNominationVoting(nomi))
					if (quick) {
						return new TaskQuickVotingInTheNomination(targetId,
								nomi, intCountVotes, targetName, false);
					} else
						return new TaskVotingInTheNomination(targetId, nomi,
								intCountVotes, targetName, false);
				// турнир
				if (nomi == Nomination.TOURNAMENT)
					if (!quick)
						return new TaskVotingTournament(targetId,
								intCountVotes, targetName, false);
					else
						return new TaskQuickVotingTournament(targetId, nomi,
								intCountVotes, targetName, false);
				// PhotoTags
				if (Nomination.isPhotoTagsNomination(nomi)) {
					return new TaskVotingPhototags(targetId, nomi,
							intCountVotes, targetName, false);
				}
				// Рейтинг
				if (nomi == Nomination.RATING) {
					return new TaskVotingRating(targetId, intCountVotes,
							targetName, false);
				}
			} catch (NumberFormatException e) {
				return null;
			}
		}

		return null;
	}

	/**
	 * Удаляет из строки пробелы, табы
	 * 
	 * @param value
	 * @return
	 */
	public static String removeSpace(String value) {
		String[] removeElements = { " ", "	" };
		StringBuffer s = new StringBuffer(value);
		for (String element : removeElements) {
			int index = s.indexOf(element);
			while (index != -1) {
				s.delete(index, index + element.length());
				index = s.indexOf(element);
			}
		}
		return s.toString();
	}

	/**
	 * Возращает номер номинации по имени, нечуствительна к регистру
	 * 
	 * @param name
	 *            имя номинации
	 * @return номер номинации, -1 если номинации с таким именем нет
	 */
	public static int nominationByName(String name) {
		String searchName = name.toLowerCase();
		for (int i = 0; i < NOMINATIONS.length; i++)
			if (searchName.indexOf(NOMINATIONS[i].toLowerCase()) > -1) {
				return i;
			}
		return -1;
	}

	public static Nomination getNominationByName(String name) {
		String searchName = name.toUpperCase();
		for (Nomination nomination : Nomination.LIST_ALL_NOMINATIONS)
			if (searchName.indexOf(nomination.name) > -1) {
				return nomination;
			}
		return null;
	}

	/**
	 * возращет имя номинации по номеру
	 * 
	 * @param numberNomination
	 *            номер номинации
	 * @return null если номинации с таким номером нет
	 */
	public static String nominationByNumber(int numberNomination) {
		if ((numberNomination > -1) && (numberNomination < NOMINATIONS.length)) {
			return NOMINATIONS[numberNomination];
		} else
			return null;
	}
}
