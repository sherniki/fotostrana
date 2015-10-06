package fotostrana.ru.users.filtersUsers;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Возращает всех пользователей
 * 
 */
public class AllUsers implements UsersFilter {

	@Override
	public boolean filtrate(User user) {
		return true;
	}

}
