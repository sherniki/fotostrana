package fotostrana.ru.gui.LoadTaskWindow.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;

import fotostrana.ru.gui.TaskWindow.SpinnerPanel;
import fotostrana.ru.task.Scheduler;
import fotostrana.ru.task.schedulers.SchedulerWithLimitedThreads;
import fotostrana.ru.task.schedulers.SchedulerWithRandomDelay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Box;

import java.awt.Component;

/**
 * Панель планировщика выполнения группы
 * 
 */
public class SchedulerGroupPanel extends JPanel {
	private static final long serialVersionUID = 5475050211960580648L;

	JRadioButton radioButtonCountThreads = new JRadioButton(
			"Количество потоков", true);
	JRadioButton radioButtonDelay = new JRadioButton(
			"Задержка между запросами", false);
	SchedulerDelayPanel schedulerDelayPanel = new SchedulerDelayPanel();
	SchedulerCountTheardsPanel countTheardsPanel = new SchedulerCountTheardsPanel();
	private SettingsPanel settings = new SettingsPanel();

	public SchedulerGroupPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new TitledBorder("Выполнение задания"));

		JPanel chooseTheSchedulerPanel = new JPanel();
		chooseTheSchedulerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		chooseTheSchedulerPanel.setMaximumSize(new Dimension(1000, 30));

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioButtonCountThreads);
		buttonGroup.add(radioButtonDelay);

		radioButtonCountThreads.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				countTheardsPanel.setVisible(true);
				schedulerDelayPanel.setVisible(false);
			}
		});

		radioButtonDelay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				countTheardsPanel.setVisible(false);
				schedulerDelayPanel.setVisible(true);
			}
		});

		chooseTheSchedulerPanel.add(radioButtonCountThreads);
		chooseTheSchedulerPanel.add(radioButtonDelay);
		settings.setAlignmentX(Component.CENTER_ALIGNMENT);

		// this.add(Box.createHorizontalStrut(5));
		this.add(settings);

		// settingsPanel.add(checkBoxVitis);

		add(Box.createVerticalStrut(5));
		this.add(chooseTheSchedulerPanel);

		add(Box.createVerticalStrut(5));
		this.add(countTheardsPanel);
		this.add(schedulerDelayPanel);

		reset();
	}

	public void reset() {
		radioButtonCountThreads.setSelected(true);
		schedulerDelayPanel.setVisible(false);
		countTheardsPanel.setVisible(true);
		schedulerDelayPanel.reset();
		settings.reset();
	}

	/**
	 * Запускать все задания
	 * 
	 * @return
	 */
	public boolean isExeuteAll() {
		return settings.isExeuteAll();
	}

	/**
	 * Заходить на страницу пользователя после успешного голоса
	 * 
	 * @return
	 */
	public boolean isVisit() {
		return settings.isVisit();
	}

	public Date getTimerDate() {
		return settings.getTimerDate();
	}

	public boolean isTimerExecute() {
		return settings.isTimerExecute();
	}

	public boolean isManually() {
		return settings.isManually();
	}

	public Scheduler createScheduler() {
		if (radioButtonCountThreads.isSelected()) {
			return new SchedulerWithLimitedThreads(null,
					countTheardsPanel.getCountThreads());
		}
		if (radioButtonDelay.isSelected()) {
			int minDelay = schedulerDelayPanel.getMinDelay();
			int maxDelay = schedulerDelayPanel.getMaxDelay();
			return new SchedulerWithRandomDelay(null, minDelay, maxDelay);
		}
		return null;
	}

	/**
	 * Планировать задания
	 * 
	 * @return
	 */
	public boolean isTasksScheduler() {
		return settings.isTasksScheduler();
	}
}

class SettingsPanel extends JPanel {
	private Dimension maxSizeLine = new Dimension(5000, 30);
	private static final long serialVersionUID = 1L;
	private TimerPanel timerPanel = new TimerPanel("Время старта :",
			getIniDate());
	JCheckBox setTasks = new JCheckBox("Применить к каждому заданию");
	JRadioButton executedAll = new JRadioButton("Сразу выполнять");
	JRadioButton executeByTimer = new JRadioButton("По таймеру");
	JRadioButton executeManually = new JRadioButton("В ручную");
	JCheckBox checkBoxVitis = new JCheckBox("Заходить на страницу");
	private Action changeTypeExecute = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == executeByTimer)
				timerPanel.setEnabled(true);
			else
				timerPanel.setEnabled(false);
		}
	};

	public SettingsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel line3 = new JPanel(new BorderLayout(5, 5));
		line1.setMaximumSize(maxSizeLine);
		line2.setMaximumSize(maxSizeLine);
		line3.setMaximumSize(maxSizeLine);

		line1.add(executedAll);
		line1.add(executeByTimer);
		line1.add(executeManually);
		ButtonGroup group = new ButtonGroup();
		group.add(executeByTimer);
		group.add(executeManually);
		group.add(executedAll);
		executeByTimer.addActionListener(changeTypeExecute);
		executedAll.addActionListener(changeTypeExecute);
		executeManually.addActionListener(changeTypeExecute);

		line2.add(setTasks);
		line2.add(checkBoxVitis);

		line3.add(timerPanel, BorderLayout.CENTER);
		this.add(line2);
		this.add(line1);
		this.add(line3);
		reset();
	}

	public void reset() {
		timerPanel.setValue(getIniDate());
		timerPanel.setEnabled(false);
		executedAll.setSelected(true);
		setTasks.setSelected(false);
		checkBoxVitis.setSelected(false);
	}

	public boolean isExeuteAll() {
		return executedAll.isSelected();
	}

	public boolean isTasksScheduler() {
		return setTasks.isSelected();
	}

	public boolean isVisit() {
		return checkBoxVitis.isSelected();
	}

	/**
	 * Возращает час ночи следующего дня
	 * 
	 * @return
	 */
	private static Date getIniDate() {
		long offsetTime = 0;
		long day = 24 * 60 * 60 * 1000;
		long time = 25 * 60 * 60 * 1000 - offsetTime;
		Date currentDate = Calendar.getInstance().getTime();
		long date = currentDate.getTime() / day * day;
		Date result = new Date(date + time);
//		 System.out.println("Result ="
//				 + TaskFotostrana.dateFormatter.format(result)+"   |"+result.getTime());
		return result;
		// return new Date();
	}

	public Date getTimerDate() {
		return timerPanel.getValue();
	}

	public boolean isTimerExecute() {
		return executeByTimer.isSelected();
	}

	public boolean isManually() {
		return executeManually.isSelected();
	}

}

class SchedulerDelayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private SpinnerPanel minDelay = new SpinnerPanel();
	private SpinnerPanel maxDelay = new SpinnerPanel();

	public SchedulerDelayPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(minDelay);
		add(Box.createVerticalStrut(5));
		this.add(maxDelay);
		reset();
	}

	public int getMinDelay() {
		return minDelay.getValue();
	}

	public int getMaxDelay() {
		return maxDelay.getValue();
	}

	public void reset() {
		minDelay.initializationPanel("Минимальная задержка (в секундах)", 1, 1,
				1);
		maxDelay.initializationPanel("Максимальная задержка (в секундах)", 2,
				2, 1);
	}
}

class SchedulerCountTheardsPanel extends JPanel {
	public static int DEFAULT_COUNT = 1000;
	private static final long serialVersionUID = 1L;
	JLabel labelCountTheards = new JLabel("Количесто потоков выполнения ");
	public JTextField textCountTheards = new JTextField(7);

	public SchedulerCountTheardsPanel() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setMaximumSize(new Dimension(1000, 30));
		add(labelCountTheards);
		add(textCountTheards);
	}

	public int getCountThreads() {
		try {
			return Integer.parseInt(textCountTheards.getText());
		} catch (Exception e) {
		}
		return DEFAULT_COUNT;
	}

	public void reset() {
		textCountTheards.setText("");
	}

}
