package fotostrana.ru.gui.LoadTaskWindow.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fotostrana.ru.gui.LoadTaskWindow.panels.FileSelectionPanel;
import fotostrana.ru.task.TaskManager;
import fotostrana.ru.users.User;
import fotostrana.ru.users.UserManager;
import fotostrana.ru.users.filtersUsers.FilterByProfiles;

import javax.swing.JCheckBox;

/**
 * Панель выбора анкет пользователей для выполнения задания
 * 
 */
public class FilterUsersPanel extends JPanel {
	/**
	 * Фильтр по анектным данным
	 */
	public static FilterByProfiles filterUsers = new FilterByProfiles("");

	public Set<User> selectionUsers = new TreeSet<User>();
	private static final long serialVersionUID = 4106915413924827810L;
	public JRadioButton allUsers = new JRadioButton("Все", true);
	public JRadioButton downloadFromFile = new JRadioButton(
			"Загрузить из файла", false);
	public JRadioButton setManually = new JRadioButton("Задать вручную", false);

	private FileSelectionPanel fileSelection;
	private UserManualSelectionPanel manualySelectionPanel = new UserManualSelectionPanel(
			this);
	private ActionListener actionListenerButtonLoadFile;
	private UsersTable usersTable;
	private JPanel tablePanel = new JPanel(new BorderLayout());
	/**
	 * Флаг показывает загружался ли файл
	 */
	public boolean isDownloadFromFile = false;
	private JCheckBox onlySelectedProfiles = new JCheckBox("Только эти анкеты");

	/**
	 * Фильтрует пользователей по анкетным данным
	 * 
	 * @param value
	 * @return
	 */
	public static List<User> getUser(String value) {
		filterUsers.setValue(value);
		List<User> users = UserManager.USER_MANAGER.getUsers(filterUsers);
		if ((users.size() == 0) && (value.indexOf("fotostrana.ru") > -1)) {
			users.add(new User(value));
		}
		return users;

	}

	public void update() {
		usersTable.updateUI();
		tablePanel.updateUI();
	};

	/**
	 * Очищает все данные на панеле
	 */
	public void reset() {
		selectionUsers = new TreeSet<User>();
		usersTable.setUsers(selectionUsers);
		allUsers.setSelected(true);
		manualySelectionPanel.textField.setText("");
		isDownloadFromFile = false;

		fileSelection.setVisible(false);
		fileSelection.setPath(TaskManager.TASK_MANAGER.fileWithFilterProfiles);
		manualySelectionPanel.setVisible(false);
		tablePanel.setVisible(false);
		onlySelectedProfiles.setSelected(true);
	}

	public FilterUsersPanel() {

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new TitledBorder(
				"Анкеты пользователей для выполнения заданий"));
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(allUsers);
		buttonGroup.add(downloadFromFile);
		buttonGroup.add(setManually);
		radioPanel.add(allUsers);
		radioPanel.add(downloadFromFile);
		radioPanel.add(setManually);

		actionListenerButtonLoadFile = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadUsersFromFile();
			}
		};

		usersTable = new UsersTable(selectionUsers);
		JScrollPane scrollPane = new JScrollPane(usersTable);
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		fileSelection = new FileSelectionPanel("Загрузить",
				actionListenerButtonLoadFile);
		fileSelection.actionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		fileSelection.setMaximumSize(new Dimension(5000, 30));
		fileSelection.setPath(TaskManager.TASK_MANAGER.fileWithFilterProfiles);
		manualySelectionPanel.setMaximumSize(new Dimension(5000, 30));

		onlySelectedProfiles.setSelected(true);
		tablePanel.add(onlySelectedProfiles, BorderLayout.NORTH);

		allUsers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileSelection.setVisible(false);
				manualySelectionPanel.setVisible(false);
				tablePanel.setVisible(false);
			}
		});

		downloadFromFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileSelection.setVisible(true);
				manualySelectionPanel.setVisible(false);
				tablePanel.setVisible(true);
			}
		});
		setManually.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileSelection.setVisible(false);
				manualySelectionPanel.setVisible(true);
				tablePanel.setVisible(true);
			}
		});

		// reset();

		this.add(radioPanel);
		this.add(fileSelection);
		this.add(Box.createVerticalStrut(5));
		this.add(manualySelectionPanel);
		this.add(Box.createVerticalStrut(10));
		this.add(tablePanel);

	}

	public void loadUsersFromFile() {
		String file = fileSelection.getFilePath();
		if (file != null) {
			Scanner in = null;
			try {
				in = new Scanner(new File(file), "UTF-8");
				int countReadLine = 0;
				int countLoadUsers = 0;
				while (in.hasNextLine()) {
					String line = in.nextLine();
					countReadLine++;
					List<User> res = getUser(line);
					for (User user : res) {
						if (selectionUsers.add(user)) {
							countLoadUsers++;
						}
					}
				}
				isDownloadFromFile = true;
				if (countReadLine == 0) {
					JOptionPane.showMessageDialog(FilterUsersPanel.this,
							"Файл пуст.");
				}
				if (countLoadUsers == 0) {
					JOptionPane.showMessageDialog(FilterUsersPanel.this,
							"Недобалено ни одного пользователя.");
				} else {
					JOptionPane.showMessageDialog(FilterUsersPanel.this,
							"Добалено " + countLoadUsers + " анкет.");
				}
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(FilterUsersPanel.this,
						"Файл не найден.");
			} finally {
				if (in != null)
					in.close();
			}
		} else {
			JOptionPane.showMessageDialog(FilterUsersPanel.this,
					"Файл не задан");
		}
		update();
	}

	public boolean onlySelectedProfiles() {
		return onlySelectedProfiles.isSelected();
	}
}

/**
 * Панель ручного выбора анкет пользователей
 * 
 */
class UserManualSelectionPanel extends JPanel {
	private UserManualSelectionPanel thisPanel;
	private static final long serialVersionUID = 1L;
	public JTextField textField = new JTextField();
	private JButton addButton = new JButton("Добавить");

	/**
	 * @param users
	 *            список пользователей к которому будут добавляться новые
	 *            пользователи
	 */
	public UserManualSelectionPanel(final FilterUsersPanel filterPanel) {
		thisPanel = this;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel label = new JLabel("Данные пользователя");

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<User> result = FilterUsersPanel.getUser(textField
						.getText());

				if (result.size() == 0) {
					JOptionPane.showMessageDialog(thisPanel,
							"Пользовать не найден.");
				} else {
					boolean showDialog = false;
					for (User user : result) {
						if (!filterPanel.selectionUsers.add(user)) {
							if (!showDialog) {
								JOptionPane.showMessageDialog(thisPanel,
										"Некоторые анкеты уже есть.");
								showDialog = true;
							}
						}
					}
				}
				filterPanel.update();
			}
		});

		this.add(label);
		this.add(Box.createHorizontalStrut(10));
		this.add(textField);
		this.add(Box.createHorizontalStrut(10));
		this.add(addButton);
	}
}
