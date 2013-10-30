package es.arcadiaconsulting.appstoresstats.ios.model;

import java.util.Date;

public class AppInfo {
	
	private boolean isDeployed;
	
	private String id;
	
	private Date releaseDate;

	public boolean isDeployed() {
		return isDeployed;
	}

	public void setDeployed(boolean isDeployed) {
		this.isDeployed = isDeployed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

}
