package fotostrana.ru.task.tasks.tournament;

import java.util.Date;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.events.tasks.EventCompliteTheTask;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.tournament.RequestVisitTournament;
import fotostrana.ru.task.tasks.TaskFotostrana;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.tournament.FilterTournament;

/**
 * Заходит пользователями которые могут голосовать в турнире на страницу турнира
 * 
 */
public class SignIntoTournament extends TaskFotostrana {

	public SignIntoTournament() {
		super();
		usersFilter.filters.clear();
		usersFilter.addFilter(new FilterTournament());
		descriptionTask = "Открытие страницы турнира. ";
	}

	// @Override
	// public Task parseTask(String value) {
	// if (value.indexOf("открыть страницу турнира") > -1)
	// return new SignIntoTournament();
	// else
	// return null;
	// }

	@Override
	protected Request createNewRequest() {
		User user = getRandomUser();
		if (user == null)
			return null;
		RequestVisitTournament requestVisitTournament = new RequestVisitTournament(
				user);
		requestVisitTournament.setEventListener(this);
		return requestVisitTournament;
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
		request.stop();
	}

	@Override
	public void finish() {
		timeFinish = new Date();
		Event event = new EventCompliteTheTask(this);

		state = "Завершено. Открыло " + countSuccessfulVotes.get() + " из "
				+ countVotes + " анкет. Время : " + taskExecutionTime();
		Log.LOGGING.addTaskLog(getDescription() + getTaskState(),
				Log.TYPE_POSITIVE);
		eventListener.handleEvent(event);
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/contest/team/?mailNy14=1";
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
			state = "Выполнение невозможно, нет подходящих аккаунтов.";
			scheduler.stop();
		}

	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		countSuccessfulVotes.incrementAndGet();
		Log.LOGGING.addUserLog("Зайдено в турнир с анкеты "
				+ request.getUser().id, Log.TYPE_POSITIVE);

	}

}
