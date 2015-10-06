package fotostrana.ru.users;

/**
 * Позиции и места в голосовании
 * 
 */
public class VotingPositions {
	public static String[] names = { "турнир", "город", "очарование",
			"симпатия", "superstar", };
	/**
	 * Количество голосов
	 */
	public int[] vote;
	/**
	 * место
	 */
	public int[] place;

	/**
	 * Позиции и места в голосовании
	 * 
	 */
	public VotingPositions() {
		vote = new int[names.length];
		place = new int[names.length];
		for (int i = 0; i < names.length; i++) {
			vote[i] = -1;
			place[i] = -1;
		}
	}

	/**
	 * Возращает индекс по имени
	 * 
	 * @param name
	 * @return
	 */
	public int getIndex(String name) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].compareTo(name) == 0)
				return i;
		}
		return -1;
	}

	// /**
	// * Возращает отчет в форме для сохранения
	// *
	// * @return
	// */
	// public String[] getReport() {
	// String[] report = new String[names.length * 2];
	// return report;
	// }

	/**
	 * Создает по массиву параметров
	 * 
	 * @param parameters
	 * @return
	 */
	public static VotingPositions create(String[] parameters) {
		VotingPositions votingPositions = new VotingPositions();
		int maxNumber = Math.min(2 * names.length, parameters.length);
		for (int i = 0; i < maxNumber; i++) {
			try {
				int value = Integer.parseInt(parameters[i]);
				int j = i / 2;
				if (i % 2 == 0) {
					votingPositions.place[j] = value;
				} else {
					votingPositions.vote[j] = value;
				}
			} catch (Exception e) {
				continue;
			}

		}
		return votingPositions;
	}

}
