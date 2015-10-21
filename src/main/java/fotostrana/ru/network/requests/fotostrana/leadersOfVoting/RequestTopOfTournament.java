package fotostrana.ru.network.requests.fotostrana.leadersOfVoting;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Request;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.users.User;

/**
 * Получает лидеров личного голосования в Турнире
 * 
 */
public class RequestTopOfTournament extends RequestLeadersOfVoting {
	/**
	 * Номер страницы
	 */
	public int page = 2;
	/**
	 * Количество анкет на странице
	 */
	public int limit = 7;

	/**
	 * @param user
	 *            пользователь от имени которого выполняется запрос
	 */
	public RequestTopOfTournament(EventListener eventListener,User user) {
		super(eventListener,user, Nomination.TOURNAMENT);
		typeRequest = TYPE_POST;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				List<Integer> listId = ParserJSON.getIntegers(result, "id");
				List<Integer> listPoints = ParserJSON.getIntegers(result,
						"points");
				List<Integer> listPosition = ParserJSON.getIntegers(result,
						"pos");
				if (listId.size() != listPoints.size())
					System.out.println(result);
				// int minSize = Math.min(listId.size(), listPoints.size());
				for (int j = 0; j < listId.size(); j++)
					if (listId.get(j) != ParserJSON.ERROR_INT) {
						RecordReport record = new RecordReport(listId.get(j),
								User.GENDER_UNKNOW, Region.PERSONAL_VOTING);
						record.nomination = nomination;
						record.setPoints(listPoints.get(j));
						record.setPositionInYourRegion(listPosition.get(j));
						records.add(record);
					}
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		if (event != null)
			eventListener.handleEvent(event);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/team/gettop/";
	}

	@Override
	public HttpEntity getRequestData() {
		String value = "ajax=1&page=" + page + "&limit=" + limit;
		return Request.getPostData_APPLICATION_FORM_URLENCODED(value);
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> header = new TreeMap<String, String>();
		header.put("Referer",
				"http://fotostrana.ru/contest/team/?fromServiceBlock=1&ls=0&isFavlb=1");
		return header;
	}

	@Override
	public RequestLeadersOfVoting clone() {
		RequestTopOfTournament request = new RequestTopOfTournament(eventListener,user);
		request.limit = this.limit;
		request.page = this.page;
		return request;
	}

}
