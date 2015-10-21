package fotostrana.ru.network.filters;


/**
 * Проверяет, принадлежит ли страница Фото Стране
 * 
 */
public class FilterFotostrana extends Filter {
	public static final String[] keyWords = { "фотострана", "fotostrana",
			"<title>загрузите фотографию</title>" };
	public static final String[] charsets = { "windows-1251", "windows1251",
			"cp1251", "utf-8", "utf8" };
	public static final String[] JSON = { "{", "}", ":" };
	public static final String[] NOT_JSON_ELEMENT = { "<html>" };

	@Override
	public boolean filtrate(String result) {
		// Проверка в формате HTML
		if ((result.indexOf("<html") != -1)||(result.indexOf("</html")!=-1)) {
			int countFail = 0;
			for (String keyword : keyWords) {
				if (result.indexOf(keyword) == -1)
					countFail++;
			}
			if (countFail > 1)
				return false;

			int i = result.indexOf("charset=");
			if (i == -1) {
				return false;
			}
			i = i + "charset=".length();
			int f = result.indexOf("\"", i);
			if (f == -1)
				return false;
			String charset = result.substring(i, f);
			for (String key : charsets) {
				if (charset.compareTo(key) == 0)
					return true;
			}
			return false;
		} else {
			// Проверка в формате JSON
			for (String jsonElement : JSON)
				if (result.indexOf(jsonElement) == -1) {
					return false;
				}
			for (String notJSONElement : NOT_JSON_ELEMENT)
				if (result.indexOf(notJSONElement) != -1) {
					return false;
				}
			return true;
		}
	}
}
