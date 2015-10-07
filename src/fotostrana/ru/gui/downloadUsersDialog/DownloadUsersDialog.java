package fotostrana.ru.gui.downloadUsersDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import configuration.ApplicationConfiguration;
import fotostrana.ru.Application;
import fotostrana.ru.gui.LoadTaskWindow.panels.FileSelectionPanel;
import fotostrana.ru.log.Log;
import fotostrana.ru.users.UserManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class DownloadUsersDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JRadioButton cleanRadioButton = new JRadioButton("Очищать файл");
	private JRadioButton notCleanRadioButton = new JRadioButton(
			"Не очищать файл");
	private FileSelectionPanel fileSelectionPanel = new FileSelectionPanel();
	private final Component verticalStrut = Box.createVerticalStrut(5);
	private final Component verticalStrut_1 = Box.createVerticalStrut(5);

	/**
	 * Create the dialog.
	 */
	public DownloadUsersDialog() {
		setModal(true);
		setBounds(100, 100, 450, 150);
		setResizable(false);
		setTitle("Загрузка анкет");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setMaximumSize(new Dimension(5000, 30));

		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPanel.add(panel);

		contentPanel.add(verticalStrut);
		fileSelectionPanel.setMaximumSize(new Dimension(5000, 30));
		fileSelectionPanel.setMultiSelectionEnabled(false);
		fileSelectionPanel.setFileSelectionMode(JFileChooser.FILES_ONLY);
		contentPanel.add(fileSelectionPanel);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(cleanRadioButton);
		buttonGroup.add(notCleanRadioButton);
		cleanRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClean();
			}
		});

		cleanRadioButton
				.setToolTipText("Можно выбрать только один текствоый файл с анкетами, который после загрузки будет очищен");
		cleanRadioButton.setSelected(true);
		panel.add(cleanRadioButton);
		notCleanRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNotClean();
			}
		});

		notCleanRadioButton
				.setToolTipText("Можно выбирать несколько файлов и папок с анкетами, после загрузки очищаться не будут ");
		panel.add(notCleanRadioButton);

		contentPanel.add(verticalStrut_1);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Загрузить");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String file = fileSelectionPanel.getFilePath();
						if (file != null) {
							DownloadUsersDialog.this.setVisible(false);

							String[] files = file.split("[;]");
							int countProfiles = 0;
							for (String filePath : files) {
								countProfiles += UserManager.USER_MANAGER
										.readListUsersWithFile(filePath, true);
							}
							
							if (cleanRadioButton.isSelected()) {
								File f = new File(file);
								if (f.delete())
									try {
										f.createNewFile();
									} catch (IOException e1) {
										Log.LOGGING.printStackTraceException(
												e1, Log.GROUP_FILE);
									}
							}
							if (countProfiles == 0) {
								Application.APPLICATION
										.showError("Нет анкет для загрузки.");
							}
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	public void setDefaultPath(String defaultPath) {
		if (defaultPath != null) {
			fileSelectionPanel.setPath(defaultPath);
			actionClean();
			if (defaultPath.indexOf(";") != -1) {
				actionNotClean();
			} else {
				File f = new File(defaultPath);
				if (f.isDirectory())
					actionNotClean();
			}
		}
	}

	private void actionNotClean() {
		notCleanRadioButton.setSelected(true);
		fileSelectionPanel
				.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileSelectionPanel.setMultiSelectionEnabled(true);
	}

	private void actionClean() {
		cleanRadioButton.setSelected(true);
		fileSelectionPanel.setMultiSelectionEnabled(false);
		fileSelectionPanel.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			String defaultPath = ApplicationConfiguration.INSTANCE
					.getValue("configuration.Users.FolderWithTextProfiles");
			setDefaultPath(defaultPath);
		} else {
			ApplicationConfiguration.INSTANCE.setValue(
					"configuration.Users.FolderWithTextProfiles",
					fileSelectionPanel.getFilePath());
		}
		super.setVisible(b);
	}

}
