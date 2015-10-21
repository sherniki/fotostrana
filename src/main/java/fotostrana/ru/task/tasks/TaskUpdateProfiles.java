package fotostrana.ru.task.tasks;

import java.util.LinkedList;
import java.util.Set;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.RequestUpdateTheProfiles;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.AllUsers;

/**
 * Задания обновления информации в анкетах
 * 
 */
public class TaskUpdateProfiles extends TaskFotostrana {

	/**
	 * Обновляет анкеты по фильтру
	 */
	public TaskUpdateProfiles() {
		super();
		usersFilter.filters.clear();
		usersFilter.addFilter(new AllUsers());
		descriptionTask = "обновление анкет. ";
	}

	/**
	 * Обновляет анкеты заданые в списке
	 * 
	 * @param users
	 *            список пользователей которых необходимо обновить
	 */
	public TaskUpdateProfiles(Set<User> users) {
		this();
		listUsers = new LinkedList<User>();
		listUsers.addAll(users);
	}

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		RequestUpdateTheProfiles requestUpdateTheQuestionnaire = new RequestUpdateTheProfiles(
				user);
		requestUpdateTheQuestionnaire.setEventListener(this);
		return requestUpdateTheQuestionnaire;
	}

	@Override
	public void finish() {
		if (countVotes > 0)
			state = "Обновлено " + countSuccessfulVotes.get() + " из "
					+ countVotes + " анкет.";
		else
			state = "Нет новых анкет.";
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
		if (listUsers == null)
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
			scheduler.stop();
		}

	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		countSuccessfulVotes.incrementAndGet();
		Log.LOGGING.addUserLog(
				"Обновлена информация о анкете " + request.getUser().id,
				Log.TYPE_POSITIVE);

	}

}
