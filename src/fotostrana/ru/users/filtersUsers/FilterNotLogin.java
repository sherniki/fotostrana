package fotostrana.ru.users.filtersUsers;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Возращет неавторизованых пользователей
 * 
 */
public class FilterNotLogin implements UsersFilter {

	@Override
	public boolean filtrate(User user) {
		return !user.isAutorizted();
	}

}
