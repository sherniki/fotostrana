package fotostrana.ru.gui.loadProxyWindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fotostrana.ru.gui.LoadTaskWindow.panels.FileSelectionPanel;
import fotostrana.ru.network.NetworkManager;
import fotostrana.ru.network.TypeProxy;
import fotostrana.ru.network.proxy.ProxyManager;

import javax.swing.BoxLayout;
import javax.swing.JRadioButton;

/**
 * Диалог загрузки прокси из файла
 * 
 */
public class DialogDownloadProxyFromFile extends JDialog {
	private static final long serialVersionUID = -4464092340337451444L;
	private final JPanel contentPanel = new JPanel();
	private FileSelectionPanel fileSelectionPanel = new FileSelectionPanel(
			"Файл с прокси:");
	private PanelTypeProxy panelTypeProxy = new PanelTypeProxy();

	/**
	 * Create the dialog.
	 */
	public DialogDownloadProxyFromFile() {
		setModal(true);
		setResizable(false);
		setTitle("Загрузка списка прокси из файла");
		setBounds(100, 100, 450, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(fileSelectionPanel);
		FlowLayout flowLayout = (FlowLayout) panelTypeProxy.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPanel.add(panelTypeProxy);
		fileSelectionPanel.setMaximumSize(new Dimension(500, 30));
		fileSelectionPanel.setPreferredSize(new Dimension(500, 30));

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("Загрузить");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String file = fileSelectionPanel.getFilePath();
				if ((file != null) && (!file.isEmpty())) {
					ProxyManager.PROXY_MANAGER.loadListProxyFromFile(file,
							panelTypeProxy.getSelectedType());
					DialogDownloadProxyFromFile.this.setVisible(false);
					NetworkManager.NETWORK_MANAGER
							.addTheMaximumNumberOfConnections();

				} else {
					JOptionPane.showMessageDialog(
							DialogDownloadProxyFromFile.this,
							"Выберите файл с прокси.", "Ошибка",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
	}

	// public void reset() {
	// panelTypeProxy.reset();
	// }
}

class PanelTypeProxy extends JPanel {
	private static final long serialVersionUID = 1L;
	private JRadioButton buttonHTTP = new JRadioButton("HTTP");
	private JRadioButton buttonSOCKS = new JRadioButton("SOCKS");

	public PanelTypeProxy() {
		add(new JLabel("Тип прокси:"));
		setPreferredSize(new Dimension(500, 30));
		setMaximumSize(new Dimension(500, 30));
		add(buttonHTTP);
		add(buttonSOCKS);
		ButtonGroup group = new ButtonGroup();
		group.add(buttonHTTP);
		group.add(buttonSOCKS);
		reset();
	}

	public TypeProxy getSelectedType() {
		if (buttonHTTP.isSelected())
			return TypeProxy.HTTP;
		return TypeProxy.SOCKS;
	}

	public void reset() {
		buttonHTTP.setSelected(true);
	}

}
