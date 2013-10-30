package es.arcadiaconsulting.appstoresstats.ios.io;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.ios.model.AppInfo;
import es.arcadiaconsulting.appstoresstats.ios.model.Constants;

public class JSONParser {
	
	private static final Logger logger = LoggerFactory.getLogger(JSONParser.class);
	
	public static AppInfo getAPPInfoByID(String appleID){
		AppInfo appInfo = new AppInfo();
		try {
			String httpResponse = HTTPClientHelper.executeHttpGetWithParams(Constants.HTTP_URL_APPLE_APP_GET, new String[]{Constants.ID_NAME,appleID});
		
			JSONObject jsonObject = new JSONObject(httpResponse);
			int resultCount = jsonObject.getInt("resultCount");
			if(resultCount==0){
				appInfo.setDeployed(false);
				appInfo.setId(appleID);
				return appInfo;
			}else if(resultCount > 1){
				logger.error("There are more than one app with same id");
				return null;
			}else{
				JSONObject result = jsonObject.getJSONObject("results");
				String releaseDate = result.getString("releaseDate");
				appInfo.setDeployed(true);
				appInfo.setId(appleID);
				
				//TODO get more parameters
				appInfo.setReleaseDate(DateHelper.buildDateFromUTCString(releaseDate));
				return appInfo;
				
				
			}
			
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception parsing");;
		}
		
		
		return appInfo;
	}
	

}
