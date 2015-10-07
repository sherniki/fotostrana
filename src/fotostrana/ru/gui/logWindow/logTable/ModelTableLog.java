package fotostrana.ru.gui.logWindow.logTable;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import fotostrana.ru.log.LogRecord;

/**
 * Модель таблицы логов
 * 
 */
public class ModelTableLog extends AbstractTableModel {
	public static String[] COLUMN_NAME = { "Время", "Сообщение" };
	private static final long serialVersionUID = 1L;
	public List<LogRecord> listLog;

	/**
	 * Модель таблицы логов
	 * 
	 * @param listLog
	 */
	public ModelTableLog(List<LogRecord> listLog) {
		setListLog(listLog);
	}

	/**
	 * Задает список логов
	 * 
	 * @param listLog
	 */
	public void setListLog(List<LogRecord> listLog) {
		this.listLog = listLog;
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAME.length;
	}

	@Override
	public int getRowCount() {
		return listLog.size();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAME[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		LogRecord record = listLog.get(row);
		if (record != null) {
			switch (column) {
			case 0:
				return record.getDateString();
			case 1:
				return record.getMessage();
			default:
				return "";
			}
		}
		return "";
	}
}
