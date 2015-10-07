package fotostrana.ru.network.requests.fotostrana;

import java.io.File;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.filters.BannedFilter;
import fotostrana.ru.network.requests.fotostrana.nominations.RequestChecksVote;
import fotostrana.ru.network.requests.fotostrana.nominations.RequestVisitNomination;
import fotostrana.ru.network.requests.fotostrana.tournament.RequestCheckTournament;
import fotostrana.ru.network.requests.fotostrana.tournament.RequestVisitTournament;
import fotostrana.ru.network.requests.fotostrana.uploadAvatar.UploadAvatar2;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;

/**
 * Запрос для обновления информации о анкете
 * 
 */
public class RequestUpdateTheProfiles extends RequestFotostrana {
	public final static String KEY_TARGET_ID = "targetId";
	private RequestChecksVote requestChecksVote;
	private RequestCheckTournament requestCheckTournament;
	private LoginRequest loginRequest;
	private RequestVisitTournament visitTournament;

	/**
	 * Запрос для обновление информации о анкете
	 * 
	 * @param user
	 *            пользователь чья анкета будет обновлена
	 */
	public RequestUpdateTheProfiles(User user) {
		super(user);
		requestChecksVote = new RequestChecksVote(user, user.id, 0);
		requestChecksVote.setParentRequest(this);
		requestCheckTournament = new RequestCheckTournament(this, user.id);
		visitTournament = new RequestVisitTournament(this);
		listRequests.clear();

		if (!user.isAutorizted()) {
			loginRequest = new LoginRequest(this);
			listRequests.add(loginRequest);
		}
		listRequests.add(new RequestVisitNomination(this));
		listRequests.add(requestChecksVote);
		listRequests.add(visitTournament);

	}

	@Override
	public void setResult(String result) {
	}

	@Override
	public void handleEvent(Event event) {
		Request request = ((EventOfNetworkRequestsFotostrana) event)
				.getRequest();
		if (request == loginRequest) {
			requestChecksVote.targetId = user.id;
			requestCheckTournament.targetId = user.id;
		}

		if (event instanceof EventBan) {
			handleEvenetBan((EventBan) event);
		}

		if (event instanceof EventIsNotAuthorization) {
			EventBan eventBan = new EventBan(this);
			eventBan.setReason("Невозможно авторизоваться");
			super.handleEvent(eventBan);
		}

		if (request == requestChecksVote) {
			for (int i = 0; i < 4; i++) {
				user.votingPositions.vote[i + 1] = requestChecksVote.votes[i];
				user.votingPositions.place[i + 1] = requestChecksVote.position[i];
			}
//			user.name = requestChecksVote.targetName;
		}

		if (request == visitTournament) {
			if (visitTournament.isPhone == true)
				listRequests.add(requestCheckTournament);
		}

		if (request == requestCheckTournament) {
			if (requestCheckTournament.noTeam) {
				int i = listRequests.indexOf(requestCheckTournament);
				listRequests.add(i, new RequestVisitTournament(this));
				back();
				return;
			}
			if (requestCheckTournament.targetColor != null)
				user.color = requestCheckTournament.targetColor;
			user.votingPositions.vote[0] = requestCheckTournament.points;
			user.votingPositions.place[0] = requestCheckTournament.position;

			if (user.isCanVoteTournament) {
				listRequests.add(new RequestVisitTournament(this));
			} else
				user.tournamentToken = "";
		}

		if (indexNextRequest == listRequests.size()) {
			eventListener
					.handleEvent(new EventRequestExecutedSuccessfully(this));
		}

	}

	protected void handleEvenetBan(EventBan event) {
		String reason = event.getReason();
		if (reason == BannedFilter.NO_MAIN_PHOTO) {
			File avatar = UserManager.USER_MANAGER.getNextAvatar();
			if (avatar != null) {
				UploadAvatar2 uploadAvatar = new UploadAvatar2(this, avatar);
				listRequests.add(indexNextRequest, uploadAvatar);
				return;
			}
		}
		super.handleEvent(event);
	}

	@Override
	public String getURL() {
		return URL_FOTOSTRANA;
	}

}
