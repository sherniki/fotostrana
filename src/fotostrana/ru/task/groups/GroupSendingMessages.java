package fotostrana.ru.task.groups;

import java.util.LinkedList;
import java.util.List;

import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.SuccessfulSentMessage;
import fotostrana.ru.reports.sendMessages.Message;
import fotostrana.ru.task.GroupTask;
import fotostrana.ru.task.tasks.TaskSendMessages;
import fotostrana.ru.users.User;

/**
 * Группа заданий отправки сообщений
 * 
 */
public class GroupSendingMessages extends GroupTask {
	private int minDelay;
	private int maxDelay;
	/**
	 * Количество уже отправленых сообщений
	 */
	private int countSentMessages;
	/**
	 * Необходимо отправит соообщений
	 */
	private int needToSend;

	/**
	 * Успешно отправленые сообщения
	 */
	public List<Message> messages = new LinkedList<Message>();

	/**
	 * Группа заданий отправки сообщений
	 * 
	 * @param eventListener
	 *            слушатель событий группы
	 * @param minDelay
	 *            минимальная задержка между сообщениями
	 * @param maxDelay
	 *            максимальная задержка
	 */
	public GroupSendingMessages(EventListener eventListener, int minDelay,
			int maxDelay) {
		super();
		name = "Рассылка сообщений";
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.eventListener = eventListener;
	}

	/**
	 * Добавляет рассылку сообщений
	 * 
	 * @param user
	 *            анкета с которой будут отправляться сообщения
	 * @param messages
	 *            сообщения
	 */
	public void addMailing(User user, List<Message> messages) {
		needToSend += messages.size();
		addTask(new TaskSendMessages(user, messages, minDelay, maxDelay));
	}

	@Override
	public synchronized void handleEvent(Event event) {
		if (event instanceof SuccessfulSentMessage) {
			countSentMessages++;
			messages.add(((SuccessfulSentMessage) event).getMessage());
		}
		super.handleEvent(event);
	}

	@Override
	public String getTaskState() {
		state = "Отправлено " + countSentMessages + " из " + needToSend
				+ " сообщений.";
		return state;
	}

	/**
	 * Успешно отправленные сообщения
	 * 
	 * @return
	 */
	public List<Message> getMessages() {
		return messages;
	}

}
