package fotostrana.ru.network.requests.fotostrana.rating;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

public class RequestLevelPoints extends RequestFotostrana {
	/**
	 * «Нравится», поставленное к чужому фото
	 */
	public static final int POINTS_LIKE = 0;
	/**
	 * Просмотр фото в конкурсе «Лицо с обложки»
	 */
	public static final int POINTS_VIEW_PHOTO = 1;

	public static final String[] nameItem = {
			"«Нравится», поставленное к чужому фото",
			"Просмотр фото в конкурсе «Лицо с обложки»" };
	public static final int[] pointsPerItem = { 2, 1 };
	private static String s1 = "activity-log-item-title\\\">\\n";
	private static String s2 = "activity-log-item-info\\\">+ ";
	public int levelPoints = -1;
	public boolean isDoublePoints = false;
	private Map<String, Integer> historyPoints;

	public RequestLevelPoints(RequestFotostrana parrentRequest) {
		super(parrentRequest);
		historyPoints = new HashMap<String, Integer>();
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/activityRating/ajax/infoPopup/?_ajax=1&userId="
				+ user.id;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				String slevelpoints = removeSpace(ParserJSON.getSubstring(
						result, "activity-info-level-points\\\">", " "));
				try {
					levelPoints = Integer.parseInt(slevelpoints);
				} catch (Exception e) {
					slevelpoints = removeSpace(ParserJSON.getSubstring(result,
							"activity-info-top-item-points\\\"><strong>",
							"<\\/strong>"));
					try {
						levelPoints = Integer.parseInt(slevelpoints);
					} catch (Exception e2) {
					}
				}
				isDoublePoints = result.indexOf("activity-multiply-points") > -1;
				int i = result.indexOf(s1);
				if (i == -1) {
					System.out.println(Request.uXXXX(result));
				}

				try {
					System.out.println("------------");
					while (i > -1) {
						// String s = result.substring(i);
						// System.out.println(s);
						String title = RequestLevelPoints.uXXXX(ParserJSON
								.getSubstring(result, s1, "<\\/td>", i).trim());
						String sValue = removeSpace(ParserJSON.getSubstring(
								result, s2, "<\\/td>", i));
						Integer value = Integer.parseInt(sValue);
						historyPoints.put(title, value);
						i = result.indexOf(s1, i + 1);
						System.out.println(title + "=" + value);
					}
					System.out.println("------------");
				} catch (Exception e) {
					e.printStackTrace();
				}
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> result = new TreeMap<String, String>();
		result.put("Host", "fotostrana.ru");
		result.put("X-Requested-With", "XMLHttpRequest");
		result.put("Referer", "http://fotostrana.ru/user/" + user.id
				+ "/?from=header.menu");
		return result;
	}

	private static String removeSpace(String s) {
		String result = s;
		int i = result.indexOf(" ");
		while (i > 0) {
			result = result.substring(0, i) + result.substring(i + 1);
			i = result.indexOf(" ");
		}
		return result;
	}

	/**
	 * Возращает количество заработаных балов в категории
	 * 
	 * @param nameItem
	 *            имя категории
	 * @return
	 */
	public int getPoints(String nameItem) {
		Integer result = historyPoints.get(nameItem);
		if (result != null)
			return result;
		else
			return 0;
	}

	/**
	 * Возращает количество заработаных балов в категории
	 * 
	 * @param nameItem
	 *            индекс категории
	 * @return
	 */
	public int getPoints(int item) {
		return getPoints(nameItem[item]);
	}

	public static int getPointsPerItem(int item) {
		return pointsPerItem[item];
	}

}
