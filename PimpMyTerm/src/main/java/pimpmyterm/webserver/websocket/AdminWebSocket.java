package pimpmyterm.webserver.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;

import com.google.gson.Gson;
import pimpmyterm.beans.SchSession;
import pimpmyterm.beans.User;
import pimpmyterm.beans.UserSchSessions;
import pimpmyterm.core.Core;
import pimpmyterm.threads.SentOutputTask;
import pimpmyterm.utils.Fonctions;
import pimpmyterm.webserver.servlets.AdminServlet;

@ServerEndpoint(value = "/echo")
public class AdminWebSocket implements WebSocketListener {

	/**
	 * 
	 */

	private static Map<String, Session> allSessions = new HashMap<String, Session>();;
	Boolean close = false;

	SentOutputTask run;

	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		close = true;
		Fonctions.trace("DBG", "onWebSocketClose " + statusCode + " " + reason, "CORE");
	}

	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {
		Fonctions.trace("DBG", "onWebSocketConnect", "CORE");
		User aUser = Core.getInstance().getUserByIp(session.getRemoteAddress().getAddress().getHostAddress());
		if (aUser != null) {

			allSessions.put(session.getRemoteAddress().getAddress().getHostAddress().toString(), session);

			close = false;
			run = new SentOutputTask(aUser, this);
			Thread thread = new Thread(run);
			thread.start();
		} else {
			Fonctions.trace("ERR", "Couldn't find user by networkIdentification " + session.getRemoteAddress().getAddress().getHostAddress(), "CORE");
			close = true;
		}
	}

	@OnWebSocketError
	public void onWebSocketError(Throwable e) {
		Fonctions.trace("DBG", "onWebSocketError", "CORE");

	}

	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		Fonctions.trace("DBG", "onWebSocketBinary", "CORE");
	}

	@OnMessage
	public void onWebSocketText(String message) {
		Fonctions.trace("DBG", "onWebSocketText " + message, "CORE");
		if (!close) {

			if (StringUtils.isNotEmpty(message)) {

				Map<String,Object> jsonRoot = new Gson().fromJson(message.toString(), Map.class);

				String command = (String) jsonRoot.get("command");
				Integer keyCode = null;
				Double keyCodeDbl = (Double) jsonRoot.get("keyCode");
				if (keyCodeDbl != null) {
					keyCode = keyCodeDbl.intValue();
				}

				String userId = (String) jsonRoot.get("user");
				String id = (String) jsonRoot.get("id");
				
				UserSchSessions userSchSessions = AdminServlet.getUserSchSessionMap().get(userId);
				if (userSchSessions != null) {
					findSession(command, keyCode, userId, id, userSchSessions);
				}else {
					Fonctions.trace("ERR", "Couldn't find userId in mapSession, reinstanciate session" + userId, "CORE");
					//Reinstanciate a session
					User aUser=Core.getInstance().getUsers().get(userId);
					if ( aUser != null && aUser.getInstanceIdToSessionId().containsKey(id))
					{
						AdminServlet.createTerms(aUser.getInstanceIdToSessionId().get(id), aUser, id);
						userSchSessions = AdminServlet.getUserSchSessionMap().get(userId);
						findSession(command, keyCode, userId, id, userSchSessions);
					}else
					{
						Fonctions.trace("ERR", "Couldn't  reinstanciate session, because don't find instanceId(" + id + ")->sessionId mapping.", "CORE");	
					}
				}
			}

		}


	}

	private void findSession(String command, Integer keyCode, String userId, String id,
			UserSchSessions userSchSessions) {
		SchSession schSession = userSchSessions.getSchSessionMap().get(id);
		if ( schSession != null )
		{
			if (keyCode != null) {
				if (keyMap.containsKey(keyCode)) {
					try {
						schSession.getCommander().write(keyMap.get(keyCode));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			} else {
				schSession.getCommander().print(command);
			}
		}else
		{
			Fonctions.trace("ERR", "Couldn't find sessionId " + id + " for user " + userId + " seems like storing system not working.", "CORE");
		}
	}

	public static Map<String, Session> getAllSessions() {
		return allSessions;
	}

	public static void setAllSessions(Map<String, Session> allSessions) {
		AdminWebSocket.allSessions = allSessions;
	}

	public Boolean getClose() {
		return close;
	}

	public void setClose(Boolean close) {
		this.close = close;
	}

	public SentOutputTask getRun() {
		return run;
	}

	public void setRun(SentOutputTask run) {
		this.run = run;
	}

	/**
	 * Maps key press events to the ascii values
	 */
	static Map<Integer, byte[]> keyMap = new HashMap<Integer, byte[]>();

	static {
		// ESC
		keyMap.put(27, new byte[] { (byte) 0x1b });
		// ENTER
		keyMap.put(13, new byte[] { (byte) 0x0d });
		// LEFT
		keyMap.put(37, new byte[] { (byte) 0x1b, (byte) 0x4f, (byte) 0x44 });
		// UP
		keyMap.put(38, new byte[] { (byte) 0x1b, (byte) 0x4f, (byte) 0x41 });
		// RIGHT
		keyMap.put(39, new byte[] { (byte) 0x1b, (byte) 0x4f, (byte) 0x43 });
		// DOWN
		keyMap.put(40, new byte[] { (byte) 0x1b, (byte) 0x4f, (byte) 0x42 });
		// BS
		keyMap.put(8, new byte[] { (byte) 0x7f });
		// TAB
		keyMap.put(9, new byte[] { (byte) 0x09 });
		// CTR
		keyMap.put(17, new byte[] {});
		// DEL
		keyMap.put(46, "\033[3~".getBytes());
		// CTR-A
		keyMap.put(65, new byte[] { (byte) 0x01 });
		// CTR-B
		keyMap.put(66, new byte[] { (byte) 0x02 });
		// CTR-C
		keyMap.put(67, new byte[] { (byte) 0x03 });
		// CTR-D
		keyMap.put(68, new byte[] { (byte) 0x04 });
		// CTR-E
		keyMap.put(69, new byte[] { (byte) 0x05 });
		// CTR-F
		keyMap.put(70, new byte[] { (byte) 0x06 });
		// CTR-G
		keyMap.put(71, new byte[] { (byte) 0x07 });
		// CTR-H
		keyMap.put(72, new byte[] { (byte) 0x08 });
		// CTR-I
		keyMap.put(73, new byte[] { (byte) 0x09 });
		// CTR-J
		keyMap.put(74, new byte[] { (byte) 0x0A });
		// CTR-K
		keyMap.put(75, new byte[] { (byte) 0x0B });
		// CTR-L
		keyMap.put(76, new byte[] { (byte) 0x0C });
		// CTR-M
		keyMap.put(77, new byte[] { (byte) 0x0D });
		// CTR-N
		keyMap.put(78, new byte[] { (byte) 0x0E });
		// CTR-O
		keyMap.put(79, new byte[] { (byte) 0x0F });
		// CTR-P
		keyMap.put(80, new byte[] { (byte) 0x10 });
		// CTR-Q
		keyMap.put(81, new byte[] { (byte) 0x11 });
		// CTR-R
		keyMap.put(82, new byte[] { (byte) 0x12 });
		// CTR-S
		keyMap.put(83, new byte[] { (byte) 0x13 });
		// CTR-T
		keyMap.put(84, new byte[] { (byte) 0x14 });
		// CTR-U
		keyMap.put(85, new byte[] { (byte) 0x15 });
		// CTR-V
		// keyMap.put(86, new byte[]{(byte) 0x16});
		// CTR-W
		keyMap.put(87, new byte[] { (byte) 0x17 });
		// CTR-X
		keyMap.put(88, new byte[] { (byte) 0x18 });
		// CTR-Y
		keyMap.put(89, new byte[] { (byte) 0x19 });
		// CTR-Z
		keyMap.put(90, new byte[] { (byte) 0x1A });
		// CTR-[
		keyMap.put(219, new byte[] { (byte) 0x1B });
		// CTR-]
		keyMap.put(221, new byte[] { (byte) 0x1D });

	}

}
