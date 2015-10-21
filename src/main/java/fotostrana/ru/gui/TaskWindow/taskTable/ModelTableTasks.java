package fotostrana.ru.gui.TaskWindow.taskTable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.TaskManager;

/**
 * Модель таблицы заданий
 * 
 */
public class ModelTableTasks extends AbstractTableModel {
	// public static String[] COLUMN_NAME = { "Имя", "Id", "Задание",
	// "Состояние" };
	public static String[] COLUMN_NAME = { "Задание", "Состояние" };
	private static final long serialVersionUID = -3711987765768182637L;
	public List<AbstractTask> tasks;

	public ModelTableTasks() {
		tasks = TaskManager.TASK_MANAGER.getTasks();
	}

	@Override
	public String getColumnName(int arg0) {
		return COLUMN_NAME[arg0];
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAME.length;
	}

	@Override
	public int getRowCount() {
		return tasks.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		AbstractTask task = tasks.get(arg0);
		if (task == null)
			return "";
		switch (arg1) {
		// case 0:
		// return task.getTargetName();
		// case 1:
		// return task.getTargetId();
		case 0:
			return task.getDescription();
		case 1:
			return task.getTaskState();
		default:
			return "";
		}
	}

	public void setTasks(List<AbstractTask> tasks) {
		this.tasks = tasks;
	};
}
