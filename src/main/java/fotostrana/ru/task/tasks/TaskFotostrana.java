package fotostrana.ru.task.tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
//import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventOfNetworkRequests;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.task.Task;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.UsersFilter;
import fotostrana.ru.users.filtersUsers.MacroFilter;

/**
 * Интерфейс заданий для Фотостраны
 * 
 */
public abstract class TaskFotostrana extends Task {
	public static DateFormat dateFormatter = new SimpleDateFormat(
			"HH:mm dd.MM.yyyy");
	private static DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
	static {
		if (TimeZone.getDefault().getID().compareTo("Europe/Moscow") == 0) {
			dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		}

		timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	/**
	 * Максимальное количество запросов которое можно выполнить
	 */
	protected int maxCountRequest;
	/**
	 * id пользователя который является целью задания
	 */
	protected String targetId;
	protected String targetName = "Не указано";
	protected Random random = new Random();

	/**
	 * Поьзователи которые могут выполнять задание, получает от USERMANAGER
	 */
	protected List<User> listUsers;

	/**
	 * Фильтр пользователей которые могут выполнять задание
	 */
	protected MacroFilter usersFilter = new MacroFilter();

	/**
	 * количество голосов которое необходимо накрутить
	 */
	protected int countVotes;

	/**
	 * Обязательные анкеты
	 */
	protected List<User> mandatoryProfiles = null;
	protected List<User> usedUsers = new LinkedList<User>();

	/**
	 * Успешно выполненых запросов
	 */
	protected AtomicInteger countSuccessfulVotes = new AtomicInteger(0);

	/**
	 * Добавляет еще один фильтр к заданию
	 * 
	 * @param newFilter
	 * @return
	 */
	public boolean addFilter(UsersFilter newFilter) {
		return usersFilter.addFilter(newFilter);
	}

	public void addMandatoryProfiles(Collection<User> profiles) {
		if (mandatoryProfiles == null)
			mandatoryProfiles = new LinkedList<User>();
		mandatoryProfiles.addAll(profiles);
	}

	@Override
	protected void execute() {
		listUsers = UserManager.USER_MANAGER.getUsers(usersFilter);
		if (mandatoryProfiles != null)
			listUsers.addAll(mandatoryProfiles);

		Log.LOGGING.addTaskLog("Новое задание : " + descriptionTask
				+ ". Подходящих анкет " + listUsers.size(),
				Log.getTypeMessage(listUsers.size()));
		if (listUsers.size() > 0) {
			state = "Выполняется";
			scheduler.start();
		} else {
			state = "Выполнение невозможно, нет подходящих аккаунтов.";
			scheduler.stop();
		}
	}

	@Override
	public int getCountOfTaskToBePerformed() {
		return countVotes;
	}

	/**
	 * Количество успешно накрученых голосов
	 * 
	 * @return
	 */
	public int getCountSuccessfulVotes() {
		return countSuccessfulVotes.get();
	};

	/**
	 * Количество голосов которое нужно было накрутить
	 * 
	 * @return
	 */
	public int getCountVotes() {
		return countVotes;
	}

	/**
	 * Возращает случайного пользователя из списка доступных пользователей, при
	 * этом он удаляется из списка
	 * 
	 * @return null если нет пользователей
	 */
	public User getRandomUser() {
		User result = null;
		// если выбраный пользователь уже забанен, выбирается другой
		do {
			if (listUsers.size() == 0)
				return null;
			int index = random.nextInt(listUsers.size());

			for (User user : listUsers) {
				if (index == 0) {
					result = user;
					break;
				}
				index--;
			}
			listUsers.remove(result);
		} while (result.isBanned);

		if (result != null)
			usedUsers.add(result);
		return result;
	}

	public String getTargetId() {
		return targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	@Override
	public String getTaskState() {
		switch (scheduler.getState()) {
		case STATE_NOT_STARTED:
			if (scheduledTimeStart != null) {
				Date currentDate = new Date();
				String remainedString = "";
				// System.out.println("Start date="
				// + dateFormatter.format(scheduledTimeStart));
				if (scheduledTimeStart.getTime() > currentDate.getTime()) {
					Date remained = new Date(scheduledTimeStart.getTime()
							- currentDate.getTime());
					remainedString = "Осталось "
							+ timeFormatter.format(remained);
				}
				return "Запланировано на "
						+ dateFormatter.format(scheduledTimeStart) + ". "
						+ remainedString;
			}
			return "Не выполняется";
		case STATE_RUN:
			return countSuccessfulVotes.get() + " из " + countVotes;
		case STATE_PAUSE:
			return "Пауза:" + countSuccessfulVotes.get() + " из " + countVotes;
		default:
			return state;
		}
	}

	public UsersFilter getUsersFilter() {
		return usersFilter;
	}

	@Override
	protected void handleNetworkEvent(EventOfNetworkRequests event) {
		if (event instanceof EventOfNetworkRequestsFotostrana)
			handleNetworkEvent((EventOfNetworkRequestsFotostrana) event);
	}

	protected void handleNetworkEvent(EventOfNetworkRequestsFotostrana event) {
		RequestFotostrana request = event.getRequest();

		if (event instanceof EventBan) {
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

	/**
	 * Обработка запроса не авторизорованого пользователя
	 * 
	 * @param request
	 *            запрос
	 */
	protected void handleNotAutorization(RequestFotostrana request) {
		request.getUser().resetCookie();
		listUsers.add(request.getUser());
		// System.out.println("Пользователь " + request.getUser().getId()
		// + "неавторизован.");
	}

	@Override
	protected void handleSuccessfullRequest(Request request) {
		if (request instanceof RequestFotostrana)
			handleSuccessfullRequest((RequestFotostrana) request);
	}

	protected abstract void handleSuccessfullRequest(RequestFotostrana request);

	/**
	 * Обработка запроса при котором произошол бан пользователя
	 * 
	 * @param request
	 *            запрос
	 */
	protected void handleTheRequestWithTheBan(EventBan event) {
		RequestFotostrana request = event.getRequest();
		request.getUser().setDescription(event.getReason());
		UserManager.USER_MANAGER.addToBan(request.getUser());
		// System.out.println("Пользователь " + request.getUser().getId()
		// + " попал в бан.");
	}

	/**
	 * Удаляет заданый фильтр
	 * 
	 * @param removeFilter
	 * @return
	 */
	public boolean removeFilter(UsersFilter removeFilter) {
		return usersFilter.removeFilter(removeFilter);
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * Добавляет пользователю выполненое действие
	 * 
	 * @param request
	 */
	protected void setUserAction(RequestFotostrana request, int nomination) {
		// User user = request.getUser();
		// UserActions action = new UserActions(user.id, targetId, new Date(),
		// nomination);
		// user.addACtion(action);
	}
}
