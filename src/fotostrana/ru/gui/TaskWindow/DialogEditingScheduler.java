package fotostrana.ru.gui.TaskWindow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fotostrana.ru.task.schedulers.SchedulerWithLimitedThreads;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;

/**
 * Диалог редактирования планировщика выолнения задания
 * 
 */
public class DialogEditingScheduler extends JDialog {
	private static final long serialVersionUID = -7235915012779265241L;
	private final JPanel contentPanel = new JPanel();
	private SchedulerWithLimitedThreads scheduler;
	private JSpinner spinner = new JSpinner();
	private JCheckBox checkBoxMaximum = new JCheckBox("Максимум");
	private JButton okButton = new JButton("Задать");

	/**
	 * Create the dialog.
	 */
	public DialogEditingScheduler() {
		setResizable(false);
		setModal(true);
		setTitle("Потоки выполнения");
		setBounds(100, 100, 321, 101);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

		JLabel labelCount = new JLabel("Количество потоков");
		contentPanel.add(labelCount);

		Component horizontalStrut = Box.createHorizontalStrut(10);
		contentPanel.add(horizontalStrut);

		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1),
				null, new Integer(1)));
		contentPanel.add(spinner);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		contentPanel.add(horizontalStrut_1);

		contentPanel.add(checkBoxMaximum);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton.setEnabled(false);
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!checkBoxMaximum.isSelected())
					try {
						int spinnerValue = (Integer) spinner.getValue();
						if (spinnerValue < 1)
							throw new Exception();
						scheduler.setMaximumCountConcurentTask(spinnerValue);
					} catch (Exception e1) {
						JOptionPane
								.showMessageDialog(DialogEditingScheduler.this,
										"Неверно указано количество потоков выполения.");
						return;
					}
				else {
					int maxValue = scheduler.getCountOfTaskToBePerformed()
							- scheduler.getCountCompletedTask();
					scheduler.setMaximumCountConcurentTask(maxValue);
				}
				close();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Отмена");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();

			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

	}

	public void close() {
		okButton.setEnabled(false);
		this.setVisible(false);
	}

	public boolean setScheduler(SchedulerWithLimitedThreads newScheduler) {
		if (newScheduler != null) {
			scheduler = newScheduler;
			spinner.getModel().setValue(
					scheduler.getMaximumCountConcurentTask());
			checkBoxMaximum.setSelected(false);
			okButton.setEnabled(true);
			return true;
		}
		return false;
	}

}
