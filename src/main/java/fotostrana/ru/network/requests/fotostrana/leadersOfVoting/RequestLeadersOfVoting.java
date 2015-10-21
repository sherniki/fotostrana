package fotostrana.ru.network.requests.fotostrana.leadersOfVoting;

import java.util.LinkedList;
import java.util.List;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.requests.fotostrana.LoginRequest;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.reports.leadersOfVoting.Nomination;
import fotostrana.ru.reports.leadersOfVoting.RecordReport;
import fotostrana.ru.users.User;

/**
 * Получает лидеров голосования
 * 
 */
public abstract class RequestLeadersOfVoting extends RequestFotostrana {
	/**
	 * Записи с получеными пользователями
	 */
	protected List<RecordReport> records;
	/**
	 * Номинация
	 */
	protected Nomination nomination;

	/**
	 * @param user
	 * @param nomination
	 *            название номинации
	 */
	public RequestLeadersOfVoting(EventListener eventListener, User user,
			Nomination nomination) {
		super(user);
		this.eventListener = eventListener;
		records = new LinkedList<RecordReport>();
		setNomination(nomination);
		if (!user.isAutorizted()) {
			loginRequest = new LoginRequest(this);
			listRequests.add(0, loginRequest);
		}
	}

	public Nomination getNomination() {
		return nomination;
	}

	public void setNomination(Nomination nominationName) {
		if (nominationName != null)
			this.nomination = nominationName;
	}

	public List<RecordReport> getRecords() {
		return records;
	}

	public abstract RequestLeadersOfVoting clone();

}
