package fotostrana.ru.users.filtersUsers.tournament;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбирает анкеты для быстрого голосования в турнире
 * 
 */
public class FilterQuickTournament implements UsersFilter {

	/**
	 * Минимальное количество быстрых голосв для того чтобы пройти фильтр
	 */
	public int countQuickVotes = 2;

	public FilterQuickTournament() {
	}

	/**
	 * @param countVotes
	 *            Минимальное количество быстрых голосв для того чтобы пройти
	 *            фильтр
	 */
	public FilterQuickTournament(int countVotes) {
		if (countVotes > 2)
			countQuickVotes = countVotes;
	}

	@Override
	public boolean filtrate(User user) {
		if ((user.isCanVoteTournament)
				&& (user.quickTournament.get() >= countQuickVotes))
			return true;
		return false;
	}

}
