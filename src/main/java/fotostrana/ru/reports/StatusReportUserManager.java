package fotostrana.ru.reports;

public class StatusReportUserManager implements Report {
	public int countWorkingProfiles;
	public int countBannedProfiles;

	public StatusReportUserManager() {
		// TODO Auto-generated constructor stub
	}

	public StatusReportUserManager(int working, int banned) {
		countWorkingProfiles = working;
		countBannedProfiles = banned;
	}
}
