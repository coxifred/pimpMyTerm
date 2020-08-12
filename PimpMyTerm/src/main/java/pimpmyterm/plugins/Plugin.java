package pimpmyterm.plugins;

import com.google.gson.Gson;

public abstract class Plugin implements IPlugin{
	String name;
	String version;
	String description;
	Boolean updateable;

	public abstract String getName();

	public void setName(String name) {
		this.name = name;
	}



	public void setDescription(String description) {
		this.description = description;
	}

	

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	
	public String returnMe()
	{
		Gson aGson=new Gson();
		return aGson.toJson(this);
	}

	public abstract Boolean getUpdateable();

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}
	
}
