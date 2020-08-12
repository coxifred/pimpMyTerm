package pimpmyterm.webserver.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelShell;
import pimpmyterm.beans.Log;
import pimpmyterm.beans.SchSession;
import pimpmyterm.beans.SessionConfig;
import pimpmyterm.beans.SessionHostOutput;
import pimpmyterm.beans.User;
import pimpmyterm.beans.UserSchSessions;
import pimpmyterm.beans.UserSessionsOutput;
import pimpmyterm.core.Core;
import pimpmyterm.utils.Fonctions;
import pimpmyterm.utils.SSHUtil;
import pimpmyterm.utils.SessionOutputUtil;

public class AdminServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8546977083372271300L;

	static Map<String, List<Date>> ip2Connect = new HashMap<String, List<Date>>();
	static Map<String, UserSchSessions> userSchSessionMap = new ConcurrentHashMap<String, UserSchSessions>();

	public AdminServlet() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		perform(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			{
		try {
			perform(request, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void perform(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getParameter("action") != null) {
			String action = request.getParameter("action");
			switch (action) {
			case "login":
				authenfication(request, response);
				break;
			case "logout":
				logout(request, response);
				break;
			case "isLogged":
				isLogged(request, response);
				break;
			case "getMe":
				getMe(request, response);
				break;
			case "getLogs":
				getLogs(request, response);
				break;
			case "saveSessions":
				saveSessions(request, response);
				break;
			case "saveWorkbenchs":
				saveWorkbenchs(request, response);
				break;
			case "getSessionSize":
				getSessionSize(request, response);
				break;
			case "getNextSessionId":
				getNextSessionId(request, response);
				break;
			case "createSession":
				createSession(request, response);
				break;
			case "closeSession":
				closeSession(request, response);
				break;
			case "resetFactoryWorkbench":
				resetFactoryWorkbench(request, response);
				break;
			case "debug":
				debug(request, response);
				break;
			case "changeSizeSession":
				changeSizeSession(request, response);
				break;
			case "getWebSocketPort":
				getWebSocketPort(request,response);
				break;
			}

		}
	}
	
	private void getWebSocketPort(HttpServletRequest request, HttpServletResponse response)
	{
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null )
		{
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println(Core.getInstance().getWebSocketPort());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	private void resetFactoryWorkbench(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String workbench = request.getParameter("workbench");
	
		if (requester != null && workbench != null && ! "".equals(workbench) && requester.getWorkbenchs().containsKey(workbench)) {
			String id=workbench.replace("workbench", "");
			if (workbench.equals("all"))
			{
				for (String wb:requester.getWorkbenchs().keySet())
				{
					String idwb=wb.replace("workbench", "");
					requester.getWorkbenchs().put(wb,"<div id=termContainer" + idwb + " class=\"term-container\">");
				}
			}else
			{
			requester.getWorkbenchs().put(workbench,"<div id=termContainer" + id + " class=\"term-container\">");
			}
			requester.saveUser();
		}
	}
	


	private void saveSessions(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String sessions = request.getParameter("payload");

		if (requester != null) {
			Gson aGson = new Gson();
			SessionConfig[] sessionz = aGson.fromJson(sessions, SessionConfig[].class);
			requester.getSessions().clear();
			for (SessionConfig aSession : sessionz) {
				requester.getSessions().add(aSession);
			}
			requester.saveUser();
		}
	}
	

	private void saveWorkbenchs(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String workbenchs = request.getParameter("payload");

		if (requester != null && workbenchs != null ) {
			Gson aGson = new Gson();
			Map<String,String> wbz = aGson.fromJson(workbenchs, Map.class);
			for ( String aWb:wbz.keySet())
			{
				requester.getWorkbenchs().put(aWb,wbz.get(aWb));
			}
			requester.saveUser();
		}
	}
	
	

	private void getSessionSize(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String instanceId = request.getParameter("instanceId");

		if (requester != null) {
			
			UserSessionsOutput userSessionsOutput=SessionOutputUtil.userSessionsOutputMap.get(requester.getName());
			if (userSessionsOutput != null) {
				SessionHostOutput sessionHost = userSessionsOutput.getSessionOutputMap().get(Integer.valueOf(instanceId));
				Double size = 0d;
				DecimalFormat df = new DecimalFormat("#.##");
				size = sessionHost.getLastOutput().length() * 8/ 1024d;
				PrintWriter out;
				try {
					out = response.getWriter();
					out.println(df.format(size).replace(",", "."));
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}

		}

	}

	private String getNextSessionId(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(Core.getInstance().getNextSessionId());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void createSession(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String sessionId = request.getParameter("sessionId");
		String instanceId = request.getParameter("instanceId");

		if (requester != null && sessionId != null && !sessionId.isEmpty() && instanceId != null) {
			createTerms(sessionId, requester, instanceId);
			requester.getInstanceIdToSessionId().put(instanceId, sessionId);
		}

	}
	
	private void changeSizeSession(HttpServletRequest request, HttpServletResponse response)
	{
		User requester = (User) request.getSession().getAttribute("USER");
		String sessionId=request.getParameter("sessionId");
		Integer cols=Integer.decode(request.getParameter("cols"));
		Integer rows=Integer.decode(request.getParameter("rows"));
		Integer wp=Integer.decode(request.getParameter("wp"));
		Integer hp=Integer.decode(request.getParameter("hp"));
		if ( requester != null && sessionId != null && cols != null && ! "".equals(cols) && rows != null && ! "".equals(rows) && wp != null && ! "".equals(wp) && hp != null && ! "".equals(hp))
		{
			UserSchSessions userSchSession = getUserSchSessionMap().get(requester.getName());
			if ( userSchSession != null )
			{
				SchSession theSession=userSchSession.getSchSessionMap().get(sessionId);
				if ( theSession != null )
				{
					ChannelShell channel=(ChannelShell) theSession.getChannel();
					Fonctions.trace("DBG", "Resize session to " + cols + "cols " + rows + "rows " + wp + "wp " + hp + "hp", "CORE");
					channel.setPtySize(cols, rows,wp,hp);
				}else
				{
					Fonctions.trace("ERR","changeSizeSession, Couldn't retrieve sessionId","CORE");
				}
			}else
			{
				Fonctions.trace("ERR","changeSizeSession, Couldn't retrieve user remote session","CORE");
			}
		}else
		{
			Fonctions.trace("ERR","Error in changeSizeSession, some parameters are incorrects","CORE");
		}
	}
	
	private void closeSession(HttpServletRequest request, HttpServletResponse response)
	{
		User requester = (User) request.getSession().getAttribute("USER");
		String sessionId=request.getParameter("sessionId");
		
		if ( requester != null && sessionId != null )
		{
			UserSchSessions userSchSession = getUserSchSessionMap().get(requester.getName());
			if ( userSchSession != null )
			{
				SchSession theSession=userSchSession.getSchSessionMap().get(sessionId);
				if ( theSession != null )
				{
					theSession.getChannel().disconnect();
					userSchSession.getSchSessionMap().remove(sessionId);
					requester.getInstanceIdToSessionId().remove(sessionId);
					requester.saveUser();
					Fonctions.trace("DBG", "Disconnect session", "CORE");
				}else
				{
					Fonctions.trace("ERR","closeSession, Couldn't retrieve sessionId","CORE");
				}
			}else
			{
				Fonctions.trace("ERR","closeSession, Couldn't retrieve user remote session","CORE");
			}
		}else
		{
			Fonctions.trace("ERR","Error in closeSession, some parameters are incorrects","CORE");
		}
	}

	/**
	 * creates composite terminals if there are errors or authentication issues.
	 */
	public static void createTerms(String sessionId, User aUser, String instanceId) {
		// Session Classique (utilisÃ©e par les Terms)
		if (!getUserSchSessionMap().containsKey(aUser.getName())) {
			// CrÃ©ation du user avec ajout de la session demandÃ©e
			SchSession session = SSHUtil.openSSHTermOnSystem(aUser, sessionId, instanceId);
			session.setHostName(sessionId);
			session.setUserName(aUser.getName());
			UserSchSessions userSchSession = new UserSchSessions();
			userSchSession.getSchSessionMap().put(instanceId, session);
			getUserSchSessionMap().put(aUser.getName(), userSchSession);
		} else {
			// Ajout de la session demandÃ©e (user dejÃ  prÃ©sent)
			SchSession session = SSHUtil.openSSHTermOnSystem(aUser, sessionId, instanceId);
			session.setHostName(sessionId);
			session.setUserName(aUser.getName());

			UserSchSessions userSchSession = getUserSchSessionMap().get(aUser.getName());

			userSchSession.getSchSessionMap().put(instanceId, session);
			getUserSchSessionMap().put(aUser.getName(), userSchSession);
		}
		aUser.saveUser();
	}

	private void getLogs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String include = request.getParameter("include");
		String exclude = request.getParameter("exclude");

		if (requester != null) {
			if (requester.getName().equals("admin")) {
				response.getWriter().write(toGson(Log.getLogs(include, exclude, Core.getInstance().getLogs())));
			} else {

			}
		}
		response.getWriter().write("");

	}

	private void getPlugins(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");

		if (requester != null) {

		}
	}

	private void getAvailablePlugins(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");

		if (requester != null) {
			response.getWriter().write(toGson(Core.getInstance().getAvailablePlugins()));
		}
	}

	private void debug(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String debugMode = request.getParameter("debug");
		if (requester != null && requester.getName().equals("admin")) {
			if ("false".equals(debugMode)) {
				Core.getInstance().setDebug(false);
				Fonctions.trace("INFO", "Farewell to Debug mode", "CORE");
			}
			if ("true".equals(debugMode)) {
				Core.getInstance().setDebug(true);
				Fonctions.trace("INFO", "Respawn Debug mode", "CORE");
			}

		}
		response.getWriter().write(Core.getInstance().getDebug().toString());

	}

	private void getMe(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			response.getWriter().write(toGson(requester));
		}
	}

	private String toGson(Object obj) {
		Gson aGson = new Gson();
		return aGson.toJson(obj);
	}


	private void authenfication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("user");
		String passwd = request.getParameter("passwd");
		manageFail2Ban(request);
		if (User.isAuthentified(user, passwd)) {
			Fonctions.trace("INF", "Connection from ip successfull for user " + user, "CORE");
			request.getSession().setAttribute("USER", Core.getInstance().getUsers().get(user));
			Core.getInstance().getUsers().get(user).setRemoteAdress(request.getRemoteAddr());
			response.getWriter().write("/main.html");
		} else {
			response.getWriter().write("/index.html");
		}
	}

//	/**
//	 * creates composite terminals if there are errors or authentication issues.
//	 */
//	private void createTerms(HttpServletRequest request, HttpServletResponse response) {
//
//		User requester = (User) request.getSession().getAttribute("USER");
//		String hostName = request.getParameter("hostName");
//		String passwd = request.getParameter("passwd");
//		Integer instanceId = Integer.valueOf(request.getParameter("instanceId"));
//
//		// Session Classique (utilisÃ©e par les Terms)
//		if (!getUserSchSessionMap().containsKey(requester.getName())) {
//			// CrÃ©ation du user avec ajout de la session demandÃ©e
//			SchSession session = SSHUtil.openSSHTermOnSystem(requester, hostName, instanceId);
//			session.setHostName(hostName);
//			session.setUserName(requester.getName());
//			UserSchSessions userSchSession = new UserSchSessions();
//			userSchSession.getSchSessionMap().put(instanceId, session);
//			getUserSchSessionMap().put(requester.getName(), userSchSession);
//		} else {
//			// Ajout de la session demandÃ©e (user dejÃ  prÃ©sent)
//			SchSession session = SSHUtil.openSSHTermOnSystem(requester, hostName, instanceId);
//			session.setHostName(hostName);
//			session.setUserName(requester.getName());
//
//			UserSchSessions userSchSession = getUserSchSessionMap().get(requester.getName());
//
//			userSchSession.getSchSessionMap().put(instanceId, session);
//			getUserSchSessionMap().put(requester.getName(), userSchSession);
//		}

//	}

	private void manageFail2Ban(HttpServletRequest request) {
		String remoteIp = request.getRemoteAddr();
		List<Date> aDateList = new ArrayList<Date>();
		if (ip2Connect.containsKey(remoteIp)) {
			aDateList = ip2Connect.get(remoteIp);
		}
		Date aDate = new Date();
		aDateList.add(aDate);
		ip2Connect.put(remoteIp, aDateList);
		// Counting acces for last 10 minutes, if > 10, then slowing
		Calendar start = Calendar.getInstance();
		start.add(Calendar.MINUTE, -10);
		Calendar end = Calendar.getInstance();
		// Discovering dates
		List<Date> newDates = new ArrayList<Date>();
		for (Date theDate : aDateList) {
			if (theDate.after(start.getTime()) && theDate.before(end.getTime())) {
				newDates.add(theDate);
			}
		}
		ip2Connect.put(remoteIp, newDates);
		Fonctions.trace("INF", newDates.size() + " connection(s) within 10 minutes from ip " + remoteIp + " @" + aDate,
				"CORE");
		if (newDates.size() > 10) {
			Fonctions.trace("INF", "Slowing connection, by waiting " + newDates.size() + " * 120 secs for ip "
					+ remoteIp + " @" + aDate, "CORE");
			Fonctions.attendre(newDates.size() * 120000);
		}
		Fonctions.trace("INF", "Trying Connection from ip " + remoteIp + " @" + aDate, "CORE");
	}

	private void isLogged(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (request.getSession().getAttribute("USER") != null) {
			response.getWriter().write("true");
		} else {
			response.getWriter().write("false");
		}

	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().removeAttribute("USER");
	}

	public static Map<String, UserSchSessions> getUserSchSessionMap() {
		return userSchSessionMap;
	}

	public static void setUserSchSessionMap(Map<String, UserSchSessions> userSchSessionMap) {
		AdminServlet.userSchSessionMap = userSchSessionMap;
	}

}