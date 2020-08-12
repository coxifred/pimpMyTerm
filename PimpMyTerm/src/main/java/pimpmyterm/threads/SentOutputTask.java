package pimpmyterm.threads;

import java.util.List;

import com.google.gson.Gson;
import pimpmyterm.beans.SessionOutput;
import pimpmyterm.beans.User;
import pimpmyterm.utils.SessionOutputUtil;
import pimpmyterm.webserver.websocket.AdminWebSocket;

/**
 * class to send output to web socket client
 */
public class SentOutputTask implements Runnable {

	AdminWebSocket adminWebSocket;
	User aUser;

	public SentOutputTask(User aUser, AdminWebSocket adminSocket) {
		this.adminWebSocket = adminSocket;
		this.aUser = aUser;
	}

	public void run() {
		while (!adminWebSocket.getClose()) {
			List<SessionOutput> outputList = SessionOutputUtil.getOutput(aUser);
			try {
				if (outputList != null && !outputList.isEmpty()) {
					for (SessionOutput so:outputList)
					{
						aUser.addSessionData(so);
					}
					String json = new Gson().toJson(outputList);
					
					AdminWebSocket.getAllSessions().get(aUser.getRemoteAdress()).getRemote().sendString(json);
				}
				Thread.sleep(50);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

}
