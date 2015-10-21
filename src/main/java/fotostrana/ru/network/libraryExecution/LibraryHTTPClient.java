package fotostrana.ru.network.libraryExecution;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import fotostrana.ru.log.Log;
import fotostrana.ru.network.Connection;
import fotostrana.ru.network.LibraryExecutionRequests;
import fotostrana.ru.network.Request;
import fotostrana.ru.network.TypeProxy;
import fotostrana.ru.network.proxy.AddressProxy;

/**
 * Адаптер для библиотеки HTTPClient
 * 
 */
public class LibraryHTTPClient implements LibraryExecutionRequests {

	public final static HttpHost target = new HttpHost(Request.URL_FOTOSTRANA);
	/**
	 * Соединение к которому относится адаптер
	 */
	protected Connection connection;

	protected CloseableHttpClient httpclient;
	/**
	 * Конфигурация запроса
	 */
	protected RequestConfig config;
	protected InetSocketAddress proxyAddr;

	/**
	 * Создает адаптер для библиотеки HTTPClient
	 * 
	 * @param connection
	 *            соединение для которого нужен адаптер
	 */
	public LibraryHTTPClient(Connection connection) {
		this.connection = connection;
		changeProxy();
	}

	@Override
	public String executeRequest(Request request) throws Exception {
		String result = "";
		HttpRequestBase requestHttp = getHTTPRequest(request);
		if (requestHttp == null)
			return null;

		HttpClientContext context = (request.isContextUsed) ? request
				.getHttpContext() : HttpClientContext.create();
		if (connection.getAddressProxy().getType() == TypeProxy.SOCKS) {
			context.setAttribute("socks.address", proxyAddr);
		}
		CloseableHttpResponse response = null;

		// if (request.isContextUsed)
		response = httpclient.execute(requestHttp, context);
		// else
		// response = httpclient.execute(requestHttp);
		context.removeAttribute("socks.address");
		if (response == null)
			return null;
		if (!request.isUnanswered)
			result = getResultResponse(response, request.getCharsetResponse());
		EntityUtils.consume(response.getEntity());
		response.close();
		return result;
	}

	/**
	 * Возращает результат запроса в текстовом виде, преобразовывая в нижний
	 * регистр
	 * 
	 * @param response
	 *            запрос
	 * @return строка с результатом
	 * @param charsetResponse
	 *            кодировка ответа
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	protected String getResultResponse(CloseableHttpResponse response,
			String charsetResponse) throws IllegalStateException, IOException {
		Scanner content = new Scanner(response.getEntity().getContent(),
				charsetResponse);
		// Scanner content = new Scanner(response.getEntity().getContent());
		StringBuilder resultStringBuilder = new StringBuilder();
		while (content.hasNextLine()) {
			String line = Log.toUTF8(content.nextLine());
			resultStringBuilder.append(line + '\n');
		}
		content.close();
		return resultStringBuilder.toString().toLowerCase();
	}

	/**
	 * Создает http запрос
	 * 
	 * @return null если неудалось создать запрос
	 */
	protected HttpRequestBase getHTTPRequest(Request request) {
		HttpRequestBase result = null;
		switch (request.getType()) {
		case Request.TYPE_GET:
			result = new HttpGet(request.getURL());
			break;
		case Request.TYPE_POST:
			result = new HttpPost(request.getURL());

			HttpEntity entity = request.getRequestData();
			if (entity != null) {
				HttpPost postrequest = (HttpPost) result;
				postrequest.setEntity(entity);
			}
			break;
		default:
			break;
		}
		if (result != null) {
			result.setConfig(config);
			result.addHeader("User-Agent", Connection.USER_AGENT);
			Map<String, String> additionalFields = request.headers();
			if (additionalFields != null) {
				Set<String> keys = additionalFields.keySet();
				for (String key : keys) {
					result.addHeader(key, additionalFields.get(key));
				}
			}
			Header[] h = result.getAllHeaders();
			Arrays.sort(h, new HeaderComparator());
			result.setHeaders(h);

		}
		return result;
	}

	class HeaderComparator implements Comparator<Header> {

		@Override
		public int compare(Header arg0, Header arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}

	}

	@Override
	public void close() {
		try {
			httpclient.close();
		} catch (IOException e) {
		}
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public boolean changeProxy() {
		AddressProxy newAddress = connection.getAddressProxy();
		proxyAddr = new InetSocketAddress(newAddress.ip(), newAddress.port());

		Builder builder = RequestConfig.custom()
				.setConnectTimeout(Connection.TIME_OUT)
				.setConnectionRequestTimeout(Connection.TIME_OUT)
				.setSocketTimeout(Connection.TIME_OUT)
				.setCircularRedirectsAllowed(true).setMaxRedirects(10);

		switch (newAddress.getType()) {
		case HTTP:
			httpclient = HttpClients.createDefault();
			if (newAddress.getProxy().compareTo(AddressProxy.STRING_NO_PROXY) != 0) {
				HttpHost proxy = new HttpHost(newAddress.ip(),
						newAddress.port(), "http");
				builder = builder.setProxy(proxy);
			}
			break;
		case SOCKS:
			Registry<ConnectionSocketFactory> reg = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", new MyConnectionSocketFactory()).build();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
					reg);
			httpclient = HttpClients.custom().setConnectionManager(cm).build();
			break;
		}

		config = builder.build();
		return true;
	}

	static class MyConnectionSocketFactory implements ConnectionSocketFactory {

		public Socket createSocket(final HttpContext context)
				throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context
					.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}

		public Socket connectSocket(final int connectTimeout,
				final Socket socket, final HttpHost host,
				final InetSocketAddress remoteAddress,
				final InetSocketAddress localAddress, final HttpContext context)
				throws IOException, ConnectTimeoutException {
			Socket sock;
			if (socket != null) {
				sock = socket;
			} else {
				sock = createSocket(context);
			}
			if (localAddress != null) {
				sock.bind(localAddress);
			}
			try {
				sock.connect(remoteAddress, connectTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException(ex, host,
						remoteAddress.getAddress());
			}
			return sock;
		}

	}

}
