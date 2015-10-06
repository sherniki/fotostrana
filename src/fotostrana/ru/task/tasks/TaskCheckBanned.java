package fotostrana.ru.task.tasks;

import java.util.Set;

import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.RequestOpenUserPage;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.AllUsers;
import fotostrana.ru.users.filtersUsers.NotUsers;

public class TaskCheckBanned extends TaskFotostrana {
	User sourceUser = null;
	private int countBanned = 0;

	public TaskCheckBanned() {
		usersFilter.filters.clear();
		usersFilter.addFilter(new NotUsers());
		mandatoryProfiles = UserManager.USER_MANAGER.getUsers(new AllUsers());
		// sourceUser = source;
		descriptionTask = "Проверка на бан";
		countVotes = mandatoryProfiles.size();
		scheduler.setCountOfTaskToBePerformed(mandatoryProfiles.size());
	}
	
	public TaskCheckBanned(Set<User> users){
		this();
		mandatoryProfiles.clear();
		mandatoryProfiles.addAll(users);
		countVotes = mandatoryProfiles.size();
		scheduler.setCountOfTaskToBePerformed(mandatoryProfiles.size());
	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		countSuccessfulVotes.incrementAndGet();
	}

	@Override
	protected Request createNewRequest() {
		if (listUsers.size() > 0) {
			User user = listUsers.get(0);
			listUsers.remove(0);
			RequestOpenUserPage request = new RequestOpenUserPage(user);
			request.setEventListener(this);
			return request;
		}
		return null;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/";
	}

	@Override
	protected void handleTheRequestWithTheBan(EventBan event) {
		countSuccessfulVotes.incrementAndGet();
		countBanned++;
		super.handleTheRequestWithTheBan(event);
	}

	@Override
	public void finish() {
		state = "Проверено " + countSuccessfulVotes.get() + " анкет. Из них "
				+ countBanned + " забаненых.";
		super.finish();
	}

}
