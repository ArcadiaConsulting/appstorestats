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
package es.arcadiaconsulting.appstoresstats.ios.io;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.common.AppNotPublishedException;
import es.arcadiaconsulting.appstoresstats.ios.model.AutoingestionBean;
import es.arcadiaconsulting.appstoresstats.ios.model.Constants;
import es.arcadiaconsulting.appstoresstats.ios.model.UnitData;

public class Autoingestion
{

//TODO: Add map  of autoingestionbean with date and check in getunitsbydate si se ha buscado antes por esa fecha
  private static HashMap<String, List<AutoingestionBean>> autoingestionMap = new HashMap<String, List<AutoingestionBean>>();	

  public static void clearAutoingestionMap(){
	  autoingestionMap = new HashMap<String, List<AutoingestionBean>>();
  }
	
  private static final Logger logger = LoggerFactory.getLogger(Autoingestion.class);
	
  public static List<UnitData> getUnitsByDate(/**String propertiesFile,*/ String user,String password,String vendorId, String reportType, String dateType, String reportSubType, String date, String sku){
	  
	  
	  try{
		  
		  if(autoingestionMap!=null && autoingestionMap.containsKey(date+dateType)){
			  return getUnits(autoingestionMap.get(date+dateType),sku);
		  }
		  List<AutoingestionBean> is=	getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,dateType,reportSubType,date});
		  if(is==null){
			  logger.error("Problem getting Autoingestion");
			  return null;
		  }
		  autoingestionMap.put(date+dateType, is);
		  return getUnits(is,sku);
	  }catch(IOException e){
		  logger.error("Exception on getUnits method");
		  return null;
	  }
		  
		  
	  
  }
  
  public static String getAppleIDBySKU(/**String propertiesFile,*/ String user,String password,String vendorId, String reportType, String reportSubType, GregorianCalendar date, String sku) throws IOException{
	  String line = new String();
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	  GregorianCalendar dateIterator = (GregorianCalendar)date.clone();
	  
	  //buscamos por dias hasta la semana
	  for (int i = 0; i < 6; i++) {
		  List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,Constants.DATE_TYPE_DAILY,reportSubType,sdf.format(dateIterator.getTime())});
		  if(salesOutputReader!= null){
			  for (Iterator iterator = salesOutputReader.iterator(); iterator.hasNext();) {
				  AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
					.next();
				  if(autoingestionBean.getSku().equals(sku)){
					  return autoingestionBean.getAppleIdentifier();
				  }
			
			  }
		  }
		  dateIterator.add(Calendar.DATE, -1);
	  }
	  
	//buscamos por semanas  hasta el mes
	  for (int i = 0; i < 4; i++) {
		  List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,Constants.DATE_TYPE_WEEDLY,reportSubType,sdf.format(dateIterator.getTime())});
		  if(salesOutputReader!= null){
		  for (Iterator iterator = salesOutputReader.iterator(); iterator.hasNext();) {
			AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
					.next();
			if(autoingestionBean.getSku().equals(sku)){
				 return autoingestionBean.getAppleIdentifier();
			 }
			
		  }
		  }
		  dateIterator.add(Calendar.WEEK_OF_YEAR, -1);
	  }
	//buscamos por meses  hasta el año
	  for (int i = 0; i < 12; i++) {
		  List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,Constants.DATE_TYPE_MONTHLY,reportSubType,sdf.format(dateIterator.getTime())});
		  if(salesOutputReader!= null){
		  for (Iterator iterator = salesOutputReader.iterator(); iterator.hasNext();) {
			AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
					.next();
			if(autoingestionBean.getSku().equals(sku)){
				 return autoingestionBean.getAppleIdentifier();
			 }
			
		  }
		  }
		  dateIterator.add(Calendar.MONTH, -1);
	  }
	  
	  
	  
	  //buscamos por año hasta 2010
	  for (int i = 0; i < 12; i++) {
		  List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[]{/**propertiesFile,*/user,password,vendorId,reportType,Constants.DATE_TYPE_YEARLY,reportSubType,sdf.format(dateIterator.getTime())});
		  if(salesOutputReader!= null){
		  for (Iterator iterator = salesOutputReader.iterator(); iterator.hasNext();) {
			AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
					.next();
			if(autoingestionBean.getSku().equals(sku)){
				 return autoingestionBean.getAppleIdentifier();
			 }
			
		  }
		  }
		  dateIterator.add(Calendar.YEAR, -1);
		  if(dateIterator.get(Calendar.YEAR)<=2010){
			  logger.warn("Not found apple id by sku since 2010");
			  throw new AppNotPublishedException("Not found apple id by sku since 2010");
		  }
	  }
	  
	  return null;
	   
	  /**
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
	*/
  }
  
  
  public static List<UnitData> getUnits(List<AutoingestionBean> salesOutput, String sku) throws IOException{
	  List<UnitData> unitDataList = new Vector<UnitData>();
	  
	  for (Iterator iterator = salesOutput.iterator(); iterator.hasNext();) {
		AutoingestionBean autoingestionbean = (AutoingestionBean) iterator.next();
		boolean isupdate = false;
		for (int i = 0; i < Constants.UPDATE_PRODUCT_TYPE_IDENTIFYER.length; i++) {
			if(Constants.UPDATE_PRODUCT_TYPE_IDENTIFYER[i].equals(autoingestionbean.getProductTypeIdentifier())){
				isupdate = true;
				break;
			}			
		}
		
		if(autoingestionbean.getSku().equals(sku)&& !isupdate){
			 unitDataList.add(new UnitData(autoingestionbean.getCountryCode(),autoingestionbean.getUnits()));
		 }
		
	}
	 
	  
	  /**
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
		*/
	  return unitDataList;
	 
	  
  }
  
  
	
  private static List<AutoingestionBean> getSalesOutput(String[] paramArrayOfString)
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
        logger.info("The username and password parameters have been deprecated. Please use the properties file for user credentials.");

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

      if (((HttpURLConnection)localObject1).getHeaderField("ERRORMSG") != null){
       if(isError(((HttpURLConnection)localObject1).getHeaderField("ERRORMSG")))
    	  logger.error(((HttpURLConnection)localObject1).getHeaderField("ERRORMSG"));
       else
    	   logger.warn(((HttpURLConnection)localObject1).getHeaderField("ERRORMSG"));
      }
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
  private static List<AutoingestionBean> getFile(HttpURLConnection paramHttpURLConnection) throws IOException
  {
  //  String str = paramHttpURLConnection.getHeaderField("filename");
	 
    //logger.info(str);
   int i = 0;

    	//return paramHttpURLConnection.getInputStream();
    
    //BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramHttpURLConnection.getInputStream());
    
    try {
		String fileString = decompress(paramHttpURLConnection.getInputStream());
		return buildAutoingestionTable(fileString.split("\n"));
	} catch (Exception e) {
		logger.error("problem  decommpressing file",e );
		return null;
		
	}
    
    /**
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str));

    byte[] arrayOfByte = new byte[1024];

    while ((i = localBufferedInputStream.read(arrayOfByte)) != -1) {
      localBufferedOutputStream.write(arrayOfByte, 0, i);
    }

   localBufferedInputStream.close();
    localBufferedOutputStream.close();

  System.out.println("File Downloaded Successfully ");
    
  return null;
  */
  }
  
  public static String decompress(InputStream input) throws Exception {
	  // opens the compressed file
	  GZIPInputStream in = new GZIPInputStream(input);
	  ByteArrayOutputStream output = new ByteArrayOutputStream();
	  // Transfer bytes from the compressed file to the output file
	  byte[] buffer = new byte[1024];
	  int len;
	  while ((len = in.read(buffer)) > 0) {
	    output.write(buffer, 0, len);
	  }
	  in.close();
	  output.close();
	  return output.toString();
	}
  
  
  public static List<AutoingestionBean> buildAutoingestionTable(String[] autoingestionTable){
	  List<AutoingestionBean> autoingestionBean = new Vector<AutoingestionBean>();
	  
	  for (int i = 1; i < autoingestionTable.length; i++) {
		autoingestionBean.add(buildAutogestionBean(autoingestionTable[i].trim().split("\t")));
	  }
	  
	  return autoingestionBean;
	  
  }
  
  
  public static AutoingestionBean buildAutogestionBean(String[] autoingestionLine){
		AutoingestionBean bean = new AutoingestionBean();
	  	
		
	  bean.setProvider(autoingestionLine[0]);
		
	  bean.setProviderCountry(autoingestionLine[1]);
		
	  bean.setSku(autoingestionLine[2]);
		
	  bean.setDeveloper(autoingestionLine[3]);
		
	  bean.setTitle(autoingestionLine[4]);
		
	  bean.setVersion(autoingestionLine[5]);
		
	  bean.setProductTypeIdentifier(autoingestionLine[6]);
		
	  bean.setUnits(Integer.valueOf(autoingestionLine[7]).intValue());
		
	  bean.setDeveloperProceds(Integer.valueOf(autoingestionLine[8]).intValue());
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		try {
			bean.setBeginDate(sdf.parse(autoingestionLine[9]));
		} catch (ParseException e) {
			logger.error("Build AutoingestionBean error, Incorrect beginDate format",e);
		}
		
		
		try{
		bean.setEndDate(sdf.parse(autoingestionLine[10]));
		}catch(ParseException e) {
			logger.error("Build AutoingestionBean error, Incorrect endDate format",e);
		}
		bean.setCustomerCurrency(autoingestionLine[11]);
		
		bean.setCountryCode(autoingestionLine[12]);
		
		bean.setCurrencyOfProcess(autoingestionLine[13]);
		
		bean.setAppleIdentifier(autoingestionLine[14]);
		
		bean.setCustomerPrice(Float.valueOf(autoingestionLine[15]).floatValue());
		
		if(autoingestionLine.length>16)
			bean.setPromoCode(autoingestionLine[16]);
		
		if(autoingestionLine.length>17)
			bean.setParentIdentifier(autoingestionLine[17]);
		
		if(autoingestionLine.length>18)
		bean.setSubscription(autoingestionLine[18]);
		
		if(autoingestionLine.length>19)
		bean.setPeriod(autoingestionLine[19]);
		
		return bean;
	}
  private static boolean isError(String error)
  {
	  if(error.startsWith(Constants.NOT_DAILY_STATS_ERROR)||error.startsWith(Constants.NOT_PUBLISHED_ERROR)||error.startsWith(Constants.NOT_WEEKLY_STATS_ERROR))
		  return false;
	  return true;
  }
  
}