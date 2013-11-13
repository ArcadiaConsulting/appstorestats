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

public class Rating {
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
	 * App Version
	 */
	private String appVersion;
	
	/**
	 * Rate Date
	 */
	private Date date;
	
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@SuppressWarnings("unused")
	public Rating(int rate, String opinion, String user,Date date, String version) {
		super();
		this.rate = rate;
		this.opinion = opinion;
		this.user = user;
		this.appVersion = version;
		this.date = date;
	}
	
	
	public Rating(){
		super();
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
	

}
