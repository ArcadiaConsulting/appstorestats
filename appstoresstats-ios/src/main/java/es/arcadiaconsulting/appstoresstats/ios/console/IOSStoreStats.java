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
package es.arcadiaconsulting.appstoresstats.ios.console;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.CommonStatsData.Platform;
import es.arcadiaconsulting.appstoresstats.common.IStoreStats;
import es.arcadiaconsulting.appstoresstats.common.Rating;
import es.arcadiaconsulting.appstoresstats.ios.io.Autoingestion;
import es.arcadiaconsulting.appstoresstats.ios.io.DateHelper;
import es.arcadiaconsulting.appstoresstats.ios.io.DateHelperException;
import es.arcadiaconsulting.appstoresstats.ios.io.HTTPClientHelper;
import es.arcadiaconsulting.appstoresstats.ios.io.JSONParser;
import es.arcadiaconsulting.appstoresstats.ios.io.RSSHelper;
import es.arcadiaconsulting.appstoresstats.ios.model.AppInfo;
import es.arcadiaconsulting.appstoresstats.ios.model.Constants;
import es.arcadiaconsulting.appstoresstats.ios.model.StatsDataIOS;
import es.arcadiaconsulting.appstoresstats.ios.model.UnitData;

public class IOSStoreStats implements IStoreStats{
	
	private static final Logger logger = LoggerFactory.getLogger(IOSStoreStats.class);

	@Override
	/**
	 * La fecha inicial tiene que ser posterior al despliegue
	 */
	public CommonStatsData getStatsForApp(String user, String password,
			String appId, Date initDate, Date endDate, String vendorId)  {
		Date current = new Date(System.currentTimeMillis());
		GregorianCalendar enddateCalendar = new GregorianCalendar();
		enddateCalendar.setTime(current);
		enddateCalendar.add(Calendar.DATE, -1);
		GregorianCalendar initDateGregorian = new GregorianCalendar();
		initDateGregorian.setTime(initDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String appleId;
		try {
			appleId = Autoingestion.getAppleIDBySKU(user, password, vendorId, Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY, Constants.REPORT_SUBTYPE_SUMMARY_NAME, sdf.format(initDateGregorian.getTime()), appId);
		} catch (IOException e1) {
			logger.error("error getting appleid",e1);
			return null;
			
		}
		
		AppInfo appInfo = JSONParser.getAPPInfoByID(appleId);
		Date releaseDate = appInfo.getReleaseDate();
		String appName = appInfo.getAppName();
StatsDataIOS statsData= new StatsDataIOS(appId,endDate,releaseDate,appName);
		GregorianCalendar releasecalendar = new GregorianCalendar();
		releasecalendar.setTime(releaseDate);
		

		//get rating
		String downloadURL = RSSHelper.getItunesURL(appleId);
		if(downloadURL!=null){
			statsData.setDownloadURL(downloadURL);
			List<Rating> ratingList = RSSHelper.getItunesRating(appleId);
			int ratingPlus = 0;
			for (Iterator iterator = ratingList.iterator(); iterator.hasNext();) {
				Rating rating = (Rating) iterator.next();
				if(initDate.before(rating.getDate())&&endDate.after(rating.getDate()))
					ratingPlus = ratingPlus +rating.getRate();
			}
			
			statsData.setRatings(ratingList);
			statsData.setAverageRate(ratingPlus/ratingList.size());
		}else{
			logger.info("There are not rate information we cant get downloadURL or rating");
		}
		
		
		if(initDate.before(releaseDate))
			initDate = (Date)releaseDate.clone();
		
		
		try {
			List<UnitData> unitData = DateHelper.getUnitDataByDate(initDate, endDate, appId, user, password, vendorId);
			statsData.setUnitDataList(unitData);
			int units = 0;
			for (Iterator iterator = unitData.iterator(); iterator.hasNext();) {
				UnitData unitData2 = (UnitData) iterator.next();
				units= units + unitData2.getUnits();
				
			}	
			
			statsData.setDownloadsNumber(units);
		} catch (DateHelperException e) {
			logger.error("Error getting units");
			return null;
		}
		
		return statsData;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see es.arcadiaconsulting.appstoresstats.common.IStoreStats#getFullStatsForApp(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * en la fecha actual debe haber habido alguna descarga en el mes ultimo
	 */
	public CommonStatsData getFullStatsForApp(String user, String password,
			String appId, String vendorId) {
		Date endDate = new Date(System.currentTimeMillis());
		GregorianCalendar enddateCalendar = new GregorianCalendar();
		enddateCalendar.setTime(endDate);
		enddateCalendar.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String appleId;
		try {
			appleId = Autoingestion.getAppleIDBySKU(user, password, vendorId, Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY, Constants.REPORT_SUBTYPE_SUMMARY_NAME, sdf.format(enddateCalendar.getTime()), appId);
		} catch (IOException e1) {
			logger.error("error getting appleid",e1);
			return null;
			
		}
		
		
		AppInfo appInfo = JSONParser.getAPPInfoByID(appleId);
		Date initDate = appInfo.getReleaseDate();
		String appName = appInfo.getAppName();
StatsDataIOS statsData= new StatsDataIOS(appId,endDate,initDate,appName);
		
//get rating
		String downloadURL = RSSHelper.getItunesURL(appleId);
		if(downloadURL!=null){
			statsData.setDownloadURL(downloadURL);
			List<Rating> ratingList = RSSHelper.getItunesRating(appleId);
			int ratingPlus = 0;
			for (Iterator iterator = ratingList.iterator(); iterator.hasNext();) {
				Rating rating = (Rating) iterator.next();
				ratingPlus = ratingPlus +rating.getRate();
			}
			
			statsData.setRatings(ratingList);
			statsData.setAverageRate(ratingPlus/ratingList.size());
		}else{
			logger.info("There are not rate information we cant get downloadURL or rating");
		}
		try {
			List<UnitData> unitData = DateHelper.getFullUnitData(initDate, endDate, appId, user, password, vendorId);
			statsData.setUnitDataList(unitData);
			int units = 0;
			for (Iterator iterator = unitData.iterator(); iterator.hasNext();) {
				UnitData unitData2 = (UnitData) iterator.next();
				units= units + unitData2.getUnits();
				
			}	
			
			statsData.setDownloadsNumber(units);
			statsData.setFirstDeploymentDate(initDate);
			
		} catch (DateHelperException e) {
			logger.error("Error getting units");
			return null;
		}
		
		return statsData;
	}
	



}
