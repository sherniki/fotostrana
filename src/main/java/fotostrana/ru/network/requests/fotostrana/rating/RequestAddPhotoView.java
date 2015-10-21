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

public class RequestAddPhotoView extends RequestFotostrana {
	private RequestVoteInRating requestVote;

	public RequestAddPhotoView(RequestVoteInRating voteRequest) {
		super(voteRequest);
		requestVote = voteRequest;
		typeRequest = TYPE_POST;
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/rating/rating/addphotoview/";
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
					boolean iLike = ParserJSON.getBoolean(result, "ret", ",",
							0);
					if (iLike) {
						event = new EventRequestExecutedSuccessfully(this);
					} else
						event = new EventCanNotVote(this);

			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		if (event != null)
			eventListener.handleEvent(event);

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
		return value;
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> h = new TreeMap<String, String>();
		h.remove("Referer");
		h.put("Referer", "http://fotostrana.ru/rating/user/"
				+ requestVote.targetId + "/?fsrating=photoid-"
				+ requestVote.photoId + "+offset-1+currentPhoto-1+pageSource-"
				+ requestVote.pageSource);

		return h;

	}

}
