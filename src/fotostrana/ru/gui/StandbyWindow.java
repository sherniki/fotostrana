package fotostrana.ru.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class StandbyWindow extends JDialog {
	private static final long serialVersionUID = -4572135606032481874L;

	public String CONST_WAIT = ".Подождите...";
	private JLabel label = new JLabel(CONST_WAIT);
	private JProgressBar progressBar = new JProgressBar();



	public StandbyWindow(JFrame parent) {
		super(parent);
		setLocationRelativeTo(parent);
		setUndecorated(true);
		setModalityType(ModalityType.TOOLKIT_MODAL);
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		setBounds(100, 100, 400, 100);
		getContentPane().setLayout(null);

		label.setBounds(10, 20, 380, 15);
		getContentPane().add(label);

		progressBar.setBounds(10, 50, 380, 25);
		progressBar.setIndeterminate(true);
		getContentPane().add(progressBar);


	}

	/**
	 * Показывает окно с заданым описанием (без блокировки вызвавшего потока)
	 * 
	 */
	public void showWindow() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				StandbyWindow.this.setVisible(true);
			}
		});
	}

	/**
	 * @param text
	 *            описание ожидания
	 */
	public void setText(String text) {
		label.setText(text + CONST_WAIT);
	}

	//
	public void close() {
		setVisible(false);
	}
}
