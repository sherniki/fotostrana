package fotostrana.ru.users.filtersUsers;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Фильтрует пользователей по анкетным данным, фильтр пройден, если найдено
 * вхождение искомых данных хотяябы в одном из полей: имя, id,логин,ссылка
 * авторизации,цвет команды;
 * 
 * поиск ведется без учета регистра!
 * 
 */
public class FilterByProfiles implements UsersFilter {
	/**
	 * Данные которые необходимо найти
	 */
	private String value;

	/**
	 * @param value
	 *            искомые данные, приводятся к нижнему регистру
	 */
	public FilterByProfiles(String value) {
		setValue(value);
	}

	@Override
	public boolean filtrate(User user) {
		if (search(user.id, value))
			return true;
		if (search(user.urlAutoConnection + ",,", value))
			return true;
		if (search(user.name, value))
			return true;
		if (search(user.login, value))
			return true;
		if (search(user.color, value))
			return true;
		return false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value.toLowerCase();
	}

	private boolean search(String value, String param) {
		if (value == null)
			return false;
		return value.toLowerCase().indexOf(param) != -1;
	}

}
