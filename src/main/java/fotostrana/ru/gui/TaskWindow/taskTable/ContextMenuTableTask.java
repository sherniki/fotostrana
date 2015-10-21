package fotostrana.ru.gui.TaskWindow.taskTable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import fotostrana.ru.Application;
import fotostrana.ru.gui.TaskWindow.DialogEditingScheduler;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.Scheduler;
import fotostrana.ru.task.schedulers.SchedulerWithLimitedThreads;

/**
 * Контексное меню таблицы с заданиями
 * 
 */
public class ContextMenuTableTask extends JPopupMenu implements
		PopupMenuListener {
	private static final long serialVersionUID = 7488885967492503955L;
	private JMenuItem itemStart = new JMenuItem("Запустить");
	private JMenuItem itemPause = new JMenuItem("Пауза");
	private JMenuItem itemStop = new JMenuItem("Отменить");
	private JMenuItem itemSetScheduler = new JMenuItem("Количество потоков");
	private JMenuItem itemShowBrowser = new JMenuItem("Просмотреть в браузере");
	private DialogEditingScheduler dialogEditingScheduler = new DialogEditingScheduler();

	private TaskTable taskTable;

	public ContextMenuTableTask(final TaskTable taskTable) {
		this.taskTable = taskTable;
		this.addPopupMenuListener(this);
		Action startAction = new AbstractAction("Запустить") {
			private static final long serialVersionUID = -6308373688697442084L;

			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractTask selectedTask = taskTable.getSelectedTask();
				if (selectedTask != null) {
					switch (selectedTask.getState()) {
					case Scheduler.STATE_NOT_STARTED:
						selectedTask.start();
						break;
					case Scheduler.STATE_PAUSE:
						selectedTask.continueExecution();
						break;
					default:
						break;
					}
					taskTable.update();
				}
			}
		};

		Action pauseAction = new AbstractAction("Пауза") {
			private static final long serialVersionUID = -6308373688697442084L;

			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractTask selectedTask = taskTable.getSelectedTask();
				if (selectedTask != null) {
					selectedTask.pauseExecution();
					taskTable.update();
				}
			}
		};

		Action stopAction = new AbstractAction("Отменить") {
			private static final long serialVersionUID = -6308373688697442084L;

			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractTask selectedTask = taskTable.getSelectedTask();
				if (selectedTask != null) {
					selectedTask.stop();
					taskTable.update();
				}
			}
		};

		Action setSchedulerAction = new AbstractAction("Количество потоков") {
			private static final long serialVersionUID = -6308373688697442084L;

			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractTask selectedTask = taskTable.getSelectedTask();
				if (selectedTask != null) {
					dialogEditingScheduler
							.setScheduler((SchedulerWithLimitedThreads) selectedTask
									.getScheduler());
					dialogEditingScheduler.setVisible(true);
				}
			}
		};

		Action showBrowserAction = new AbstractAction("Просмотреть в браузере") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				AbstractTask selectedTask = taskTable.getSelectedTask();
				if (selectedTask != null)
					Application.APPLICATION.browseURL(selectedTask
							.getTargetUrl());
			}
		};

		itemStart.addActionListener(startAction);
		itemPause.addActionListener(pauseAction);
		itemStop.addActionListener(stopAction);
		itemSetScheduler.addActionListener(setSchedulerAction);
		itemShowBrowser.addActionListener(showBrowserAction);
		this.add(itemStart);
		this.add(itemPause);
		this.add(itemStop);
//		this.add(itemSetScheduler);
		this.add(itemShowBrowser);
	}

	public void addPopup(Component component) {
		final JPopupMenu popup = this;
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		itemStart.setEnabled(false);
		itemPause.setEnabled(false);
		itemStop.setEnabled(false);
		itemSetScheduler.setEnabled(false);
		itemShowBrowser.setEnabled(true);
		AbstractTask selectedTask = taskTable.getSelectedTask();
		if (selectedTask != null) {
			switch (selectedTask.getState()) {
			case Scheduler.STATE_NOT_STARTED:
			case Scheduler.STATE_PAUSE:
				itemStart.setEnabled(true);
				itemStop.setEnabled(true);
				itemSetScheduler.setEnabled(true);
				break;
			case Scheduler.STATE_RUN:
				itemPause.setEnabled(true);
				itemStop.setEnabled(true);
				itemSetScheduler.setEnabled(true);
				break;
			default:
				break;
			}
		}

	}
}
