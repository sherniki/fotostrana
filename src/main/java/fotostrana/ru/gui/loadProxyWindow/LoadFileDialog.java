package fotostrana.ru.gui.loadProxyWindow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fotostrana.ru.gui.LoadTaskWindow.panels.FileSelectionPanel;

/**
 * Диалог загрузки из файла
 * 
 */
public class LoadFileDialog extends JDialog {
	private ActionListener DEFAULT_LISTENER;
	private static final long serialVersionUID = -7388220128123480754L;
	private final JPanel contentPanel = new JPanel();
	private FileSelectionPanel fileSelectionPanel;

	// private ActionListener listenerDownload;

	/**
	 * Create the dialog.
	 */
	public LoadFileDialog() {
		setModal(true);
		setBounds(100, 100, 600, 70);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		DEFAULT_LISTENER = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};

		// listenerDownload = DEFAULT_LISTENER;

		fileSelectionPanel = new FileSelectionPanel("Загрузить",
				DEFAULT_LISTENER);

		contentPanel.add(fileSelectionPanel, BorderLayout.CENTER);
	}

	public void setFileSelectionMode(int mode) {
		fileSelectionPanel.setFileSelectionMode(mode);
	}

	public void setMultiSelectionEnabled(boolean enabled) {
		fileSelectionPanel.setMultiSelectionEnabled(enabled);
	}

	public String getSelectedFile() {
		return fileSelectionPanel.getFilePath();
	}

	public void setListener(ActionListener listener) {
		if (listener == null)
			listener = DEFAULT_LISTENER;
		// listenerDownload = listener;
		fileSelectionPanel.setActionListener(listener);
	}

	public ActionListener getListener() {
		return fileSelectionPanel.getListener();
	}

	public void setDefaultPath(String defaultPath) {
		fileSelectionPanel.setPath(defaultPath);
	}

}
