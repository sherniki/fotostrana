package fotostrana.ru.network;

/**
 * Создает JavaScript код для распространеных действий
 * 
 */
public class JavaScriptGenerator {

	/**
	 * Нажатие на элемент заданого класса
	 * 
	 * @param className
	 *            класс элемента
	 * @param index
	 *            порядковый номер компонент
	 * @return
	 */
	public static String onClick(String className, int index) {
		return "var elements=" + " document.getElementsByClassName('"
				+ className + "');" + '\n'
				// + "alert( elements.length );"
				+ "elements[" + index + "].click();" + '\n';
	}

	/**
	 * Нажатие на первый компонент заданого класса
	 * 
	 * @param className
	 *            класс элемента
	 * @return
	 */
	public static String onClick(String className) {
		return onClick(className, 0);
	}

	/**
	 * Заполняет текстовое поле
	 * 
	 * @param componentId
	 *            ид текстового компонента
	 * @param value
	 *            значение
	 * @return
	 */
	public static String setTextValue(String componentId, String value) {
		return "var component = document.getElementById('" + componentId
				+ "');" + '\n' + "component.value='" + toJSString(value) + "';"
				+ '\n';
	}

	public static String toJSString(String value) {
		String[] lines = value.split("\n");
		String newValue = lines[0];
		for (int i = 1; i < lines.length; i++) {
			newValue += "\\n" + lines[i];
		}
		return newValue;
	}
}
