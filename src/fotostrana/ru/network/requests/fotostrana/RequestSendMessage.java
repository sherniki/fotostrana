package fotostrana.ru.network.requests.fotostrana;

import java.util.Date;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.JavaScriptGenerator;
import fotostrana.ru.network.libraryExecution.DJBrowser.DJBrowser;
import fotostrana.ru.network.requests.RequestDJNativeBrowser;
import fotostrana.ru.reports.sendMessages.Message;

/**
 * Оправляет сообщение в чат
 * 
 */
public class RequestSendMessage extends RequestFotostrana implements
		RequestDJNativeBrowser {
	public static String TEXT_FIELD = "im-text";
	public static String BUTTON_SEND = "btn btn-blue";

	/**
	 * Получатель сообщения
	 */
	// public String targetId;

	/**
	 * JavaScript для отправки сообщения
	 */
	private String JS;
	public Message message;
	private boolean isExecuteScript;

	public RequestSendMessage(Message message) {
		super(message.sender);
		this.message = message;

		typeRequest = TYPE_GET;
		libraryExecution = Connection.LIBRARY_DJNATIVEBROWSER;
		this.isUnanswered = true;
		JS = JavaScriptGenerator.onClick(TEXT_FIELD)
				+ JavaScriptGenerator.setTextValue(TEXT_FIELD, message.message)
				+ JavaScriptGenerator.onClick(BUTTON_SEND);

		if (!user.isAutorizted()) {
			listRequests.add(0, new LoginRequest(this));
		}

	}

	@Override
	public void setResult(String result) {
		if (user.isBanned) {
			eventListener.handleEvent(new EventBan(this));
			return;
		}

		message.isSent = true;
		message.timeSend = new Date();
		System.out.println(message.toString());
		eventListener.handleEvent(new EventRequestExecutedSuccessfully(this));
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/chat/?userId=" + message.targetId;
	}

	@Override
	public void сallbackPageFinishedLoading(JWebBrowser webBrowser) {
		if (webBrowser.getResourceLocation().indexOf(
				"http://fotostrana.ru/support/showban") != -1) {
			user.isBanned = true;
			return;
		}

		if (!isExecuteScript) {
			isExecuteScript = true;
			System.out.println(JS);
			DJBrowser.executeJavascript(webBrowser, JS);
		}
	}

}
