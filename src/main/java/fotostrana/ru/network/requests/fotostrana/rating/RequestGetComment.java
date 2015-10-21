package fotostrana.ru.network.requests.fotostrana.rating;

import java.util.Map;

import org.apache.http.HttpEntity;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Получает коментарии и лайки фотографии
 * 
 */
public class RequestGetComment extends RequestFotostrana {
	// public static final int MODE_BEFORE_VOTE = 0;
	// public static final int MODE_AFTER_VOTE = 1;
	private AbstractRequestVote requestVote;

	// private int mode;

	public RequestGetComment(AbstractRequestVote parrentRequest) {
		super(parrentRequest);
		requestVote = parrentRequest;
		typeRequest = TYPE_POST;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				requestVote.setPointsBeforeVoting(ParserJSON.getInt(result,
						"likescount"));
				requestVote.iLike = ParserJSON.getInt(result, "ilike");
				if (requestVote.iLike == 0) {
					event = new EventRequestExecutedSuccessfully(this);
				} else {
					if (requestVote.isLike()) {
						requestVote.setPointsAfterVoting(requestVote
								.getPointsBeforeVoting());
						event = new EventRequestExecutedSuccessfully(this);
					} else
						event = new EventCanNotVote(this);
				}
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/userphoto/ajax/getcomments/";
	}

	@Override
	public HttpEntity getRequestData() {
		return Request.getPostData_APPLICATION_FORM_URLENCODED(getData());
	}

	@Override
	public boolean handleExeption(Exception e) {
		// if (e instanceof IncorrectResponseException) {
		// Request.addErrorLogRecord(this);
		// }
		return false;
	}

	private String getData() {
		String value = "img=" + requestVote.photoId + "&limit=5&pageSource="
				+ requestVote.pageSource + "&action=init&source=1&albumId="
				+ requestVote.albumId + "&isView=1&suggested=true&_fs2ajax=1";
		return value;
	}

	@Override
	public Map<String, String> headers() {
		return requestVote.headers();
	}

}
