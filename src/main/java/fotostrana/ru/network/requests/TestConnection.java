package fotostrana.ru.network.requests;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.connections.TestFails;
import fotostrana.ru.events.connections.TestSuccessfullyCompleted;
//import fotostrana.ru.log.Log;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.Request;

/**
 * Запрос для тестирования соединения
 * 
 */
public class TestConnection extends Request {
	public static final String[] keywords = { "яндекс" };
	private Connection conection;

	/**
	 * @param connection
	 *            соединение которое проверяется
	 */
	public TestConnection(Connection connection) {
		super();
		this.conection = connection;
		eventListener = connection.getSourceRequests();
	}

	@Override
	public String getURL() {
		return "http://www.yandex.ua";
	}

	@Override
	public void setResult(String result) {
		if (checkResult(result)) {
			eventListener.handleEvent(new TestSuccessfullyCompleted(conection));
		} else {
			eventListener.handleEvent(new TestFails(conection));
		}
	}

	/**
	 * Проверяет результат на коректность
	 * 
	 * @param result
	 * @return
	 */
	public boolean checkResult(String result) {
		// for (String keyword : keywords)
		// if (result.indexOf(keyword) == -1)
		// return false;
		String title = ParserJSON.getSubstring(result, "<title>", "</title>");
		if (title != null) {
			// title = Log.toUTF8(title);
			if (title.compareTo("рїрѕрґрµрєсѓ") == 0) {
//				System.out.println("+1");
				return true;
			}
		}
		return false;
	}

}
