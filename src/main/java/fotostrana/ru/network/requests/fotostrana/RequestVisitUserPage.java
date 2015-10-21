package fotostrana.ru.network.requests.fotostrana;
import fotostrana.ru.log.Log;

/**
 * Заходит на страницу заданого пользователя от имени заданого бота
 * 
 */
public class RequestVisitUserPage extends RequestFotostrana {
	private String idVisitUser;

	/**
	 * Заходит на страницу заданого пользователя от имени заданого бота
	 * 
	 * @param parrentRequest
	 *            задает бота от имени которого будет выполнятся визит
	 * @param idVisitUser
	 *            пользователь на страницу которого нужно зайти
	 */
	public RequestVisitUserPage(RequestFotostrana parrentRequest,
			String idVisitUser) {
		super(parrentRequest);
		this.idVisitUser = idVisitUser;
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/user/" + idVisitUser + "/";
	}

	@Override
	public void setResult(String result) {
		if (loginFilter.filtrate(result) && (bannedFilter.filtrate(result))) {
			Log.LOGGING.addTaskLog("Зайдено на страницу " + idVisitUser
					+ " с анкеты " + user.id + ".", Log.TYPE_POSITIVE);
		}
	}

}
