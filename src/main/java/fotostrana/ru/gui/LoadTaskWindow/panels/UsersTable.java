package fotostrana.ru.gui.LoadTaskWindow.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import fotostrana.ru.users.User;

/**
 * Таблица пользователей
 * 
 */
public class UsersTable extends JTable {
	private static final long serialVersionUID = 1L;
	private ModelTableUsers modelTable;
	private ContextMenuTableUsers contextMenu = new ContextMenuTableUsers(this);

	public UsersTable(Set<User> users) {
		modelTable = new ModelTableUsers(users);
		setModel(modelTable);
		contextMenu.addPopup(this);
		setColumnSize();
	}

	public UsersTable() {
		modelTable = new ModelTableUsers();
		setModel(modelTable);
		contextMenu.addPopup(this);
		setColumnSize();
	}

	private void setColumnSize() {
		getColumn(this.getColumnName(0)).setMaxWidth(120);
		getColumn(this.getColumnName(1)).setMaxWidth(80);
		getColumn(this.getColumnName(2)).setMaxWidth(80);

	}

	public Set<User> getUsers() {
		return modelTable.getUsers();
	}

	public void setUsers(Set<User> users) {
		modelTable.setUsers(users);
	}

	public boolean addUser(User user) {
		return modelTable.users.add(user);
	}

	/**
	 * Возращает выбраноого пользователя
	 * 
	 * @return
	 */
	public User getSelectedUser() {
		return modelTable.getUserToIntdex(getSelectedRow());
	}

	public boolean removeUser(User user) {
		return modelTable.users.remove(user);
	}

	public boolean removeUserToIndex(int index) {
		User user = modelTable.getUserToIntdex(index);
		if (user != null) {
			return removeUser(user);
		}
		return false;
	}

	public boolean removeSelectedUser() {
		return removeUserToIndex(getSelectedRow());
	}

	class ModelTableUsers extends AbstractTableModel {
		public String[] COLUMN_NAME = { "Имя", "ИД", "Цвет", "Автологин" };
		private static final long serialVersionUID = 1L;
		public Set<User> users;

		public Set<User> getUsers() {
			return users;
		}

		public void setUsers(Set<User> users) {
			this.users = users;
		}

		public ModelTableUsers() {
			users = new HashSet<User>();
		}

		public ModelTableUsers(Set<User> users) {
			this.users = users;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAME[column];
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAME.length;
		}

		@Override
		public int getRowCount() {
			return users.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			User user = getUserToIntdex(row);
			if (user != null) {
				if (column == 0)
					return user.name;
				if (column == 1)
					return user.id;
				if (column == 2)
					return user.color;
				if (column == 3)
					return user.urlAutoConnection;
			}
			return null;
		}

		public User getUserToIntdex(int index) {
			for (User user : users) {
				if (index == 0)
					return user;
				else {
					if (index > 0)
						index--;
					else
						return null;
				}
			}
			return null;
		}

	}

}

/**
 * Контексное меню таблицы с заданиями
 * 
 */
class ContextMenuTableUsers extends JPopupMenu {
	private static final long serialVersionUID = 7488885967492503955L;
	private JMenuItem itemRemove;

	// private TaskTable taskTable;

	public ContextMenuTableUsers(final UsersTable usersTable) {
		// this.taskTable = taskTable;
		itemRemove = new JMenuItem("Удалить");
		Action removeAction = new AbstractAction("") {
			private static final long serialVersionUID = -6308373688697442084L;

			@Override
			public void actionPerformed(ActionEvent e) {
				usersTable.removeSelectedUser();
				usersTable.updateUI();
			}
		};

		itemRemove.addActionListener(removeAction);
		this.add(itemRemove);
	}

	public void addPopup(Component component) {
		final JPopupMenu popup = this;
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
