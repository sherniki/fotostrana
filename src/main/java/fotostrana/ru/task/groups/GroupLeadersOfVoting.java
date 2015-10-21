package fotostrana.ru.task.groups;

import java.util.List;
import java.util.Set;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.tasks.EventTask;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.reports.leadersOfVoting.ReportLeadersOfVoting;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.GroupTask;
import fotostrana.ru.task.tasks.TaskCheckBanned;
import fotostrana.ru.task.tasks.leadersOfVoting.TaskLeadersOfNomination;
import fotostrana.ru.task.tasks.leadersOfVoting.TaskLeadersOfRating;
import fotostrana.ru.task.tasks.leadersOfVoting.TaskLeadersOfTournament;
import fotostrana.ru.task.tasks.leadersOfVoting.TaskLeadersOfVoting;
import fotostrana.ru.users.User;

/**
 * Группа заданий получения лидеров голосования
 * 
 */
public class GroupLeadersOfVoting extends GroupTask {
	public ReportLeadersOfVoting report;
	TaskCheckBanned taskCheckBanned;
	public Set<User> users;

	/**
	 * @param users
	 *            анкеты пользователей которые будут отправлять сообщения
	 */
	public GroupLeadersOfVoting(EventListener eventListener, Set<User> users) {
		super();
		name = "Получение лидеров голосований";
		state = name;
		descriptionTask = name;
		if (eventListener != null)
			this.eventListener = eventListener;
		report = new ReportLeadersOfVoting(Nomination.ALL_NOMINATIONS);
		this.users = users;
		taskCheckBanned = new TaskCheckBanned(users);
		addTask(taskCheckBanned);
	}

	/**
	 * Добавляет задание получения лидеров в заданой номинации
	 * 
	 * @param nomination
	 *            номинация
	 * @param regions
	 *            регионы
	 */
	public void leadersOfNomination(Nomination nomination, List<Region> regions) {
		addTask(new TaskLeadersOfNomination(nomination, regions));
	}

	public void leadersOfAllNominations(List<Region> regions) {
		for (Nomination nomination : Nomination.VOTING_NOMINATION) {
			leadersOfNomination(nomination, regions);
		}
	}

	/**
	 * Добавляет задание получения лидеров в турнире
	 */
	public void leadersOfTournament() {
		addTask(new TaskLeadersOfTournament());
	}

	/**
	 * Добавляет задание получения лидеров в рейтинге
	 * 
	 * @param regions
	 *            регионы
	 */
	public void leadersOfRating(List<Region> regions) {
		addTask(new TaskLeadersOfRating(regions));
	}

	/**
	 * Получает лидеров всех видов голосований во всех номинациях
	 * 
	 * @param regions
	 */
	public void allLeaders(List<Region> regions) {
		leadersOfAllNominations(regions);
		leadersOfTournament();
	}

	@Override
	protected void handleEventTask(EventTask event) {
		AbstractTask task = event.getTask();
		if (task instanceof TaskLeadersOfVoting) {
			report.addRecords(((TaskLeadersOfVoting) task).getListLeaders());
		}
	}

}
