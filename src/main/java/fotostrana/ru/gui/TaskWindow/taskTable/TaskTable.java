package fotostrana.ru.gui.TaskWindow.taskTable;

import java.util.List;

import javax.swing.JTable;

import fotostrana.ru.task.AbstractTask;

public class TaskTable extends JTable {
	private static final long serialVersionUID = -6563890499399620320L;
	private ModelTableTasks modelTableTasks;


	public TaskTable() {
		modelTableTasks = new ModelTableTasks();
		setModel(modelTableTasks);
		
		this.setDefaultRenderer(Object.class, new TaskTableCellRenderer());
		// getColumn(this.getColumnName(0)).setMaxWidth(120);
		// getColumn(this.getColumnName(1)).setPreferredWidth(150);
		// getColumn(this.getColumnName(1)).setMaxWidth(300);
	}

	public void setTasks(List<AbstractTask> tasks) {
		modelTableTasks.setTasks(tasks);
		update();
	};
	
	/**
	 * Возращет задание по индексу
	 * 
	 * @param index
	 * @return null если неверный индекс
	 */
	public AbstractTask getTask(int index) {
		return modelTableTasks.tasks.get(index);
	}

	/**
	 * Возращает выбраное задание
	 * 
	 * @return
	 */
	public AbstractTask getSelectedTask() {
		return getTask(getSelectedRow());

	}
	
	public void update() {
		updateUI();
		// Выдиляет перое задание
		if (getRowCount() > 0)
			setRowSelectionInterval(0, 0);
	}
}
