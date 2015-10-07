package fotostrana.ru.network.requests.fotostrana.leadersOfVoting;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.users.User;

/**
 * Получает информацию о количестве голосов и месте в номинациях для заданого
 * региона и пола
 * 
 */
public class RequestLeadersInNominations extends RequestLeadersOfVoting {

	/**
	 * Мксимальное количество людей одного пола
	 */
	public int limit = 5;
	private int page = 1;
	private int gender = 0;
	public Region region;

	private int state = 1;

	/**
	 * Получает информацию о количестве голосов и месте в номинациях для
	 * заданого региона и пола
	 * 
	 * @param user
	 *            пользоатель от имени которого будет получане информация
	 * @param page
	 *            номер страницы
	 * @param nominationId
	 *            номинация
	 * @param region
	 *            регион
	 */
	public RequestLeadersInNominations(EventListener eventListener,User user, int page,
			Nomination nomination, Region region) {
		super(eventListener,user, nomination);
		if (page > 1)
			this.page = page;
		this.region = region;
		setStartState();

	}

	private void setStartState() {
		if ((nomination.id == 0) && (page == 1)) {
			state = 0;
		} else
			state = 1;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				int i = 0;
				while (true) {
					i = result.indexOf("id", i);
					if (i == -1)
						break;
					int id = ParserJSON.getInt(result, "id", i);
					if (id != ParserJSON.ERROR_INT) {
						RecordReport record = new RecordReport(id, gender,
								region);
						record.nomination = nomination;
						record.setName(ParserJSON.getString(result, "name", i));
						record.setPoints(ParserJSON.getInt(result, "points", i));
						record.setPositionInYourRegion(ParserJSON.getInt(
								result, "position", i));
						if (state == 0) {
							int week = ParserJSON.getInt(result, "week", i);
							if (week != ParserJSON.ERROR_INT) {
								record.positionInYourRegion += (week - 1) * 3;
							}
						}
						if (record.positionInYourRegion > limit)
							break;
						records.add(record);
					}
					i += 3;
				}

				if (state == 0) {
					state = 1;
					back();
				} else {
					if (gender == 1)
						event = new EventRequestExecutedSuccessfully(this);
					else {
						setStartState();
						gender = 1;
						back();
					}
				}
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		if (event != null)
			eventListener.handleEvent(event);

	}

	@Override
	public String getURL() {
		if (state == 0)
			return "http://fotostrana.ru/contest/fs2Ajax/weekwinners/?_ajax=1&json=1&nominationId="
					+ nomination.id
					+ "&gender="
					+ gender
					+ "&regionId="
					+ region.id;
		else
			return "http://fotostrana.ru/contest/fs2/nominationtop/?_ajax=1&json=1"
					+ "&nominationId="
					+ nomination.id
					+ "&gender="
					+ gender
					+ "&regionId=" + region.id + "&page=" + page;
	}

	@Override
	public RequestLeadersOfVoting clone() {
		RequestLeadersInNominations request = new RequestLeadersInNominations(
				eventListener,user, page, nomination, region);
		request.state = this.state;
		request.records = this.records;
		return request;
	}
}
