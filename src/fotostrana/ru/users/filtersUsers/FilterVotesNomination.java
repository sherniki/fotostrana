package fotostrana.ru.users.filtersUsers;

import fotostrana.ru.users.User;
import fotostrana.ru.users.UsersFilter;

/**
 * Отбирает пользователей для голосования в номинациях Отбрасывает пользователей
 * которые сегодня уже голосовали за этого человека
 * 
 */
 class FilterVotesNomination implements UsersFilter {
//	private String targetId;

	/**
	 * Отбирает пользователей для голосования в номинациях
	 * 
	 * @param targetId
	 *            пользователь за которого нужно проголосовать
	 */
	public FilterVotesNomination(String targetId) {
//		this.targetId = targetId;
	}

	@Override
	public boolean filtrate(User user) {
//		Set<UserActions> actions = user.getUsersActions();
//		for (UserActions userAction : actions) {
//			if (userAction.targetId.compareTo(targetId) == 0) {
//				return false;
//			}
//		}
		return true;
	}

}
