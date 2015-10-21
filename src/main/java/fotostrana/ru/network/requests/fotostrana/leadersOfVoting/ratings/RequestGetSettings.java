package fotostrana.ru.network.requests.fotostrana.leadersOfVoting.ratings;

import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;

import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;
import fotostrana.ru.users.User;

public class RequestGetSettings extends RequestFotostrana {

	public RequestGetSettings(User user) {
		super(user);
		typeRequest = TYPE_POST;
	}

	@Override
	public void setResult(String result) {
		eventListener.handleEvent(new EventRequestExecutedSuccessfully(this));
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/rating/ajax/getsettingstooltip/";
	}

	@Override
	public HttpEntity getRequestData() {
		return Request
				.getPostData_APPLICATION_FORM_URLENCODED("filter_tab=0&_fs2ajax=1");
	}
	
	@Override
	public Map<String, String> headers() {
		Map<String, String> headers = new TreeMap<String, String>();
		String sipletoken = user.getCookie("simpletoken").getValue();
		headers.put("X-Simple-Token", sipletoken);
		headers.put("X-Requsted-With", "XMLHttpRequest");
		headers.put("Referer", "http://fotostrana.ru/");
		return headers;
	}

}
