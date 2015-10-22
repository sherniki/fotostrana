package fotostrana.ru.gui.TaskWindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

import fotostrana.ru.Application;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.application.EventCloseApplication;
import fotostrana.ru.gui.StandbyWindow;
import fotostrana.ru.gui.TaskWindow.menu.MenuWindowTask;
import fotostrana.ru.gui.TaskWindow.taskTable.ContextMenuTableTask;
import fotostrana.ru.gui.TaskWindow.taskTable.TaskTable;
import fotostrana.ru.reports.StatusReportUserManager;
import fotostrana.ru.users.UserManager;

/**
 * Окно с заданиями
 * 
 */
public class WindowTasks extends JFrame implements EventListener {
	private String title = "Задания";
	private static final long serialVersionUID = -2833549254166935493L;
	public static int DELAY_UPDATE_TABLE = 1 * 1000;
	private JPanel contentPane;
	public TaskTable tableTask;
	private StandbyWindow standbyWindow;
	// private WindowTasks thisFrame;
	// private EventListener eventListener;

	private Timer timerUpdateTable;
	private ActionListener listenerUpdate = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			tableTask.updateUI();
			tableTask.repaint();

			StatusReportUserManager report = UserManager.USER_MANAGER
					.getStatusReport();
			String s = "НЕТ";
			if (report.countWorkingProfiles > 0)
				s = Integer.toString(report.countWorkingProfiles);
			String newTitle ="Version:"+Application.APPLICATION.getVersion()+"; "+ title + "  (" + s + " рабочих анкет, "
					+ report.countBannedProfiles + " забаненых)";
			setTitle(newTitle);
		}
	};

	/**
	 * Create the frame.
	 */
	public WindowTasks(final EventListener eventListener) {
		// thisFrame = this;
		// this.eventListener = eventListener;
		// setModal(true);
		MenuWindowTask menu = new MenuWindowTask(this);

		setJMenuBar(menu);
		standbyWindow = new StandbyWindow(this);
		// standbyWindow.setVisible(true);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 710, 514);
		setTitle(title);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setDescriptionWaitingText("Выполняется закрытие программы");
				eventListener.handleEvent(new EventCloseApplication());
				standbyWindow.setVisible(true);
			}
		});
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tableTask = new TaskTable();
		ContextMenuTableTask contextMenuTableTask = new ContextMenuTableTask(
				tableTask);
		contextMenuTableTask.addPopup(tableTask);
		add(new JScrollPane(tableTask), BorderLayout.CENTER);

		timerUpdateTable = new Timer(DELAY_UPDATE_TABLE, listenerUpdate);
		timerUpdateTable.start();

	}

	@Override
	public synchronized void handleEvent(Event event) {
	}

	public void showError(String message) {
		if (this.isVisible()) {
//		  Component parrent=standbyWindow.isVisible()?standbyWindow:this;
		  Component parrent=this;
			JOptionPane.showMessageDialog(parrent, message, "Ошибка",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Позатать окно ожидания
	 * 
	 * @param text
	 *            пояснения к ожиданию
	 */
	public void showStandbyWindow() {
		standbyWindow.setLocationRelativeTo(this);
		standbyWindow.showWindow();
	}

	public void setDescriptionWaitingText(String descriptionWaiting) {
		standbyWindow.setText(descriptionWaiting);
	}

	/**
	 * Скрывает окно ожидания
	 */
	public void closeStandbyWindow() {
	  while (!standbyWindow.isVisible()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    }
		standbyWindow.close();
	}
}
