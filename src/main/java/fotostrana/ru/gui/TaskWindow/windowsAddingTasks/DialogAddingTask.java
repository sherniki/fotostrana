package fotostrana.ru.gui.TaskWindow.windowsAddingTasks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fotostrana.ru.task.AbstractTask;
import fotostrana.ru.task.TaskManager;

/**
 * Диалог добавления нового задания
 * 
 */
public class DialogAddingTask extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel buttonPane = new JPanel();

	/**
	 * Панель которая релизует создание задания
	 */
	private PanelCreateTask panelCreateTask;

	/**
	 * Create the dialog.
	 */
	public DialogAddingTask() {
		setBounds(100, 100, 450, 300);
		setModal(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DialogAddingTask.this.close();
			}
		});
		buttonPane.setMaximumSize(new Dimension(5000, 40));
		buttonPane.setPreferredSize(new Dimension(5000, 40));
		getContentPane().setLayout(new BorderLayout());

		{
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Выполнить");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (panelCreateTask != null) {
							AbstractTask task = panelCreateTask.createTask();
							if (task == null) {
								showError();
								return;
							}
							TaskManager.TASK_MANAGER.executeTask(task);
						}
						DialogAddingTask.this.close();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Отмена");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						DialogAddingTask.this.close();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Задает новую панель заданий
	 * 
	 * @param panelCreateTask
	 *            новая панель
	 */
	public void setPanelCreateTask(PanelCreateTask panelCreateTask) {
		if (panelCreateTask != null) {
			reset();
			this.panelCreateTask = panelCreateTask;
			this.setTitle(panelCreateTask.titleDialog());
			this.getContentPane()
					.add(this.panelCreateTask, BorderLayout.CENTER);
			int newHeight = panelCreateTask.getPreferredSize().height
					+ buttonPane.getMaximumSize().height + 50;
			this.setSize(new Dimension(450, newHeight));
		}
	}

	/**
	 * Очищает окно
	 */
	public void reset() {
		if (panelCreateTask != null) {
			getContentPane().remove(panelCreateTask);
			panelCreateTask.reset();
		}
		this.panelCreateTask = null;
		this.setTitle("");
	}

	/**
	 * Закрывает окно
	 */
	public void close() {
		reset();
		setVisible(false);
	}

	/**
	 * Выводит окно с ошибкой, текст берется из панели с заданием
	 */
	public void showError() {
		JOptionPane.showMessageDialog(this, panelCreateTask.errorMessage(),
				"Ошибка", JOptionPane.ERROR_MESSAGE);
	}

}
