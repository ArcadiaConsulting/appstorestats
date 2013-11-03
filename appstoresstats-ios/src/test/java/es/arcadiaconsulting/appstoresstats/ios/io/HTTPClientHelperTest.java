package es.arcadiaconsulting.appstoresstats.ios.io;

import es.arcadiaconsulting.appstoresstats.ios.model.Constants;
import junit.framework.TestCase;

public class HTTPClientHelperTest extends TestCase {
	
	
	
	public void testExecuteHttpGetWithParams(){
		
		String getResponse = HTTPClientHelper.executeHttpGetWithParams(Constants.HTTP_URL_APPLE_APP_GET, new String[]{Constants.ID_NAME,"662257575"});
		assertTrue(getResponse.contains("\"resultCount\":1"));
		getResponse = HTTPClientHelper.executeHttpGetWithParams("https://itunes.apple.com/search", new String[]{"term","jack+johnson","limit","25"});
		assertTrue(getResponse.contains("\"resultCount\":25"));
	}

}
