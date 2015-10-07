package fotostrana.ru.gui.TaskWindow.windowsAddingTasks;

import java.awt.Dimension;

import fotostrana.ru.gui.LoadTaskWindow.panels.FileSelectionPanel;
import fotostrana.ru.reports.sendMessages.Message;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.SendReports;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.TaskSendMessages;
import fotostrana.ru.users.User;

import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.FlowLayout;

import fotostrana.ru.gui.TaskWindow.SpinnerPanel;

public class PanelSendReports extends PanelCreateTask {
	private Dimension MAX_SIZE = new Dimension(1000, 30);
	FileSelectionPanel fileProfiles = new FileSelectionPanel("Файл с анкетой  ");
	FileSelectionPanel fileRecords = new FileSelectionPanel("Файл с отчетами");
	JCheckBox checkBox = new JCheckBox(
			"Сотри переписку через действия. До связи!");
	private static final long serialVersionUID = 1L;
	private final SpinnerPanel panelMinDelay = new SpinnerPanel(
			"Минимальная задержка, минут ");
	private final SpinnerPanel panelMaxDelay = new SpinnerPanel(
			"Максимальная задержка, минут");

	public PanelSendReports() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(Box.createVerticalStrut(3));
		fileProfiles.setMaximumSize(MAX_SIZE);
		fileRecords.setMaximumSize(MAX_SIZE);
		add(fileProfiles);
		add(Box.createVerticalStrut(5));
		add(fileRecords);

		Component verticalStrut = Box.createVerticalStrut(5);
		add(verticalStrut);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(panel);
		panel.add(checkBox);

		add(panelMinDelay);

		add(Box.createVerticalStrut(10));

		add(panelMaxDelay);

		add(Box.createVerticalStrut(10));
		reset();
		this.setPreferredSize(new Dimension(450, 158));
	}

	@Override
	public AbstractTask createTask() {
		SendReports sendReports = TaskManager.TASK_MANAGER.sendRepots;
		sendReports.loadFiles(fileProfiles.getFilePath(),
				fileRecords.getFilePath());
		if (sendReports.users.size() == 0) {
			errorMessage = "Нет анкет.";
			return null;
		}
		if (sendReports.messages.size() == 0) {
			errorMessage = "Нет данных для рассылки.";
			return null;
		}

		int minDelay = panelMinDelay.getValue() * 60;
		int maxDelay = panelMaxDelay.getValue() * 60;
		if (maxDelay < minDelay)
			maxDelay = minDelay + 1 * 60;

		User user = sendReports.users.first();
		for (Message message : sendReports.messages) {
			message.sender = user;
			if (checkBox.isSelected())
				message.message += '\n' + "Сотри переписку через действия. До связи!";
		}
		sendReports.isUsed = true;
		return new TaskSendMessages(user, sendReports.messages, minDelay,
				maxDelay);
	}

	@Override
	public String titleDialog() {
		return "Рассылка отчетов голосования";
	}

	@Override
	public void reset() {
		fileProfiles.setPath(TaskManager.TASK_MANAGER.sendRepots.fileWithUsers);
		fileRecords
				.setPath(TaskManager.TASK_MANAGER.sendRepots.fileWithRecords);
		checkBox.setSelected(false);
		panelMinDelay.initializationPanel("Минимальная задержка, минут ", 1, 1,
				1);
		panelMaxDelay.initializationPanel("Максимальная задержка, минут", 2, 2,
				1);
	}

}
