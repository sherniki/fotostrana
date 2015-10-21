package fotostrana.ru.network.proxy;

import java.util.Date;

import fotostrana.ru.network.TypeProxy;

/**
 * Адресс прокси
 * 
 */
public class AddressProxy implements Comparable<AddressProxy> {
	public static final Date DEFAULT_TIME = new Date(0);
	public static final String STRING_NO_PROXY = "Без прокси:0";
	public static final AddressProxy NO_PROXY = new AddressProxy(
			STRING_NO_PROXY, TypeProxy.HTTP);
	private String privateIp;
	private int privatePort;
	private TypeProxy type;
	public Date timeLastCheck;
	private int countRemainingClones = 1;

	/**
	 * Создает по строке и числу
	 * 
	 * @param ip
	 * @param port
	 */
	public AddressProxy(String ip, int port) {
		this.privatePort = port;
		this.privateIp = ip;
		timeLastCheck = DEFAULT_TIME;
	}

	/**
	 * Создает по строке вида ip:port
	 * 
	 * @param ipAndPort
	 */
	public AddressProxy(String ipAndPort, TypeProxy type) {
		String[] a = ipAndPort.split("[:]");
		privateIp = a[0];
		privatePort = Integer.parseInt(a[1]);
		timeLastCheck = DEFAULT_TIME;
		this.type = type;
	}

	public String getProxy() {
		return privateIp + ":" + privatePort;
	}

	public TypeProxy getType() {
		return type;
	}

	public int port() {
		return privatePort;
	}

	public String ip() {
		return privateIp;
	}

	@Override
	public boolean equals(Object arg0) {
		return compareTo((AddressProxy) arg0) == 0;
	}

	@Override
	public String toString() {
		return getProxy();
	}

	@Override
	public int compareTo(AddressProxy arg0) {
		return this.toString().compareTo(arg0.toString());
	}

	public int getCountRemainingClones() {
		return countRemainingClones;
	}

	public void setCountRemainingClones(int countRemainingClones) {
		this.countRemainingClones = countRemainingClones;
	}
}
