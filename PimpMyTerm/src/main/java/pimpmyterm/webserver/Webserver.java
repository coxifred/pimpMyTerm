package pimpmyterm.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pimpmyterm.utils.Fonctions;

public class Webserver {

	String ip;
	Integer port;
	JettyWebServer webSocketThread;
	
	static String uuid=""; 
	static String guruPassword="";
	static String state="";
	static String shareBox="";
	static List<User> users=new ArrayList<User>();

	public static Set<Integer> icons=new TreeSet<Integer>();
	
	
	public void startWebSocket(Boolean http2) {
		


		Fonctions.trace("INF", "Starting JettyWebServer on ip " + ip + " listening on port " + port + " hardware", "CORE");
		webSocketThread = new JettyWebServer(ip, getPort(),http2);
	}

	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public JettyWebServer getWebSocketThread() {
		return webSocketThread;
	}

	public void setWebSocketThread(JettyWebServer webSocketThread) {
		this.webSocketThread = webSocketThread;
	}

	public static String getUuid() {
		return uuid;
	}

	public static void setUuid(String uuid) {
		if (Webserver.uuid ==null || "".equals(Webserver.uuid) )
		{
		Webserver.uuid = uuid;
		}
	}

	public static String getGuruPassword() {
		return guruPassword;
	}

	public static void setGuruPassword(String guruPassword,String uuid) {
		if ( uuid != null && uuid.equals(Webserver.uuid))
		{
		if ( Webserver.guruPassword == null || "".equals(Webserver.guruPassword ))
		{
		Webserver.guruPassword = guruPassword;
		Webserver.setState("passworded");
		}
		}
	}

	public static String getState() {
		return state;
	}

	public static void setState(String state) {
		Webserver.state = state;
	}

	public static List<User> getUsers() {
		return users;
	}

	public static void setUsers(List<User> users) {
		Webserver.users = users;
	}
	
	public static User getUser(String username)
	{
		for (User anUser:users)
		{
			if ( username.equals(anUser.getName()))
			{
				return anUser;
			}
		}
		return null;
	}



}
