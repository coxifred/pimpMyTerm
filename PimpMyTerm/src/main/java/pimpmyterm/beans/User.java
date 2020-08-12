package pimpmyterm.beans;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pimpmyterm.core.Core;
import pimpmyterm.utils.Fonctions;
import com.thoughtworks.xstream.XStream;

public class User {

	String description;
	String email;
	String sessionId;
	transient String remoteAdress;
	Map<String, String> workbenchs = new HashMap<String, String>();
	Map<String,List<String>> sessionsData=new HashMap<String,List<String>>();
	Map<String,String> instanceIdToSessionId=new HashMap<String,String>();

	public User() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public SessionConfig getSessionById(String sessionId) {
		for (SessionConfig aSession : getSessions()) {
			if (aSession.getSessionId().equals(sessionId)) {
				return aSession;
			}
		}
		return null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	String name;
	String password;
	UserSettings settings = new UserSettings();
	List<SessionConfig> sessions = new ArrayList<SessionConfig>();

	Date lastUpdate = new Date();

	public static Boolean isAuthentified(String user, String passwd) {
		Boolean retour = false;
		if (Core.getInstance().getUsers().values().size() == 0) {
			User admin = new User();
			admin.setName("admin");
			admin.setPassword(Core.getInstance().getAdminPassword());
			Core.getInstance().getUsers().put("admin", admin);
		}
		for (User aUser : Core.getInstance().getUsers().values()) {
			if (aUser.getName().equals(user) && aUser.getPassword().contentEquals(passwd)) {
				return true;
			}
		}

		return retour;
	}

	public static User getUserByName(String user) {
		for (User aUser : Core.getInstance().getUsers().values()) {
			if (aUser.getName().equals(user)) {
				return aUser;
			}
		}
		return null;
	}

	public void saveUser() {
		XStream aStream = new XStream();
		try {
			File aFile = new File(System.getProperty("dataPath", Core.getInstance().getDataPath()));
			Fonctions.trace("DBG", "Saving file to " + aFile.getAbsolutePath() + "/pimpMyTerm_user_" + name + ".xml",
					"CORE");
			aStream.toXML(this, new FileWriter(aFile.getAbsolutePath() + "/pimpMyTerm_user_" + name + ".xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, User> loadUsers() {
		Map<String, User> userList = new HashMap<String, User>();
		XStream aStream = new XStream();
		File aFile = new File(System.getProperty("dataPath", Core.getInstance().getDataPath()));
		if (aFile.exists()) {
			for (File theFile : aFile.listFiles()) {
				if (theFile.getName().startsWith("pimpMyTerm") && theFile.getName().endsWith(".xml")) {
					try {
						User aUser = (User) aStream.fromXML(new FileReader(theFile));
						userList.put(aUser.getName(), aUser);

					} catch (Exception e) {
						Fonctions.trace("ERR", "Couldn't reload " + theFile + e.getMessage(), "CORE");
					}
				}
			}

		} else {
			Fonctions.trace("DEAD",
					"Couldn't evaluate dataPath [" + aFile
							+ "], is it empty? Please set dataPath on a valid path in aCore.xml or in -DdataPath=...",
					"CORE");
		}

		return userList;

	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void deleteUser() {
		File aFile = new File(name + ".xml");
		aFile.delete();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserSettings getSettings() {
		return settings;
	}

	public void setSettings(UserSettings settings) {
		this.settings = settings;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<SessionConfig> getSessions() {
		return sessions;
	}

	public void setSessions(List<SessionConfig> sessions) {
		this.sessions = sessions;
	}

	public String getRemoteAdress() {
		return remoteAdress;
	}

	public void setRemoteAdress(String remoteAdress) {
		this.remoteAdress = remoteAdress;
	}

	public Map<String, String> getWorkbenchs() {
		return workbenchs;
	}

	public void setWorkbenchs(Map<String, String> workbenchs) {
		this.workbenchs = workbenchs;
	}

	public Map<String, List<String>> getSessionsData() {
		return sessionsData;
	}

	public void setSessionsData(Map<String, List<String>> sessionsData) {
		this.sessionsData = sessionsData;
	}
	
	public void addSessionData(SessionOutput aSessionOutput)
	{
		List<String> aList=new ArrayList<String>();
		if ( sessionsData.containsKey(aSessionOutput.instanceId))
		{
			aList=sessionsData.get(aSessionOutput.instanceId);
		}
		aList.add(aSessionOutput.output);
		if ( aList.size() > getSettings().getMAX_LINES_HISTORY_BY_SESSION())
		{
			aList.remove(0);aList.remove(1);
			aList.add(0,"--No more history max " + getSettings().getMAX_LINES_HISTORY_BY_SESSION() + " reached, check settings--\r\n");
		}
		sessionsData.put(aSessionOutput.instanceId, aList);
	}

	public Map<String, String> getInstanceIdToSessionId() {
		return instanceIdToSessionId;
	}

	public void setInstanceIdToSessionId(Map<String, String> instanceIdToSessionId) {
		this.instanceIdToSessionId = instanceIdToSessionId;
	}

}
