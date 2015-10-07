package fotostrana.ru.gui.logWindow.logTable;

import javax.swing.JTable;

import fotostrana.ru.log.Log;
import fotostrana.ru.log.LogRecord;

/**
 * Таблица логов
 * 
 */
public class LogTable extends JTable {
	private ModelTableLog modelTable;
	private int showGroup = Log.GROUP_ALL;

	public LogTable() {
		modelTable = new ModelTableLog(Log.LOGGING.getGroup(showGroup));
		this.setModel(modelTable);
		getColumn(this.getColumnName(0)).setMaxWidth(100);
		this.setDefaultRenderer(Object.class, new LogTableCellRenderer());

	}

	private static final long serialVersionUID = 1591551265928607705L;

	public LogRecord getRecord(int index) {
		return modelTable.listLog.get(index);
	}

	/**
	 * Устанавливает заданую группу логов
	 * 
	 * @param groupLog
	 *            группа логов
	 */
	public void setShowGroup(int groupLog) {
		showGroup = groupLog;
		modelTable.setListLog(Log.LOGGING.getGroup(groupLog));
	}

}
