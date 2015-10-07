package fotostrana.ru.network.filters;


/**
 * Фильтр проверки на бан. Возращает true если страница незабанена, false -если
 * забанена(параметрах причина блокировки)
 */
public class BannedFilter extends Filter {
	// public static final String KEY_BAN_REASON = "ban_reason";
	public static final String NO_MAIN_PHOTO = "Нет фотографии.";
	public static final String[] keywords_NO_MAIN_PHOTO = {
			"<title>загрузите фотографию</title>",
			"freevotereason\":\"no_main_photo" };
	private String keyWord = "ваша страница заблокирована";
	private String noEmail = "почтовый ящик перестал работать";
	private String keyStartReason = "ban-block-reason-mes ban-block-mb8";
	private String keyFinishReason = "</div>";

	private String reason;

	@Override
	public boolean filtrate(String result) {
		if (!checkPhoto(result)) {
			reason = NO_MAIN_PHOTO;
			return false;
		}

		if (result.indexOf(noEmail) > -1) {
			reason = noEmail;
			return false;
		}

		int i = result.indexOf(keyWord);
		if (i != -1) {
			int startIndex = result.indexOf(
					"ban-block-karavaggio-liter2 ban-block-mb10\">", i)
					+ "ban-block-karavaggio-liter2 ban-block-mb10\">".length()
					+ 2;
			int finishIndex = result.indexOf(" ", startIndex);
			String dateBan = "";
			if (finishIndex > startIndex)
				dateBan = result.substring(startIndex, finishIndex);

			String reason = "Неизвестно. Обнаружено при авторизации.";
			startIndex = result.indexOf(keyStartReason, i);
			if (startIndex > -1) {
				startIndex += keyStartReason.length() + 2;
				finishIndex = result.indexOf(keyFinishReason, startIndex);
				if (finishIndex > startIndex)
					reason = result.substring(startIndex, finishIndex);
			}

			String res = "";
			if (dateBan != "")
				res = "Дата: " + dateBan + ".";
			if (reason != "") {
				res = res + "Причина: " + reason;
			}
			this.reason = res;

			return false;
		}
		i = result.indexOf("canvotefreereason\":\"banned");
		if (i > -1) {
			reason = "Неизвестно. Обнаружено при голосвании в номинации.";
			return false;
		}

		return true;
	}

	/**
	 * Проверяет на наличие сообщений о незагруженой фотографии
	 * 
	 * @param result
	 * @return false если нет аватарки
	 */
	protected boolean checkPhoto(String result) {
		for (String keyword : keywords_NO_MAIN_PHOTO) {
			if (result.indexOf(keyword) > -1)
				return false;
		}
		return true;
	}

	public String getReason() {
		return reason;
	}
}
