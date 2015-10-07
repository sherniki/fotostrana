package fotostrana.ru.network.requests.fotostrana.phototags;

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

public class RequestAlbumIdPhototags extends RequestFotostrana {
	private RequestVotePhototags phototags;

	public RequestAlbumIdPhototags(RequestVotePhototags parrentRequest) {
		super(parrentRequest);
		phototags = parrentRequest;
		typeRequest = TYPE_POST;
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/phototags/viewer/getphotosdirection/";
	}

	@Override
	public void setResult(String result) {
		Event event;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				String start = "gallery_id\":\"" + phototags.photoId;
				int iStart = result.indexOf(start);
				String userId = ParserJSON.getString(result, "user_id", iStart);
				String albumID = ParserJSON.getString(result,
						"gallery_album_id", iStart);
				if ((albumID != null)) {
					phototags.albumId = albumID;
					phototags.targetId = userId;
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
	public HttpEntity getRequestData() {
		String value = "img="
				+ phototags.photoId
				+ "&pageSource=35&clientW=1903&clientH=296&layout%5Btab%5D="
				+ phototags.layoutTab
				+ "&layout%5Btag%5D="
				+ phototags.layoutTag
				+ "&layout%5Bsort%5D=1&offset=0&album=false&suggested=0&_fs2ajax=1";
		return Request.getPostData_APPLICATION_FORM_URLENCODED(value);
	}

	@Override
	public Map<String, String> headers() {
		return phototags.headers();
	}

}
