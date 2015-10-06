package fotostrana.ru.users.filtersUsers;

import java.util.Set;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбрасывает пользователей которых нет в списке
 * 
 */
public class FilterForGivenList implements UsersFilter {
	public Set<User> users;

	public FilterForGivenList(Set<User> users) {
		this.users = users;
	}

	@Override
	public boolean filtrate(User user) {
		for (User currentUser : users)
			if (user.compareTo(currentUser) == 0) {
				return true;
			}
		return false;
	}

}
