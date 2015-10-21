package fotostrana.ru.network.filters;


/**
 * Фильтр ошибок соединения
 */
public class FilterError404 extends Filter {
	public static final String[] keyWords = { "404 not found", "error 404","http status 404 ",
			"403 forbidden" };

	@Override
	public boolean filtrate(String result) {
		for (String key : keyWords) {
			int index = result.indexOf(key);
			if (index > -1) {
				return false;
			}
		}
		return true;
	}

}
