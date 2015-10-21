package fotostrana.ru.network.filters;

/**
 * Фильтр результа запроса
 * 
 */
public abstract class Filter {
	public static final String KEY_RESULT = "result";

	/**
	 * Фыльтровать запрос
	 * 
	 * @param result
	 *            данные для фильтрации
	 * @return результат фильтрования
	 */
	public abstract boolean filtrate(String result);

	/**
	 * Выполянет проветрку по ключевым словам
	 * 
	 * @param result
	 *            проверяемая строка
	 * @param keywords
	 *            список ключевых слов
	 * @return true -если есть все ключевык слова
	 */
	public static boolean checkingByKeywords(String result, String[] keywords) {
		for (String keyword : keywords)
			if (result.indexOf(keyword) == -1) {
				return false;
			}
		return true;
	}
}
