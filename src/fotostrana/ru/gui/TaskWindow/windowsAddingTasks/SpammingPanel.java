package fotostrana.ru.gui.TaskWindow.windowsAddingTasks;

import fotostrana.ru.gui.TaskWindow.SpinnerPanel;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.task.tasks.TaskSendingSpam;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.SwingConstants;

/**
 * Панель создание задания рассылки спама
 * 
 */
public class SpammingPanel extends PanelCreateTask {
	private SpinnerPanel panelMinDelay = new SpinnerPanel("");
	private SpinnerPanel panelMaxDelay = new SpinnerPanel();
	private SpinnerPanel panelMaxCountMessage = new SpinnerPanel();
	private JCheckBox nomCITY = new JCheckBox(Nomination.CITY.name);
	private JCheckBox nomCHARM = new JCheckBox(Nomination.CHARM.name);
	private JCheckBox nomSYMPATHY = new JCheckBox(Nomination.SYMPATHY.name);
	private JCheckBox nomSUPERSTAR = new JCheckBox(Nomination.SUPERSTAR.name);
	private JCheckBox nomTOURNAMENT = new JCheckBox(Nomination.TOURNAMENT.name);
	private JCheckBox nomRATING = new JCheckBox(Nomination.RATING.name);
	private final JCheckBox checkBoxOnlineOnly = new JCheckBox(
			"Только тем кто online", false);

	public SpammingPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBorder(new TitledBorder("Номинации голосования"));

		add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		panel.add(nomCITY);
		panel.add(nomCHARM);
		panel.add(nomSYMPATHY);
		panel.add(nomSUPERSTAR);
		panel.add(nomTOURNAMENT);
		panel.add(nomRATING);

		Component verticalStrut_2 = Box.createVerticalStrut(5);
		add(verticalStrut_2);

		JPanel panel_1 = new JPanel();
		panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_1.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_1.setBorder(new TitledBorder("Параметры отправки сообщений"));
		add(panel_1);
		JPanel panel_2 = new JPanel();
		Component verticalStrut_5 = Box.createVerticalStrut(5);
		panel_2.setMaximumSize(new Dimension(5000,30));
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		panel_1.add(verticalStrut_5);
		panel_2.setAlignmentY(Component.BOTTOM_ALIGNMENT);

		panel_1.add(panel_2);
		FlowLayout fl_panel_2 = new FlowLayout(FlowLayout.LEFT, 10, 0);
		panel_2.setLayout(fl_panel_2);
		checkBoxOnlineOnly.setHorizontalAlignment(SwingConstants.LEFT);
		checkBoxOnlineOnly.setVerticalAlignment(SwingConstants.BOTTOM);
		panel_2.add(checkBoxOnlineOnly);
		checkBoxOnlineOnly.setAlignmentY(Component.TOP_ALIGNMENT);

		Component verticalStrut_4 = Box.createVerticalStrut(10);
		panel_1.add(verticalStrut_4);
		panel_1.add(panelMinDelay);

		Component verticalStrut = Box.createVerticalStrut(10);
		panel_1.add(verticalStrut);
		panel_1.add(panelMaxDelay);

		Component verticalStrut_1 = Box.createVerticalStrut(10);
		panel_1.add(verticalStrut_1);
		panel_1.add(panelMaxCountMessage);

		Component verticalStrut_3 = Box.createVerticalStrut(10);
		panel_1.add(verticalStrut_3);

		this.setPreferredSize(new Dimension(450, 243));
		reset();
	}

	private static final long serialVersionUID = 1L;

	@Override
	public AbstractTask createTask() {
		int minDelay = panelMinDelay.getValue() * 60;
		int maxDelay = panelMaxDelay.getValue() * 60;
		if (maxDelay < minDelay)
			maxDelay = minDelay + 1 * 60;
		int maxCountMessages = panelMaxCountMessage.getValue();
		List<Nomination> nominations = new ArrayList<Nomination>();
		if (nomCITY.isSelected())
			nominations.add(Nomination.CITY);
		if (nomCHARM.isSelected())
			nominations.add(Nomination.CHARM);
		if (nomSUPERSTAR.isSelected())
			nominations.add(Nomination.SUPERSTAR);
		if (nomSYMPATHY.isSelected())
			nominations.add(Nomination.SYMPATHY);
		if (nomTOURNAMENT.isSelected())
			nominations.add(Nomination.TOURNAMENT);
		if (nomRATING.isSelected())
			nominations.add(Nomination.RATING);
		if (nominations.size() == 0) {
			errorMessage = "Невыбраны номинации.";
			return null;
		}
		TaskManager.TASK_MANAGER.isSendingSpam = true;
		return new TaskSendingSpam(TaskManager.TASK_MANAGER.spam, nominations,
				maxCountMessages, checkBoxOnlineOnly.isSelected(), minDelay,
				maxDelay);
	}

	@Override
	public String titleDialog() {
		return "Рассылка спама";
	}

	@Override
	public void reset() {
		panelMinDelay.initializationPanel(
				"Минимальное время ожидание (в минутах)", 1, 1, 1);
		panelMaxDelay.initializationPanel(
				"Максимальное время ожидание (в минутах)", 5, 2, 1);
		panelMaxCountMessage.initializationPanel(
				"Маскимальное количество сообщений с одной анкеты", 10, 1, 1);
		nomCITY.setSelected(true);
		nomCHARM.setSelected(true);
		nomSYMPATHY.setSelected(true);
		nomSUPERSTAR.setSelected(true);
		nomTOURNAMENT.setSelected(true);
		nomRATING.setSelected(true);
		checkBoxOnlineOnly.setSelected(false);
		;
	}

}
