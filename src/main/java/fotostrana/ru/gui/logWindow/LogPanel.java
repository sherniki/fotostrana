package fotostrana.ru.gui.logWindow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import fotostrana.ru.gui.logWindow.logTable.LogTable;
import fotostrana.ru.log.Log;

/**
 * Панель логов
 * 
 */
public class LogPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private LogTable logTable = new LogTable();;
	private JScrollPane scrollPane = new JScrollPane(logTable);
	private FilterGroupPanel filterPanel = new FilterGroupPanel();
	private JCheckBox autoScroll = new JCheckBox("Автопрокрутка записей", true);

	public LogPanel() {
		this.setBorder(new TitledBorder("Логи"));
		setLayout(new BorderLayout(5, 0));
		this.add(filterPanel, BorderLayout.NORTH);
		this.add(autoScroll, BorderLayout.SOUTH);
		this.add(scrollPane, BorderLayout.CENTER);

	}

	public void update() {
		if (autoScroll.isSelected()) {
			logTable.scrollRectToVisible(logTable.getCellRect(
					logTable.getRowCount() - 1, 0, true));

		}
		logTable.updateUI();
	}

	/**
	 * Панель с фильтрами групп логов
	 * 
	 */
	class FilterGroupPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private ButtonGroup buttonGroup = new ButtonGroup();
		private JRadioButton all = new JRadioButton("Все", true);
		private JRadioButton tasks = new JRadioButton("Задания", false);
		private JRadioButton network = new JRadioButton("Сеть", false);
		private JRadioButton users = new JRadioButton("Анкеты", false);
		private JRadioButton files = new JRadioButton("Файлы", false);

		public FilterGroupPanel() {
			buttonGroup.add(all);
			buttonGroup.add(tasks);
			buttonGroup.add(network);
			buttonGroup.add(users);
			buttonGroup.add(files);

			all.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					logTable.setShowGroup(Log.GROUP_ALL);
					LogPanel.this.update();
				}
			});

			tasks.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					logTable.setShowGroup(Log.GROUP_TASKS);
					LogPanel.this.update();
				}
			});
			network.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					logTable.setShowGroup(Log.GROUP_NETWORK);
					LogPanel.this.update();
				}
			});

			users.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					logTable.setShowGroup(Log.GROUP_USER);
					LogPanel.this.update();
				}
			});

			files.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					logTable.setShowGroup(Log.GROUP_FILE);
					LogPanel.this.update();
				}
			});

			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel("Тип событий :");
			this.add(label);
			this.add(all);
			this.add(tasks);
			this.add(network);
			this.add(users);
			this.add(files);
		}

	}
}
