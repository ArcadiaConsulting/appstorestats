package es.arcadiaconsulting.appstoresstats.ios.console;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.CommonStatsData.Platform;
import es.arcadiaconsulting.appstoresstats.common.IStoreStats;
import es.arcadiaconsulting.appstoresstats.ios.io.DateHelper;
import es.arcadiaconsulting.appstoresstats.ios.io.DateHelperException;
import es.arcadiaconsulting.appstoresstats.ios.io.HTTPClientHelper;
import es.arcadiaconsulting.appstoresstats.ios.io.JSONParser;
import es.arcadiaconsulting.appstoresstats.ios.model.StatsDataIOS;
import es.arcadiaconsulting.appstoresstats.ios.model.UnitData;

public class IOSQueryHelper implements IStoreStats{
	
	private static final Logger logger = LoggerFactory.getLogger(IOSQueryHelper.class);

	@Override
	public CommonStatsData getStatsForApp(String user, String password,
			String appId, Date initDate, Date endDate, String vendorId)  {
		
		StatsDataIOS statsData= new StatsDataIOS(appId,endDate,initDate);
		
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
		Date initDate = JSONParser.getAPPInfoByID(appId).getReleaseDate();
StatsDataIOS statsData= new StatsDataIOS(appId,endDate,initDate);
		
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
	



}
