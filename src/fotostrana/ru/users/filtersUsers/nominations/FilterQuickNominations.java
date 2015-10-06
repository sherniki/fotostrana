package fotostrana.ru.users.filtersUsers.nominations;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбирает пользователей с быстрыми голосами, можно установить нижнюю границу
 * количества голосов; отбрасывает анкеты которые могут участвовать в турнире
 * 
 */
public class FilterQuickNominations implements UsersFilter {

	/**
	 * Минимальное количество быстрых голосв для того чтобы пройти фильтр
	 */
	public int countQuickVotes = 2;

	public FilterQuickNominations() {
	}

	/**
	 * @param count
	 *            минимальное количество быстрых голосов
	 */
	public FilterQuickNominations(int count) {
		if (count > 2) {
			countQuickVotes = count;
		}
	}

	@Override
	public boolean filtrate(User user) {
		// if (((user.isCanVoteTournament == false))
		// && (user.quickNomination.get() >= countQuickVotes))
		if ((user.quickNomination.get() >= countQuickVotes))
			return true;
		else
			return false;
	}

}
