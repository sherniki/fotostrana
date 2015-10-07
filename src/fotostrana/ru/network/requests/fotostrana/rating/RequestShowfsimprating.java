package fotostrana.ru.network.requests.fotostrana.rating;

import java.util.Map;

import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

public class RequestShowfsimprating extends RequestFotostrana {

	public RequestShowfsimprating(RequestFotostrana parrentRequest) {
		super(parrentRequest);
	}

	@Override
	public void setResult(String result) {
		eventListener.handleEvent(new EventRequestExecutedSuccessfully(this));
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/userphoto/fsimp/showfsimprating/?_fs2ajax=1";
	}

	@Override
	public Map<String, String> headers() {
		Map<String, String> h = parentRequest.headers();
		return h;
	}
}
