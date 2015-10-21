package fotostrana.ru.network.requests.fotostrana.leadersOfVoting;

import java.util.List;

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
 * Получение лидеров команд в турнире
 * 
 */
public class RequestLeadresOfTournament extends RequestLeadersOfVoting {
	public int page = 1;
	public int limit = 12;
	public Region team;

	/**
	 * @param user
	 *            пользователь от имени которого будет выполнятся запрос
	 * @param team
	 *            команда
	 */
	public RequestLeadresOfTournament(EventListener eventListener, User user,
			Region team) {
		super(eventListener, user, Nomination.TOURNAMENT);
		this.team = team;
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
				if (listId.size() != listPoints.size())
					System.out.println(result);
				for (int j = 0; j < listId.size(); j++)
					if (listId.get(j) != ParserJSON.ERROR_INT) {
						RecordReport record = new RecordReport(listId.get(j),
								User.GENDER_UNKNOW, team);
						record.nomination = nomination;
						record.setPoints(listPoints.get(j));
						record.setPositionInYourRegion((page - 1) * limit + j
								+ 1);
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
	public HttpEntity getRequestData() {
		String value = "team=" + team.id + "&ajax=1&page=" + page + "&limit="
				+ limit;
		return Request.getPostData_APPLICATION_FORM_URLENCODED(value);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/teamajax/getcommandusers/";
	}

	@Override
	public RequestLeadersOfVoting clone() {
		RequestLeadresOfTournament request = new RequestLeadresOfTournament(eventListener,
				user, team);
		request.limit = this.limit;
		request.page = this.page;
		return request;
	}
}
