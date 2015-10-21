package fotostrana.ru.events.network;

import fotostrana.ru.network.requests.fotostrana.RequestSendMessage;
import fotostrana.ru.reports.sendMessages.Message;

/**
 * Успешно отправлено сообщение
 * 
 */
public class SuccessfulSentMessage extends EventOfNetworkRequests {

	public SuccessfulSentMessage(RequestSendMessage request) {
		super(request);
	}

	public Message getMessage() {
		return ((RequestSendMessage) request).message;
	}

}
