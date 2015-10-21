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
		if ((user.id.toLowerCase().indexOf(value) != -1)
				|| ((user.urlAutoConnection.toLowerCase() + ",,")
						.indexOf(value) != -1)
				|| (user.name.toLowerCase().indexOf(value) != -1)
				|| (user.login.toLowerCase().indexOf(value) != -1)
				|| (user.color.toLowerCase().indexOf(value) != -1)) {
			return true;
		}
		return false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value.toLowerCase();
	}

}
