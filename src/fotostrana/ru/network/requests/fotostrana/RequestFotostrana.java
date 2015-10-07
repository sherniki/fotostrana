package fotostrana.ru.network.requests.fotostrana;

import org.apache.http.client.protocol.HttpClientContext;

import fotostrana.ru.network.Request;
import fotostrana.ru.network.filters.BannedFilter;
import fotostrana.ru.network.filters.LoginFilter;
import fotostrana.ru.users.User;

public abstract class RequestFotostrana extends Request {
	/**
	 * Пользователь, от имени которого выполняются запросы
	 */
	protected User user;

	protected BannedFilter bannedFilter;
	protected LoginFilter loginFilter;
	protected LoginRequest loginRequest;

	/**
	 * Создание запроса без родителя
	 * 
	 * @param user
	 *            пользователь от имени которого будет выполнятся запрос
	 */
	public RequestFotostrana(User user) {
		super(null);
		this.user = user;
		setParentRequest(null);
	}

	public RequestFotostrana(RequestFotostrana parrentRequest) {
		super(parrentRequest);
		setParentRequest(parrentRequest);
	}

	/**
	 * Проверяет авторизован ли пользователь, если неавторизован перывм запросом
	 * устанавливает запрос авторизации
	 */
	protected void isLogin() {
		if (!user.isAutorizted()) {
			loginRequest = new LoginRequest(this);
			listRequests.add(0, loginRequest);
		}
	}

	@Override
	public void setParentRequest(Request parentRequest) {
		super.setParentRequest(parentRequest);
		if (parentRequest == null) {
			loginFilter = new LoginFilter();
			bannedFilter = new BannedFilter();
		} else {
			if (parentRequest instanceof RequestFotostrana) {
				RequestFotostrana fotoStranaRequest = (RequestFotostrana) parentRequest;
				this.user = fotoStranaRequest.getUser();
				this.loginFilter = fotoStranaRequest.getLoginFilter();
				this.bannedFilter = fotoStranaRequest.getBannedFilter();
			}

		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public HttpClientContext getHttpContext() {
		return user.httpContext;
	}

	public BannedFilter getBannedFilter() {
		return bannedFilter;
	}

	public LoginFilter getLoginFilter() {
		return loginFilter;
	}
}
