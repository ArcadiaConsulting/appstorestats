package com.github.andlyticsproject.model;

import java.util.Date;

public class AppHistoricalStatsElement {
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	private Date date;
	private String number;
	public AppHistoricalStatsElement(Date date, String number) {
		super();
		this.date = date;
		this.number = number;
	}
}
