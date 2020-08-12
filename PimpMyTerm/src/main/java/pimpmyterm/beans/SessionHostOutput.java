package pimpmyterm.beans;

/**
 * host id and string builder output
 */
public class SessionHostOutput {
    String hostName;
    StringBuilder output;
    StringBuilder lastOutput;
    
    public SessionHostOutput(String hostname, StringBuilder output,StringBuilder lastOutput){
        this.hostName=hostname;
        this.output=output;
        this.lastOutput=lastOutput;
    }

   

    public String getHostName() {
		return hostName;
	}



	public void setHostName(String hostName) {
		this.hostName = hostName;
	}



	public StringBuilder getLastOutput() {
		return lastOutput;
	}



	public void setLastOutput(StringBuilder lastOutput) {
		this.lastOutput = lastOutput;
	}



	public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(StringBuilder output) {
        this.output = output;
    }
}
