package fotostrana.ru.users;

import java.io.Serializable;
import java.util.Date;

/**
 * Описвыает действие пользователя
 * 
 */
public class UserActions implements Serializable, Comparable<UserActions> {
	public static final int VOTE_TOURNAMENT = 100;

	private static final long serialVersionUID = -2321389954667039265L;
	/**
	 * пользователь который выполнил действие
	 */
	public String userId;
	/**
	 * пользователь над которым проиведено действие
	 */
	public String targetId;
	/**
	 * дата выполнения действия
	 */
	public Date dateAction;
	/**
	 * тип выполненого действия
	 */
	public int typeAction;

	// public String description;

	public UserActions(String userId, String targetId, Date dateAction,
			int typeAction) {
		super();
		this.userId = userId;
		this.targetId = targetId;
		this.dateAction = dateAction;
		this.typeAction = typeAction;
		// description = "";
	}

	@Override
	public int compareTo(UserActions o) {
		if (userId.compareTo(o.userId) == 0)
			if (targetId.compareTo(o.targetId) == 0)
				if (typeAction == o.typeAction) {
					return 0;
				}
		return -1;
	}

}
