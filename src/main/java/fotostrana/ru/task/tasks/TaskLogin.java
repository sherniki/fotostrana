package fotostrana.ru.task.tasks;

import java.util.Date;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.LoginRequest;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.FilterNotLogin;

/**
 * Логинит заданых пользователей
 * 
 */
public class TaskLogin extends TaskFotostrana {
	// /**
	// * Список пользователей которых необходимо авторизовать
	// */
	// private ConcurrentSkipListSet<User> users;

	/**
	 * Авторизует заданых пользователей
	 * 
	 * @param users
	 */
	public TaskLogin() {
		super();
		usersFilter.addFilter(new FilterNotLogin());
		// countVotes = users.size();
		// scheduler.setCountOfTaskToBePerformed(users.size());
		descriptionTask = "Авторизация";
	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		countSuccessfulVotes.incrementAndGet();
		Log.LOGGING.addUserLog("Анкета залогинена " + request.getUser().id,
				Log.TYPE_POSITIVE);
	}

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		LoginRequest loginRequest = new LoginRequest(user);
		loginRequest.setEventListener(this);
		return loginRequest;
	}

	@Override
	public void finish() {
		state = "Обновлено " + countSuccessfulVotes.get() + " из " + countVotes
				+ " анкет.";
		super.finish();
	}

	@Override
	protected void handleNetworkEvent(EventOfNetworkRequestsFotostrana event) {
		RequestFotostrana request = event.getRequest();

		if (event instanceof EventBan) {
			countSuccessfulVotes.incrementAndGet();
			handleTheRequestWithTheBan((EventBan) event);
		}

		if (event instanceof EventRequestExecutedSuccessfully) {
			handleSuccessfullRequest(request);
		}

		if (event instanceof EventIsNotAuthorization) {
			countSuccessfulVotes.incrementAndGet();
			handleNotAutorization(request);
		}

		if (event instanceof FailEvent) {
			request.stop();
		}
	}

	@Override
	public String getTargetUrl() {
		return Request.URL_FOTOSTRANA;
	}

	@Override
	protected void execute() {
		timeStart = new Date();

		listUsers = UserManager.USER_MANAGER.getUsers(usersFilter);
		countVotes = listUsers.size();
		scheduler.setCountOfTaskToBePerformed(countVotes);
		// System.out.println(listUsers.size());

		Log.LOGGING.addTaskLog("Новое задание : " + descriptionTask,
				Log.TYPE_NEUTRAL);
		if (listUsers.size() > 0) {
			state = "Выполняется";
			scheduler.start();
		} else {
			state = "Все анкеты уже залогинены.";
			scheduler.stop();
		}

	}
}

// @Override
// protected void handleSuccessfullRequest(RequestFotostrana request) {
// countSuccessfulVotes.incrementAndGet();
// Log.LOGGING.addUserLog(
// "Обновлена информация о анкете " + request.getUser().id,
// Log.TYPE_POSITIVE);
//
// }