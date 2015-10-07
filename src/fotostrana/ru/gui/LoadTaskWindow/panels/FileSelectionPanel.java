package fotostrana.ru.gui.LoadTaskWindow.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;

/**
 * Панель выбора файла имеет кнопку actionButton, для обработки загружено файла
 * 
 */
public class FileSelectionPanel extends JPanel {
	/**
	 * Кнопка обработчик загрузки файла
	 */
	public JButton actionButton = new JButton();
	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser = new JFileChooser();
	private File currentDirectory = new File(".");
	private String filePath = "";
	private JTextField fileTextEdit;
	private ActionListener listener;
	private JLabel fileLabel = new JLabel("Файл ");
	private final Component horizontalStrut = Box.createHorizontalStrut(5);

	/**
	 * Создает панель без кнопки обработки файла
	 */
	public FileSelectionPanel() {
		fileChooser.setCurrentDirectory(currentDirectory);
		System.out.println("tCurrentDirectory = "
				+ fileChooser.getCurrentDirectory());

		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new TXTFileFilter());

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		fileTextEdit = new JTextField();
		fileTextEdit.setMaximumSize(new Dimension(10000, 30));
		JButton selectionFileButton = new JButton("Выбрать файл");
		setActionListener(null);

		add(horizontalStrut);

		this.add(fileLabel);
		this.add(Box.createHorizontalStrut(10));
		this.add(fileTextEdit);
		this.add(Box.createHorizontalStrut(10));
		this.add(selectionFileButton);
		this.add(Box.createHorizontalStrut(10));
		this.add(actionButton);

		selectionFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setPath(openFileDialog());
			}
		});
	}

	public void setFileSelectionMode(int mode) {
		fileChooser.setFileSelectionMode(mode);
	}

	public void setMultiSelectionEnabled(boolean enabled) {
		fileChooser.setMultiSelectionEnabled(enabled);
	}

	/**
	 * Создает панель с заданой текстовой подписью и без кнопки обработки
	 * 
	 * @param title
	 *            текст надписи
	 */
	public FileSelectionPanel(String title) {
		this();
		setLabelCaption(title);
	}

	/**
	 * Создает панель с кнопкой обработки
	 * 
	 * @param titleActionButton
	 *            надпись на кнопке обработки
	 * @param actionListenerButton
	 *            обработка загрузки файла
	 */
	public FileSelectionPanel(String titleActionButton,
			ActionListener actionListenerButton) {
		this();
		setButtonActionCaption(titleActionButton);
		setActionListener(actionListenerButton);
	}

	/**
	 * Задает адрес файла
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		if (path != null) {
			filePath = path;
			fileTextEdit.setText(path);
			File f=new File(path);
//			System.out.println("Path = "+f.getAbsolutePath());
//			System.out.println("exists = "+f.exists());
			fileChooser.setCurrentDirectory(f);
//			System.out.println("tCurrentDirectory = "
//					+ fileChooser.getCurrentDirectory());
			actionButton.setEnabled(true);
		}
	}

	/**
	 * Диалог выбора файла открытия
	 * 
	 * @return адресс файла,null - если небыл выбран файл
	 */
	public String openFileDialog() {
		String fileName = null;
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.isMultiSelectionEnabled()) {
				File[] files = fileChooser.getSelectedFiles();
				fileName = "";
				for (int i = 0; i < files.length - 1; i++) {
					fileName += files[i].getPath() + ";";
				}
				fileName += files[files.length - 1].getPath();
			} else
				fileName = fileChooser.getSelectedFile().getPath();
		}
		return fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	class TXTFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".txt")
					|| f.isDirectory();
		}

		@Override
		public String getDescription() {
			return "Текстовые файлы";
		}

	}

	public void setActionListener(ActionListener newListener) {
		if (newListener != null) {
			actionButton.removeActionListener(this.listener);
			listener = newListener;
			actionButton.addActionListener(newListener);
			actionButton.setVisible(true);
		} else {
			actionButton.removeActionListener(this.listener);
			actionButton.setVisible(false);
			listener = null;
		}
	}

	public ActionListener getListener() {
		return listener;
	}

	public void setLabelCaption(String caption) {
		fileLabel.setText(caption);
	}

	public void setButtonActionCaption(String caption) {
		actionButton.setText(caption);
	}
}
