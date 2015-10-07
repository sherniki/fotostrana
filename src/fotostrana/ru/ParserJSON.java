package fotostrana.ru;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Предназначен для извлечения информации из строки в формате JSON
 * 
 */
public class ParserJSON {
	public static String[] SEPARATORS = { ",", "}" };
	/**
	 * Возращаемое значение когда неудалось прочитать число
	 */
	public static final int ERROR_INT = Integer.MIN_VALUE;

	/**
	 * Возращает подстроку находящююся между задаными строками
	 * 
	 * @param source
	 *            источник
	 * @param startValue
	 *            подстрока начало
	 * @param finishValue
	 *            подстрока конец
	 * @return null если нельзя выделить подстроку
	 */
	public static String getSubstring(String source, String startValue,
			String finishValue) {
		return getSubstring(source, startValue, finishValue, 0);
	}

	/**
	 * Возращает подстроку находящююся между задаными строками, поиск ведется с
	 * заданого индеска
	 * 
	 * @param source
	 *            источник
	 * @param startValue
	 *            подстрока начало
	 * @param finishValue
	 *            подстрока конец
	 * @param startIndex
	 *            индекс начала поиска
	 * @return null если нельзя выделить подстроку
	 */
	public static String getSubstring(String source, String startValue,
			String finishValue, int startIndex) {
		if ((source == null) || (startValue == null) || (finishValue == null)
				|| (startIndex < 0) || (startIndex > source.length()))
			return null;
		int iStart = source.indexOf(startValue, startIndex);
		if (iStart > -1) {
			iStart += startValue.length();
			int iFinish = source.indexOf(finishValue, iStart);
			if (iFinish > -1) {
				String result = source.substring(iStart, iFinish);
				return result;
			}
		}
		return null;
	}

	/**
	 * Возращает значение строкового параметра по имени
	 * 
	 * @param source
	 *            строка источник
	 * @param nameParameter
	 *            имя параметра
	 * @return null если неудалось получить значение
	 */
	public static String getString(String source, String nameParameter) {
		return getString(source, nameParameter, 0);
	}

	/**
	 * Возращает значение строкового параметра по имени,начиная с заданого
	 * индекса
	 * 
	 * @param source
	 *            строка источник
	 * @param nameParameter
	 *            имя параметра
	 * @param startIndex
	 *            начальный индекс
	 * @return null если неудалось получить значение
	 */
	public static String getString(String source, String nameParameter,
			int startIndex) {
		if ((nameParameter == null) || (source == null) || (startIndex < 0)
				|| (startIndex > source.length()))
			return null;
		return getSubstring(source, nameParameter + "\":\"", "\"", startIndex);
	}

	/**
	 * Возращщает целое число по названию параметра
	 * 
	 * @param source
	 *            строка источник
	 * @param nameParameter
	 *            имя параметра
	 * @return Integer.MIN_VALUE если неудалось получить значение
	 */
	public static int getInt(String source, String nameParameter) {
		return getInt(source, nameParameter, 0);
	}

	/**
	 * Возращщает целое число по названию параметра,начиная с заданого символа
	 * 
	 * @param source
	 *            строка источник
	 * @param nameParameter
	 *            имя параметра
	 * @param startIndex
	 *            начальный индекс
	 * @return Integer.MIN_VALUE если неудалось получить значение
	 */
	public static int getInt(String source, String nameParameter, int startIndex) {
		if ((nameParameter == null) || (source == null) || (startIndex < 0)
				|| (startIndex > source.length()))
			return ERROR_INT;
		for (String separator : SEPARATORS) {
			String strResult = getSubstring(source, nameParameter + "\":",
					separator, startIndex);
			try {
				int r = Integer.parseInt(strResult);
				return r;
			} catch (Exception e) {
				continue;
			}

		}
		return ERROR_INT;
	}

	/**
	 * Возращает список строковых значений
	 * 
	 * @param source
	 *            источник
	 * @param name
	 *            название елемента
	 * @return null - если некоректные аргументы, если в строке источнике нет
	 *         елементво с заданым именм, то возращается пустой список
	 */
	public static List<String> getStrings(String source, String name) {
		if ((source == null) || (name == null))
			return null;
		List<String> list = new ArrayList<String>();
		int i = 0;
		String nameElement = name + "\":";
		while (true) {
			i = source.indexOf(nameElement, i);
			if (i == -1)
				break;
			String currentValue = getString(source, name, i);
			if (currentValue == null)
				currentValue = "";
			list.add(currentValue);
			i += name.length();
		}
		return list;
	}

	/**
	 * Возращает список числовы значений
	 * 
	 * @param source
	 *            источник
	 * @param name
	 *            название елемента
	 * @return null - если некоректные аргументы, если в строке источнике нет
	 *         елементво с заданым именем, то возращается пустой список
	 */
	public static List<Integer> getIntegers(String source, String name) {
		if ((source == null) || (name == null))
			return null;
		List<Integer> list = new ArrayList<Integer>();
		int i = 0;
		String nameElement = name + "\":";
		while (true) {
			i = source.indexOf(nameElement, i);
			if (i == -1)
				break;
			Integer currentValue = getInt(source, name, i);
			// if (currentValue == null)
			// currentValue = "";
			list.add(currentValue);
			i += name.length();
		}
		return list;
	}

	/**
	 * Возращает список подстрок
	 * 
	 * @param source
	 *            источник
	 * @param prefix
	 *            префикс
	 * @param suffix
	 *            суфикс искомой подстроки
	 * @return
	 */
	public static List<String> getSubstings(String source, String prefix,
			String suffix) {
		if ((source == null) || (prefix == "") || (suffix == null))
			return null;
		List<String> list = new LinkedList<String>();
		int i = 0;
		while (true) {
			i = source.indexOf(prefix, i);
			if (i == -1)
				break;
			String substring = getSubstring(source, prefix, suffix, i);
			if (substring != null) {
				list.add(substring);
				i += prefix.length();
			} else
				break;

		}
		return list;
	}

	/**
	 * Возращает список чиселвыделеных по подстрокам
	 * 
	 * @param source
	 *            источник
	 * @param prefix
	 *            префикс
	 * @param suffix
	 *            суфикс искомой подстроки
	 * @return
	 */
	public static List<Integer> getIntegersOfSubstrings(String source,
			String prefix, String suffix) {
		List<String> stringList = getSubstings(source, prefix, suffix);
		if (stringList == null)
			return null;
		List<Integer> list = new LinkedList<Integer>();
		for (String substring : stringList)
			try {
				Integer value = new Integer(substring);
				list.add(value);
			} catch (Exception e) {
			}
		return list;
	}

	/**
	 * Возвращает значение по имениб елемент заканчивается ","
	 * 
	 * @param source
	 * @param name
	 * @param startIndex
	 * @return
	 */
	public static Boolean getBoolean(String source, String name, int startIndex) {
		return getBoolean(source, name, ",", startIndex);
	}

	/**
	 * Возвращает значение по имени и символу окончания елемента
	 * 
	 * @param source
	 * @param nameАы
	 * @param finishSymbol
	 *            символ окончание елемента
	 * @param startIndex
	 * @return
	 */
	public static Boolean getBoolean(String source, String name,
			String finishSymbol, int startIndex) {
		if ((source == null) || (name == null) || (startIndex < 0)
				|| (startIndex > source.length()))
			return null;
		String s = getSubstring(source, name + "\":", finishSymbol, startIndex);
		if (s != null) {
			s = s.toLowerCase();
			if (s.compareTo("false") == 0)
				return new Boolean(false);
			if (s.compareTo("true") == 0)
				return new Boolean(true);
		}
		return null;
	}
}
