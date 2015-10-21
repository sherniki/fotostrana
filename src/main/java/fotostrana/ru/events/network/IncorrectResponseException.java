package fotostrana.ru.events.network;

/**
 * Некоректный ответ от сервера
 * 
 */
public class IncorrectResponseException extends Exception {
	private static final long serialVersionUID = 1L;
	public String resultResponse;

	public IncorrectResponseException(String URL, String resultResponse) {
		super("Некорректный ответ от сервера. URL=" + URL);
		this.resultResponse = resultResponse;
	}

}
