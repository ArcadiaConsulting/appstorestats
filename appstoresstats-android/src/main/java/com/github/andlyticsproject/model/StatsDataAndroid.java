package com.github.andlyticsproject.model;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;

public class StatsDataAndroid extends CommonStatsData {
	/**
	 * Indicates the number of errors reported by users
	 */
	private int errorNumber;
	/**
	 * Indicates the total number of comments
	 */
	private int ratingNumber;
	public StatsDataAndroid() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getErrorNumber() {
		return errorNumber;
	}
	public void setErrorNumber(int errorNumber) {
		this.errorNumber = errorNumber;
	}
	public int getRatingNumber() {
		return ratingNumber;
	}
	public void setRatingNumber(int ratingNumber) {
		this.ratingNumber = ratingNumber;
	}

}
