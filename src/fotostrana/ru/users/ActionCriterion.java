package fotostrana.ru.users;

public interface ActionCriterion {

	/**
	 * Критерий отбора выполненых дейчтсвий
	 * 
	 * @param action
	 *            действие
	 * @return true если подходит
	 */
	public boolean checkAction(UserActions action);
}
