package com.github.andlyticsproject.console.v2;

import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;

import static com.github.andlyticsproject.console.v2.Constants.*;
import static com.github.andlyticsproject.console.v2.CommonMatchers.*;

import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;
import com.github.andlyticsproject.model.StatsDataAndroid;

import es.arcadiaconsulting.appstoresstats.common.IStoreStats;
@RunWith(MockitoJUnitRunner.class)
public class IStoreStatsAndroidTest extends TestCase{
  
	@Mock
    private DevConsoleV2Protocol protocol;
	@Mock
    private PasswordAuthenticator authenticator;
	   
	@Mock
    private DefaultHttpClient httpClient;
	   
	   
	
	@InjectMocks
   private DevConsoleV2 console;



	@Test
	    public void testGetFullStats() throws Exception {
		//	DeveloperConsoleAccount account=new DeveloperConsoleAccount(DEVELOPER_ACCOUNT_OK,PACKAGE_NAME_OK);
		//	DeveloperConsoleAccount[] arrayAccounts={account};
		//	Date endDate=new Date();
		//	when(protocol.hasSessionCredentials()).thenReturn(true);
		//	when(protocol.getSessionCredentials()).thenReturn(CREDENTIALS_OK);
		//	when(authenticator.authenticate(false)).thenReturn(CREDENTIALS_OK);
		//	when(authenticator.authenticateSilently(false)).thenReturn(CREDENTIALS_OK);
			 
		  //  Date initDate=new Date(1378062000000L);
		//endDate.setTime(1378699200000L);
			
			
			
		//	when(console.authenticate(false)).thenReturn(true);
			//StatsDataAndroid inf=(StatsDataAndroid) stats.getStatsForApp(DEVELOPER_ACCOUNT_OK, PASSOK, PACKAGE_NAME_OK,initDate,endDate,null);

	 
	        //Verify if saveUser was invoked on userService with given 'user' object.
	       // Mockito.verify(stats).getStatsForApp(DEVELOPER_ACCOUNT_OK, PASSOK, PACKAGE_NAME_OK,initDate,endDate,null);
	 
	        //Verify with Argument Matcher
	       
	    }
	@Test
	public void testGetStatsDataAndroidBetweenDates() throws ClientProtocolException, IOException
	{	IStoreStatsAndroid stats=new IStoreStatsAndroid();
		stats.console=console;
		when(protocol.hasSessionCredentials()).thenReturn(true);
		when(protocol.getSessionCredentials()).thenReturn(CREDENTIALS_OK);
		when(protocol.createFetchAppsUrl(DEVELOPERID)).thenReturn(FETCH_APP_INFOS_URL);
		when(protocol.createFetchAppInfosRequest()).thenReturn(FETCH_APP_INFOS_POST);
		when(httpClient.execute(argThat(new HttpPostAppInfosArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_INFOS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppStatsArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_STATS_JSON);

		when(protocol.parseAppInfosResponse(anyString(), anyString(),anyString(), anyBoolean())).thenCallRealMethod();
		when(protocol.createFetchStatisticsUrl(DEVELOPERID)).thenReturn(FETCH_APP_STATS_URL);
		when(protocol.createFetchStatisticsRequest(anyString(), anyInt())).thenReturn(FETCH_APP_STATS_POST);
		doCallRealMethod().when(protocol).parseStatisticsResponse(anyString(), isA(AppStats.class), anyInt());
		
		
	
		
		Date endDate=new Date();
		 
		    Date initDate=new Date(1381442400000L);
		endDate.setTime(1381788000000L);

		StatsDataAndroid inf=(StatsDataAndroid) stats.getStatsDataAndroidBetweenDates("abc", initDate, endDate);
		assertNull(inf);
		inf=(StatsDataAndroid) stats.getStatsDataAndroidBetweenDates(PACKAGE_NAME_OK, initDate, endDate);
		assertNotNull(inf);
		assertEquals(inf.getAppId(), PACKAGE_NAME_OK);
		assertEquals(inf.getAppName(),APP_OK_NAME);
		assertEquals(inf.getAverageRate(), Float.parseFloat("5.0"));
		assertEquals(inf.getCurrentInstallationsNumber(), 12);
		assertEquals(inf.getErrorNumber(),0);
		assertEquals(inf.getRatingNumber(),1);
		assertEquals(inf.getDownloadsNumber(),2);
		
	}
	@Test
	public void testGetBasicStatsDataAndroid() throws ClientProtocolException, IOException
	{	IStoreStatsAndroid stats=new IStoreStatsAndroid();
		stats.console=console;
		when(protocol.hasSessionCredentials()).thenReturn(true);
		when(protocol.getSessionCredentials()).thenReturn(CREDENTIALS_OK);
		when(protocol.createFetchAppsUrl(DEVELOPERID)).thenReturn(FETCH_APP_INFOS_URL);
		when(protocol.createFetchAppInfosRequest()).thenReturn(FETCH_APP_INFOS_POST);
		when(httpClient.execute(argThat(new HttpPostAppInfosArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_INFOS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppStatsArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_STATS_JSON);

		when(protocol.parseAppInfosResponse(anyString(), anyString(),anyString(), anyBoolean())).thenCallRealMethod();
		when(protocol.createFetchStatisticsUrl(DEVELOPERID)).thenReturn(FETCH_APP_STATS_URL);
		when(protocol.createFetchStatisticsRequest(anyString(), anyInt())).thenReturn(FETCH_APP_STATS_POST);
		doCallRealMethod().when(protocol).parseStatisticsResponse(anyString(), isA(AppStats.class), anyInt());
		
		
	
		
		Date endDate=new Date();
		 
		    Date initDate=new Date(1381442400000L);
		endDate.setTime(1381788000000L);
		
		StatsDataAndroid inf=(StatsDataAndroid) stats.getStatsDataAndroidBetweenDates("abc", initDate, endDate);
		assertNull(inf);
		inf=(StatsDataAndroid) stats.getBasicStatsDataAndroid(PACKAGE_NAME_OK);
		assertNotNull(inf);
		assertEquals(inf.getAppId(), PACKAGE_NAME_OK);
		assertEquals(inf.getAppName(),APP_OK_NAME);
		assertEquals(inf.getAverageRate(), Float.parseFloat("5.0"));
		assertEquals(inf.getCurrentInstallationsNumber(), 12);
		assertEquals(inf.getErrorNumber(),0);
		assertEquals(inf.getRatingNumber(),1);
		assertEquals(inf.getDownloadsNumber(),70);
		initDate=new Date(1381788000000L);
		endDate.setTime(System.currentTimeMillis());
		inf=(StatsDataAndroid) stats.getStatsDataAndroidBetweenDates("abc", initDate, endDate);
		
		
	}
}
