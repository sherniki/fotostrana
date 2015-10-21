package fotostrana.ru.reports.leadersOfVoting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fotostrana.ru.FileManager;
import fotostrana.ru.reports.Report;

/**
 * Отчет
 * 
 */
public class ReportLeadersOfVoting implements Report {
	/**
	 * Записи отчета
	 */
	private Set<RecordReport> records;
	/**
	 * Номинация
	 */
	public Nomination nomination;
	/**
	 * Дата создания отчета
	 */
	public Date created;

	public ReportLeadersOfVoting(Nomination nomination) {
		created = new Date();
		this.nomination = nomination;
		records = new TreeSet<RecordReport>();
	}

	/**
	 * Добавляет запись
	 * 
	 * @param record
	 */
	public void addRecord(RecordReport record) {
		records.add(record);
	}

	/**
	 * Добвавляет к отчету список записей
	 * 
	 * @param newRecords
	 */
	public void addRecords(Collection<RecordReport> newRecords) {
		records.addAll(newRecords);
	}

	public void print() {
		// sort(new SortByPoints());
		int i = 1;
		List<String> lines = new ArrayList<String>();
		for (RecordReport record : records) {
			String line = i + ") " + record.toString();
			lines.add(line);
			System.out.println(line);
			i++;
		}
		FileManager.writeTextFile("Лидеры в " + nomination.name + ".txt",
				lines, false);
	}

	// public void sort(Comparator<RecordReport> comparator) {
	// Collections.sort(records, comparator);
	// }

	public Set<RecordReport> getListRecord() {
		return records;
	}

}

class SortByPoints implements Comparator<RecordReport> {

	@Override
	public int compare(RecordReport arg0, RecordReport arg1) {
		return arg0.points - arg1.points;
	}

}
