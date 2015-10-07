package fotostrana.ru.network.requests.fotostrana.leadersOfVoting.ratings;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.AbstractRequestLeadersOfRating;
import fotostrana.ru.network.requests.fotostrana.leadersOfVoting.RequestLeadersOfVoting;
import fotostrana.ru.reports.leadersOfVoting.Region;
import fotostrana.ru.users.User;

/**
 * Получение лидеров в остальных номинациях
 * 
 */
public class LeadersOfPhotoTags extends AbstractRequestLeadersOfRating {
	/**
	 * Подноминация
	 */
	private Region subnomination;

	/**
	 * @param user
	 *            пользователь от имени которого будет отправляться запрос
	 * @param subnomination
	 *            Подноминация
	 */
	public LeadersOfPhotoTags(EventListener eventListener, User user,
			Region subnomination) {
		super(eventListener, user, subnomination);
		this.subnomination = subnomination;
	}

	public LeadersOfPhotoTags(EventListener eventListener, User user,
			Region subnomination, int limit) {
		this(eventListener, user, subnomination);
		this.limit = limit;
	}

	@Override
	public RequestLeadersOfVoting clone() {
		return new LeadersOfPhotoTags(eventListener, user, subnomination, limit);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/phototags/top/" + subnomination.name;
	}

}
