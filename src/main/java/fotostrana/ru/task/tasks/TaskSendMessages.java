package fotostrana.ru.task.tasks;

import java.util.LinkedList;
import java.util.List;

import fotostrana.ru.events.network.SuccessfulSentMessage;
import fotostrana.ru.log.Log;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.RequestSendMessage;
import fotostrana.ru.reports.sendMessages.Message;
import fotostrana.ru.task.schedulers.SchedulerWithRandomDelay;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.NotUsers;

/**
 * отправляет сообщения от одного пользователя
 * 
 */
public class TaskSendMessages extends TaskFotostrana {
	private User user;
	private List<Message> target;
	private int indexNextMessage;

	/**
	 * отправляет сообщения от одного пользователя
	 * 
	 * @param user
	 *            пользователь от имени которого будут отрпарвляться сообщения
	 * @param target
	 *            сообщения которые он должен отправить
	 * @param minWait
	 *            минимальная задержка между сообщениями
	 * @param maxWait
	 *            максимальная задержка между сообщениями
	 */
	public TaskSendMessages(User user, List<Message> target, int minWait,
			int maxWait) {
		super();
		this.user = user;
		descriptionTask = "Отправка " + target.size() + " сообщений с анкеты "
				+ user.id + ".";
		countVotes = target.size();
		this.target = target;
		indexNextMessage = 0;
		scheduler = new SchedulerWithRandomDelay(this, minWait, maxWait);
		scheduler.setCountOfTaskToBePerformed(countVotes);
		usersFilter.clear();
		usersFilter.addFilter(new NotUsers());
		mandatoryProfiles = new LinkedList<User>();
		mandatoryProfiles.add(user);
	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		countSuccessfulVotes.incrementAndGet();
		if (request instanceof RequestSendMessage) {
			Message message = ((RequestSendMessage) request).message;
			Log.LOGGING.addTaskLog("Отправлено сообщение: Получатель = "
					+ message.targetId + ", Отправитель = " + message.sender.id
					+ ".", Log.TYPE_POSITIVE);
			eventListener.handleEvent(new SuccessfulSentMessage(
					(RequestSendMessage) request));
		}
	}

	@Override
	protected Request createNewRequest() {
		if (indexNextMessage < target.size()) {
			Message message = target.get(indexNextMessage);
			message.sender = user;
			Request request = new RequestSendMessage(message);
			request.setEventListener(this);
			indexNextMessage++;
			return request;
		}
		return null;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/chat/";
	}

	@Override
	public void finish() {
		state = "Отправлено " + countSuccessfulVotes.get() + " из "
				+ countVotes + " сообщений";
		super.finish();
	}

}
