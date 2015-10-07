package fotostrana.ru.gui.LoadTaskWindow.panels;

import javax.swing.JPanel;

import java.awt.Dimension;

import javax.swing.JLabel;

import com.toedter.calendar.JDateChooser;

import fotostrana.ru.task.tasks.TaskFotostrana;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.BoxLayout;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SpinnerDateModel;

public class TimerPanel extends JPanel {
	private Locale defaultLocale = getLocale("RU");
	private final static long MILISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
	private static long offset;
	static {
		TimeZone currentTimeZone = TimeZone.getDefault();
		if (TimeZone.getDefault().getID().compareTo("Europe/Moscow") == 0) {
			currentTimeZone = TimeZone.getTimeZone("GMT+3");
		}
		offset = currentTimeZone.getRawOffset();
	}
	// private Date MAX_SPINNER_VALUE = new Date(MILISECONDS_IN_DAY);
	private static final long serialVersionUID = 1L;
	private static Dimension maxDimension = new Dimension(200, 30);
	private JDateChooser dateChooser = new JDateChooser(new Date(),
			"dd.MM.yyyy");
	private JLabel lblText = new JLabel();
	private JSpinner timeChooser;
	private final Component horizontalStrut_2 = Box.createHorizontalStrut(10);

	public TimerPanel() {
		System.out.println("OFFSET=" + offset);
		this.setMaximumSize(new Dimension(400, 20));
		dateChooser.setMaximumSize(maxDimension);
		dateChooser.setLocale(defaultLocale);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(horizontalStrut_2);
		lblText.setText("Text");

		add(lblText);
		lblText.setMaximumSize(new Dimension(130, 20));
		lblText.setMinimumSize(new Dimension(130, 20));
		SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null,
				Calendar.MINUTE);
		timeChooser = new JSpinner(model);
		String pattern = "HH:mm";

		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		add(horizontalStrut_1);
		DateEditor dateEditor = new JSpinner.DateEditor(timeChooser, pattern);
		timeChooser.setEditor(dateEditor);
		add(timeChooser);
		timeChooser.setMaximumSize(new Dimension(75, 20));
		dateChooser.setMaximumSize(new Dimension(200, 20));
		dateChooser.setMinimumSize(new Dimension(150, 20));

		Component horizontalStrut = Box.createHorizontalStrut(10);
		add(horizontalStrut);
		add(dateChooser);
	}

	public TimerPanel(String text, Date date) {
		this();
		setText(text);
		setValue(date);
	}

	public void setText(String newText) {
		lblText.setText(newText);
	}

	public void setValue(Date value) {
		System.out.println("**** SET VALUE*******");
		if (value != null) {
			System.out.println("CURRENT VALUE ="
					+ TaskFotostrana.dateFormatter.format(value));
			Date dateCh = new Date((value.getTime() / MILISECONDS_IN_DAY)
					* MILISECONDS_IN_DAY - TimeZone.getDefault().getRawOffset());
			System.out.println("SET DateChooserValue ="
					+ TaskFotostrana.dateFormatter.format(dateCh) + "  |"
					+ dateCh.getTime());
			dateChooser.setDate(dateCh);
			Date spinnerDate = new Date(value.getTime() % MILISECONDS_IN_DAY
					- offset);
			System.out.println("SET SpinnerValue ="
					+ TaskFotostrana.dateFormatter.format(spinnerDate) + "  |"
					+ spinnerDate.getTime());
			timeChooser.setValue(spinnerDate);
		}
	}

	public Date getValue() {
		System.out.println("**** GET VALUE*******");
		final long seconds = 60 * 1000;
		long dateLong = dateChooser.getDate().getTime()
				+ TimeZone.getDefault().getRawOffset();

		System.out.println("DATE CHOOSER ="
				+ TaskFotostrana.dateFormatter.format(new Date(dateLong))
				+ "  |" + dateLong);

		long timeLong = ((Date) timeChooser.getValue()).getTime();

		System.out.println("timeChooser ="
				+ TaskFotostrana.dateFormatter.format(new Date(timeLong))
				+ "  |" + timeLong);

		timeLong = timeLong % MILISECONDS_IN_DAY;
		timeLong = (timeLong / seconds) * seconds;
		Date result = new Date(dateLong + timeLong);
		System.out.println("GET TimerValue ="
				+ TaskFotostrana.dateFormatter.format(result) + "  |"
				+ result.getTime());
		return result;
	}

	public void setLocale(Locale newLocale) {
		dateChooser.setLocale(newLocale);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		timeChooser.setEnabled(enabled);
		dateChooser.setEnabled(enabled);
		lblText.setEnabled(enabled);
	}

	/**
	 * Возращает локализацию по абревиатуре страны, для России - "RU"
	 * 
	 * @param country
	 * @return null - если ненайдено заданую страну
	 */
	static Locale getLocale(String country) {
		Locale locale = null;
		Locale[] locales = Calendar.getAvailableLocales();
		for (Locale curLocale : locales) {
			if (curLocale.getCountry().compareTo(country) == 0) {
				locale = curLocale;
				break;
			}
		}
		return locale;
	}
}
