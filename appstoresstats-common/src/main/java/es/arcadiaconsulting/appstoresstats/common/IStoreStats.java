package es.arcadiaconsulting.appstoresstats.common;

import java.util.Date;
import java.util.List;

public interface IStoreStats {
	
	public CommonStatsData getStatsForApp(String user,String password,String appId,Date initDate,Date endDate,String vendorId);
	//public List<CommonStatsData> getStatsForAllApps(String user,String password,Date initDate,Date endDate);
	public CommonStatsData getFullStatsForApp(String user,String password,String appId,String vendorId);
	//public List<CommonStatsData> getFullStatsForAllApps(String user,String password);
	
}
