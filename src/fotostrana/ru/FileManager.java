package fotostrana.ru;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fotostrana.ru.log.Log;

/**
 * Отвечает за чтение и запись данных в файлы
 * 
 */
public class FileManager {
	/**
	 * Папка с шаблонами файлов
	 */
	public static String folderWithTemplateFiles = "Шаблоны файлов"
			+ File.separator;
	/**
	 * Файл с шаблоном нерабочих акнет
	 */
	public static String FILE_TAMPLATE_BANNED_USERS = folderWithTemplateFiles
			+ "Нерабочие анкеты.xls";

	/**
	 * Файл с шаблоном рабочих анкет
	 */
	public static String FILE_TAMPLATE_USERS = folderWithTemplateFiles
			+ "Рабочие анкеты.xls";

	public static String FILE_TEMPLATE_SPAM_REPORT = folderWithTemplateFiles
			+ "Отчет о спаме.xls";
	public static String FILE_TEMPLATE_DATABASE_PROFILES = folderWithTemplateFiles
			+ "База нерабочих анкет.xls";
	public static String FILE_TEMPLATE_SENDING = folderWithTemplateFiles
			+ "Отчет о рассылке.xls";

	/**
	 * Файл с шаблоном отчета о заданиях
	 */
	public static String fileTemplateWithTaskReport = folderWithTemplateFiles
			+ "Отчет.xls";

	public static String[] HEADER_SPAM_REPORT = { "Автологин отправителя",
			"ID отправителя", "Имя отправителя", "ID получателя", "Номинация",
			"Время отправки" };
	public static String[] HEADER_SENDING_REPORT = { "Автологин отправителя",
			"ID отправителя", "ID получателя", "Номинация", "Время отправки",
			"Сообщение" };
	/**
	 * Заголовок таблицы отчета
	 */
	public static String[] HEADER_TASK_REPORT = { "Имя", "", "тип голосования",
			"", "id за кого голосовать", "", "Нужно проголосовать", "отдано",
			"старт/ время", "финиш/ время", "продолжительность",
			"голоса на старте", "голоса на финише",
			"прибавилось за время старта", "Место на старте",
			"Место на финише", "Улучшилось за время старта" };

	/**
	 * Заголовок таблицы анкет
	 */
	public static String[] HEADER_USERS = { "e-mail", "", "Пароль от почты",
			"Пароль от ФС", "Ссылка атологоина", "", "Id",
			"Может голосовать в турнире", "Цвет команды", "Быстрые в ГОРОДЕ",
			"Быстрые в ТУРНИРЕ", "Имя", "Место в ТУРНИРЕ", "Голоса в ТУРНИРЕ",
			"Место в ГОРОДЕ", "Голоса в ГОРОДЕ", "Место в СИМПАТИЯ",
			"Голоса в СИМПАТИЯ", "Место в ОЧАРОВАНИЕ", "Голоса в ОЧАРОВАНИЕ",
			"Место в СУПЕР СТАР", "Голоса в СУПЕР СТАР",
			"Ключ для голосования  в турнире", "Причина бана" };

	/**
	 * Заголовок таблицы с забанеными анкетами
	 */
	public static String[] HEADER_DATABASE_PROFILES = { "e-mail", "",
			"Пароль от почты", "Пароль от ФС", "Ссылка атологоина", "", "Id",
			"Есть телефон", "Быстрые в ГОРОДЕ", "Быстрые в ТУРНИРЕ",
			"Причина бана" };

	/**
	 * Запись в конец талбицы
	 */
	public static int INDEX_END_TABLE = Integer.MAX_VALUE - 10;
	/**
	 * Название таблицы с нерабочими анкетами
	 */
	public static String NAME_SHEET_BANNED_USERS = "Нерабочие";
	public static String NAME_SHEET_SPAM_REPORT = "Отчет о спаме";
	public static String NAME_SHEET_SENDING = "Отчет";
	public static String NAME_SHEET_DATABASE_PROFILES = "Нерабочие";
	/**
	 * Название талбицы отчета
	 */
	public static String NAME_SHEET_TASK_REPORT = "Голосование в номинациях";

	/**
	 * Название таблицы с рабочими анкетами
	 */
	public static String NAME_SHEET_USERS = "Рабочие";
	/**
	 * Результат чтения несуществующей строки
	 */
	public static String[] NULL_ROW = new String[0];

	/**
	 * Добавляет новую строку к такблице Excel книги
	 * 
	 * @param sheet
	 *            таблица
	 * @param indexRow
	 *            индекс новой строки
	 * @param values
	 *            значения
	 */
	private static void addNewRowToSheet(Sheet sheet, int indexRow,
			String[] values) {
		Row newRow = sheet.createRow(indexRow);
		for (int i = 0; i < values.length; i++) {
			Cell newCell = newRow.createCell(i);
			newCell.setCellType(Cell.CELL_TYPE_STRING);
			newCell.setCellValue(values[i]);
		}
	}

	public static void clearFile(String filename) {
		File file = new File(filename);
		boolean result = file.delete();
		if (result) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.LOGGING.addFileLog(
						"Не удалось создать файл:" + file.getAbsolutePath(),
						Log.TYPE_NEGATIVE);
				Log.LOGGING.printStackTraceException(e);
			}
			Log.LOGGING.addFileLog(
					"Файл успешно очищен:" + file.getAbsolutePath(),
					Log.TYPE_POSITIVE);
		} else
			Log.LOGGING.addFileLog(
					"Не удалось удалить файл:" + file.getAbsolutePath(),
					Log.TYPE_NEGATIVE);
	}

	/**
	 * Делает резервную копию файлов в заданую директорию
	 * 
	 * @param directoryDestination
	 * @param files
	 */
	public static void backup(String directoryDestination, List<String> files) {
		Log.LOGGING.addFileLog("Начато резервное копирование файлов.",
				Log.TYPE_NEUTRAL);
		for (String file : files) {
			copyFileToDirectory(file, directoryDestination);
		}
		Log.LOGGING.addFileLog("Завершено резервное копирование файлов.",
				Log.TYPE_POSITIVE);
	}

	/**
	 * Очищает талбицу от данных начиная с заданой строки
	 * 
	 * @param sheet
	 *            таблица
	 * @param startIndex
	 *            индекс строки, начиная с которого будут удалены данные
	 */
	private static void clearSheet(Sheet sheet, int startIndex) {
		if (sheet != null) {
			if (startIndex < 0)
				startIndex = 0;
			for (int i = startIndex; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null)
					sheet.removeRow(row);
			}
		}
	}

	/**
	 * Копирует файл в заданую директорию
	 * 
	 * @param fileSource
	 *            файл
	 * @param directoryDestination
	 *            директрория
	 */
	public static void copyFileToDirectory(String fileSource,
			String directoryDestination) {
		File fileSrc = new File(fileSource);
		File fileDst = new File(directoryDestination);
		try {
			FileUtils.copyFileToDirectory(fileSrc, fileDst);
		} catch (IOException e) {
			Log.LOGGING.addFileLog("Ошибка копирования файла " + fileSource
					+ " : " + e.getMessage(), Log.TYPE_NEGATIVE);
		}
	}

	/**
	 * Создает новую таблицу в книге
	 * 
	 * @param workbook
	 *            книга
	 * @param nameSheet
	 *            имя таблицы
	 * @param header
	 *            заголовок таблицы
	 * @return новая таблица
	 */
	private static Sheet createXLSSheet(Workbook workbook, String nameSheet,
			String[] header) {
		Sheet newSheet = workbook.createSheet(nameSheet);
		addNewRowToSheet(newSheet, 0, header);
		return newSheet;
	}

	/**
	 * Создает книгу с таблицей
	 * 
	 * @param nameSheet
	 *            имя таблицы
	 * @param header
	 *            заголовок таблицы
	 * @return
	 */
	private static Workbook createXLSWorkbook(String nameSheet, String[] header) {
		Workbook newWorkbook = new HSSFWorkbook();
		createXLSSheet(newWorkbook, nameSheet, header);
		return newWorkbook;

	}

	/**
	 * Читает строку таблицы
	 * 
	 * @param sheet
	 *            таблица
	 * @param rowIndex
	 *            индекс строки
	 * @return значения ячеек строки, null - если неудалось прочитать строку
	 */
	private static String[] getXLSRow(Sheet sheet, int rowIndex) {
		Row currentRow = sheet.getRow(rowIndex);
		if (currentRow != null) {
			int rowLength = currentRow.getLastCellNum();
			if (rowLength > 0) {
				String[] row = new String[rowLength];
				for (int k = 0; k < row.length; k++) {
					row[k] = "";
					Cell cell = currentRow.getCell(k);
					if (cell != null) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						row[k] = cell.getStringCellValue();
					}
				}
				return row;
			}
		}
		return null;
	}

	/**
	 * Загружает из файла забаненые анкеты
	 * 
	 * @param file
	 *            файл
	 * @return
	 */
	public static List<String[]> readBannedUsers(String file) {
		return readXLS(file, NAME_SHEET_BANNED_USERS, 1);
	}

	/**
	 * Загружает из файла отчет о отправке спама
	 * 
	 * @param file
	 *            xls файл с отчетом
	 * @return
	 */
	public static List<String[]> readSpamReport(String file) {
		return readXLS(file, NAME_SHEET_SPAM_REPORT, 1);
	}

	/**
	 * Читает текстовый файл
	 * 
	 * @param file
	 *            текстовый файл
	 * @return никогда невозращает null,
	 */
	public static List<String> readTextFile(String file) {
		List<String> result = new LinkedList<String>();
		Scanner inFile = null;
		Log.LOGGING.addFileLog("Начата загрузка файла: " + file,
				Log.TYPE_NEUTRAL);
		try {
			inFile = new Scanner(new File(file));
			while (inFile.hasNextLine()) {
				result.add(inFile.nextLine());
			}
			Log.LOGGING.addFileLog("Завершена загрузка файла: " + file,
					Log.TYPE_POSITIVE);
		} catch (FileNotFoundException e) {
			Log.LOGGING.addFileLog("Ошибка! Файл :" + file + " ненайден.",
					Log.TYPE_NEGATIVE);
		} finally {
			if (inFile != null)
				inFile.close();
		}
		return result;
	}

	/**
	 * Загружает из файла рабочие анкеты
	 * 
	 * @param file
	 *            файл
	 * @return
	 */
	public static List<String[]> readWorkingUsers(String file) {
		return readXLS(file, NAME_SHEET_USERS, 1);
	}

	/**
	 * Читает данные из таблицы XLS
	 * 
	 * @param file
	 *            файл с книгой
	 * @param nameTable
	 *            название таблицы
	 * @param startIndex
	 *            индекс строки начиная с которого будут читаться данные
	 * @return пустой список, если неудалось прочитать
	 */
	public static List<String[]> readXLS(String file, String nameTable,
			int startIndex) {
		List<String[]> result = new LinkedList<String[]>();
		Workbook workbook = readXLSWorkbook(file);
		if (workbook != null) {
			Sheet sheet = workbook.getSheet(nameTable);
			if (sheet != null) {
				for (int i = startIndex; i <= sheet.getLastRowNum(); i++) {
					String[] value = getXLSRow(sheet, i);
					if (value != null)
						result.add(value);
				}
			}
		}
		return result;
	}

	/**
	 * Читает таблицу из файла
	 * 
	 * @param file
	 *            файл
	 * @param nameSheet
	 *            название таблицы
	 * @return null если неудалось прочитать таблицу
	 */
	private static Sheet readXLSSheet(String file, String nameSheet) {
		Workbook workbook = readXLSWorkbook(file);
		if (workbook != null) {
			return workbook.getSheet(nameSheet);
		}
		return null;
	}

	/**
	 * Читает книгу из XLS файла
	 * 
	 * @param file
	 *            файл
	 * @return null -если неудалось загрузить файл
	 */
	public static Workbook readXLSWorkbook(String file) {
		Workbook workbook = null;
		InputStream inputStream = null;
		try {
			Log.LOGGING.addFileLog("Начата загрузка файла: " + file,
					Log.TYPE_NEUTRAL);
			inputStream = new FileInputStream(new File(file));
			try {
				workbook = new HSSFWorkbook(inputStream);
				Log.LOGGING.addFileLog("Завершена загрузка файла: " + file,
						Log.TYPE_POSITIVE);
			} catch (IOException e) {
				workbook = null;
				Log.LOGGING.addFileLog("Ошибка чтения файл " + file
						+ ". Ошибка: " + e.getMessage(), Log.TYPE_NEGATIVE);
			}
		} catch (FileNotFoundException e) {
			Log.LOGGING.addFileLog("Ненайден файл " + file, Log.TYPE_NEGATIVE);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return workbook;
	}

	/**
	 * Загружает в файл нерабочие анкеты
	 * 
	 * @param file
	 *            файл
	 * @param data
	 *            данные
	 */
	public static void writeBannedUsers(String file, List<String[]> data) {
		writeDataInXLS(file, NAME_SHEET_BANNED_USERS, data, HEADER_USERS,
				INDEX_END_TABLE, FILE_TAMPLATE_BANNED_USERS, -1, true);
	}

	/**
	 * Записывает данные в файл XLS в заданую начиная с заданого индекса таблицу
	 * 
	 * @param file
	 *            файл с книгой
	 * @param nameSheet
	 *            название таблицы в которую будут записываться данные
	 * @param data
	 *            данные
	 * @param header
	 *            заголовок таблицы
	 * @param startIndex
	 *            индекс строки начиная с которой будут записываться данные
	 * @param templateFile
	 *            файл с шаблоном
	 * @param indexClearValue
	 *            индекс строки начиная с которой будет очищена талбица перед
	 *            записью (если значение <0 данные удаляться небудут)
	 * @param overwrite
	 *            флаг перезаписи данных,если true - новые данные будут
	 *            записываны на месте привидущих, если false,то строки начиная с
	 *            indexRow будут передвинуты вниз, а на их место будут записаны
	 *            новые
	 * 
	 */
	public static void writeDataInXLS(String file, String nameSheet,
			List<String[]> data, String[] header, int startIndex,
			String templateFile, int indexClearValue, boolean overwrite) {
		boolean isLoadTemplate = false;
		Workbook workbook = readXLSWorkbook(file);
		if (workbook == null) {
			// Загружаем из шаблона
			workbook = readXLSWorkbook(templateFile);
			isLoadTemplate = true;
		}
		if (workbook == null)
			// создаем по заголовку
			workbook = createXLSWorkbook(nameSheet, header);

		Sheet sheet = workbook.getSheet(nameSheet);

		if ((sheet == null) && (!isLoadTemplate)) {
			sheet = readXLSSheet(templateFile, nameSheet);
		}
		if (sheet == null)
			createXLSSheet(workbook, nameSheet, header);
		if (indexClearValue > -1) {
			clearSheet(sheet, indexClearValue);
		}

		writeDataInSheet(sheet, data, startIndex, overwrite);
		writeXLSWorkbook(file, workbook);
	}

	/**
	 * @param sheet
	 * @param data
	 * @param startIndex
	 * @param overwrite
	 *            флаг перезаписи данных,если true - новые данные будут
	 *            записываны на месте привидущих, если false,то строки начиная с
	 *            indexRow будут передвинуты вниз, а на их место будут записаны
	 *            новые
	 * @return
	 */
	private static Sheet writeDataInSheet(Sheet sheet, List<String[]> data,
			int startIndex, boolean overwrite) {
		try {
			int currentIndex = (startIndex == INDEX_END_TABLE) ? (sheet
					.getLastRowNum() + 1) : (startIndex);
			if ((!overwrite) && (currentIndex < sheet.getLastRowNum())) {
				int lastIndex = sheet.getLastRowNum();
				sheet.shiftRows(currentIndex, lastIndex, data.size());
			}
			for (String[] rowValue : data) {
				addNewRowToSheet(sheet, currentIndex, rowValue);
				currentIndex++;
			}
		} catch (Exception e) {
			Log.LOGGING.printStackTraceException(e);
		}
		return sheet;
	}

	public static void writeSpamReport(String file, List<String[]> data) {
		writeDataInXLS(file, NAME_SHEET_SPAM_REPORT, data, HEADER_SPAM_REPORT,
				1, FILE_TEMPLATE_SPAM_REPORT, -1, false);
	}

	public static void writeSendingReport(String file, List<String[]> data) {
		writeDataInXLS(file, NAME_SHEET_SENDING, data, HEADER_SENDING_REPORT,
				1, FILE_TEMPLATE_SENDING, -1, false);
	}

	/**
	 * Записывает в файл отчет о выполнении заданий
	 * 
	 * @param file
	 *            файл с отчетом
	 * @param data
	 *            данные
	 */
	public static void writeTaskReport(String file, List<String[]> data) {
		writeDataInXLS(file, NAME_SHEET_TASK_REPORT, data, HEADER_TASK_REPORT,
				1, fileTemplateWithTaskReport, -1, false);
	}

	/**
	 * Записывает файл
	 * 
	 * @param file
	 *            файл
	 * @param data
	 *            данные
	 * @param append
	 *            дозапись файла, true - если необхдимо дописать в конец файла
	 */
	public static void writeTextFile(String file, Collection<?> data,
			boolean append) {
		FileWriter out = null;
		try {
			Log.LOGGING.addFileLog("Начата перезапись файла: " + file,
					Log.TYPE_NEUTRAL);
			out = new FileWriter(new File(file), append);
			for (Object line : data) {
				out.write(line.toString() + "\r\n");
			}
			Log.LOGGING.addFileLog("Завершена запись файла: " + file,
					Log.TYPE_POSITIVE);
		} catch (IOException e) {
			Log.LOGGING.addFileLog("Ошибка сохранения в файл :" + file + " .",
					Log.TYPE_NEGATIVE);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
				}
		}
	}

	/**
	 * Записывает в файл рабочие анкеты анкеты
	 * 
	 * @param file
	 *            файл
	 * @param data
	 *            данные
	 */
	public static void writeWorkingUsers(String file, List<String[]> data) {
		writeDataInXLS(file, NAME_SHEET_USERS, data, HEADER_USERS, 1,
				FILE_TAMPLATE_USERS, 1, true);
	}

	/**
	 * Записывает в файл базу нерабочих анкет
	 * 
	 * @param file
	 *            файл
	 * @param data
	 *            данные
	 */
	public static void writeDatabaseBannedProfiles(String file,
			List<String[]> data) {
		writeDataInXLS(file, NAME_SHEET_DATABASE_PROFILES, data,
				HEADER_DATABASE_PROFILES, 1, FILE_TEMPLATE_DATABASE_PROFILES,
				1, true);
	}

	/**
	 * Перезаписывает книгу XLS
	 * 
	 * @param file
	 *            файл
	 * @param workbook
	 *            книга
	 */
	public static void writeXLSWorkbook(String file, Workbook workbook) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			Log.LOGGING.addFileLog("Начата перезапись файла: " + file,
					Log.TYPE_NEUTRAL);
			workbook.write(out);
			Log.LOGGING.addFileLog("Завершена запись файла: " + file,
					Log.TYPE_POSITIVE);
		} catch (IOException e) {
			Log.LOGGING.addFileLog("Ошибка записи файла: " + file
					+ ". Ошибка : " + e.getMessage(), Log.TYPE_NEGATIVE);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
				}
		}

	}

}
