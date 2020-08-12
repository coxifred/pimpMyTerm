package pimpmyterm.webserver;

public class User {
	String name;
	String avatar;
	Boolean isGuru;
	Integer nbAssigned=0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Boolean getIsGuru() {
		return isGuru;
	}
	public void setIsGuru(Boolean isGuru) {
		this.isGuru = isGuru;
	}
	public Integer getNbAssigned() {
		return nbAssigned;
	}
	public void setNbAssigned(Integer nbAssigned) {
		this.nbAssigned = nbAssigned;
	}
	
	
}
