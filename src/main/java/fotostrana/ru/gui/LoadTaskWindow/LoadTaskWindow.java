package fotostrana.ru.gui.LoadTaskWindow;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;

import fotostrana.ru.gui.LoadTaskWindow.panels.FilterUsersPanel;
import fotostrana.ru.gui.LoadTaskWindow.panels.LoadTaskPanel;
import fotostrana.ru.gui.LoadTaskWindow.panels.SchedulerGroupPanel;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.GroupTask;
import fotostrana.ru.task.Scheduler;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.TaskFotostrana;
import fotostrana.ru.task.tasks.TaskVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.filtersUsers.NotUsers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Set;

/**
 * Окно загузки новых заданий
 * 
 */
public class LoadTaskWindow extends JDialog {

	/**
	 * Группа заданий
	 */
	// private GroupTask groupTask;

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel;
	private SchedulerGroupPanel schedulerPanel = new SchedulerGroupPanel();
	private FilterUsersPanel filterUsersPanel = new FilterUsersPanel();
	private JTextField nameTextEdit = new JTextField(20);
	private LoadTaskPanel loadTaskPanel = new LoadTaskPanel();
	private LoadTaskWindow thisFrame = this;

	/**
	 * Create the dialog.
	 */
	public LoadTaskWindow() {

		setModal(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});
		setTitle("Загрузка заданий");
		setBounds(100, 100, 550, 550);
		contentPanel = (JPanel) getContentPane();
		contentPanel.setAlignmentX(LEFT_ALIGNMENT);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();

		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel nameLabel = new JLabel("Имя группы");
		namePanel.add(nameLabel);
		namePanel.add(nameTextEdit);

		namePanel.setMaximumSize(new Dimension(5000, 30));

		JPanel buttonPane = new JPanel();
		buttonPane.setMaximumSize(new Dimension(5000, 30));
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton("Добавить");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GroupTask groupTask = new GroupTask();
				if (!loadTaskPanel.isLoadFile) {
					loadTaskPanel.loadTask();
				}
				List<AbstractTask> tasks = loadTaskPanel.tasks;
				if (tasks.size() == 0) {
					JOptionPane.showMessageDialog(thisFrame, "Нет заданий.");
					thisFrame.repaint();
					return;
				}
				Set<User> users = filterUsersPanel.selectionUsers;
				boolean onlySelectedProfiles = filterUsersPanel
						.onlySelectedProfiles();
				// if(filterUsersPanel.downloadFromFile.isEnabled())
				if (users.size() != 0) {
					NotUsers discardAllProfiles = new NotUsers();
					for (AbstractTask task : tasks) {
						if (task instanceof TaskFotostrana) {
							TaskFotostrana taskF = (TaskFotostrana) task;
							taskF.addMandatoryProfiles(users);
							if (onlySelectedProfiles)
								taskF.addFilter(discardAllProfiles);
						}

					}
				}

				for (AbstractTask task : tasks)
					if (task instanceof TaskVoting) {
						((TaskVoting) task).isVisit = schedulerPanel.isVisit();
					}

				groupTask.setName(nameTextEdit.getText());
				groupTask.addTasks(tasks);
				if (groupTask.getTasks().size() == 0) {
					JOptionPane.showMessageDialog(thisFrame,
							"Все выбраные задания уже есть.");
					thisFrame.repaint();
					return;
				}
				if (!TaskManager.TASK_MANAGER.addGroupTask(groupTask)) {
					JOptionPane.showMessageDialog(thisFrame,
							"Группа с таким названием уже есть.");
					nameTextEdit.setFocusable(true);
					thisFrame.repaint();
					return;
				}

				Scheduler scheduler = schedulerPanel.createScheduler();
				if (scheduler == null) {
					JOptionPane.showMessageDialog(thisFrame,
							"Невозможно создать планировщик выполнения.",
							"Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (schedulerPanel.isTasksScheduler()) {
					groupTask.setTasksScheduler(scheduler);
				} else {
					groupTask.setScheduler(scheduler);
				}
				if (schedulerPanel.isExeuteAll()) {
					groupTask.start();
				}
				if (schedulerPanel.isTimerExecute()) {
					groupTask.setScheduledTimeStart(schedulerPanel
							.getTimerDate());
				}
				closeWindow();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Отмена");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		contentPanel.add(namePanel);
		contentPanel.add(Box.createVerticalStrut(5));
		contentPanel.add(loadTaskPanel);
		contentPanel.add(Box.createVerticalStrut(5));
		contentPanel.add(filterUsersPanel);
		contentPanel.add(Box.createGlue());
		contentPanel.add(Box.createVerticalStrut(5));
		contentPanel.add(schedulerPanel);
		contentPanel.add(buttonPane);

		reset();
	}

	public void closeWindow() {
		setVisible(false);
	}

	public void setTasks(List<AbstractTask> tasks) {
		loadTaskPanel.setTasks(tasks);
	}

	/**
	 * Очищает все данные на форме
	 */
	public void reset() {
		nameTextEdit.setText(GroupTask.DEFAULT_NAME
				+ (TaskManager.TASK_MANAGER.countGroups() + 1));

		schedulerPanel.reset();

		filterUsersPanel.reset();
		loadTaskPanel.reset();
	}
}
