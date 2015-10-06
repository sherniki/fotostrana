package fotostrana.ru.task.tasks;

import java.util.List;

import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.RequestLeadersInNominations;
import fotostrana.ru.network.requests.fotostrana.rating.RequestVoteInRating;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.AllUsers;

/**
 * Тестовое задание
 * 
 */
public class TestTask extends TaskFotostrana {
	int i = 0;
	int count = 1;
	boolean flag = false;

	public TestTask() {
		descriptionTask = "Тестовое задание";
		usersFilter.filters.clear();
		usersFilter.addFilter(new AllUsers());
		scheduler.setCountOfTaskToBePerformed(count);
	}

	@Override
	public int getCountOfTaskToBePerformed() {
		return count;
	}

	@Override
	protected Request createNewRequest() {
		if (!flag) {
			User user = null;
			for (User u : listUsers)
				if (u.id.compareTo("78251661") == 0) {
					user = u;
					break;
				}
			targetId = "58914354";
			flag = true;
			Request request = new RequestVoteInRating(user, targetId,true);
			request.setEventListener(this);
			return request;
		}
		return null;
	}

	@Override
	public void finish() {
		state = "Выполнился";
	}

	@Override
	protected void handleNetworkEvent(EventOfNetworkRequests event) {
		if (event instanceof EventRequestExecutedSuccessfully) {
			handleSuccessfullRequest(event.getRequest());
		}

	}

	@Override
	protected void handleSuccessfullRequest(Request request) {
		if (request instanceof RequestLeadersInNominations) {
			List<RecordReport> list = ((RequestLeadersInNominations) request)
					.getRecords();
			int i = 1;
			for (RecordReport recordReport : list) {
				System.out.println("Место = " + i + " ; "
						+ recordReport.toString());
				i++;
			}
		}
	}

	@Override
	public String getTaskState() {
		return state;
	}

	@Override
	public String getTargetUrl() {
		return "";
	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
	}
}
