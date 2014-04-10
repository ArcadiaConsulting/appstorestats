package es.arcadiaconsulting.appstoresstats.ios.console;

import java.util.Date;

import es.arcadiaconsulting.appstoresstats.ios.io.DateHelper;
import es.arcadiaconsulting.appstoresstats.ios.model.StatsDataIOS;

public class IOSStatsExample {

	public static void main(String[] args) {
		
		
		IOSStoreStats iosQueryHelper = new IOSStoreStats();
		//get Full app stats
		StatsDataIOS fullStatsdata = (StatsDataIOS) iosQueryHelper.getFullStatsForApp("replaceWithAppleUser", "replaceWithApplePassword", "replaceWithAppSku", "replaceWithVendorID",null);
		
		
		//get dates app stats
		Date datefryday =  DateHelper.buildDateFromUTCString("2013-08-01T11:42:40Z");
		Date datedeployed = DateHelper.buildDateFromUTCString("2013-06-29T11:42:40Z");
		StatsDataIOS datesStatsdata = (StatsDataIOS) iosQueryHelper.getStatsForApp("replaceWithAppleUser", "replaceWithApplePassword", "replaceWithAppSku", datedeployed, datefryday, "replaceWithVendorID",null);
		
	}

}
