package fotostrana.ru.network.requests;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * Интерфейс для обработки состояния браузера
 * 
 */
public interface RequestDJNativeBrowser {

	/**
	 * Браузер завершил загрузку запрашиваемой страницы
	 * 
	 * @param webBrowser
	 *            браузер который выполнил запрос
	 */
	void сallbackPageFinishedLoading(JWebBrowser webBrowser);

	// /**
	// * выполняет JavaScrip
	// *
	// * @param url
	// * адрес страницы на которой неолбходимо выполнить
	// * @param script
	// * скрипт
	// */
	// public void executeJavaScript(String url, String script);
}
