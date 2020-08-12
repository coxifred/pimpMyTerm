package pimpmyterm.threads;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import pimpmyterm.beans.SessionOutput;
import pimpmyterm.beans.User;
import pimpmyterm.utils.SessionOutputUtil;



/**
 * Task to watch for output read from the ssh session stream
 */
public class SecureShellTask implements Runnable {

    InputStream outFromChannel;
    SessionOutput sessionOutput;
    User aUser;

    public SecureShellTask(SessionOutput sessionOutput, InputStream outFromChannel,User aUser) {

        this.sessionOutput = sessionOutput;
        this.outFromChannel = outFromChannel;
        this.aUser=aUser;
    }

    public void run() {
        InputStreamReader isr = new InputStreamReader(outFromChannel);
        BufferedReader br = new BufferedReader(isr);
        try {
        	SessionOutputUtil.addOutput(aUser, sessionOutput.getHostName(), sessionOutput);
            char[] buff = new char[1024];
            int read;
            while((read = br.read(buff)) != -1) {
                SessionOutputUtil.addToOutput(aUser, sessionOutput.getInstanceId(), buff,0,read);
                Thread.sleep(50);
                
            }
            
            SessionOutputUtil.removeOutput(aUser, sessionOutput.getInstanceId());

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

}
