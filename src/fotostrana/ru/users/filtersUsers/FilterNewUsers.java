package fotostrana.ru.users.filtersUsers;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбирает пользователей у которых нет id
 * 
 */
public class FilterNewUsers implements UsersFilter {

	@Override
	public boolean filtrate(User user) {
		if (user.id.length() < 5)
			return true;
		else
			return false;
	}

}
