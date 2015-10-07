package fotostrana.ru.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Запись лога
 * 
 */
public class LogRecord implements Comparable<LogRecord> {

	/**
	 * Формат даты логов
	 */
	public static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	/**
	 * Время в отформатированом виде
	 */
	private String dateString;
	/**
	 * Запись лога в текстовом виде
	 */
	private String recordToString;
	private Date date;
	private String message;

	/**
	 * Группа событий к которой относится запись
	 */
	public int group;
	/**
	 * Тип события по качественному параметру
	 */
	public int type;

	/**
	 * Создает запись общей группы
	 * 
	 * @param message
	 *            сообщение
	 */
	public LogRecord(String message) {
		this.setDate(new Date());
		this.message = message;
		group = Log.GROUP_ALL;
		this.type = Log.TYPE_NEUTRAL;
	}

	/**
	 * Создает запись заданого типа
	 * 
	 * @param message
	 *            сообщение
	 * @param group
	 *            группа сообщений
	 */
	public LogRecord(String message, int group) {
		this(message);
		this.group = group;
	}

	/**
	 * Создает запись заданого типа
	 * 
	 * @param message
	 *            сообщение
	 * @param group
	 *            группа сообщений
	 * @param type
	 *            тип сообщений
	 */
	public LogRecord(String message, int group, int type) {
		this(message, group);
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Время события
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}

	private void setDate(Date date) {
		this.date = date;
		dateString = dateFormat.format(date);
	}

	/**
	 * Время события в строковом формате
	 * 
	 * @return
	 */
	public String getDateString() {
		return dateString;
	}

	@Override
	public String toString() {
		if (recordToString == null)
			recordToString = dateString + " : " + message;
		return recordToString;
	}

	@Override
	public int compareTo(LogRecord o) {
		if (date.getTime() != o.date.getTime())
			return (int) (date.getTime() - o.date.getTime());
		return toString().compareTo(o.toString());
	}

}