package fotostrana.ru.network.requests.fotostrana.rating;

import java.util.Map;

import org.apache.http.HttpEntity;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.network.votes.EventCanNotVote;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Получает id альбома по фотографии и владельцу
 * 
 */
public class RequestAlbumId extends RequestFotostrana {
	private RequestVoteInRating voteInRating;

	public RequestAlbumId(RequestVoteInRating parrentRequest) {
		super(parrentRequest);
		voteInRating = parrentRequest;
		typeRequest = TYPE_POST;
	}

	@Override
	public void setResult(String result) {
		Event event;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				String start = "user\\/" + voteInRating.targetId
						+ "\\/album\\/";
				String albumID = ParserJSON.getSubstring(result, start,
						"\\/photos");
				if ((albumID != null)) {
					voteInRating.albumId = albumID;
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
		return "http://fotostrana.ru/rating/rating/getphotosdirection/";
	}

	@Override
	public HttpEntity getRequestData() {
		return RequestFotostrana
				.getPostData_APPLICATION_FORM_URLENCODED(getData());
	}

	private String getData() {
		String value = "img="
				+ voteInRating.photoId
				+ "&pageSource="
				+ voteInRating.pageSource
				+ "&clientW=1263&clientH=464&ratingPage=0&album=&suggested=0&nextPhotosIds=&_fs2ajax=1";
		return value;
	}

	@Override
	public boolean handleExeption(Exception e) {
//		if (e instanceof IncorrectResponseException) {
//			Request.addErrorLogRecord(this);
//		}
		return false;
	}

	@Override
	public Map<String, String> headers() {
		return voteInRating.headers();
	}

}
