package fotostrana.ru.gui.TaskWindow;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SpinnerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel label = new JLabel();
	private JSpinner spinner = new JSpinner();

	public SpinnerPanel() {
		setMaximumSize(new Dimension(3000, 30));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		Component horizontalStrut = Box.createHorizontalStrut(5);
		add(horizontalStrut);

		add(label);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		add(horizontalStrut_1);
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1),
				null, new Integer(1)));

		add(spinner);

		Component horizontalStrut_2 = Box.createHorizontalStrut(5);
		add(horizontalStrut_2);
	}

	public SpinnerPanel(String labelCaption) {
		this();
		setLabelCaption(labelCaption);
	}

	public void initializationPanel(String text, int spinnerValue,
			int minValue, int step) {
		setLabelCaption(text);
		spinner.setModel(new SpinnerNumberModel(new Integer(spinnerValue),
				new Integer(minValue), null, new Integer(step)));
	}

	public int getValue() {
		Integer value = (Integer) spinner.getValue();
		return value.intValue();
	}

	public void setLabelCaption(String text) {
		label.setText(text);
	}
}
