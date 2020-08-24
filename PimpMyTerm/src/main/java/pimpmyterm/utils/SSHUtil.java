package pimpmyterm.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import pimpmyterm.beans.SchSession;
import pimpmyterm.beans.SessionConfig;
import pimpmyterm.beans.SessionOutput;
import pimpmyterm.beans.User;
import pimpmyterm.threads.SecureShellTask;


public class SSHUtil {

	/**
	 * open new ssh session on host system
	 * 
	 * @param passphrase     key passphrase for instance
	 * @param password       password for instance
	 * @param userId         user id
	 * @param sessionId      session id
	 * @param hostSystem     host system
	 * @param userSessionMap user session map
	 * @return status of systems
	 */
	public static SchSession openSSHTermOnSystem(User aUser, String sessionId, String instanceId) {

		JSch jsch = new JSch();

		SchSession schSession = null;

		try {
			// Retrieve SessionConfig
			SessionConfig aSession = aUser.getSessionById(sessionId);
			String login = aSession.getLoginName();
			String password = aSession.getPassword();
			if (aSession.getUseCredential()) {
				login = aUser.getName();
				password = aUser.getPassword();
			}

			// create session
			Session session = jsch.getSession(login, aSession.getHostId(), 22);
			session.setPassword(password);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(10000);

			Channel channel = session.openChannel("shell");

			((ChannelShell) channel).setPtyType("xterm");

			InputStream outFromChannel = channel.getInputStream();

			// new session output
			SessionOutput sessionOutput = new SessionOutput();
			sessionOutput.setHostName(aSession.getHostId());
			sessionOutput.setInstanceId(instanceId);

			Runnable run = new SecureShellTask(sessionOutput, outFromChannel, aUser);
			Thread thread = new Thread(run);
			thread.start();

			OutputStream inputToChannel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputToChannel, true);

			channel.connect();

			schSession = new SchSession();
			// schSession.setUserId(userId);
			schSession.setSession(session);
			schSession.setChannel(channel);
			schSession.setCommander(commander);
			schSession.setInputToChannel(inputToChannel);
			schSession.setOutFromChannel(outFromChannel);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return schSession;
	}

	public static SchSession openSSHAdminTermOnSystem(User aUser, String hostName, Integer instanceId) {

		JSch jsch = new JSch();

		SchSession schSession = null;

		try {

			// create session
			Session session = jsch.getSession(aUser.getName(), hostName, 22);

			session.setPassword(aUser.getPassword());
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");

			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(60000);
			Channel channel = session.openChannel("shell");
			
			((ChannelShell) channel).setPtyType("xterm");

			InputStream outFromChannel = channel.getInputStream();

			OutputStream inputToChannel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputToChannel, true);

			channel.connect();
			
			schSession = new SchSession();
			schSession.setSession(session);
			schSession.setChannel(channel);
			schSession.setCommander(commander);
			schSession.setInputToChannel(inputToChannel);
			schSession.setOutFromChannel(outFromChannel);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return schSession;
	}

	public static void sendFileByScp(User aUser, String hostName, User anAnotherUser, String file, String dest,
			String rights) {

		JSch jsch = new JSch();
		try {

			// create session
			Session session = jsch.getSession(anAnotherUser.getName(), hostName, 22);

			session.setPassword(anAnotherUser.getPassword());
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(60000);

			Channel channel = session.openChannel("sftp");
			channel.connect();

			ChannelSftp sftp = (ChannelSftp) channel;
			sftp.put(file, dest);

			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
