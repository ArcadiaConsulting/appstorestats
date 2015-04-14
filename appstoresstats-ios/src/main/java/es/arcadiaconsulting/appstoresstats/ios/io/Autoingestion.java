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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

public class Autoingestion {

	private static final Logger logger = LoggerFactory
			.getLogger(Autoingestion.class);
	
	public static final String PROTPERTY_AUTOINGESTION_CAHCE_DIR = "autoingestionCacheDir";
	
	/**
	 * Pattern for autogenstion cache key. Params are vendor, report type, report subtype, 
	 */
	public static final String PATTERN_AUTOINGESTION_CAHCE_KEY = "{0}-{1}-{2}-{3}-{4}";


	public static List<UnitData> getUnitsByDate(/** String propertiesFile, */
	String user, String password, String vendorId, String reportType,
			String dateType, String reportSubType, String date, String sku) throws Exception {
		try {

			List<AutoingestionBean> is = getSalesOutput(new String[] {
			user, password, vendorId, reportType, dateType, reportSubType, date });
			if (is == null) {
				logger.info("Problem getting Autoingestion for app: " + sku);
				return null;
			}
			return getUnits(is, sku);
		} catch (IOException e) {
			logger.error("Exception on getUnits method for app: " + sku);
			return null;
		}

	}

	public static String getAppleIDBySKU(/** String propertiesFile, */
	String user, String password, String vendorId, String reportType,
			String reportSubType, GregorianCalendar date, String sku)
			throws Exception {
		String line = new String();
		SimpleDateFormat sdfDay = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		GregorianCalendar dateIterator = (GregorianCalendar) date.clone();

		// buscamos por dias hasta la semana
		for (int i = 0; i < 6; i++) {
			List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[] { 
			user, password, vendorId, reportType, Constants.DATE_TYPE_DAILY,
					reportSubType, sdfDay.format(dateIterator.getTime()) });
			if (salesOutputReader != null) {
				for (Iterator iterator = salesOutputReader.iterator(); iterator
						.hasNext();) {
					AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
							.next();
					if (autoingestionBean.getSku().equals(sku)) {
						return autoingestionBean.getAppleIdentifier();
					}

				}
			}
			dateIterator.add(Calendar.DATE, -1);
		}

		// buscamos por semanas hasta el mes
		for (int i = 0; i < 4; i++) {
			List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[] {
					user, password, vendorId, reportType, Constants.DATE_TYPE_WEEDLY,
					reportSubType, sdfDay.format(dateIterator.getTime()) });
			if (salesOutputReader != null) {
				for (Iterator iterator = salesOutputReader.iterator(); iterator
						.hasNext();) {
					AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
							.next();
					if (autoingestionBean.getSku().equals(sku)) {
						return autoingestionBean.getAppleIdentifier();
					}

				}
			}
			dateIterator.add(Calendar.WEEK_OF_YEAR, -1);
		}
		// buscamos por meses hasta el año
		for (int i = 0; i < 12; i++) {
			List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[] { /**
			 * 
			 * propertiesFile,
			 */
			user, password, vendorId, reportType, Constants.DATE_TYPE_MONTHLY,
					reportSubType, sdfMonth.format(dateIterator.getTime()) });
			if (salesOutputReader != null) {
				for (Iterator iterator = salesOutputReader.iterator(); iterator
						.hasNext();) {
					AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
							.next();
					if (autoingestionBean.getSku().equals(sku)) {
						return autoingestionBean.getAppleIdentifier();
					}

				}
			}
			dateIterator.add(Calendar.MONTH, -1);
		}

		// buscamos por año hasta 2010
		for (int i = 0; i < 12; i++) {
			List<AutoingestionBean> salesOutputReader = getSalesOutput(new String[] { /**
			 * 
			 * propertiesFile,
			 */
			user, password, vendorId, reportType, Constants.DATE_TYPE_YEARLY,
					reportSubType, sdfYear.format(dateIterator.getTime()) });
			if (salesOutputReader != null) {
				for (Iterator iterator = salesOutputReader.iterator(); iterator
						.hasNext();) {
					AutoingestionBean autoingestionBean = (AutoingestionBean) iterator
							.next();
					if (autoingestionBean.getSku().equals(sku)) {
						return autoingestionBean.getAppleIdentifier();
					}

				}
			}
			dateIterator.add(Calendar.YEAR, -1);
			if (dateIterator.get(Calendar.YEAR) <= 2010) {
				logger.warn("Not found apple id by sku since 2010");
				throw new AppNotPublishedException(
						"Not found apple id by sku since 2010");
			}
		}

		return null;

		/**
		 * BufferedReader br = new BufferedReader(salesOutputReader); try {
		 * //discart first line br.readLine(); while((line =
		 * br.readLine())!=null){ if(line.trim().length()==0){ continue; } //get
		 * line elements String[] lineElements = line.split("\t"); //check SKU
		 * to get units if(lineElements[2].equals(sku)){ return
		 * lineElements[16]; } }
		 * 
		 * return null;
		 * 
		 * } catch (IOException e) { logger.error("getAppleIDBySKU Exception",
		 * e); throw e; }
		 */
	}

	public static List<UnitData> getUnits(List<AutoingestionBean> salesOutput,
			String sku) throws IOException {
		List<UnitData> unitDataList = new Vector<UnitData>();

		for (Iterator iterator = salesOutput.iterator(); iterator.hasNext();) {
			AutoingestionBean autoingestionbean = (AutoingestionBean) iterator
					.next();
			boolean isupdate = false;
			for (int i = 0; i < Constants.UPDATE_PRODUCT_TYPE_IDENTIFYER.length; i++) {
				if (Constants.UPDATE_PRODUCT_TYPE_IDENTIFYER[i]
						.equals(autoingestionbean.getProductTypeIdentifier())) {
					isupdate = true;
					break;
				}
			}

			if (autoingestionbean.getSku().equals(sku) && !isupdate) {
				unitDataList.add(new UnitData(autoingestionbean
						.getCountryCode(), autoingestionbean.getUnits()));
			}

		}

		/**
		 * InputStreamReader salesOutputReader = new
		 * InputStreamReader(salesOutput); BufferedReader br = new
		 * BufferedReader(salesOutputReader); try { //discart first line
		 * br.readLine(); while((line = br.readLine())!=null){
		 * if(line.trim().length()==0){ continue; } //get line elements String[]
		 * lineElements = line.split("\t"); //check SKU to get units
		 * if(lineElements[2].equals(sku)){ unitDataList.add(new
		 * UnitData(lineElements
		 * [13],Integer.getInteger(lineElements[7]).intValue())); } }
		 * 
		 * return unitDataList;
		 * 
		 * } catch (IOException e) { logger.error("getUnits Exception", e);
		 * throw e; }
		 */
		return unitDataList;

	}

	private static List<AutoingestionBean> getSalesOutput(
			String[] paramArrayOfString) throws Exception{
		
		String cacheKey = buildCacheKey(paramArrayOfString);
		if(existsInCache(cacheKey)) {
			return readFromCache(cacheKey);
		} else {
			HttpURLConnection httpConnection = null;
			URL url = null;
			
			try {
				String date = paramArrayOfString[6];
				String queryString = "";
				String user = paramArrayOfString[0];
				String password = paramArrayOfString[1];
				String vendorId = paramArrayOfString[2];
		
				queryString = "USERNAME=" + URLEncoder.encode(user, "UTF-8");
				queryString = queryString + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8");
				queryString = queryString + "&VNDNUMBER=" + URLEncoder.encode(vendorId, "UTF-8");
				queryString = queryString + "&TYPEOFREPORT="
						+ URLEncoder.encode(paramArrayOfString[3], "UTF-8");
				queryString = queryString + "&DATETYPE="
						+ URLEncoder.encode(paramArrayOfString[4], "UTF-8");
				queryString = queryString + "&REPORTTYPE="
						+ URLEncoder.encode(paramArrayOfString[5], "UTF-8");
				queryString = queryString + "&REPORTDATE=" + URLEncoder.encode(date, "UTF-8");
				
				url = new URL(
						"https://reportingitc.apple.com/autoingestion.tft?");
	
				httpConnection = (HttpURLConnection)url.openConnection();
	
				httpConnection.setRequestMethod("POST");
				httpConnection.setRequestProperty(
						"Content-Type", "application/x-www-form-urlencoded");
				httpConnection.setDoOutput(true);
				OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(
						( httpConnection).getOutputStream());
	
				localOutputStreamWriter.write(queryString);
				localOutputStreamWriter.flush();
				localOutputStreamWriter.close();
	
				if (httpConnection.getHeaderField("ERRORMSG") != null) {
					if (isError(httpConnection.getHeaderField("ERRORMSG"))) {
						logger.warn("getSalesOutput(String[]) - {}", httpConnection.getHeaderField("ERRORMSG"));
					} else {
						logger.warn("getSalesOutput(String[]) - {}", httpConnection.getHeaderField("ERRORMSG"));
					}
					return null;
				} else if (httpConnection.getHeaderField("filename") != null) {
					return parseAndSotreIncache(httpConnection, cacheKey); 
				} else {
					logger.warn("getSalesOutput(String[]) - {}", httpConnection.getHeaderField("ERRORMSG"));
					return null;
				}
			} catch (Exception localException3) {
				throw new RuntimeException("getSalesOutput(String[]) - Unexpected error resolving autoingestion file from Appel services", localException3);
			} finally {
				if (httpConnection != null) {
					httpConnection.disconnect();
					httpConnection = null;
				}
			}
		}
	}

	// private static void getFile(HttpURLConnection paramHttpURLConnection)
	// throws IOException
	private static List<AutoingestionBean> parseAndSotreIncache(
			HttpURLConnection paramHttpURLConnection, String cacheKey) throws IOException {
		int i = 0;

		try {
			String fileString = decompress(paramHttpURLConnection
					.getInputStream());
			writeToCahce(fileString, cacheKey);
			return buildAutoingestionTable(fileString.split("\n"));
		} catch (Exception e) {
			logger.error("parseAndSotreIncache(HttpURLConnection, String) - Error procesing autogestion file", e); //$NON-NLS-1$
			throw new RuntimeException("Error procesing autogestion file", e);
		}
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

	public static List<AutoingestionBean> buildAutoingestionTable(
			String[] autoingestionTable) {
		List<AutoingestionBean> autoingestionBean = new Vector<AutoingestionBean>();

		for (int i = 1; i < autoingestionTable.length; i++) {
			autoingestionBean.add(buildAutogestionBean(autoingestionTable[i]
					.trim().split("\t")));
		}

		return autoingestionBean;

	}

	public static AutoingestionBean buildAutogestionBean(
			String[] autoingestionLine) {
		AutoingestionBean bean = new AutoingestionBean();

		bean.setProvider(autoingestionLine[0]);

		bean.setProviderCountry(autoingestionLine[1]);

		bean.setSku(autoingestionLine[2]);

		bean.setDeveloper(autoingestionLine[3]);

		bean.setTitle(autoingestionLine[4]);

		bean.setVersion(autoingestionLine[5]);

		bean.setProductTypeIdentifier(autoingestionLine[6]);

		bean.setUnits(Integer.valueOf(autoingestionLine[7]).intValue());

		bean.setDeveloperProceds(Integer.valueOf(autoingestionLine[8])
				.intValue());

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		try {
			bean.setBeginDate(sdf.parse(autoingestionLine[9]));
		} catch (ParseException e) {
			logger.error(
					"Build AutoingestionBean error, Incorrect beginDate format",
					e);
		}

		try {
			bean.setEndDate(sdf.parse(autoingestionLine[10]));
		} catch (ParseException e) {
			logger.error(
					"Build AutoingestionBean error, Incorrect endDate format",
					e);
		}
		bean.setCustomerCurrency(autoingestionLine[11]);

		bean.setCountryCode(autoingestionLine[12]);

		bean.setCurrencyOfProcess(autoingestionLine[13]);

		bean.setAppleIdentifier(autoingestionLine[14]);

		bean.setCustomerPrice(Float.valueOf(autoingestionLine[15]).floatValue());

		if (autoingestionLine.length > 16)
			bean.setPromoCode(autoingestionLine[16]);

		if (autoingestionLine.length > 17)
			bean.setParentIdentifier(autoingestionLine[17]);

		if (autoingestionLine.length > 18)
			bean.setSubscription(autoingestionLine[18]);

		if (autoingestionLine.length > 19)
			bean.setPeriod(autoingestionLine[19]);

		return bean;
	}

	private static boolean isError(String error) {
		if (error.startsWith(Constants.NOT_DAILY_STATS_ERROR)
				|| error.startsWith(Constants.NOT_PUBLISHED_ERROR)
				|| error.startsWith(Constants.NOT_WEEKLY_STATS_ERROR))
			return false;
		return true;
	}
	
	
	
	protected static void writeToCahce(String fileContent, String cacheKey) {
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(getCacheFile(cacheKey));
			fos.write(fileContent.getBytes());
		} catch(Exception e) {
			logger.error("writeToCahce(String, String) - Unexpected error writing cache file", e); //$NON-NLS-1$ç
			throw new RuntimeException("Unexpected error writing cache file", e);
		} finally {
			try{
				fos.close();
			} catch(Exception e1) {
				logger.error("writeToCahce(String, String) - Unexpected error closing cache file", e1); //$NON-NLS-1$ç
			}
		}
	}
	
	/**
	 * Reads cache file
	 * @param cacheKey
	 * @return
	 * @throws Exception
	 */
	protected static List<AutoingestionBean> readFromCache(String cacheKey) throws Exception {
		List<AutoingestionBean> result = new ArrayList<AutoingestionBean>();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(getCacheFile(cacheKey))));
			
			// Discard fist line
			if(reader.readLine() != null) {
				String line = reader.readLine();
				while(line != null) {
					result.add(buildAutogestionBean(line.trim().split("\t")));
					line = reader.readLine();
				}
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Erro processing cache file", e);
		} finally {
			try{
				reader.close();
			} catch(Exception e1) {
				logger.error("readFromCache(String) - Error closing cache file streams", e1); //$NON-NLS-1$
			}
		}
	}
	
	
	/**
	 * Checks if an autoingestion cache file exists for given key
	 * @param cacheKey
	 * @return
	 */
	protected static boolean existsInCache(String cacheKey) {
		return getCacheFile(cacheKey).exists();
	}

	/**
	 * Resovles the autoingestion cache file for given key
	 * It does not check whether file exists or not;
	 * @param cacheKey
	 * @return
	 */
	protected static File getCacheFile(String cacheKey) {
		return  new File(getCacheDir(), cacheKey);
	}
	
	/**
	 * Generate the cache key for given request to autoingestion service
	 * @param paramArrayOfString
	 * @return
	 */
	protected static String buildCacheKey(String[] autoingestionParams) {
		return MessageFormat.format(PATTERN_AUTOINGESTION_CAHCE_KEY, 
				autoingestionParams[2], autoingestionParams[3], autoingestionParams[4], autoingestionParams[5], autoingestionParams[6]);
	}

	/**
	 * Resolve the file where to store autoingestion cache files
	 * @return
	 */
	protected static File getCacheDir() {
		String cacheDirName = System.getProperty(PROTPERTY_AUTOINGESTION_CAHCE_DIR);
		File cacheDir = null;
		if (cacheDirName != null && !cacheDirName.trim().equals("")) {
			cacheDir = new File(cacheDirName);
		} else {
			cacheDir = new File(System.getProperty("user.home"), ".autoingestion-cache");
		}
		
		if(!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		
		return cacheDir;
	}

}