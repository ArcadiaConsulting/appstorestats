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
package es.arcadiaconsulting.appstoresstats.android.console;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.andlyticsproject.console.v2.DevConsoleV2;
import com.github.andlyticsproject.model.AppHistoricalStatsElement;
import com.github.andlyticsproject.model.AppInfo;

import es.arcadiaconsulting.appstoresstats.android.model.StatsDataAndroid;
import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.IStoreStats;
import es.arcadiaconsulting.appstoresstats.common.NumberHelper;

public class AndroidStoreStats implements IStoreStats {

	DevConsoleV2 console=null;
	private final int CONNECTION_TIMEOUT=10000;
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(AndroidStoreStats.class);


	
/*	public static IStoreStatsAndroid createForAccountAndPassword(String accountName, String password,
			DefaultHttpClient httpClient) {

		return new IStoreStatsAndroid(DevConsoleV2.createForAccountAndPassword(accountName, password, httpClient));
	}*/
	/**
	 * Basisc android available stats such as: Name, average rating, total downloads
	 * @param packageName
	 * @return
	 */
	protected List<CommonStatsData> getBasicStatsDataAndroid()
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
	
	
	protected CommonStatsData getStatsDataAndroidBetweenDates(String packageName,Date initDate,Date endDate)
	{
		/*
		 * CUrrently obtaining full app list because ratings, downloads... etc are not visible from detailed app view
		 * AppInfo app=console.getAppInfo(packageName);
		 */
		if(logger.isDebugEnabled())
			logger.debug("getStatsDataAndroidBetweenDates() - {}", String.format("Getting statistics for %d", packageName));
		AppInfo app=console.getAppInfoAndStatisticsFromFullQuery(packageName);
		return buildStats(app,initDate,endDate);
	}
	
	
	
	
	/**
	 * Basic android available stats such as: Name, average rating, total downloads and active installations for specified app.
	 * @param packageName
	 * @return
	 */
	protected CommonStatsData getBasicStatsDataAndroid(String packageName)
	{
		AppInfo app=console.getAppInfoFromFullQuery(packageName);
		return buildStats(app);
		
	}

	protected StatsDataAndroid parseInstallationsBetweenDates(StatsDataAndroid stats,AppInfo app)
	{
		if(app!=null&&app.getLatestStats()!=null&&app.getLatestStats().getHistoricalStats()!=null&&app.getLatestStats().getHistoricalStats().getDailyInstallsByDevice()!=null)
		{
			int installs=0;
			Iterator<AppHistoricalStatsElement> it=app.getLatestStats().getHistoricalStats().getDailyInstallsByDevice().iterator();
			while(it.hasNext())
			{
				AppHistoricalStatsElement el=it.next();
				if(equalsOrHigerToInit(stats.getInitDate(),el.getDate()))
				{
					if(equalsOrLowerToEnd(stats.getEndDate(),el.getDate()))
					{
						installs+=Integer.parseInt(el.getNumber());
					}
					else
					{
						break;
					}
					
				
					
				}
			}
			stats.setDownloadsNumber(installs);
		}
		return stats;
	}
	protected boolean equalsOrHigerToInit(Date initDate,Date compareDate)
	{
		Calendar init=Calendar.getInstance();
		init.setTime(initDate);

		Calendar comp=Calendar.getInstance();
		comp.setTime(compareDate);
		if(init.get(Calendar.YEAR)==comp.get(Calendar.YEAR)){
		if(init.get(Calendar.DAY_OF_YEAR)<=comp.get(Calendar.DAY_OF_YEAR)){
			return true;
		}
		}
		else{
			if(init.get(Calendar.YEAR)<comp.get(Calendar.YEAR)){
				return true;
			}
		}
		return false;
	}
	protected boolean equalsOrLowerToEnd(Date endDate,Date compareDate)
	{
		Calendar end=Calendar.getInstance();
		end.setTime(endDate);
		Calendar comp=Calendar.getInstance();
		comp.setTime(compareDate);

		if(end.get(Calendar.YEAR)==comp.get(Calendar.YEAR)){
		if(end.get(Calendar.DAY_OF_YEAR)>=comp.get(Calendar.DAY_OF_YEAR))
			return true;
		}
		else if(end.get(Calendar.YEAR)>comp.get(Calendar.YEAR)){
			return true;
		}
		return false;
	}
	protected StatsDataAndroid buildStats(AppInfo app,Date initDate,Date endDate)
	{
		StatsDataAndroid stats=buildStats(app);
		if(stats!=null){
		if(initDate!=null)
			stats.setInitDate(initDate);
		if(endDate!=null)
			stats.setEndDate(endDate);
		stats=parseInstallationsBetweenDates(stats,app);
		}
		return stats;
		
		
		
	}
	
	protected StatsDataAndroid buildStats(AppInfo app)
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

	protected AndroidStoreStats(DevConsoleV2 console) {
		super();
		this.console = console;
	}

	@Override
	public CommonStatsData getStatsForApp(String user, String password,
			String appId, Date initDate, Date endDate,String vectorId) {
		console=DevConsoleV2.createForAccountAndPassword(user, password, createDefaultHttpClient());
		return getStatsDataAndroidBetweenDates(appId,initDate,endDate);
	}

	
	public List<CommonStatsData> getStatsForAllApps(String user,
			String password, Date initDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonStatsData getFullStatsForApp(String user, String password,
			String appId,String vectorId) {
		
		console=DevConsoleV2.createForAccountAndPassword(user, password, createDefaultHttpClient());
		return getBasicStatsDataAndroid(appId);
	}

	
	public List<CommonStatsData> getFullStatsForAllApps(String user,
			String password) {
		console=DevConsoleV2.createForAccountAndPassword(user, password, createDefaultHttpClient());
		return getBasicStatsDataAndroid();
	}

	protected DefaultHttpClient createDefaultHttpClient()
	{
		ThreadSafeClientConnManager cxMgr = new ThreadSafeClientConnManager( SchemeRegistryFactory.createDefault());
		cxMgr.setMaxTotal(100);
		cxMgr.setDefaultMaxPerRoute(20);
		DefaultHttpClient defHttp=new DefaultHttpClient(cxMgr);
		HttpParams params = new BasicHttpParams();
		defHttp.setParams(params);
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
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
	public AndroidStoreStats() {
		super();
		// TODO Auto-generated constructor stub
	}


	
}
