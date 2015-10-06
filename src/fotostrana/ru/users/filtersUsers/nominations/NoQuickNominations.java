package fotostrana.ru.users.filtersUsers.nominations;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Оставляет только пользователей без быстрых голосов в номинациях; отбрасывает
 * анкеты которые могут участвовать в турнире
 * 
 */
public class NoQuickNominations implements UsersFilter {

	@Override
	public boolean filtrate(User user) {
		if ((user.isCanVoteTournament == false)
				&& (user.quickNomination.get() < 2))
			return true;
		else
			return false;
	}

}
