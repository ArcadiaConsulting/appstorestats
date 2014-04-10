package com.github.andlyticsproject.console.v2;

import static com.github.andlyticsproject.console.v2.Constants.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.andlyticsproject.console.v2.CommonMatchers.HttpPostAppInfosArgumentMatcher;
import com.github.andlyticsproject.console.v2.CommonMatchers.HttpPostAppStatsArgumentMatcher;
import com.github.andlyticsproject.console.v2.CommonMatchers.HttpPostAppCommentsArgumentMatcher;
import com.github.andlyticsproject.model.AppInfo;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;

@RunWith(MockitoJUnitRunner.class)
public class DevConsoleV2Test extends TestCase{
	 @Mock
	 PasswordAuthenticator authenticator;
	@InjectMocks
    private DevConsoleV2 console;

    @Mock
    private DefaultHttpClient httpClient;
	@Mock
    private DevConsoleV2Protocol protocol;
   

   


    @Test
    public void testGetAppInfoFromFullQuery() throws ClientProtocolException, IOException
    {
    	DeveloperConsoleAccount account=new DeveloperConsoleAccount(DEVELOPER_ACCOUNT_OK,PACKAGE_NAME_OK);
		
		Date endDate=new Date();
		 
	    Date initDate=new Date(1378062000000L);
	endDate.setTime(1378699200000L);
		
   	when(protocol.hasSessionCredentials()).thenReturn(true);
		when(protocol.getSessionCredentials()).thenReturn(CREDENTIALS_OK);
		when(authenticator.authenticate(false)).thenReturn(CREDENTIALS_OK);
		when(authenticator.authenticateSilently(false)).thenReturn(CREDENTIALS_OK);
		when(protocol.createFetchAppsUrl(DEVELOPERID)).thenReturn(FETCH_APP_INFOS_URL);
		when(protocol.createFetchAppInfoRequest(anyString())).thenReturn(FETCH_APP_INFOS_POST);
		when(httpClient.execute(argThat(new HttpPostAppInfosArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_INFOS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppStatsArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_STATS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppInfosArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_INFOS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppCommentsArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_RATINGS_JSON);

		when(protocol.parseAppInfoResponse(anyString(), anyString(),anyString(), anyBoolean())).thenCallRealMethod();
		when(protocol.createFetchStatisticsUrl(DEVELOPERID)).thenReturn(FETCH_APP_STATS_URL);
		when(protocol.createFetchStatisticsRequest(anyString(), anyInt())).thenReturn(FETCH_APP_STATS_POST);
		when(protocol.createCommentsUrl(DEVELOPERID)).thenReturn(COMMENTS_URL);
		when(protocol.createFetchCommentsRequest(PACKAGE_NAME_OK, 0, 50, "es")).thenReturn(COMMENTS_POST);
		when(protocol.createFetchRatingsRequest(PACKAGE_NAME_OK)).thenReturn(RATINGS_POST);
			when(protocol.createFetchStatisticsRequest(anyString(), anyInt())).thenReturn(FETCH_APP_STATS_POST);
	
		 doCallRealMethod().when(protocol).parseStatisticsResponse(anyString(),isA(AppStats.class),anyInt());
			doCallRealMethod().when(protocol).parseRatingsResponse(anyString(),isA(AppStats.class));


		AppInfo app=console.getAppInfoFromFullQuery(PACKAGE_NAME_OK,null);
		assertNotNull(app);
		assertEquals(app.getDeveloperId(), DEVELOPERID );
		//assertEquals(app.getAccount(), DEVELOPER_ACCOUNT_OK );
		assertEquals(app.getName(), APP_OK_NAME );
		assertEquals(app.getPackageName(), PACKAGE_NAME_OK );
		assertNotNull(app.getLatestStats());
		assertEquals(app.getLatestStats().getActiveInstalls(),290);
		assertEquals(app.getLatestStats().getTotalDownloads(), 4148);
		assertEquals(app.getLatestStats().getAvgRatingDiff(), Float.parseFloat("4.7884617"));
		assertEquals(app.getLatestStats().getNumberOfComments(), 52);
		assertNull(app.getLatestStats().getHistoricalStats());
		
    }
    @SuppressWarnings("unchecked")
	@Test
    public void testGetAppInfoAndStatisticsFromFullQuery() throws ClientProtocolException, IOException
    {
    	when(protocol.hasSessionCredentials()).thenReturn(true);
		when(protocol.getSessionCredentials()).thenReturn(CREDENTIALS_OK);
		when(authenticator.authenticate(false)).thenReturn(CREDENTIALS_OK);
		when(authenticator.authenticateSilently(false)).thenReturn(CREDENTIALS_OK);
		when(protocol.createFetchAppsUrl(DEVELOPERID)).thenReturn(FETCH_APP_INFOS_URL);
		when(protocol.createFetchAppInfoRequest(anyString())).thenReturn(FETCH_APP_INFOS_POST);
		when(httpClient.execute(argThat(new HttpPostAppInfosArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_INFOS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppStatsArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_STATS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppInfosArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_INFOS_JSON);
		when(httpClient.execute(argThat(new HttpPostAppCommentsArgumentMatcher()),isA(ResponseHandler.class) )).thenReturn(APP_RATINGS_JSON);

		when(protocol.parseAppInfoResponse(anyString(), anyString(),anyString(), anyBoolean())).thenCallRealMethod();
		when(protocol.createFetchStatisticsUrl(DEVELOPERID)).thenReturn(FETCH_APP_STATS_URL);
		when(protocol.createFetchStatisticsRequest(anyString(), anyInt())).thenReturn(FETCH_APP_STATS_POST);
		when(protocol.createCommentsUrl(DEVELOPERID)).thenReturn(COMMENTS_URL);
		when(protocol.createFetchCommentsRequest(PACKAGE_NAME_OK, 0, 50, "es")).thenReturn(COMMENTS_POST);
		when(protocol.createFetchRatingsRequest(PACKAGE_NAME_OK)).thenReturn(RATINGS_POST);
			when(protocol.createFetchStatisticsRequest(anyString(), anyInt())).thenReturn(FETCH_APP_STATS_POST);
	
		 doCallRealMethod().when(protocol).parseStatisticsResponse(anyString(),isA(AppStats.class),anyInt());
			doCallRealMethod().when(protocol).parseRatingsResponse(anyString(),isA(AppStats.class));

		 AppInfo app=console.getAppInfoAndStatisticsFromFullQuery(PACKAGE_NAME_OK,null);
		assertNotNull(app);
		assertEquals(app.getDeveloperId(), DEVELOPERID );
		//assertEquals(app.getAccount(), DEVELOPER_ACCOUNT_OK );
		assertEquals(app.getName(), APP_OK_NAME );
		assertEquals(app.getPackageName(), PACKAGE_NAME_OK );
		assertNotNull(app.getLatestStats());
		assertEquals(app.getLatestStats().getActiveInstalls(), 290);
		assertEquals(app.getLatestStats().getTotalDownloads(), 4148);
		assertEquals(app.getLatestStats().getAvgRatingDiff(),Float.parseFloat("4.7884617"));
		assertEquals(app.getLatestStats().getNumberOfComments(), 52);
		assertNotNull(app.getLatestStats().getHistoricalStats());
		
    }
  
    
    
  
    
}
