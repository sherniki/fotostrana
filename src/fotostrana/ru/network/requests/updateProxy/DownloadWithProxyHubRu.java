package fotostrana.ru.network.requests.updateProxy;

import fotostrana.ru.events.EventListener;
import fotostrana.ru.events.network.updateProxy.EventAutorizatinError;
import fotostrana.ru.events.network.updateProxy.EventFrequentUpdate;
import fotostrana.ru.events.network.updateProxy.EventSuccessfulUpdateProxy;

public class DownloadWithProxyHubRu extends RequestUpdateProxy {
	private String token;
//	private String[] type = { "HTTP", "HTTPS" };
//	private String[] anon = { "HIA", "ANM" };

	public DownloadWithProxyHubRu(String token, EventListener listener) {
		super(listener);
		this.token = token;
		siteName = "proxyhub.ru";

	}

	@Override
	public String getURL() {
		return "http://proxyhub.ru/proxies/txt/?type%5B%5D=HTTP&type%5B%5D=HTTPS&anon%5B%5D=HIA&anon%5B%5D=ANM&ports=&sort_by=trust&sort_order=desc&per_page=200&uniq_ip=on&code="
				+ token;
	}

	@Override
	public void setResult(String result) {
		// System.out.println(result);
		if (result.indexOf("proxyhub") != -1) {
			eventListener.handleEvent(new EventAutorizatinError(this));
			return;
		}
		if (result.indexOf("# превышено кол-во допустимых обращений.") == -1) {
			downloadProxy = result.split("[\n]");
			eventListener.handleEvent(new EventSuccessfulUpdateProxy(this));
		} else {
			eventListener.handleEvent(new EventFrequentUpdate(this));
		}
	}

	@Override
	public String getCharsetResponse() {
		return "UTF-8";
	}

}
