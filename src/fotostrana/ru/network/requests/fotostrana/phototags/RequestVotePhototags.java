package fotostrana.ru.network.requests.fotostrana.phototags;

import java.util.Map;
import java.util.TreeMap;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.requests.fotostrana.RequestVisitUserPage;
import fotostrana.ru.network.requests.fotostrana.rating.AbstractRequestVote;
import fotostrana.ru.network.requests.fotostrana.rating.RequestGetComment;
import fotostrana.ru.network.requests.fotostrana.rating.RequestShowfsimprating;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.users.User;

public class RequestVotePhototags extends AbstractRequestVote {
	public Nomination nomination;
	private RequestTitlePhotags requestTitle;
	private RequestAlbumIdPhototags requestAlbumId;
	public String layoutTab;
	public String layoutTag;

	public RequestVotePhototags(User user, String targetPhotoId,
			Nomination nomination, EventListener eventListener,boolean isVisit) {
		super(user, "",isVisit);
		this.eventListener = eventListener;
		this.nomination = nomination;
		photoId = targetPhotoId;
		pageSource = "35";

		listRequests.clear();
		requestTitle = new RequestTitlePhotags(this);
		requestAlbumId = new RequestAlbumIdPhototags(this);
		like = new RequestPhototagsLike(this);
		getComment = new RequestGetComment(this);

		listRequests.add(beforeLevelPoints);
		listRequests.add(requestTitle);
		listRequests.add(afterLevelPoints);
		listRequests.add(new RequestShowfsimprating(this));
		listRequests.add(requestAlbumId);
		listRequests.add(getComment);
		listRequests.add(like);
		if (isVisit)
			listRequests.add(new RequestVisitUserPage(this, targetId));
		isLogin();
	}

	@Override
	public String getURL() {
		return null;
	}

	@Override
	public void setResult(String result) {
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> headers = new TreeMap<String, String>();
		String sipletoken = user.getCookie("simpletoken").getValue();
		headers.put("X-Simple-Token", sipletoken);
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("Origin", "http://fotostrana.ru");
		headers.put("Referer", "http://fotostrana.ru/phototags/feed/"
				+ nomination.alias + "/?fstags=photoid-" + photoId
				+ "+offset-0+currentPhoto-0+pageSource-35");
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

	@Override
	public void handleEvent(Event event) {
		if (event instanceof EventRequestExecutedSuccessfully) {
			if (((EventRequestExecutedSuccessfully) event).getRequest() == like) {
				this.countVotesForMessage = 1;
				eventListener.handleEvent(new EventRequestExecutedSuccessfully(
						this));
				return;
			}
		}
		super.handleEvent(event);
	}

	public void setPointsBeforeVoting(int count) {
		pointsBeforeVoting = count;
	}

	public void setPointsAfterVoting(int count) {
		points = count;
	}

}
