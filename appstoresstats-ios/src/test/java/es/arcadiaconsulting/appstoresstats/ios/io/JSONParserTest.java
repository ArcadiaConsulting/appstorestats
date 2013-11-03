package es.arcadiaconsulting.appstoresstats.ios.io;

import java.text.SimpleDateFormat;

import es.arcadiaconsulting.appstoresstats.ios.model.AppInfo;
import junit.framework.TestCase;

public class JSONParserTest extends TestCase {
	
	public void testGetAPPInfoByID(){
		
		AppInfo appInfo = JSONParser.getAPPInfoByID("662257575");
		assertEquals("662257575",appInfo.getId());
		assertEquals("Fiestas Sampedros Burgos",appInfo.getAppName());
		assertTrue(appInfo.isDeployed());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String s = formatter.format(appInfo.getReleaseDate());
		assertEquals("2013-06-21T08:32:43Z",s);
		
		appInfo = JSONParser.getAPPInfoByID("548280988");
		assertEquals("548280988", appInfo.getId());
		assertFalse(appInfo.isDeployed());
		
		
		
	}

}
