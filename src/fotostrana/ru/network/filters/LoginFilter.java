package fotostrana.ru.network.filters;

import fotostrana.ru.ParserJSON;

/**
 * Проверяет выполнен ли вход в Фотострану, false - если вход невыполнен
 * 
 */
public class LoginFilter extends Filter {
	// public static final String[] KeywordsThatMustBe = {
	// "интересные мне приложения", "события" };
	public static final String[] KeywordsShouldNotBe = { "войти",
			">регистрация<" };

	@Override
	public boolean filtrate(String result) {
		Integer userId = ParserJSON.getInt(result, "userid");
		if ((userId != null) && (userId > 10000))
			return true;
		for (String key : KeywordsShouldNotBe) {
			int index = result.indexOf(key);
			if (index > -1) {
				return false;
			}
		}
		// for (String key : KeywordsThatMustBe) {
		// int index = result.indexOf(key);
		// if (index == -1) {
		// return false;
		// }
		// }
		return true;
	}

}
