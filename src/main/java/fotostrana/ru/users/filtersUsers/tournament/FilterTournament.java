package fotostrana.ru.users.filtersUsers.tournament;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбирает пользователей которые могут голосовать в турнире
 * 
 */
public class FilterTournament implements UsersFilter {

	@Override
	public boolean filtrate(User user) {
		return user.isCanVoteTournament;
	}

}
