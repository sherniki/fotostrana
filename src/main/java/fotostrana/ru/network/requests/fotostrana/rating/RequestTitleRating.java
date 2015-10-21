package fotostrana.ru.network.requests.fotostrana.rating;

import java.util.Map;
import java.util.TreeMap;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Получение титульной страницы РЕЙТИНГА
 * 
 */
public class RequestTitleRating extends RequestFotostrana {
	private RequestVoteInRating requestVoteInRating;

	public RequestTitleRating(RequestVoteInRating request) {
		super(request);
		requestVoteInRating = request;
	}

	@Override
	public void setResult(String result) {
		Event event;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				String photoID = ParserJSON.getSubstring(result,
						"fsrating.showphoto({photoid: '", "',");
				String pageSource = ParserJSON.getSubstring(result,
						"pagesource: ", ", ");
				if ((photoID != null) && (pageSource != null)) {
					requestVoteInRating.photoId = photoID;
					requestVoteInRating.pageSource = pageSource;
					event = new EventRequestExecutedSuccessfully(this);
				} else
					event = new EventCanNotVote(this);

			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/rating/user/"
				+ requestVoteInRating.targetId + "/";
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> headers = new TreeMap<String, String>();
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Accept-Encoding", "gzip,deflate");
		headers.put("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2");
		headers.put("Cache-Control", "max-age=0");
		headers.put("Connection", "keep-alive");
		return headers;
	}
}
