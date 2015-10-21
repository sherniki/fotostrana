package fotostrana.ru.events.network.votes;

import fotostrana.ru.events.SuccessfulEvent;
import fotostrana.ru.events.network.EventOfNetworkRequestsFotostrana;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Успешно добавлены голоса
 * 
 */
public class SuccessfulVote extends EventOfNetworkRequestsFotostrana implements
		SuccessfulEvent {

	/**
	 * Количество добавленых голосов
	 */
	private int countVotes = 1;

	/**
	 * Добавлен один голос
	 * 
	 * @param request
	 *            запрос в котором произошло событие
	 */
	public SuccessfulVote(RequestFotostrana request) {
		super(request);
	}

	/**
	 * Добавлено несколько голосов
	 * 
	 * @param request
	 *            запрос в котором произошло событие
	 * @param countVotes
	 *            количество добавленых голосов
	 */
	public SuccessfulVote(RequestFotostrana request, int countVotes) {
		super(request);
		if (countVotes > 1)
			this.countVotes = countVotes;
	}

	/**
	 * Количество добавленых голосов
	 * 
	 * @return
	 */
	public int getCountVotes() {
		return countVotes;
	}

}
