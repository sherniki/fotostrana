package fotostrana.ru.gui.TaskWindow.windowsAddingTasks;

import fotostrana.ru.network.requests.fotostrana.tournament.RequestCheckTournament;
import fotostrana.ru.task.Task;
import fotostrana.ru.task.tasks.tournament.TaskTeamVoting;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.FilterByProfiles;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.Component;
import java.util.List;

import javax.swing.Box;

/**
 * Панель создания задания голосования за команду в турнире
 * 
 */
public class PanelTeamVoting extends PanelCreateTask {
	public static Integer DEFAULT_SPINNER_VALUE = new Integer(100000);
	private static final long serialVersionUID = 1L;
	private JSpinner spinnerCountVotes = new JSpinner(new SpinnerNumberModel(
			new Integer(100000), new Integer(1), null, new Integer(1000)));
	private JComboBox<String> comboBoxColor = new JComboBox<String>(
			RequestCheckTournament.COLORS_RU);
	private JTextField textTargetId = new JTextField();

	public PanelTeamVoting() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel labelId = new JLabel("За команду пользователя (ID) :");
		this.setMaximumSize(new Dimension(1000, 100));
		JPanel line1 = new JPanel();
		line1.setMaximumSize(new Dimension(1000, 20));
		line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));

		Component horizontalStrut_2 = Box.createHorizontalStrut(5);
		line1.add(horizontalStrut_2);
		JLabel labelColor = new JLabel("Цвет команды :");
		line1.add(labelColor);

		Component horizontalStrut_5 = Box.createHorizontalStrut(100);
		line1.add(horizontalStrut_5);
		comboBoxColor.setAlignmentX(Component.RIGHT_ALIGNMENT);
		line1.add(comboBoxColor);

		JPanel line2 = new JPanel();
		line2.setMaximumSize(new Dimension(1000, 20));
		line2.setLayout(new BoxLayout(line2, BoxLayout.X_AXIS));

		Component horizontalStrut_4 = Box.createHorizontalStrut(5);
		line2.add(horizontalStrut_4);
		line2.setToolTipText("Голоса будут начисляться команде  к которой принадлежит заданый пользователь");

		line2.add(labelId);

		Component horizontalStrut = Box.createHorizontalStrut(10);
		line2.add(horizontalStrut);
		line2.add(textTargetId);

		JPanel line3 = new JPanel();
		line3.setMaximumSize(new Dimension(1000, 20));
		line3.setLayout(new BoxLayout(line3, BoxLayout.X_AXIS));

		Component horizontalStrut_3 = Box.createHorizontalStrut(5);
		line3.add(horizontalStrut_3);

		JLabel labelCountVotes = new JLabel("Количество голосов :");
		line3.add(labelCountVotes);

		Component horizontalStrut_1 = Box.createHorizontalStrut(65);
		line3.add(horizontalStrut_1);
		line3.add(spinnerCountVotes);

		this.add(line1);

		Component horizontalStrut_6 = Box.createHorizontalStrut(5);
		line1.add(horizontalStrut_6);

		Component verticalStrut = Box.createVerticalStrut(10);
		add(verticalStrut);
		this.add(line2);

		Component horizontalStrut_7 = Box.createHorizontalStrut(5);
		line2.add(horizontalStrut_7);

		Component verticalStrut_1 = Box.createVerticalStrut(10);
		add(verticalStrut_1);
		this.add(line3);

		Component horizontalStrut_8 = Box.createHorizontalStrut(5);
		line3.add(horizontalStrut_8);
	}

	@Override
	public Task createTask() {
		String color = comboBoxColor.getSelectedItem().toString();
		String targetId = textTargetId.getText();
		if (targetId.length() > 0) {
			try {
				int intId = Integer.parseInt(targetId);
				if ((intId < 1) || (intId > 300000000)) {
					errorMessage = "Неверно введен Id.";
					return null;
				}
			} catch (Exception e) {
				errorMessage = "Неверно введен Id.";
				return null;
			}
		} else {
			if (targetId.length() < 5) {
				List<User> users = UserManager.USER_MANAGER
						.getUsers(new FilterByProfiles(color));
				if (users.size() > 0) {
					// targetId = users.first().id;
				} else {
					errorMessage = "Невозможно выполнить задание." + '\n'
							+ "Нет анкет из команды " + color.toUpperCase()
							+ "." + '\n'
							+ "Введите id любого человека из команды "
							+ color.toUpperCase() + ".";
					return null;
				}
			}
		}
		try {
			int countVotes = (Integer) spinnerCountVotes.getValue();
			return new TaskTeamVoting(color, countVotes, targetId);
		} catch (Exception e) {
			errorMessage = "Неверное количество голосов.";
			return null;
		}
	}

	@Override
	public String titleDialog() {
		return "Голосовать за выбраную команду";
	}

	@Override
	public void reset() {
		spinnerCountVotes.setValue(DEFAULT_SPINNER_VALUE);
		textTargetId.setText("");
	}
}
