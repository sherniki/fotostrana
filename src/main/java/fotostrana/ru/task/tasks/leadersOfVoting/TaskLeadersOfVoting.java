package fotostrana.ru.task.tasks.leadersOfVoting;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.RequestLeadersOfVoting;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.reports.leadersOfVoting.ReportLeadersOfVoting;
import fotostrana.ru.task.tasks.TaskFotostrana;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.AllUsers;

/**
 * Задание получения списка лидеров голосования
 * 
 */
public abstract class TaskLeadersOfVoting extends TaskFotostrana {
	/**
	 * Отчет с списком лидеров
	 */
	protected ReportLeadersOfVoting report;
	/**
	 * Очередь запросов
	 */
	protected Queue<RequestLeadersOfVoting> queueRequests;
	/**
	 * Пользователь от имени которого отправляются запросы
	 */
	protected User user;

	/**
	 * Номинация в которой определяются лидеры
	 */
	protected Nomination nomination;

	public TaskLeadersOfVoting(Nomination nomination) {
		report = new ReportLeadersOfVoting(nomination);
		queueRequests = new LinkedBlockingDeque<RequestLeadersOfVoting>();
		this.nomination = nomination;
		descriptionTask = "Получение лидеров в " + nomination.name + ".";
		usersFilter.addFilter(new AllUsers());
		listUsers = UserManager.USER_MANAGER.getUsers(usersFilter);
		user = getRandomUser();
	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		if (request instanceof RequestLeadersOfVoting) {
			List<RecordReport> list = ((RequestLeadersOfVoting) request)
					.getRecords();
			report.addRecords(list);
		}
		countSuccessfulVotes.incrementAndGet();
	}

	@Override
	protected Request createNewRequest() {
		return queueRequests.poll();
	}

	public ReportLeadersOfVoting getLeaders() {
		return report;
	}

	public Set<RecordReport> getListLeaders() {
		return report.getListRecord();
	}

	/**
	 * Заполнение очереди запросов
	 */
	protected abstract void fillingTheRequestQueue();

	@Override
	protected void execute() {
		if (user != null)
			fillingTheRequestQueue();
		countVotes = queueRequests.size();
		scheduler.setCountOfTaskToBePerformed(countVotes);
		super.execute();
	}

	@Override
	protected void handleTheRequestWithTheBan(EventBan event) {
		super.handleTheRequestWithTheBan(event);
		if (event.getRequest() instanceof RequestLeadersOfVoting) {
			if (event.getRequest().getUser() == user) {
				user = getRandomUser();
			}
			if (user != null) {
				RequestLeadersOfVoting request = ((RequestLeadersOfVoting) event
						.getRequest()).clone();
				request.setUser(user);
				queueRequests.add(request);
			}

		}
	}

	@Override
	public void finish() {
		state = "Найдено " + report.getListRecord().size() + " людей.";
//		 report.print();
		super.finish();
	}

}
