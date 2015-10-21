package fotostrana.ru.events.network;

import fotostrana.ru.events.FailEvent;
import fotostrana.ru.events.schedulers.SchedulerEvent;
import fotostrana.ru.network.filters.BannedFilter;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

/**
 * Обнаружено что пользователь забанен
 */
public class EventBan extends EventOfNetworkRequestsFotostrana implements
		FailEvent, SchedulerEvent {
	/**
	 * Причина бана
	 */
	private String reason;

	public EventBan(RequestFotostrana request) {
		super(request);
		BannedFilter bannedFilter = request.getBannedFilter();
		reason = bannedFilter.getReason();
	}

	public EventBan(RequestFotostrana request, String reason) {
		this(request);
		setReason(reason);
	}

	/**
	 * Возращает причину бана
	 * 
	 * @return
	 */
	public String getReason() {
		return reason;
	}

	public void setReason(String reasonBan) {
		reason = reasonBan;
	}
}
