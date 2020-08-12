
package pimpmyterm.beans;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * contains information for an ssh session
 */
public class SchSession {


    String userName;
    String hostName;
    Session session;
    Channel channel;
    PrintStream commander;
    InputStream outFromChannel;
    OutputStream inputToChannel;
  


    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public PrintStream getCommander() {
        return commander;
    }

    public void setCommander(PrintStream commander) {
        this.commander = commander;
    }

    public InputStream getOutFromChannel() {
        return outFromChannel;
    }

    public void setOutFromChannel(InputStream outFromChannel) {
        this.outFromChannel = outFromChannel;
    }

    public OutputStream getInputToChannel() {
        return inputToChannel;
    }

    public void setInputToChannel(OutputStream inputToChannel) {
        this.inputToChannel = inputToChannel;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

  

  
}
