package fotostrana.ru.reports.sendMessages;

import java.text.SimpleDateFormat;
import java.util.Date;

import fotostrana.ru.users.User;

/**
 * Текстовое сообщение
 * 
 */
public class Message {
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Получатель
	 */
	public String targetId;
	/**
	 * Текcт
	 */
	public String message;
	/**
	 * Отправитель
	 */
	public User sender;
	/**
	 * True если было оправлено
	 */
	public boolean isSent;
	/**
	 * Дата отправки, null - если небыло отправлено
	 */
	public Date timeSend;

	/**
	 * Описание
	 */
	public String description;

	/**
	 * Текстовое сообщение
	 * 
	 * @param targetId
	 *            получатель
	 * @param message
	 *            сообщение
	 */
	public Message(String targetId, String message) {
		this.targetId = targetId;
		this.message = message;
	}

	@Override
	public String toString() {
		String result = "";
		if (sender != null) {
			result += "Отправитель = " + sender.id + "; ";
		}
		result += "Получатель " + targetId + "; Сообщение = " + message
				+ "; Отправлено  = " + isSent+"; ";
		if (timeSend != null)
			result += "Время отправки = " + format.format(timeSend);
		return result;
	}
}
