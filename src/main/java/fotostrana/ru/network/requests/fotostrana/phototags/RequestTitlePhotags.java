package fotostrana.ru.network.requests.fotostrana.phototags;

import fotostrana.ru.ParserJSON;
import fotostrana.ru.events.Event;
import fotostrana.ru.events.network.EventBan;
import fotostrana.ru.events.network.EventIsNotAuthorization;
import fotostrana.ru.events.network.EventRequestExecutedSuccessfully;
import fotostrana.ru.network.requests.fotostrana.RequestFotostrana;

public class RequestTitlePhotags extends RequestFotostrana {
	private RequestVotePhototags requestPhototags;

	public RequestTitlePhotags(RequestVotePhototags parrentRequest) {
		super(parrentRequest);
		requestPhototags = parrentRequest;
	}

	@Override
	public String getURL() {
		return "http://fotostrana.ru/phototags/"
				+ requestPhototags.nomination.alias + '-'
				+ requestPhototags.photoId + "/";
	}

	@Override
	public void setResult(String result) {
		Event event;
		if (loginFilter.filtrate(result)) {
			if (bannedFilter.filtrate(result)) {
				requestPhototags.layoutTab = ParserJSON.getSubstring(result,
						"tlen.layout.tab = '", "';");
				requestPhototags.layoutTag = ParserJSON.getSubstring(result,
						"tlen.layout.tag = '", "';");
				event = new EventRequestExecutedSuccessfully(this);
			} else
				event = new EventBan(this);
		} else
			event = new EventIsNotAuthorization(this);
		eventListener.handleEvent(event);
	}

}
