package fotostrana.ru.gui.logWindow.logTable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import fotostrana.ru.log.Log;

/**
 * Отрисовывает ячейки таблицы
 * 
 */
public class LogTableCellRenderer extends JLabel implements TableCellRenderer {
	public static final Color COLOR_POSITIVE = new Color(173, 255, 47);
	public static final Color COLOR_NEUTRAL = Color.WHITE;
	public static final Color COLOR_NEGATIVE = new Color(255, 182, 193);
	private static final long serialVersionUID = 1L;

	public LogTableCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value != null) {
			this.setText(value.toString());
		}
		LogTable logTable = (LogTable) table;

		Color color = getColorByState(logTable.getRecord(row).type);
		setBackground(color);
		Border border = null;
		if (hasFocus)
			border = UIManager.getBorder("Table.focusCellHighlightBorder");
		setBorder(border);
		return this;
	}

	public Color getColorByState(int type) {
		switch (type) {
		case Log.TYPE_POSITIVE:
			return COLOR_POSITIVE;
			
		case Log.TYPE_NEUTRAL:
			return COLOR_NEUTRAL;

		case Log.TYPE_NEGATIVE:
			return COLOR_NEGATIVE;
		default:
			return COLOR_NEUTRAL;
		}
	}

}
