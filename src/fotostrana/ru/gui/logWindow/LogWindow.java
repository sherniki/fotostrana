package fotostrana.ru.gui.logWindow;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Окно с логами
 * 
 */
public class LogWindow extends JFrame {
	public static int DELAY_UPDATE = 1 * 1000;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private StatePanel panelState = new StatePanel();;
	private LogPanel panelLog = new LogPanel();;
	private Timer updateTimer;
	private ActionListener updateListener;

	/**
	 * Create the frame.
	 */
	public LogWindow() {
		setTitle("Состояние программы");
		setBounds(100, 100, 650, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		updateListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		};
		updateTimer = new Timer(DELAY_UPDATE, updateListener);
		updateTimer.start();
		
		contentPane.setLayout(new BorderLayout(5, 5));
		contentPane.add(panelState, BorderLayout.NORTH);
		contentPane.add(panelLog, BorderLayout.CENTER);
	}

	public void update() {
		if (isVisible()) {
			panelState.update();
			panelLog.update();
		}
	}
}
