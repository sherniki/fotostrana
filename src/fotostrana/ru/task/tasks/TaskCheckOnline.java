package fotostrana.ru.task.tasks;

import java.util.LinkedList;
import java.util.Map;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.network.requests.fotostrana.RequestIsOnline;

/**
 * Проверяет присутствие пользователей на сайте
 * 
 */
public class TaskCheckOnline extends TaskFotostrana {
	public Map<String, Boolean> users;
	private int countOnline;
	private LinkedList<String> queueId = new LinkedList<String>();

	/**
	 * @param users
	 *            мапа с ключом=ИД пользователей которых нужно проверить,
	 *            результат проверки будет занесен в эту же мапу
	 */
	public TaskCheckOnline(Map<String, Boolean> users) {
		this.users = users;
		countVotes = users.size();
		scheduler.setCountOfTaskToBePerformed(countVotes);
		descriptionTask = "Проверка на Online";
		for (String key : users.keySet()) {
			queueId.add(key);
		}
	}

	public TaskCheckOnline(Map<String, Boolean> users,
			EventListener eventListener) {
		this(users);
		this.eventListener = eventListener;
	}

	@Override
	protected void handleSuccessfullRequest(RequestFotostrana request) {
		if (request instanceof RequestIsOnline) {
			RequestIsOnline r = (RequestIsOnline) request;
			users.put(r.targetId, r.isOnline);
			if (r.isOnline)
				countOnline++;
		}
		countSuccessfulVotes.incrementAndGet();
	}

	@Override
	protected Request createNewRequest() {
		if (!queueId.isEmpty())
			return new RequestIsOnline(queueId.poll(), this);
		return null;
	}

	@Override
	public String getTargetUrl() {
		return "http://fotostrana.ru/";
	}

	@Override
	public void finish() {
		state = "Проверено " + countSuccessfulVotes.get() + " анкет. Из них "
				+ countOnline + " online.";
		print();
		super.finish();
	}

	private void print() {
		for (String key : users.keySet()) {
			System.out.println(key + ":" + users.get(key));
		}
	}

}
