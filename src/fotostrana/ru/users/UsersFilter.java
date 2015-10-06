package fotostrana.ru.users;

/**
 * Выбирает пользователей, которые отвечают заданым параметрам
 * 
 */
public interface UsersFilter {

	/**
	 * Проверить пользователя
	 * 
	 * @param user
	 *            пользователь который проходит проверку
	 * @return результат проверки
	 */
	boolean filtrate(User user);

}
