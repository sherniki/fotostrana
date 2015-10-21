package fotostrana.ru.users.actionCriterion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fotostrana.ru.users.ActionCriterion;
import fotostrana.ru.users.UserActions;

/**
 * сброс голосования в номинации; true если голос был в прошедних сутках false
 * если в этих
 */
public class ResetVotingInTheNomination implements ActionCriterion {
	public final static DateFormat dateFormat = new SimpleDateFormat("dd");
	private int currentDay;

	public ResetVotingInTheNomination() {
		currentDay = getDay(new Date());
	}

	@Override
	public boolean checkAction(UserActions action) {
		int day = getDay(action.dateAction);
		return (currentDay != day);
	}

	/**
	 * Возращает число месяца
	 * 
	 * @param date
	 * @return
	 */
	private int getDay(Date date) {
		String s = dateFormat.format(date);
		return Integer.parseInt(s);

	}
}
