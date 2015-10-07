package fotostrana.ru.network.requests.fotostrana.rating;

import java.util.Map;
import java.util.TreeMap;

import fotostrana.ru.network.requests.fotostrana.RequestVisitUserPage;
import fotostrana.ru.users.User;

/**
 * Голосует в РЕЙТИНГЕ
 * 
 */
public class RequestVoteInRating extends AbstractRequestVote {
	private RequestTitleRating titleRating;
	private RequestAlbumId requestAlbumId;

	public RequestVoteInRating(User user, String targetId, boolean isVisit) {
		super(user, targetId, isVisit);
		from = "rating";

		listRequests.clear();
		titleRating = new RequestTitleRating(this);
		requestAlbumId = new RequestAlbumId(this);
		like = new RequestPhotoLike(this);
		getComment = new RequestGetComment(this);

		listRequests.add(beforeLevelPoints);
		listRequests.add(titleRating);
		listRequests.add(showfsimprating);
		listRequests.add(requestAlbumId);
		listRequests.add(afterLevelPoints);
		listRequests.add(getComment);
		listRequests.add(like);
		// listRequests.add(new RequestAddPhotoView(this));
		// listRequests.add(new RequestGetComment(this,
		// RequestGetComment.MODE_AFTER_VOTE));
		if (isVisit)
			listRequests.add(new RequestVisitUserPage(this, targetId));
		isLogin();
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> headers = new TreeMap<String, String>();
		String sipletoken = user.getCookie("simpletoken").getValue();
		headers.put("X-Simple-Token", sipletoken);
		headers.put("X-Requested-With", "XMLHttpRequest");
		// headers.put("Origin", "http://fotostrana.ru");
		headers.put("Referer", "http://fotostrana.ru/rating/user/" + targetId
				+ "/");
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Accept-Encoding", "gzip,deflate,sdch");
		headers.put("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2");
		headers.put("Cache-Control", "max-age=0");
		headers.put("Connection", "keep-alive");
		headers.put("Host", "fotostrana.ru");
		// headers.put("Proxy-Connection", "keep-alive");
		return headers;
	}

}
