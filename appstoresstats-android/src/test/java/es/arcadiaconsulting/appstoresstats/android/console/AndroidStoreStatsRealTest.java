package es.arcadiaconsulting.appstoresstats.android.console;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.IStoreStats;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AndroidStoreStatsRealTest {

	/**
	 * This test obtain real statistics from the developer console.
	 * Need a real user and password to perform the auth.
	 * Due privacy and security a test account isn't provided, so need to be assigned manually on auth.properties.
	 */
	@Test
	public void testObtainRealStatistics() throws IOException {

		//email and password are obtained from env and are required
		String userEmail = System.getProperty("STATS_USER_EMAIL");
		assertNotNull("A google email need to be provided as env property STATS_USER_EMAIL", userEmail);

		String userPassword = System.getProperty("STATS_USER_PASSWORD");
		assertNotNull("The password for the email need to be provided as env property STATS_USER_PASSWORD", userPassword);

		//store and app package can be provided as env prop or use a default value (LifList manager)
		String storeToTest = System.getProperty("STATS_STORE") != null ? System.getenv("STATS_STORE") : "LifList";
		String packageName = System.getProperty("STATS_APP_PACKAGE") != null ? System.getenv("STATS_APP_PACKAGE") : "com.liflist.manager.android";

		//fetch the statistics for the app
		IStoreStats fetchStats =  new AndroidStoreStats();
		CommonStatsData stats = fetchStats.getFullStatsForApp(userEmail, userPassword, packageName, null, storeToTest);

		//check there is data obtained
		assertNotNull(stats);
		assertNotEquals(stats.getDownloadsNumber(), 0);
		assertTrue(stats.getDownloadsNumber() > 0);

		//print the current vales (for manual check)
		System.out.println("The app has " + stats.getCurrentInstallationsNumber() + " active installs");
		System.out.println("The app has " + stats.getDownloadsNumber() + " total downloads");

	}

}
