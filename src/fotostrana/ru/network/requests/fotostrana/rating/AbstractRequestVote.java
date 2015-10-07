package fotostrana.ru.network.requests.fotostrana.rating;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestVote;
import fotostrana.ru.users.User;

public class AbstractRequestVote extends RequestVote {

	public String photoId;
	public String albumId;
	public String pageSource;
	public int iLike;
	public String from;

	protected RequestGetComment getComment;
	protected RequestPhotoLike like;
	protected RequestShowfsimprating showfsimprating;
	protected RequestLevelPoints beforeLevelPoints;
	protected RequestLevelPoints afterLevelPoints;

	public AbstractRequestVote(User user, String targetId, boolean isVisit) {
		super(user, 1, targetId, isVisit);
		showfsimprating = new RequestShowfsimprating(this);
		beforeLevelPoints = new RequestLevelPoints(this);
		afterLevelPoints = new RequestLevelPoints(this);
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResult(String result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof EventRequestExecutedSuccessfully) {
			Request request = ((EventRequestExecutedSuccessfully) event)
					.getRequest();
			if ((request == like) || ((request == getComment) && (isLike()))) {
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

	public int beforeLevelPoints() {
		return beforeLevelPoints.levelPoints;
	}

	public int afterLevelPoints() {
		return afterLevelPoints.levelPoints;
	}

	public boolean isDoublePoints() {
		return beforeLevelPoints.isDoublePoints;
	}

	public boolean isLike() {
		int deltaPoints = afterLevelPoints
				.getPoints(RequestLevelPoints.POINTS_LIKE)
				- beforeLevelPoints.getPoints(RequestLevelPoints.POINTS_LIKE);
		if (deltaPoints > 0) {
			if (isDoublePoints())
				deltaPoints = deltaPoints / 2;
		}
		return deltaPoints >= RequestLevelPoints
				.getPointsPerItem(RequestLevelPoints.POINTS_LIKE);
	}

}
