package com.github.andlyticsproject.console.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HttpContext;

import com.github.andlyticsproject.model.AppInfo;
import com.github.andlyticsproject.model.StatsDataAndroid;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.IStoreStats;
import es.arcadiaconsulting.appstoresstats.common.NumberHelper;

public class IStoreStatsAndroid implements IStoreStats {

	DevConsoleV2 console=null;
	

	
/*	public static IStoreStatsAndroid createForAccountAndPassword(String accountName, String password,
			DefaultHttpClient httpClient) {

		return new IStoreStatsAndroid(DevConsoleV2.createForAccountAndPassword(accountName, password, httpClient));
	}*/
	/**
	 * Basisc android available stats such as: Name, average rating, total downloads
	 * @param packageName
	 * @return
	 */
	private List<CommonStatsData> getBasicStatsDataAndroid()
	{
		List<CommonStatsData> result=null;
		List<AppInfo> apps=console.getAppInfo();
		if(apps!=null){
		result=new ArrayList<CommonStatsData>();
		for(AppInfo app:apps)
		{
			result.add(buildStats(app));
		}
		}
		return result;
		
	}
	/**
	 * Basic android available stats such as: Name, average rating, total downloads for specified app.
	 * @param packageName
	 * @return
	 */
	private CommonStatsData getBasicStatsDataAndroid(String packageName)
	{
		AppInfo app=console.getAppInfo(packageName);
		return buildStats(app);
		
	}
	private StatsDataAndroid buildStats(AppInfo app)
	{  StatsDataAndroid stats=null;
		if(app!=null){
		stats=new StatsDataAndroid();
		stats.setAndroidPlatform();
		stats.setAppId(app.getPackageName());
		
		stats.setAppName(app.getName());
		if(app.getLatestStats()!=null){
		stats.setAverageRate(NumberHelper.round(app.getLatestStats().getAvgRatingDiff(),2));
		stats.setDownloadsNumber(app.getLatestStats().getTotalDownloads());
		stats.setCurrentInstallationsNumber(app.getLatestStats().getActiveInstalls());
		stats.setRatingNumber(app.getLatestStats().getNumberOfComments());
		stats.setErrorNumber(app.getLatestStats().getNumberOfErrors());
		}
		}
		return stats;
	}

	private IStoreStatsAndroid(DevConsoleV2 console) {
		super();
		this.console = console;
	}

	@Override
	public CommonStatsData getStatsForApp(String user, String password,
			String appId, Date initDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommonStatsData> getStatsForAllApps(String user,
			String password, Date initDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized CommonStatsData getFullStatsForApp(String user, String password,
			String appId) {
		
		console=DevConsoleV2.createForAccountAndPassword(user, password, createDefaultHttpClient());
		return getBasicStatsDataAndroid(appId);
	}

	@Override
	public List<CommonStatsData> getFullStatsForAllApps(String user,
			String password) {
		console=DevConsoleV2.createForAccountAndPassword(user, password, createDefaultHttpClient());
		return getBasicStatsDataAndroid();
	}

	private DefaultHttpClient createDefaultHttpClient()
	{
		ThreadSafeClientConnManager cxMgr = new ThreadSafeClientConnManager( SchemeRegistryFactory.createDefault());
		cxMgr.setMaxTotal(100);
		cxMgr.setDefaultMaxPerRoute(20);
		DefaultHttpClient defHttp=new DefaultHttpClient(cxMgr);
		defHttp.setRedirectStrategy(new DefaultRedirectStrategy() {                
	        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {
	            boolean isRedirect=false;
	            try {
	                isRedirect = super.isRedirected(request, response, context);
	            } catch (ProtocolException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            if (!isRedirect) {
	                int responseCode = response.getStatusLine().getStatusCode();
	                if (responseCode == 301 || responseCode == 302) {
	                    return true;
	                }
	            }
	            return isRedirect;
	        }
	    });
		return defHttp;
	}
	public IStoreStatsAndroid() {
		super();
		// TODO Auto-generated constructor stub
	}


	
}
