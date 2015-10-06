package fotostrana.ru.users.filtersUsers;

import java.util.ArrayList;
import java.util.List;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Макрофильтр для фильтров пользователей Пройден когда пройдены все вложеные
 * фильтры или когда нет ниодного вложеного фильтра
 * 
 */
public class MacroFilter implements UsersFilter {
	public List<UsersFilter> filters = new ArrayList<UsersFilter>();

	@Override
	public boolean filtrate(User user) {
		for (UsersFilter currentFilter : filters)
			if (!currentFilter.filtrate(user)) {
				return false;
			}
		return true;
	}

	public boolean addFilter(UsersFilter newFilter) {
		return filters.add(newFilter);
	}

	public boolean removeFilter(UsersFilter removeFilter) {
		return filters.remove(removeFilter);
	}

	public void clear() {
		filters.clear();
	}

}
