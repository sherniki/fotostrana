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
 * Ставил лайк фото
 *
 */
public class RequestPhotoLike extends RequestFotostrana {
	public static final int STATE_LIKE = 0;
	public static final int STATE_BAD_PHOTO = 1;
	AbstractRequestVote requestVote;
	private int state = STATE_LIKE;

	public RequestPhotoLike(AbstractRequestVote parrentRequest) {
		super(parrentRequest);
		typeRequest = TYPE_POST;
		requestVote = parrentRequest;
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				if (result.indexOf("showbadphotopopup.photouploaded") == -1) {
					requestVote.setPointsAfterVoting(ParserJSON.getInt(result,
							"vote_count"));
					boolean iLike = ParserJSON.getBoolean(result, "ilike", "}",
							0);
					if (iLike) {
						event = new EventRequestExecutedSuccessfully(this);
					} else
						event = new EventCanNotVote(this);
				} else {
					state = STATE_BAD_PHOTO;
					back();
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
		return "http://fotostrana.ru/userphoto/ajax/like/";
	}

	@Override
	public HttpEntity getRequestData() {
		return Request.getPostData_APPLICATION_FORM_URLENCODED(getData());
	}

	protected String getData() {
		String value = "photoId="
				+ requestVote.photoId
				+ "&ownerId="
				+ requestVote.targetId
				+ "&pageSource=fsrating-btn&addPhotoView%5Bimg%5D="
				+ requestVote.photoId
				+ "&addPhotoView%5BpageSource%5D="
				+ requestVote.pageSource
				+ "&addPhotoView%5Baction%5D=init&addPhotoView%5Bsource%5D=1&addPhotoView%5BalbumId%5D="
				+ requestVote.albumId
				+ "&addPhotoView%5Bpool%5D=3&from=rating&ratingTab=0&additionalData%5BactionStat%5D=fsrating-btn&additionalData%5BstatSentId%5D=1&additionalData%5Bsuggested%5D=0&_fs2ajax=1";
		if (state == STATE_BAD_PHOTO)
			value += "&_close_bpp=1";
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
		Map<String, String> h = requestVote.headers();
		if (state == STATE_BAD_PHOTO) {
			h.remove("Referer");
			h.put("Referer", "http://fotostrana.ru/rating/user/"
					+ requestVote.targetId + "/?fsrating=photoid-"
					+ requestVote.photoId
					+ "+offset-1+currentPhoto-1+pageSource-"
					+ requestVote.pageSource);

		}
		return h;

	}

}
