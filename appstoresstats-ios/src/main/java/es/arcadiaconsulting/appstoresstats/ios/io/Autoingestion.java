package es.arcadiaconsulting.appstoresstats.ios.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.ios.model.UnitData;

public class Autoingestion
{

  private static final Logger logger = LoggerFactory.getLogger(Autoingestion.class);
	
  public static List<UnitData> getUnitsByDate(/**String propertiesFile,*/ String user,String password,String vendorId, String reportType, String dateType, String reportSubType, String date, String sku){
	  try{
		  InputStream is=	getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,dateType,reportSubType,date});
		  if(is==null){
			  logger.error("Problem getting Autoingestion");
			  return null;
		  }
		  return getUnits(is,sku);
	  }catch(IOException e){
		  logger.error("Exception on getUnits method");
		  return null;
	  }
		  
		  
	  
  }
  
  public static String getAppleIDBySKU(/**String propertiesFile,*/ String user,String password,String vendorId, String reportType, String dateType, String reportSubType, String date, String sku) throws IOException{
	  String line = new String();
	  InputStreamReader salesOutputReader = new InputStreamReader(getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,dateType,reportSubType,date}));
 	  BufferedReader br = new BufferedReader(salesOutputReader);
	  try {
		 //discart first line 
		 br.readLine();
		while((line = br.readLine())!=null){
			 if(line.trim().length()==0){
				 continue;
			 }
			 //get line elements
			 String[] lineElements = line.split("\t");
			 //check SKU to get units 
			 if(lineElements[2].equals(sku)){
				 return lineElements[16];
			 }
		}
	  
		return null;
		
	} catch (IOException e) {
		logger.error("getAppleIDBySKU Exception", e);
		throw e;
	}
  }
  
  
  public static List<UnitData> getUnits(InputStream salesOutput, String sku) throws IOException{
	  List<UnitData> unitDataList = new Vector<UnitData>();
	  String line = new String();
	  InputStreamReader salesOutputReader = new InputStreamReader(salesOutput);
 	  BufferedReader br = new BufferedReader(salesOutputReader);
	  try {
		 //discart first line 
		 br.readLine();
		while((line = br.readLine())!=null){
			 if(line.trim().length()==0){
				 continue;
			 }
			 //get line elements
			 String[] lineElements = line.split("\t");
			 //check SKU to get units 
			 if(lineElements[2].equals(sku)){
				 unitDataList.add(new UnitData(lineElements[13],Integer.getInteger(lineElements[7]).intValue()));
			 }
		}
		return unitDataList;
		
	} catch (IOException e) {
		logger.error("getUnits Exception", e);
		throw e;
	}

	 
	  
  }
  
  
	
  private static InputStream getSalesOutput(String[] paramArrayOfString)
  {
    int i = 0;
    String str1 = null;
    String str2 = "";
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;

    File localFile1 = new File(".");
    try {
      String str7 = localFile1.getCanonicalPath();
      str6 = paramArrayOfString[0];
      String localObject2 = str7 + "/" + str6;
      boolean bool1 = str6.endsWith(".properties");

      File localFile2 = new File((String)localObject2);
      boolean bool2 = localFile2.exists();
      if ((bool1) && (bool2))
      {
        Properties localProperties = new Properties();
        localProperties.load(new FileInputStream((String)localObject2));
        str3 = localProperties.getProperty("userID");
        str4 = localProperties.getProperty("password");
        str5 = paramArrayOfString[1];

        if ((str3.isEmpty()) || (null == str3) || (null == str4) || (str4.isEmpty()))
        {
          
        	logger.error("Please check the parameters in properties file ");

          return null;
        }

        if ((paramArrayOfString.length < 5) || (paramArrayOfString.length > 6)) {
          logger.error("Please enter all the required parameters.  For help, please download the latest User Guide from the Sales and Trends module in iTunes Connect.");

          
          return null;
        }
        i = 1; } else {
        if (bool1) {
          logger.error("Property file missing");
          return null;
        }if ((bool2) && (!bool1)) {
          logger.error("Property File missing. Please use the properties file for user credentials");
          return null;
        }
      }
      if (!bool1)
      {
        logger.error("The username and password parameters have been deprecated. Please use the properties file for user credentials.");

        if ((paramArrayOfString.length < 6) || (paramArrayOfString.length > 7)) {
          logger.error("Please enter all the required parameters.  For help, please download the latest User Guide from the Sales and Trends module in iTunes Connect.");

          return null;
        }
        str3 = paramArrayOfString[0];
        str4 = paramArrayOfString[1];
        str5 = paramArrayOfString[2];
      }
    } catch (Exception localException1) {
      logger.error("Exception", localException1);
    }

    if ((i == 1) && (paramArrayOfString.length == 6) && (null != paramArrayOfString[5])) {
      str1 = paramArrayOfString[5];
    } else if ((i == 0) && (paramArrayOfString.length == 7) && (null != paramArrayOfString[6])) {
      str1 = paramArrayOfString[6];
    } else {
      Calendar localObject1 = Calendar.getInstance();
      SimpleDateFormat localObject2 = new SimpleDateFormat("yyyyMMdd");
      ((Calendar)localObject1).add(5, -1);
      str1 = ((SimpleDateFormat)localObject2).format(((Calendar)localObject1).getTime()).toString();
    }

    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      str2 = "USERNAME=" + URLEncoder.encode(str3, "UTF-8");
      str2 = str2 + "&PASSWORD=" + URLEncoder.encode(str4, "UTF-8");
      str2 = str2 + "&VNDNUMBER=" + URLEncoder.encode(str5, "UTF-8");

      if (i == 1) {
        str2 = str2 + "&TYPEOFREPORT=" + URLEncoder.encode(paramArrayOfString[2], "UTF-8");

        str2 = str2 + "&DATETYPE=" + URLEncoder.encode(paramArrayOfString[3], "UTF-8");
        str2 = str2 + "&REPORTTYPE=" + URLEncoder.encode(paramArrayOfString[4], "UTF-8");

        str2 = str2 + "&REPORTDATE=" + URLEncoder.encode(str1, "UTF-8");
      }
      else
      {
        str2 = str2 + "&TYPEOFREPORT=" + URLEncoder.encode(paramArrayOfString[3], "UTF-8");

        str2 = str2 + "&DATETYPE=" + URLEncoder.encode(paramArrayOfString[4], "UTF-8");
        str2 = str2 + "&REPORTTYPE=" + URLEncoder.encode(paramArrayOfString[5], "UTF-8");

        str2 = str2 + "&REPORTDATE=" + URLEncoder.encode(str1, "UTF-8");
      }
    }
    catch (Exception localException2) {
      logger.error("Some problem occured.",localException2);
    }

    try
    {
      localObject2 = new URL("https://reportingitc.apple.com/autoingestion.tft?");

      localObject1 = (HttpURLConnection)((URL)localObject2).openConnection();

      ((HttpURLConnection)localObject1).setRequestMethod("POST");
      ((HttpURLConnection)localObject1).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      ((HttpURLConnection)localObject1).setDoOutput(true);
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(((HttpURLConnection)localObject1).getOutputStream());

      localOutputStreamWriter.write(str2);
      localOutputStreamWriter.flush();
      localOutputStreamWriter.close();

      if (((HttpURLConnection)localObject1).getHeaderField("ERRORMSG") != null)
        logger.error(((HttpURLConnection)localObject1).getHeaderField("ERRORMSG"));
      else if (((HttpURLConnection)localObject1).getHeaderField("filename") != null)
         return getFile((HttpURLConnection)localObject1);
    }
    catch (Exception localException3)
    {
      logger.error("The report you requested is not available at this time.  Please try again in a few minutes.", localException3);
    }
    finally {
      if (localObject1 != null) {
        ((HttpURLConnection)localObject1).disconnect();
        localObject1 = null;
      }
    }
	return null;
  }

  //private static void getFile(HttpURLConnection paramHttpURLConnection) throws IOException
  private static InputStream getFile(HttpURLConnection paramHttpURLConnection) throws IOException
  {
    String str = paramHttpURLConnection.getHeaderField("filename");
    logger.info(str);
 //   int i = 0;

    	return paramHttpURLConnection.getInputStream();
    
 //   BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramHttpURLConnection.getInputStream());

/*    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str));

    byte[] arrayOfByte = new byte[1024];

    while ((i = localBufferedInputStream.read(arrayOfByte)) != -1) {
      localBufferedOutputStream.write(arrayOfByte, 0, i);
    }
*/
 //   localBufferedInputStream.close();
//    localBufferedOutputStream.close();

//   System.out.println("File Downloaded Successfully ");
    
    
  }
}