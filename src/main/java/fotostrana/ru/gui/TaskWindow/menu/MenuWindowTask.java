package fotostrana.ru.gui.TaskWindow.menu;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fotostrana.ru.Application;
import fotostrana.ru.gui.LoadTaskWindow.LoadTaskWindow;
import fotostrana.ru.gui.TaskWindow.WindowTasks;
import fotostrana.ru.gui.TaskWindow.windowsAddingTasks.DialogAddingTask;
import fotostrana.ru.gui.TaskWindow.windowsAddingTasks.PanelSendReports;
import fotostrana.ru.gui.TaskWindow.windowsAddingTasks.PanelTeamVoting;
import fotostrana.ru.gui.TaskWindow.windowsAddingTasks.SpammingPanel;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.TaskCheckBanned;
import fotostrana.ru.task.tasks.TaskUpdateProfiles;
import fotostrana.ru.task.tasks.tournament.SignIntoTournament;

/**
 * Главное меню окна с заданиями
 * 
 */
public class MenuWindowTask extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private JMenu menuTasks = new JMenu("Задания");

	private JMenuItem itemLoadTasks = new JMenuItem("Загрузить");
	private JMenuItem itemUpdateQuestionnaires = new JMenuItem(
			"Обновить анкеты");
	private JMenuItem itemSingIntoTournament = new JMenuItem("Открывать турнир");
	private JMenuItem itemNewTaskTeamVoting = new JMenuItem(
			"Голосовать за команду");
	private JMenuItem itemSpamming = new JMenuItem("Рассылка спама");
	private JMenuItem itemCheckBanned = new JMenuItem("Проверить на бан");
	private JMenuItem itemReports = new JMenuItem("Отчитывалка");

	private Action actionLoadTasks;
	private Action actionUpdateQuestionnaires;
	private Action actionSignInTournament;
	private Action actionTaskTeamVoting;
	private Action actionSpamming;
	private Action actionCheckBanned;
	private Action actionReports;

	private LoadTaskWindow loadTaskWindow;
	private DialogAddingTask dialogAddingTask = new DialogAddingTask();
	private PanelTeamVoting panelTeamVoting;
	private SpammingPanel spammingPanel;
	private PanelSendReports reportsPanel;

	// private PanelCheckBanned checkBannedPanel = new PanelCheckBanned();

	public MenuWindowTask(final WindowTasks windowTasks) {
		super();

		setLayout(new FlowLayout(FlowLayout.LEFT));

		actionLoadTasks = new AbstractAction("LoadTasks") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (loadTaskWindow == null)
					loadTaskWindow = new LoadTaskWindow();
				loadTaskWindow.reset();
				loadTaskWindow.setVisible(true);
				windowTasks.tableTask.update();
			}
		};
		actionUpdateQuestionnaires = new AbstractAction("UpdateQuestionnaires") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (loadTaskWindow == null)
					loadTaskWindow = new LoadTaskWindow();
				List<AbstractTask> list = new ArrayList<AbstractTask>();
				list.add(new TaskUpdateProfiles());
				loadTaskWindow.reset();
				loadTaskWindow.setTasks(list);
				loadTaskWindow.setVisible(true);
				windowTasks.tableTask.update();
			}
		};

		actionSignInTournament = new AbstractAction("SignInTournament") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				TaskManager.TASK_MANAGER.executeTask(new SignIntoTournament());
				windowTasks.tableTask.update();
			}
		};

		actionTaskTeamVoting = new AbstractAction("TaskTeamVoting") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (panelTeamVoting == null)
					panelTeamVoting = new PanelTeamVoting();
				dialogAddingTask.setPanelCreateTask(panelTeamVoting);
				dialogAddingTask.setVisible(true);
			}
		};

		actionSpamming = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TaskManager.TASK_MANAGER.isSendingSpam) {
					JOptionPane
							.showMessageDialog(
									windowTasks,
									"Спам уже рассылался.Для следующей рассылки перезапустите программу.",
									"Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						TaskManager.TASK_MANAGER.loadSpamFiles();
					}
				});
				t.start();

				if (spammingPanel == null)
					spammingPanel = new SpammingPanel();
				dialogAddingTask.setPanelCreateTask(spammingPanel);
				dialogAddingTask.setVisible(true);
			}
		};

		actionCheckBanned = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				TaskManager.TASK_MANAGER.executeTask(new TaskCheckBanned());
			}
		};

		actionReports = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TaskManager.TASK_MANAGER.sendRepots.isUsed) {
					JOptionPane
							.showMessageDialog(
									windowTasks,
									"Отчеты уже рассылались.Для следующей рассылки перезапустите программу.",
									"Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (reportsPanel == null)
					reportsPanel = new PanelSendReports();
				dialogAddingTask.setPanelCreateTask(reportsPanel);
				dialogAddingTask.setVisible(true);
			}
		};

		itemLoadTasks.addActionListener(actionLoadTasks);
		itemUpdateQuestionnaires.addActionListener(actionUpdateQuestionnaires);
		itemSingIntoTournament.addActionListener(actionSignInTournament);
		itemNewTaskTeamVoting.addActionListener(actionTaskTeamVoting);
		itemSpamming.addActionListener(actionSpamming);
		itemCheckBanned.addActionListener(actionCheckBanned);
		itemReports.addActionListener(actionReports);

		menuTasks.add(itemLoadTasks);
		menuTasks.add(itemUpdateQuestionnaires);
		menuTasks.add(itemSpamming);
		menuTasks.add(itemReports);
		menuTasks.add(itemCheckBanned);
		// menuTasks.add(itemNewTaskTeamVoting);
		// menuTasks.add(itemSingIntoTournament);

		if (Application.currentOS() != Application.WINDOWS_OS) {
			itemSpamming.setEnabled(false);
			itemReports.setEnabled(false);
			String toolTipText = "Доступно только в Windows";
			itemReports.setToolTipText(toolTipText);
			itemSpamming.setToolTipText(toolTipText);
		}

		this.add(menuTasks);
		this.add(new MenuSettings());
	}
}
