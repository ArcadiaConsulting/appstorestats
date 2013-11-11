package com.github.andlyticsproject.model;

import java.util.List;

public class AppHistoricalStats {

	private List<AppHistoricalStatsElement> currentInstallsByDevice;
	private List<AppHistoricalStatsElement> dailyInstallsByDevice;
	private List<AppHistoricalStatsElement> dailyUninstallsByDevice;
	private List<AppHistoricalStatsElement> dailyUpdatesByUser;
	private List<AppHistoricalStatsElement> currentInstallsByUser;
	private List<AppHistoricalStatsElement> totalInstallsByUser;
	private List<AppHistoricalStatsElement> dailyInstallsByUser;
	private List<AppHistoricalStatsElement> gmcMessages;
	private List<AppHistoricalStatsElement> gmcRegisters;
	private List<AppHistoricalStatsElement> averageDailyRating;
	private List<AppHistoricalStatsElement> averageTotalRating;
	public List<AppHistoricalStatsElement> getCurrentInstallsByDevice() {
		return currentInstallsByDevice;
	}
	public void setCurrentInstallsByDevice(
			List<AppHistoricalStatsElement> currentInstallsByDevice) {
		this.currentInstallsByDevice = currentInstallsByDevice;
	}
	public List<AppHistoricalStatsElement> getDailyInstallsByDevice() {
		return dailyInstallsByDevice;
	}
	public void setDailyInstallsByDevice(
			List<AppHistoricalStatsElement> dailyInstallsByDevice) {
		this.dailyInstallsByDevice = dailyInstallsByDevice;
	}
	public List<AppHistoricalStatsElement> getDailyUninstallsByDevice() {
		return dailyUninstallsByDevice;
	}
	public void setDailyUninstallsByDevice(
			List<AppHistoricalStatsElement> dailyUninstallsByDevice) {
		this.dailyUninstallsByDevice = dailyUninstallsByDevice;
	}
	public List<AppHistoricalStatsElement> getDailyUpdatesByUser() {
		return dailyUpdatesByUser;
	}
	public void setDailyUpdatesByUser(
			List<AppHistoricalStatsElement> dailyUpdatesByUser) {
		this.dailyUpdatesByUser = dailyUpdatesByUser;
	}
	public List<AppHistoricalStatsElement> getCurrentInstallsByUser() {
		return currentInstallsByUser;
	}
	public void setCurrentInstallsByUser(
			List<AppHistoricalStatsElement> currentInstallsByUser) {
		this.currentInstallsByUser = currentInstallsByUser;
	}
	public List<AppHistoricalStatsElement> getTotalInstallsByUser() {
		return totalInstallsByUser;
	}
	public void setTotalInstallsByUser(
			List<AppHistoricalStatsElement> totalInstallsByUser) {
		this.totalInstallsByUser = totalInstallsByUser;
	}
	public List<AppHistoricalStatsElement> getDailyInstallsByUser() {
		return dailyInstallsByUser;
	}
	public void setdailyInstallsByUser(
			List<AppHistoricalStatsElement> dailyInstallsByUser) {
		this.dailyInstallsByUser = dailyInstallsByUser;
	}
	public List<AppHistoricalStatsElement> getGmcMessages() {
		return gmcMessages;
	}
	public void setGmcMessages(List<AppHistoricalStatsElement> gmcMessages) {
		this.gmcMessages = gmcMessages;
	}
	public List<AppHistoricalStatsElement> getGmcRegisters() {
		return gmcRegisters;
	}
	public void setGmcRegisters(List<AppHistoricalStatsElement> gmcRegisters) {
		this.gmcRegisters = gmcRegisters;
	}
	public List<AppHistoricalStatsElement> getAverageDailyRating() {
		return averageDailyRating;
	}
	public void setAverageDailyRating(
			List<AppHistoricalStatsElement> averageDailyRating) {
		this.averageDailyRating = averageDailyRating;
	}
	public List<AppHistoricalStatsElement> getAverageTotalRating() {
		return averageTotalRating;
	}
	public void setAverageTotalRating(
			List<AppHistoricalStatsElement> averageTotalRating) {
		this.averageTotalRating = averageTotalRating;
	}

}
