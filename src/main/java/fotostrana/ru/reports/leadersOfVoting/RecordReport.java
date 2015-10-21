package fotostrana.ru.reports.leadersOfVoting;

import java.util.Comparator;
import fotostrana.ru.users.User;

/**
 * Запись в отчете о лидерах в голосовании
 * 
 */
public class RecordReport implements Comparator<RecordReport>,
		Comparable<RecordReport> {

	public int id;

	public String name;
	public int gender;
	public int points;
	public int positionInYourRegion;
	public Nomination nomination;
	public Region region;
	public boolean isOnline = true;

	public RecordReport(int id) {
		name = "";
		gender = User.GENDER_UNKNOW;
		this.id = id;
	}

	public RecordReport(int id, int gender) {
		this(id);
		this.gender = gender;
	}

	public RecordReport(int id, int gender, String name) {
		this(id, gender);
		this.name = name;
	}

	public RecordReport(int id, int gender, Region region) {
		this(id, gender);
		this.region = region;
	}

	public RecordReport(int id, int gender, Nomination nomination, Region region) {
		this(id, gender, region);
		this.nomination = nomination;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null)
			this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		if (region != null)
			this.region = region;
	}

	public int getPositionInYourRegion() {
		return positionInYourRegion;
	}

	public void setPositionInYourRegion(int positionInYourRegion) {
		this.positionInYourRegion = positionInYourRegion;
	}

	@Override
	public String toString() {
		return "Регион = " + region.name + " Номинация = " + nomination.name
				+ " ; " + "Имя = " + name + " ; " + "Id = " + id + " ; "
				+ "Голосов =  " + points + " ; " + "Местов регионе = "
				+ positionInYourRegion;
	}

	@Override
	public int compare(RecordReport arg0, RecordReport arg1) {
		return arg0.id - arg1.id;
	}

	@Override
	public int compareTo(RecordReport arg0) {
		return compare(this, arg0);
	}

}
