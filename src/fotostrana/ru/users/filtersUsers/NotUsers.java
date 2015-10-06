package fotostrana.ru.users.filtersUsers;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбрасывает все анкеты
 * 
 */
public class NotUsers implements UsersFilter {
	@Override
	public boolean filtrate(User user) {
		return false;
	}

}
