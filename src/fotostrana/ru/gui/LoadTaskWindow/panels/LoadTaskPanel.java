package fotostrana.ru.gui.LoadTaskWindow.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import configuration.ApplicationConfiguration;

import fotostrana.ru.gui.TaskWindow.taskTable.TaskTable;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.Task;
import fotostrana.ru.task.TaskManager;

/**
 * Панель для загрузки заданий из файла
 * 
 */
public class LoadTaskPanel extends JPanel {
	public boolean isLoadFile = false;
	public List<AbstractTask> tasks = new ArrayList<AbstractTask>();

	private static final long serialVersionUID = 1L;

	private FileSelectionPanel fileSelectionPanel;
	private ActionListener actionListenerButtonLoadFile;
	private JPanel tablePanel = new JPanel(new BorderLayout());
	private TaskTable taskTable = new TaskTable();;

	public LoadTaskPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new TitledBorder("Загрузка заданий"));
		actionListenerButtonLoadFile = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadTask();
			}
		};

		fileSelectionPanel = new FileSelectionPanel("Загрузить",
				actionListenerButtonLoadFile);
		fileSelectionPanel.setMaximumSize(new Dimension(5000, 30));

		taskTable.setTasks(tasks);
		JScrollPane scrollPanel=new JScrollPane(taskTable);
		tablePanel.add(scrollPanel, BorderLayout.CENTER);

		this.add(fileSelectionPanel);
		this.add(Box.createVerticalStrut(10));
		this.add(tablePanel);

	}

	public void reset() {
		isLoadFile = false;
		tasks = new ArrayList<AbstractTask>();
		taskTable.setTasks(tasks);
		tablePanel.setVisible(false);
		String defaultFile = ApplicationConfiguration.INSTANCE
				.getValue("configuration.Task.DefaultFileWithTask");
		if (defaultFile != null) {
			fileSelectionPanel.setPath(defaultFile);
		}
	}

	/**
	 * Загружает задания из файла
	 */
	public void loadTask() {
		String file = fileSelectionPanel.getFilePath();
		if (file != null) {
			isLoadFile = true;
			Set<Task> loadTasks = TaskManager.TASK_MANAGER
					.loadTaskWithFile(file);
			if (loadTasks.size() == 0) {
				JOptionPane.showMessageDialog(tablePanel, "Нет заданий.");
				return;
			}
			boolean check = true;
			for (Task task : loadTasks) {
				if (!tasks.add(task))
					check = false;
			}
			if (!check) {
				JOptionPane.showMessageDialog(tablePanel,
						"Некоторые задания уже есть.");
			}
		}
		tablePanel.setVisible(true);
		taskTable.update();
		// System.out.println(tasks.size());
	}

	public void setTasks(List<AbstractTask> tasks) {
		taskTable.setTasks(tasks);
		this.tasks = tasks;
		isLoadFile = true;
		tablePanel.setVisible(true);
	}
}
