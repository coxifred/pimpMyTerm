
package pimpmyterm.beans;


/**
 * Output from ssh session
 */
public class SessionOutput {
   
    String hostName;
    String instanceId;
    String output;
  



   
    public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	
 


   

	public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}