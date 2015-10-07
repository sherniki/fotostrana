package fotostrana.ru.network.filters.proxy;

import java.util.Date;

import fotostrana.ru.network.proxy.AddressProxy;

/**
 * Отбирает прокси у которых время проверки работоспособности больше чем заданый
 * интевал времени
 * 
 */
public class FilterByTimeLaskChecking implements ProxyFilter {
	private long timeInterval;
	private Date currentTime = new Date();

	/**
	 * Отбирает прокси у которых время проверки работоспособности больше чем
	 * заданый интевал времени
	 * 
	 * @param timeInterval
	 *            интервал времени в минутах
	 */
	public FilterByTimeLaskChecking(int timeInterval) {
		setTimeInterval(timeInterval);
		;
	}

	@Override
	public boolean filtrate(AddressProxy proxy) {
		return (currentTime.getTime() - proxy.timeLastCheck.getTime() > timeInterval);
	}

	public void setTimeInterval(int interval) {
		if (interval <= 0)
			interval = 1;
		timeInterval = interval * 60 * 1000;
	}

}
