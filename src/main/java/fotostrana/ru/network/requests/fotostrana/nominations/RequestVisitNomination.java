package fotostrana.ru.network.requests.fotostrana.nominations;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

public class RequestVisitNomination extends RequestFotostrana {
	public RequestVisitNomination(RequestFotostrana parrentRequest) {
		super(parrentRequest);
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/contest/" + user.id + "/";
	}

	@Override
	public void setResult(String result) {
		Event event = null;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				user.name = ParserJSON.getSubstring(result,
						"<span class=\"user-name trebuchet ellipsis\">",
						"</span>");
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

}
