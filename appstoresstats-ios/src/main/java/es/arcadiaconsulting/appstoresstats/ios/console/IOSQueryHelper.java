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
import es.arcadiaconsulting.appstoresstats.ios.io.Autoingestion;
import es.arcadiaconsulting.appstoresstats.ios.io.DateHelper;
import es.arcadiaconsulting.appstoresstats.ios.io.DateHelperException;
import es.arcadiaconsulting.appstoresstats.ios.io.HTTPClientHelper;
import es.arcadiaconsulting.appstoresstats.ios.io.JSONParser;
import es.arcadiaconsulting.appstoresstats.ios.model.AppInfo;
import es.arcadiaconsulting.appstoresstats.ios.model.Constants;
import es.arcadiaconsulting.appstoresstats.ios.model.StatsDataIOS;
import es.arcadiaconsulting.appstoresstats.ios.model.UnitData;

public class IOSQueryHelper implements IStoreStats{
	
	private static final Logger logger = LoggerFactory.getLogger(IOSQueryHelper.class);

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String appleId;
		try {
			appleId = Autoingestion.getAppleIDBySKU(user, password, vendorId, Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY, Constants.REPORT_SUBTYPE_SUMMARY_NAME, sdf.format(enddateCalendar.getTime()), appId);
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
		GregorianCalendar initDateGregorian = new GregorianCalendar();
		initDateGregorian.setTime(initDate);

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
	public CommonStatsData getFullStatsForApp(String user, String password,
			String appId, String vendorId) {
		Date endDate = new Date(System.currentTimeMillis());
		GregorianCalendar enddateCalendar = new GregorianCalendar();
		enddateCalendar.setTime(endDate);
		enddateCalendar.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String appleId;
		try {
			appleId = Autoingestion.getAppleIDBySKU(user, password, vendorId, Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY, Constants.REPORT_SUBTYPE_SUMMARY_NAME, sdf.format(enddateCalendar.getTime()), appId);
		} catch (IOException e1) {
			logger.error("error getting appleid",e1);
			return null;
			
		}
		
		AppInfo appInfo = JSONParser.getAPPInfoByID(appleId);
		Date initDate = appInfo.getReleaseDate();
		String appName = appInfo.getAppName();
StatsDataIOS statsData= new StatsDataIOS(appId,endDate,initDate,appName);
		
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
