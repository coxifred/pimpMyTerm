package pimpmyterm.beans;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Log {
	Date date;
	String s_date;
	String severity;
	String from;
	String message;
	String thread;

	public Log() {
	}

	public Log(Date date, String s_date, String thread, String severity, String from, String message) {
		this.date = date;
		this.s_date = s_date;
		this.thread = thread;
		this.severity = severity;
		this.from = from;
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getS_date() {
		return s_date;
	}

	public void setS_date(String s_date) {
		this.s_date = s_date;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public static Log copyLog(Log aLog) {
		return new Log(aLog.getDate(), aLog.getS_date(), aLog.getThread(), aLog.getSeverity(), aLog.getFrom(),
				aLog.getMessage());
	}

	public static List<Log> getLogs(String include, String exclude, List<Log> entries) {
		Vector<Log> returnLog = new Vector<Log>();
		if (include == null && exclude == null) {
			return entries;
		} else {

			for (Log originalLog : entries) {
				Log aLog = Log.copyLog(originalLog);
				Boolean includeB = false;
				if (include != null && !"".equals(include)) {
					if (aLog.getS_date().contains(include)) {
						includeB = true;
						aLog.setS_date(aLog.getS_date().replaceAll(include, "<font color=red>" + include + "</font>"));
					}
					if (aLog.getMessage().contains(include)) {
						includeB = true;
						aLog.setMessage(
								aLog.getMessage().replaceAll(include, "<font color=#fc00f8>" + include + "</font>"));
					}
					if (aLog.getSeverity().contains(include)) {
						includeB = true;
						aLog.setSeverity(
								aLog.getSeverity().replaceAll(include, "<font color=red>" + include + "</font>"));
					}
					if (aLog.getThread().contains(include)) {
						includeB = true;
						aLog.setThread(aLog.getThread().replaceAll(include, "<font color=red>" + include + "</font>"));
					}
					if (aLog.getFrom().contains(include)) {
						includeB = true;
						aLog.setFrom(aLog.getFrom().replaceAll(include, "<font color=red>" + include + "</font>"));
					}
				} else {
					includeB = true;
				}

				Boolean excludeB = false;
				if (exclude != null && !"".equals(exclude)) {
					if (aLog.getS_date().contains(exclude)) {
						excludeB = true;
					}
					if (aLog.getMessage().contains(exclude)) {
						excludeB = true;
					}
					if (aLog.getSeverity().contains(exclude)) {
						excludeB = true;
					}
					if (aLog.getThread().contains(exclude)) {
						excludeB = true;
					}
					if (aLog.getFrom().contains(exclude)) {
						excludeB = true;
					}
				} else {
					excludeB = false;
				}

				if (includeB && !excludeB) {
					returnLog.add(aLog);

				}
			}

		}

		return returnLog;
	}

}
