package es.arcadiaconsulting.appstoresstats.common;

import java.util.Date;
import java.util.List;

public class CommonStatsData {
	

	/**
	 * Number of downloads for app between indicated dates
	 */
	int downloadsNumber;
	/**
	 * App identification for Store
	 */
	String appId;
	/**
	 * Current platform (iOS or android)
	 */
	Platform platform;
	/**
	 * Date in which the app was first released
	 */
	Date firstDeploymentDate;
	/**
	 * Average rate for app
	 */
	float averageRate;
	/**
	 * Ratings data for app
	 * 	int rate;
	 * 	String opinion;
	 * 	String user;
	 * 	String device;
	 */
	List<Rating> ratings;
	/**
	 * App name as shown in Stores
	 */
	String appName;
	/**
	 * Url for app download
	 */
	String downloadURL;
	/**
	 * Initial Date for current stats
	 */
	Date initDate;
	/**
	 * 
	 */
	Date endDate;
	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	public int getDownloadsNumber() {
		return downloadsNumber;
	}

	public void setDownloadsNumber(int downloadsNumber) {
		this.downloadsNumber = downloadsNumber;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Date getFirstDeploymentDate() {
		return firstDeploymentDate;
	}

	public void setFirstDeploymentDate(Date firstDeploymentDate) {
		this.firstDeploymentDate = firstDeploymentDate;
	}

	public float getAverageRate() {
		return averageRate;
	}

	public void setAverageRate(float averageRate) {
		this.averageRate = averageRate;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}


	
	public enum Platform {
	    iOS, android 
	}
	
	private class Rating
	{

		/**
		 * User rating for current app
		 */
		int rate;
		/**
		 * User opinion for current app
		 */
		String opinion;
		/**
		 * User name
		 */
		String user;
		/**
		 * User device
		 */
		String device;
		@SuppressWarnings("unused")
		public Rating(int rate, String opinion, String user, String device) {
			super();
			this.rate = rate;
			this.opinion = opinion;
			this.user = user;
			this.device = device;
		}
		@SuppressWarnings("unused")
		public int getRate() {
			return rate;
		}
		@SuppressWarnings("unused")
		public void setRate(int rate) {
			this.rate = rate;
		}
		@SuppressWarnings("unused")
		public String getOpinion() {
			return opinion;
		}
		@SuppressWarnings("unused")
		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}
		
		
		@SuppressWarnings("unused")
		public String getUser() {
			return user;
		}
		@SuppressWarnings("unused")
		public void setUser(String user) {
			this.user = user;
		}
		@SuppressWarnings("unused")
		public String getDevice() {
			return device;
		}
		@SuppressWarnings("unused")
		public void setDevice(String device) {
			this.device = device;
		}

	};
}
