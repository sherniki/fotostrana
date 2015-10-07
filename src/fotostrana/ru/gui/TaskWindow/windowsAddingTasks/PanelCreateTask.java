package fotostrana.ru.gui.TaskWindow.windowsAddingTasks;

import javax.swing.JPanel;

import fotostrana.ru.task.AbstractTask;

/**
 * Панель создания задания
 * 
 */
public abstract class PanelCreateTask extends JPanel {
	private static final long serialVersionUID = 1L;
	protected String errorMessage = "Неверно заданы параметры.";

	/**
	 * Создает задание
	 * 
	 * @return null -если есть ошибки и невозможно создать задание
	 */
	public abstract AbstractTask createTask();

	/**
	 * Надпись
	 * 
	 * @return
	 */
	public abstract String titleDialog();

	/**
	 * Очищает панель
	 */
	public abstract void reset();

	/**
	 * возращает текстовое описание ошибки возникшей при создании задания
	 * 
	 * @return
	 */
	public String errorMessage() {
		return errorMessage;
	}

}
