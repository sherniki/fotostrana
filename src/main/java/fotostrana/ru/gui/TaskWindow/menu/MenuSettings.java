package fotostrana.ru.gui.TaskWindow.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fotostrana.ru.Application;
import fotostrana.ru.gui.downloadUsersDialog.DownloadUsersDialog;
import fotostrana.ru.gui.loadProxyWindow.DialogDownloadProxyFromFile;
import fotostrana.ru.gui.logWindow.LogWindow;
import fotostrana.ru.network.proxy.ProxyManager;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.ProcessingBannedProfiles;

/**
 * Меню параметров программы
 * 
 */
public class MenuSettings extends JMenu {
	private static final long serialVersionUID = 1L;

	private JMenuItem itemLog = new JMenuItem("Состояние программы");
	private JMenuItem itemUpdateProxy = new JMenuItem("Обновить прокси");
	private JMenuItem itemLoadProxyFromFile = new JMenuItem(
			"Загрузить прокси из файла");
	private JMenuItem itemLoadUsersFromFile = new JMenuItem(
			"Загрузить анкеты из файла");
	private JMenuItem itemProcessingBannedProfiles = new JMenuItem(
			"Обьединить забаненые анкеты");
	private JCheckBoxMenuItem itemIsWithoutSound = new JCheckBoxMenuItem(
			"Без звука", Application.APPLICATION.isWithoutSound());

	private Action actionShowLog;
	private Action actionUpdateProxy;
	private Action actionLoadProxyFromFile;
	private Action actionLoadUsersFromFile;
	private Action actionProcessingProfiles;
	private Action actionWithoutSound;

	private LogWindow logWindow = new LogWindow();;
	private DialogDownloadProxyFromFile loadProxyDialog;
	private DownloadUsersDialog loadProfilesDialog;

	public MenuSettings() {
		this.setText("Параметры");

		actionShowLog = new AbstractAction("ShowLog") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				logWindow.setVisible(true);
			}
		};

		actionUpdateProxy = new AbstractAction("UpdateProxy") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProxyManager.PROXY_MANAGER.updateProxy();
			}
		};

		actionLoadProxyFromFile = new AbstractAction("LoadProxyFromFile") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (loadProxyDialog == null) {
					loadProxyDialog = new DialogDownloadProxyFromFile();
				}
				loadProxyDialog.setVisible(true);
			}
		};

		actionLoadUsersFromFile = new AbstractAction("LoadUsersFromFile") {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (loadProfilesDialog == null) {
					loadProfilesDialog = new DownloadUsersDialog();
					// String defaultPath = System.getProperty("user.home")
					// + "\\desktop\\лапка\\Reports\\Results\\"
					// + GregorianCalendar.getInstance().getWeekYear();
				}
				loadProfilesDialog.setVisible(true);
			}
		};

		actionProcessingProfiles = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProcessingBannedProfiles task = new ProcessingBannedProfiles();
				TaskManager.TASK_MANAGER.executeTask(task);
			}
		};

		actionWithoutSound = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Application.APPLICATION.setWithoutSound(itemIsWithoutSound
						.isSelected());
			}
		};

		itemLog.addActionListener(actionShowLog);
		itemUpdateProxy.addActionListener(actionUpdateProxy);
		itemLoadProxyFromFile.addActionListener(actionLoadProxyFromFile);
		itemLoadUsersFromFile.addActionListener(actionLoadUsersFromFile);
		itemProcessingBannedProfiles
				.addActionListener(actionProcessingProfiles);
		itemIsWithoutSound.addActionListener(actionWithoutSound);

		this.add(itemLog);
		this.add(itemUpdateProxy);
		this.add(itemLoadProxyFromFile);
		this.add(itemLoadUsersFromFile);
		this.add(itemProcessingBannedProfiles);
		this.add(itemIsWithoutSound);
	}

}
