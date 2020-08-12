package pimpmyterm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import pimpmyterm.beans.SessionHostOutput;
import pimpmyterm.beans.SessionOutput;
import pimpmyterm.beans.User;
import pimpmyterm.beans.UserSessionsOutput;


public class SessionOutputUtil {

	public static Map<String, UserSessionsOutput> userSessionsOutputMap = new ConcurrentHashMap<String, UserSessionsOutput>();

	/**
	 * removes session for user session
	 * 
	 * @param sessionId session id
	 */
	public static void removeUserSession(User aUser, String instanceId) {
		UserSessionsOutput userSessionsOutput = userSessionsOutputMap.get(aUser.getName());
		if (userSessionsOutput != null) {
			userSessionsOutput.getSessionOutputMap().remove(instanceId);
		}

	}

	/**
	 * removes session output for host system
	 * 
	 * @param sessionId  session id
	 * @param instanceId id of host system instance
	 */
	public static void removeOutput(User aUser, String instanceId) {

		UserSessionsOutput userSessionsOutput = userSessionsOutputMap.get(aUser.getName());
		if (userSessionsOutput != null) {
			userSessionsOutput.getSessionOutputMap().remove(instanceId);
		}
	}

	/**
	 * adds a new output
	 * 
	 * @param sessionId     session id
	 * @param hostId        host id
	 * @param sessionOutput session output object
	 */
	public static void addOutput(User aUser, String hostname, SessionOutput sessionOutput) {

		UserSessionsOutput userSessionsOutput = userSessionsOutputMap.get(aUser.getName());
		if (userSessionsOutput == null) {
			userSessionsOutputMap.put(aUser.getName(), new UserSessionsOutput());
			userSessionsOutput = userSessionsOutputMap.get(aUser.getName());
		}
		userSessionsOutput.getSessionOutputMap().put(sessionOutput.getInstanceId(),
				new SessionHostOutput(hostname, new StringBuilder(), new StringBuilder()));

	}

	/**
	 * adds a new output
	 * 
	 * @param sessionId  session id
	 * @param instanceId id of host system instance
	 * @param value      Array that is the source of characters
	 * @param offset     The initial offset
	 * @param count      The length
	 */
	public static void addToOutput(User aUser, String instanceId, char value[], int offset, int count) {

		UserSessionsOutput userSessionsOutput = userSessionsOutputMap.get(aUser.getName());
		if (userSessionsOutput != null) {
			userSessionsOutput.getSessionOutputMap().get(instanceId).getOutput().append(value, offset, count);
		}

	}

	/**
	 * returns list of output lines
	 * 
	 * @param sessionId session id object
	 * @return session output list
	 */
	public static List<SessionOutput> getOutput(User aUser) {
		List<SessionOutput> outputList = new ArrayList<SessionOutput>();

		UserSessionsOutput userSessionsOutput = userSessionsOutputMap.get(aUser.getName());
		if (userSessionsOutput != null) {

			for (String key : userSessionsOutput.getSessionOutputMap().keySet()) {

				// get output chars and set to output
				try {
					SessionHostOutput sessionHostOutput = userSessionsOutput.getSessionOutputMap().get(key);
					String hostName = sessionHostOutput.getHostName();
					StringBuilder sb = sessionHostOutput.getOutput();
					if (sb != null) {
						SessionOutput sessionOutput = new SessionOutput();
						sessionOutput.setHostName(hostName);
						sessionOutput.setInstanceId(key);

						sessionOutput.setOutput(sb.toString());
						if (StringUtils.isNotEmpty(sessionOutput.getOutput())) {
							sessionHostOutput.setLastOutput(sessionHostOutput.getLastOutput().append(sb.toString()));
						}

						// On regarde si on est en mode reload
						Boolean reload = false;

						if (StringUtils.isNotEmpty(sessionOutput.getOutput())) {
							outputList.add(sessionOutput);
							userSessionsOutput.getSessionOutputMap().put(key, new SessionHostOutput(hostName,
									new StringBuilder(), sessionHostOutput.getLastOutput()));
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

		}

		return outputList;
	}

}
