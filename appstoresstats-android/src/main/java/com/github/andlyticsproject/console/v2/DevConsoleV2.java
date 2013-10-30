package com.github.andlyticsproject.console.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.github.andlyticsproject.console.AuthenticationException;
import com.github.andlyticsproject.console.DevConsole;
import com.github.andlyticsproject.console.DevConsoleException;
import com.github.andlyticsproject.console.NetworkException;
import com.github.andlyticsproject.model.AppInfo;
import com.github.andlyticsproject.model.AppStats;
import com.github.andlyticsproject.model.Comment;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;
//import java.net.URI;

/**
 * This is a WIP class representing the new v2 version of the developer console.
 * The aim is to build it from scratch to make it a light weight and as well
 * documented at the end as possible. Once it is done and available to all
 * users, we will rip out the old code and replace it with this.
 * 
 * Once v2 is available to all users, there is scope for better utilising the
 * available statistics data, so keep that in mind when developing this class.
 * For now though, keep it simple and get it working.
 * 
 * See https://github.com/AndlyticsProject/andlytics/wiki/Developer-Console-v2
 * for some more documentation
 * 
 * This class fetches the data, which is then passed using {@link JsonParser}
 * 
 */
public class DevConsoleV2 implements DevConsole {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DevConsoleV2.class);

	// 30 seconds -- for both socket and connection
	public static final int TIMEOUT = 30 * 1000;

	private static final String TAG = DevConsoleV2.class.getSimpleName();

	private static final boolean DEBUG = false;

	private DefaultHttpClient httpClient;
	private DevConsoleAuthenticator authenticator;
	private String accountName;
	private DevConsoleV2Protocol protocol;

	private ResponseHandler<String> responseHandler = HttpClientFactory.createResponseHandler();
	//private ResponseHandler<byte[]> responseHandler = new ResponseHandler<byte[]>();

 
	
	public static DevConsoleV2 createForAccountAndPassword(String accountName, String password,
			DefaultHttpClient httpClient) {
		DevConsoleAuthenticator authenticator = new PasswordAuthenticator(accountName, password,
				httpClient);

		return new DevConsoleV2(httpClient, authenticator, new DevConsoleV2Protocol());
	}

	private DevConsoleV2(DefaultHttpClient httpClient, DevConsoleAuthenticator authenticator,
			DevConsoleV2Protocol protocol) {
		this.httpClient = httpClient;
		this.authenticator = authenticator;
		this.accountName = authenticator.getAccountName();
		this.protocol = protocol;
	}

	/**
	 * Gets a list of available apps for the given account
	 * 
	 * @param activity
	 * @return
	 * @throws DevConsoleException
	 */
	public synchronized List<AppInfo> getAppInfo() throws DevConsoleException {
		try {
			// the authenticator launched a sub-activity, bail out for now
			if (!authenticateWithCachedCredentialas()) {
				return new ArrayList<AppInfo>();
			}

			return fetchAppInfosAndStatistics();
		} catch (AuthenticationException ex) {
			if (!authenticateFromScratch()) {
				return new ArrayList<AppInfo>();
			}

			return fetchAppInfosAndStatistics();
		}
	}
	/**
	 * Gets a data for specific app for the given account
	 * 
	 * @param activity
	 * @return
	 * @throws DevConsoleException
	 */
	public synchronized AppInfo getAppInfoFromFullQuery(String packageName) throws DevConsoleException {
		try {
			// the authenticator launched a sub-activity, bail out for now
			if (!authenticateWithCachedCredentialas()) {
				return null;
			}

			return fetchAppInfoAndStatisticsFromFullQuery(packageName);
		} catch (AuthenticationException ex) {
			if (!authenticateFromScratch()) {
				return null;
			}

			return fetchAppInfoAndStatisticsFromFullQuery(packageName);
		}
	}
	
	private AppInfo fetchAppInfoAndStatisticsFromFullQuery(String packageName)
	{
		List<AppInfo> apps = fetchAppInfos();
		
		for(AppInfo app:apps)
		{
			if(app.getPackageName().equals(packageName))
			{
				fetchStatistics(app, app.getLatestStats(), 4);
				//fetchRatings(app, app.getLatestStats());
				return app;
			}
		}
		return null;
	}
	private List<AppInfo> fetchAppInfosAndStatistics() {
		// Fetch a list of available apps
		List<AppInfo> apps = fetchAppInfos();

		for (AppInfo app : apps) {
			// Fetch remaining app statistics
			// Latest stats object, and active/total installs is fetched in fetchAppInfos
			AppStats stats = app.getLatestStats();
			
			
			 fetchRatings(app, stats);
			 
               //TODO: obtain locale fix implementation
			//stats.setNumberOfComments(fetchCommentsCount(app, "es-Es"));
		}

		return apps;
	}

	/**
	 * Gets a list of comments for the given app based on the startIndex and
	 * count
	 *
	 * @param packageName
	 * @param startIndex
	 * @param count
	 * @return
	 * @throws DevConsoleException
	 */
	public synchronized List<Comment> getComments(String packageName,
			String developerId, int startIndex, int count, String displayLocale)
			throws DevConsoleException {
		try {
			if (!authenticateWithCachedCredentialas()) {
				return new ArrayList<Comment>();
			}

			return fetchComments(packageName, developerId, startIndex, count, displayLocale);
		} catch (AuthenticationException ex) {
			if (!authenticateFromScratch()) {
				return new ArrayList<Comment>();
			}

			return fetchComments(packageName, developerId, startIndex, count, displayLocale);
		}
	}

	public Comment replyToComment(String packageName, String developerId, String commentUiqueId,
			String reply) {
		String response = post(protocol.createCommentsUrl(developerId),
				protocol.createReplyToCommentRequest(packageName, commentUiqueId, reply),
				developerId);

		return protocol.parseCommentReplyResponse(response);
	}
	
	
	/**
	 * Fetches a combined list of apps for all avaiable console accounts
	 * 
	 * @return combined list of apps
	 * @throws DevConsoleException
	 */
        
	private List<AppInfo> fetchAppInfos() throws DevConsoleException {
		List<AppInfo> result = new ArrayList<AppInfo>();
		for (DeveloperConsoleAccount consoleAccount : protocol.getSessionCredentials()
				.getDeveloperConsoleAccounts()) {
			String developerId = consoleAccount.getDeveloperId();
                        String developername = consoleAccount.getName();
			if (logger.isDebugEnabled()) {
				logger.debug("fetchAppInfos() - {}", "Getting apps for: " + developername + " - " + developerId); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			String response = post(protocol.createFetchAppsUrl(developerId),
					protocol.createFetchAppInfosRequest(), developerId);

			// don't skip incomplete apps, so we can get the package list
			List<AppInfo> apps = protocol.parseAppInfosResponse(response, accountName,developerId, true);
			if (apps.isEmpty()) {
				continue;
			}
                        
			result.addAll(apps);
			List<String> incompletePackages = new ArrayList<String>();
			for (AppInfo app : apps) {
				if (app.isIncomplete()) {
					result.remove(app);
					incompletePackages.add(app.getPackageName());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("fetchAppInfos() - {}", String.format("Found %d apps for %s", apps.size(), developerId)); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug("fetchAppInfos() - {}", String.format("Incomplete packages: %d", incompletePackages.size())); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (incompletePackages.isEmpty()) {
				continue;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("fetchAppInfos() - {}", String.format("Got %d incomplete apps, issuing details request", incompletePackages.size())); //$NON-NLS-1$ //$NON-NLS-2$
			}
                
                response = post(protocol.createFetchAppsUrl(developerId),
                                protocol.createFetchAppInfosRequest(incompletePackages), developerId);
                        
                // if info is not here, not much to do, skip
                List<AppInfo> extraApps = protocol.parseAppInfosResponse(response, developerId,accountName, true);
			if (logger.isDebugEnabled()) {
				logger.debug("fetchAppInfos() - {}", String.format("Got %d extra apps from details request", extraApps.size())); //$NON-NLS-1$ //$NON-NLS-2$
			}
                for (AppInfo appInfo : extraApps) {
                        appInfo.setDeveloperId(developerId);
                        appInfo.setDeveloperName(consoleAccount.getName());
                }
			result.addAll(extraApps);
                        
		}
                
                
		return result;
	}

	/**
	 * Fetches statistics for the given packageName of the given statsType and
	 * adds them to the given {@link AppStats} object
	 * 
	 * This is not used as statistics can be fetched via fetchAppInfos Can use
	 * it later to get historical etc data
	 *
	 * @param stats
	 * @param statsType
	 * @throws DevConsoleException
	 */
       
	@SuppressWarnings("unused")
	private void fetchStatisticsForSingleApp(AppInfo appInfo, AppStats stats, int statsType)
			throws DevConsoleException {
		String developerId = appInfo.getDeveloperId();
		String response = post(protocol.createFetchStatisticsUrl(developerId),
				protocol.createFetchStatisticsRequest(appInfo.getPackageName(), statsType),
				developerId);
		protocol.parseStatisticsResponse(response, stats, statsType);
	}	
	@SuppressWarnings("unused")
	private void fetchStatistics(AppInfo appInfo, AppStats stats, int statsType)
			throws DevConsoleException {
		String developerId = appInfo.getDeveloperId();
		String response = post(protocol.createFetchStatisticsUrl(developerId),
				protocol.createFetchStatisticsRequest(appInfo.getPackageName(), statsType),
				developerId);
		protocol.parseStatisticsResponse(response, stats, statsType);
	}

	/**
	 * Fetches ratings for the given packageName and adds them to the given {@link AppStats} object
	 *
	 *            The app to fetch ratings for
	 * @param stats
	 *            The AppStats object to add them to
	 * @throws DevConsoleException
	 */
	private void fetchRatings(AppInfo appInfo, AppStats stats) throws DevConsoleException {
		String developerId = appInfo.getDeveloperId();
		String response = post(protocol.createCommentsUrl(developerId),
				protocol.createFetchRatingsRequest(appInfo.getPackageName()), developerId);
		protocol.parseRatingsResponse(response, stats);
	}

	/**
	 * Fetches the number of comments for the given packageName
	 *
	 * @return
	 * @throws DevConsoleException
	 */
	private int fetchCommentsCount(AppInfo appInfo, String displayLocale)
			throws DevConsoleException {
		// TODO -- this doesn't always produce correct results
		// emulate the console: fetch first 50, get approx num. comments,
		// fetch last 50 (or so) to get exact number.
		int finalNumComments = 0;
		int pageSize = 100;

		String developerId = appInfo.getDeveloperId();
		String response = post(protocol.createCommentsUrl(developerId),
				protocol.createFetchCommentsRequest(appInfo.getPackageName(), 0, pageSize,
						displayLocale), developerId);
		int approxNumComments = protocol.extractCommentsCount(response);
		if (approxNumComments <= pageSize) {
			// this has a good chance of being exact
			return approxNumComments;
		}

		response = post(
				protocol.createCommentsUrl(developerId),
				protocol.createFetchCommentsRequest(appInfo.getPackageName(), approxNumComments
						- pageSize, pageSize, displayLocale), developerId);
		finalNumComments += protocol.extractCommentsCount(response);

		return finalNumComments;
	}

	private List<Comment> fetchComments(String packageName, String developerId, int startIndex,
			int count, String displayLocale) throws DevConsoleException {
		List<Comment> comments = new ArrayList<Comment>();
		String response = post(protocol.createCommentsUrl(developerId),
				protocol.createFetchCommentsRequest(packageName, startIndex, count, displayLocale),
				developerId);
		comments.addAll(protocol.parseCommentsResponse(response));

		return comments;
	}

	private boolean authenticateWithCachedCredentialas() {
		return authenticate(false);
	}

	private boolean authenticateFromScratch() {
		return authenticate(true);
	}

	/**
	 * Logs into the Android Developer Console
	 *
	 * @throws DevConsoleException
	 */
	private boolean authenticate(boolean invalidateCredentials)
			throws DevConsoleException {
		if (invalidateCredentials) {
			protocol.invalidateSessionCredentials();
		}

		if (protocol.hasSessionCredentials()) {
			// nothing to do
			return true;
		}

		SessionCredentials sessionCredentials = authenticator.authenticateSilently(invalidateCredentials);
		protocol.setSessionCredentials(sessionCredentials);

		return protocol.hasSessionCredentials();
	}

	public String post(String url, String postData, String developerId) {
		try {
                        HttpPost post = new HttpPost(url);
                        protocol.addHeaders(post, developerId);
                                                
			post.setEntity(new StringEntity(postData, HTTP.UTF_8));
                        
			if (DEBUG) {
				CookieStore cookieStore = httpClient.getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				for (Cookie c : cookies) {
					if (logger.isDebugEnabled()) {
						logger.debug("post(String, String, String) - {}", String.format("****Cookie**** %s=%s", c.getName(), c.getValue())); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}

			return httpClient.execute(post, responseHandler);
		} catch (HttpResponseException e) {
			if (e.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
				throw new AuthenticationException(e);
			}
			throw new NetworkException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}

	}

	public boolean canReplyToComments() {
		return protocol.canReplyToComments();
	}

	public boolean hasSessionCredentials() {
		return protocol.hasSessionCredentials();
	}
	private AppInfo fetchAppInfo(String packageName) throws DevConsoleException {
		AppInfo result = null;
		for (DeveloperConsoleAccount consoleAccount : protocol.getSessionCredentials()
				.getDeveloperConsoleAccounts()) {
			String developerId = consoleAccount.getDeveloperId();
                        String developername = consoleAccount.getName();
			if (logger.isDebugEnabled()) {
				logger.debug("fetchAppInfos() - {}", "Getting apps for: " + developername + " - " + developerId); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			String response = post(protocol.createFetchAppsUrl(developerId),
					protocol.createFetchAppInfoRequest(packageName), developerId);

			// don't skip incomplete apps, so we can get the package list
			AppInfo app = protocol.parseAppInfoResponse(response, accountName,developerId, true);
			
                        
			result=app;
			
				/*
				 * When consulting a single app it will always be incomplete
				 * if (app.isIncomplete()) {
					result=null;
					if (logger.isDebugEnabled()) {
						logger.debug("fetchAppInfos() - {}", String.format("Incomplete apps, issuing details request")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					response = post(protocol.createFetchAppsUrl(developerId),
                            protocol.createFetchAppInfoRequest(packageName), developerId);
				      // if info is not here, not much to do, skip
	                AppInfo extraApp = protocol.parseAppInfoResponse(response,accountName, developerId, true);
				if (logger.isDebugEnabled()) {
					logger.debug("fetchAppInfos() - {}", String.format("Got extra app from details request")); //$NON-NLS-1$ //$NON-NLS-2$
				}
	              if(extraApp!=null)
				result=extraApp;
				}*/
			
			

			
                
                
                        
          
                        
		}
                
                
		return result;
	}

	protected AppInfo getAppInfo(String packageName) {
		try {
			// the authenticator launched a sub-activity, bail out for now
			if (!authenticateWithCachedCredentialas()) {
				return null;
			}

			return fetchAppInfoAndStatistics(packageName);
		} catch (AuthenticationException ex) {
			if (!authenticateFromScratch()) {
				return null;
			}

			return fetchAppInfoAndStatistics(packageName);
		}
	}

	private AppInfo fetchAppInfoAndStatistics(String packageName) {
		// Fetch a list of available apps
		AppInfo app = fetchAppInfo(packageName);
		fetchStatistics(app, app.getLatestStats(), 4);
		

		return app;
	}


}
