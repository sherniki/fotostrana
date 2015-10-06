package configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Конфигурация
 * 
 */
public class Configuration implements IConfiguration {
	private HashMap<String, String> map;

	public Configuration() {
		map = new HashMap<String, String>();
	}

	public void setValue(String key, String value) {
		map.put(key, value);
	}

	public String getValue(String key) {
		return map.get(key);
	}

	public List<String> getAllKey() {
		Set<String> allKey = this.map.keySet();
		ArrayList<String> result = new ArrayList<String>();

		for (String key : allKey)
			if (!key.equals("")) {
				result.add(key);
			}

		return result;
	}

}