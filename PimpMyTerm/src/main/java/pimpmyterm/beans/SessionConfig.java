package pimpmyterm.beans;

public class SessionConfig {
	
	String sessionId;
	String hostId;
	Boolean showInTab;
	Boolean showWorkBench;
	Boolean transparent;
	Boolean useCredential;
	String loginName;
	String password;
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public Boolean getShowInTab() {
		return showInTab;
	}
	public void setShowInTab(Boolean showInTab) {
		this.showInTab = showInTab;
	}
	public Boolean getShowWorkBench() {
		return showWorkBench;
	}
	public void setShowWorkBench(Boolean showWorkBench) {
		this.showWorkBench = showWorkBench;
	}
	public Boolean getTransparent() {
		return transparent;
	}
	public void setTransparent(Boolean transparent) {
		this.transparent = transparent;
	}
	public Boolean getUseCredential() {
		return useCredential;
	}
	public void setUseCredential(Boolean useCredential) {
		this.useCredential = useCredential;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
