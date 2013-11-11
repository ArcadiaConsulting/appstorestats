/**
* Copyright 2013 Arcadia Consulting C.B.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/
package es.arcadiaconsulting.appstoresstats.common;

import java.util.Date;
import java.util.List;

public class CommonStatsData {
	
	/**
	 * Current installations of application
	 */
	private int currentInstallationsNumber;

	/**
	 * Number of downloads for app between indicated dates
	 */
	private int downloadsNumber;
	/**
	 * App identification for Store
	 */
	private String appId;
	/**
	 * Current platform (iOS or android)
	 */
	private Platform platform;
	/**
	 * Date in which the app was first released
	 */
	private Date firstDeploymentDate;
	/**
	 * Average rate for app
	 */
	private float averageRate;
	/**
	 * Ratings data for app
	 * 	int rate;
	 * 	String opinion;
	 * 	String user;
	 * 	String device;
	 */
	private List<Rating> ratings;
	/**
	 * App name as shown in Stores
	 */
	private String appName;
	/**
	 * Url for app download
	 */
	private String downloadURL;
	/**
	 * Initial Date for current stats
	 */
	private Date initDate;
	/**
	 * 
	 */
	private Date endDate;
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
	public  void setIOSPlatform()
	{
		this.platform=Platform.iOS;
	}
	public void setAndroidPlatform()
	{
		this.platform=Platform.android;
	}
	public int getCurrentInstallationsNumber() {
		return currentInstallationsNumber;
	}

	public void setCurrentInstallationsNumber(int currentInstallationsNumber) {
		this.currentInstallationsNumber = currentInstallationsNumber;
	}
	private class Rating
	{

		/**
		 * User rating for current app
		 */
		private int rate;
		/**
		 * User opinion for current app
		 */
		private String opinion;
		/**
		 * User name
		 */
		private String user;
		/**
		 * User device
		 */
		private String device;
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
