package fotostrana.ru.gui.TaskWindow.taskTable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import fotostrana.ru.task.Scheduler;

/**
 * Отрисовывает ячейки таблицы
 * 
 */
public class TaskTableCellRenderer extends JLabel implements TableCellRenderer {
	public static final Color COLOR_COMPLETE = new Color(152, 251, 152);
	public static final Color COLOR_RUN = new Color(175, 238, 238);
	public static final Color COLOR_NO_RUN = Color.WHITE;
	private static final long serialVersionUID = 1L;

	public TaskTableCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value != null) {
			this.setText(value.toString());
		}
		TaskTable taskTable = (TaskTable) table;

		Color color = getColorByState(taskTable.getTask(row).getState());
		setBackground(color);
		Border border = null;
		if (hasFocus)
			border = UIManager.getBorder("Table.focusCellHighlightBorder");
		setBorder(border);
		return this;
	}

	public Color getColorByState(int state) {
		switch (state) {
		case Scheduler.STATE_COMPLETED:
			return COLOR_COMPLETE;

		case Scheduler.STATE_RUN:
			return COLOR_RUN;

			// case Scheduler.STATE_NOT_STARTED:
			// case Scheduler.STATE_PAUSE:
			// ;
		default:
			return COLOR_NO_RUN;
		}
	}

}
