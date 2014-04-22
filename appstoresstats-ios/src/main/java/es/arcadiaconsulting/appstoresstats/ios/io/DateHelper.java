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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.ios.model.Constants;
import es.arcadiaconsulting.appstoresstats.ios.model.UnitData;

public class DateHelper {
	
	

	private static final Logger logger = LoggerFactory
			.getLogger(DateHelper.class);

	public static Date buildDateFromUTCString(String utc){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			return formatter.parse(utc);
		} catch (ParseException e) {
			logger.error("incorrect Date",e );
			return null;
		}
		
	}
	
	
	
	public static List<UnitData> getFullUnitData(Date deploymentDate,
			Date queryDate, String sku,/** String propertiesFile,*/ String user,
			String password, String vendorId) throws DateHelperException {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

		List<UnitData> unitDataList = new Vector<UnitData>();

		// iterator for compare dates
		GregorianCalendar dateIterator = new GregorianCalendar();
		dateIterator.setTime(queryDate);

		GregorianCalendar deploymentDateCalendar = new GregorianCalendar();
		deploymentDateCalendar.setTime(deploymentDate);

		
		
		// get last day checkable, must be 1 days before if today is the initial day
		Date currentday= new Date(System.currentTimeMillis());
		GregorianCalendar currentDayCalendar = new GregorianCalendar();
		currentDayCalendar.setTime(currentday);
		if( (currentDayCalendar.get(Calendar.YEAR)==dateIterator.get(Calendar.YEAR) && currentDayCalendar.get(Calendar.DAY_OF_YEAR)==dateIterator.get(Calendar.DAY_OF_YEAR))){
			dateIterator.add(Calendar.DATE, -1);
		
		}
		//check dates
		if(deploymentDateCalendar.get(Calendar.YEAR)>dateIterator.get(Calendar.YEAR) || 
				(deploymentDateCalendar.get(Calendar.YEAR)==dateIterator.get(Calendar.YEAR) && deploymentDateCalendar.get(Calendar.DAY_OF_YEAR)>dateIterator.get(Calendar.DAY_OF_YEAR))){
			
			logger.error("Incorrect Dates, First date must be 2 days previous to final date "+sku);
			throw new DateHelperException("Incorrect Dates, First date must be 2 days previous to final date "+sku);
			
		}
		

		// if iterator is less or equal to deployment date we cant check units
		if (dateIterator.before(deploymentDateCalendar)) {

			logger.error("Incorrect date  "+sku);
			throw new DateHelperException(
					"We cant get Results. There are not sales too. Wait some days");

		}
		
		//getting years query
		int deploymentyear = deploymentDateCalendar.get(Calendar.YEAR);
		int dateIteratorYear = dateIterator.get(Calendar.YEAR);
		GregorianCalendar yearIterator = deploymentDateCalendar;
		while(deploymentyear < dateIteratorYear){
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_YEARLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(yearIterator.getTime()), sku);
			if (dayUnitData == null) {
				logger.info("Error Getting year units "+sku);
				//throw new DateHelperException( "Problem getting day sales. Please see log for more information");
			}else{
				unitDataList.addAll(dayUnitData);
			}
			
			deploymentyear = deploymentyear+1;
			yearIterator.add(Calendar.YEAR, 1);
			deploymentDateCalendar = (GregorianCalendar)yearIterator.clone();
			deploymentDateCalendar.set(Calendar.DAY_OF_YEAR,1);
			
			//if query is on first day of year return response
			if(dateIterator.get(Calendar.MONTH)==1&&dateIterator.get(Calendar.DAY_OF_MONTH)==1)
				return cleanUnitDataList(unitDataList);
			
		}
		
		//iterate for months (iterate to month)		
		GregorianCalendar monthIterator = (GregorianCalendar)dateIterator.clone();
		if(monthIterator.get(Calendar.MONTH)==deploymentDateCalendar.get(Calendar.MONTH)){
			//si es el mismo mes la consulta hacemos la consulta dia a dia
			GregorianCalendar dayIterator = deploymentDateCalendar;
			while(dayIterator.get(Calendar.DAY_OF_YEAR)<=dateIterator.get(Calendar.DAY_OF_YEAR)){

				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(dayIterator.getTime()), sku);
				if (dayUnitData == null) {
					logger.info("Error Getting day units day: " + dayIterator.getTime()+": "+sku);
					return cleanUnitDataList(unitDataList);
				}
				unitDataList.addAll(dayUnitData);
				dayIterator.add(Calendar.DATE, 1);
				
			}
			
			
		}else{	
		while(monthIterator.get(Calendar.MONTH)-1>-1){
			monthIterator.add(Calendar.MONTH, -1);
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(monthIterator.getTime()), sku);
			if (dayUnitData == null) {
				logger.info("Error Getting month units for app: "+sku);
				//throw new DateHelperException(
				//		"Problem getting day sales. Please see log for more information");
			}else{
				unitDataList.addAll(dayUnitData);
			}
			//if query is first day of month return unit data
//			if(dateIterator.get(Calendar.DAY_OF_MONTH)==dateIterator.getActualMaximum(Calendar.DAY_OF_MONTH))
//					return unitDataList;			
		}
		
		//iterate for weeks
		//first do petiton for day to sunday
		GregorianCalendar weekIterator = new GregorianCalendar(dateIterator.get(Calendar.YEAR),dateIterator.get(Calendar.MONTH),1);
		//si cae en 1 el lunes y la fecha de consulta es superior al 7 que sera domingo se hara la consulta unicamente con la semana
		if(weekIterator.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && dateIterator.get(Calendar.DAY_OF_MONTH)>=7){
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(weekIterator.getTime()), sku);
			if (dayUnitData == null) {
				logger.info("Error Getting week units for app: "+sku);
				//throw new DateHelperException(
				//		"Problem getting day sales. Please see log for more information");
			}else{
				unitDataList.addAll(dayUnitData);
			}
			if(dateIterator.get(Calendar.DAY_OF_MONTH)==7)
				return cleanUnitDataList(unitDataList);
			weekIterator.add(Calendar.DATE, 7);
			
		}else{
		//si no coincide con la primera semana llegamos al domingo de la primera semana dia a dia
		do{
			
				
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(weekIterator.getTime()), sku);
				if (dayUnitData == null) {
					logger.info("Error Getting week units for app "+sku);
					//throw new DateHelperException(
					//		"Problem getting day sales. Please see log for more information");
				}else{
					unitDataList.addAll(dayUnitData);
				}
				//si es el mismo dia que el ultimo dia consultable se retorna
				if(weekIterator.get(Calendar.YEAR)==dateIterator.get(Calendar.YEAR)&&
						weekIterator.get(Calendar.MONTH)==dateIterator.get(Calendar.MONTH)&&
						weekIterator.get(Calendar.DAY_OF_MONTH)==dateIterator.get(Calendar.DAY_OF_MONTH))
						return cleanUnitDataList(unitDataList);
				
				weekIterator.add(Calendar.DATE, 1);
				
			
		}while(weekIterator.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY);
		}
		//hacemos la consulta de semanas mientras el dia del iterador mas 6 sea menor o igual que la fecha de la query
		//la consulta debe hacerse por domingos asi que hay que sumar los seis dias
		while(weekIterator.get(Calendar.DAY_OF_MONTH)+7<=dateIterator.get(Calendar.DAY_OF_MONTH)){
			if(weekIterator.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
				weekIterator.add(Calendar.DAY_OF_MONTH, 6);
			}else{
				weekIterator.add(Calendar.DAY_OF_MONTH, 7);
			}
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(weekIterator.getTime()), sku);
			if (dayUnitData == null) {
				logger.info("Problem getting week sales for app: "+sku);
				//throw new DateHelperException("Problem getting week sales. Please see log for more information");
				
				//hacemos por ultimo la consulta hasta llegar al dia de la consulta ya que no se ha encontrado estadisticas de alguna semana aun
				while(weekIterator.get(Calendar.DAY_OF_MONTH)<=dateIterator.get(Calendar.DAY_OF_MONTH)){

					dayUnitData = Autoingestion.getUnitsByDate(
							/**propertiesFile,*/ user, password, vendorId,
							Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
							Constants.REPORT_SUBTYPE_SUMMARY_NAME,
							sdf.format(weekIterator.getTime()), sku);
					if (dayUnitData == null) {
						logger.info("there are not day sales; " +  sdf.format(weekIterator.getTime())+" "+sku);
						return cleanUnitDataList(unitDataList);
					}
					unitDataList.addAll(dayUnitData);
					weekIterator.add(Calendar.DATE, 1);
					if(weekIterator.get(Calendar.DAY_OF_MONTH)==dateIterator.get(Calendar.DAY_OF_MONTH))
						return cleanUnitDataList(unitDataList);
					
				}
			}else{
			unitDataList.addAll(dayUnitData);
			
			if(weekIterator.get(Calendar.DAY_OF_YEAR)+7==dateIterator.get(Calendar.DAY_OF_YEAR)){
				dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(weekIterator.getTime()), sku);
				if (dayUnitData == null) {
					logger.info("there are not day sales; " +  sdf.format(weekIterator.getTime())+" "+sku);
					return cleanUnitDataList(unitDataList);
				}
				unitDataList.addAll(dayUnitData);
				
				return cleanUnitDataList(unitDataList);
			}
			weekIterator.add(Calendar.DAY_OF_MONTH, 1);
		
			}
		}
		//hacemos por ultimo la consulta hasta llegar al dia de la consulta
		while(weekIterator.get(Calendar.DAY_OF_MONTH)<=dateIterator.get(Calendar.DAY_OF_MONTH)){

			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(weekIterator.getTime()), sku);
			if (dayUnitData == null) {
				logger.info("there are not day sales; " +  sdf.format(weekIterator.getTime()));
				return cleanUnitDataList(unitDataList);
			}else{
			unitDataList.addAll(dayUnitData);
			}
			weekIterator.add(Calendar.DATE, 1);
			
		}
		}
		return cleanUnitDataList(unitDataList);
		
		
		
/** Redo to init for years		
		// getting day units until sunday or deployment day or first sunday
		// without 1 week passed
		while ((Calendar.SUNDAY != dateIterator.get(Calendar.DAY_OF_WEEK) || (Calendar.SUNDAY == dateIterator
				.get(Calendar.DAY_OF_WEEK) && isFirstSunday(
				deploymentDateCalendar, dateIterator)))) {
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					propertiesFile, user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(dateIterator), sku);
			if (dayUnitData == null) {
				logger.error("Error Getting day units");
				throw new DateHelperException(
						"Problem getting day sales. Please see log for more information");
			}
			unitDataList.addAll(dayUnitData);
			dateIterator.add(Calendar.DATE, -1);
			// if dateIterator is the same that deployment day return item
			if(dateIterator.get(Calendar.YEAR)==deploymentDateCalendar.get(Calendar.YEAR)&&dateIterator.get(Calendar.DAY_OF_MONTH)==deploymentDateCalendar.get(Calendar.DAY_OF_MONTH)&&dateIterator.get(Calendar.MONTH)==deploymentDateCalendar.get(Calendar.MONTH))
				return unitDataList;
		}
		
		//init week iteration
		//if is first week is less than 7 (if is 7 we can do one week petition) we must do a petition for day to arrive to first month day
		if(dateIterator.get(Calendar.DAY_OF_WEEK)<7){
			
		}
		while(){
			
		}
		
*/

	}

	private static boolean isFirstSunday(Calendar deploymentDate,
			Calendar sunday) {
		int dayOfWeek = deploymentDate.get(Calendar.DAY_OF_WEEK);
		Calendar dateIterator = deploymentDate;
		dateIterator.add(Calendar.DATE, 8 - dayOfWeek);
		if (dateIterator.get(Calendar.DAY_OF_WEEK_IN_MONTH) == sunday
				.get(Calendar.DAY_OF_WEEK_IN_MONTH))
			return true;

		return false;

	}
	
	private static List<UnitData> cleanUnitDataList(List<UnitData> unitDataListIn){
		List<UnitData> cleanedUnitDataList = new Vector<UnitData>();
		for (Iterator iterator = unitDataListIn.iterator(); iterator
				.hasNext();) {
			UnitData unitData = (UnitData) iterator.next();
			boolean countryIsAdded = false;
			for (Iterator iterator2 = cleanedUnitDataList.iterator(); iterator2
					.hasNext();) {
				UnitData unitDataCleaned = (UnitData) iterator2.next();
				if(unitData.getCountryCode().equals(unitDataCleaned.getCountryCode())){
					countryIsAdded = true;
					break;
				}
					
			}
			if(!countryIsAdded){
				
				UnitData newUnitData = new UnitData(unitData.countryCode, getAllCountryDataUnits(unitDataListIn,unitData.countryCode));
				cleanedUnitDataList.add(newUnitData);
			}
			
		}
		
		
		return cleanedUnitDataList;
	}
	
	private static int getAllCountryDataUnits(List<UnitData> unitData, String country ){
		int units=0;
		for (Iterator iterator = unitData.iterator(); iterator.hasNext();) {
			UnitData unitData2 = (UnitData) iterator.next();
			if(unitData2.getCountryCode().equals(country))
					units = units + unitData2.getUnits();
		}
		return units;
	}
	
	
	
	// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin entre 1 y 6 meses domingo
	public static final int USE_CASE_0 = 0; 
	// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin entre 1 y 6 meses no domingo
	public static final int USE_CASE_1 = 1;
	// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin entre 1 y 6 meses domingo
	public static final int USE_CASE_2 = 2;
	// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin entre 1 y 6 meses no domingo
	public static final int USE_CASE_3 = 3;
	// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin menos de un mes
	public static final int USE_CASE_4 = 4;
	// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin menos de un mes
	public static final int USE_CASE_5 = 5;
	// consulta fecha inicio y fin menos de un mes
	public static final int USE_CASE_6=6;
	// consulta fecha inicio entre 6 y 12 meses dia uno, y fecha de fin entre 6 y 12 meses ultimo dia del mes
	public static final int USE_CASE_7 = 7;
	// consulta fecha inicio entre 6 y 12 meses no dia uno, y fecha de fin entre 6 y 12 meses ultimo dia del mes
	public static final int USE_CASE_8 = 8;
	// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 6 y 12 meses no ultimo dia del mes
	public static final int USE_CASE_9 = 9;
	// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 1 y 6 meses domingo
	public static final int USE_CASE_10 = 10;
	// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 1 y 6 meses no domingo
	public static final int USE_CASE_11 = 11;
	// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin menos de un mes
	public static final int USE_CASE_12 = 12;
	// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 6 y 12 meses no ultimo dia del mes
	public static final int USE_CASE_13 = 13;
	// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 1 y 6 meses domingo
	public static final int USE_CASE_14 = 14;
	// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 1 y 6 meses no domingo
	public static final int USE_CASE_15 = 15;
	// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin menos de un mes
	public static final int USE_CASE_16 = 16;
	// consulta fecha inicio mas de un año, fecha fin ultimo dia del año
	public static final int USE_CASE_17 = 17;
	// consulta fecha inicio mas de un año, fecha fin anterior a seis meses
	public static final int USE_CASE_18 = 18;
	// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 6 y 12 meses
	public static final int USE_CASE_19 = 19;
	// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 1 y 6 meses domingo
	public static final int USE_CASE_20 = 20;
	// consulta fecha inicio mas de un año, fecha fin menos de un mes
	public static final int USE_CASE_21 = 21;
	// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 1 y 6 meses no domingo
		public static final int USE_CASE_22 = 22;
	
	public static int getDateUseCase(Date firstDate, Date secondDate){
		GregorianCalendar firstDateCalendar = new GregorianCalendar();
		firstDateCalendar.setTime(firstDate);
		GregorianCalendar secondDateCalendar = new GregorianCalendar();
		secondDateCalendar.setTime(secondDate);
		
		Date currentDate = new Date(System.currentTimeMillis());
		GregorianCalendar currentDateCalendar = new GregorianCalendar();
		currentDateCalendar.setTime(currentDate);
		
		GregorianCalendar oneMonth = (GregorianCalendar)currentDateCalendar.clone();
		oneMonth.add(Calendar.MONTH, -1);
		//GregorianCalendar threeMonth = (GregorianCalendar)currentDateCalendar.clone();
		//threeMonth.add(Calendar.MONTH, -3);
		GregorianCalendar sixMonth = (GregorianCalendar)currentDateCalendar.clone();
		sixMonth.add(Calendar.MONTH, -6);
		GregorianCalendar oneYear = (GregorianCalendar) currentDateCalendar.clone();
		oneYear.add(Calendar.YEAR, -1);
		
		// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin entre 1 y 6 meses domingo
		if(firstDateCalendar.before(oneMonth)&&(firstDateCalendar.after(sixMonth))&&firstDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			return USE_CASE_0; 
		// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin entre 1 y 6 meses no domingo
		if(firstDateCalendar.before(oneMonth)&&(firstDateCalendar.after(sixMonth))&&firstDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
			return USE_CASE_1;
		// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin entre 1 y 6 meses domingo
		if(firstDateCalendar.before(oneMonth)&&(firstDateCalendar.after(sixMonth))&&firstDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			return USE_CASE_2;
		// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin entre 1 y 6 meses no domingo
		if(firstDateCalendar.before(oneMonth)&&(firstDateCalendar.after(sixMonth))&&firstDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
			return USE_CASE_3;
		// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin menos de un mes
		if(firstDateCalendar.before(oneMonth)&&(firstDateCalendar.after(sixMonth))&&firstDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
				&&secondDateCalendar.after(oneMonth))
			return USE_CASE_4;
		// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin menos de un mes
		if(firstDateCalendar.before(oneMonth)&&(firstDateCalendar.after(sixMonth))&&firstDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY
				&&secondDateCalendar.after(oneMonth))
			return USE_CASE_5;
		// consulta fecha inicio y fin menos de un mes
		if(firstDateCalendar.after(oneMonth)&&secondDateCalendar.after(oneMonth))
			return USE_CASE_6;
		// consulta fecha inicio entre 6 y 12 meses dia uno, y fecha de fin entre 6 y 12 meses ultimo dia del mes
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)==1
				&&secondDateCalendar.before(sixMonth)&&(secondDateCalendar.after(oneYear))&&secondDateCalendar.get(Calendar.DAY_OF_MONTH)==secondDateCalendar.getMaximum(Calendar.DAY_OF_MONTH))
			return USE_CASE_7;
		// consulta fecha inicio entre 6 y 12 meses no dia uno, y fecha de fin entre 6 y 12 meses ultimo dia del mes
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)!=1
				&&secondDateCalendar.before(sixMonth)&&(secondDateCalendar.after(oneYear))&&secondDateCalendar.get(Calendar.DAY_OF_MONTH)==secondDateCalendar.getMaximum(Calendar.DAY_OF_MONTH))
			return USE_CASE_8;
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 6 y 12 meses no ultimo dia del mes
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)==1
				&&secondDateCalendar.before(sixMonth)&&(secondDateCalendar.after(oneYear))&&secondDateCalendar.get(Calendar.DAY_OF_MONTH)!=secondDateCalendar.getMaximum(Calendar.DAY_OF_MONTH))
			return USE_CASE_9;
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 1 y 6 meses domingo
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)==1
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			return USE_CASE_10;
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 1 y 6 meses no domingo
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)==1
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
			return USE_CASE_11;
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin menos de un mes
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)==1
				&&secondDateCalendar.after(oneMonth))
			return USE_CASE_12;
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 6 y 12 meses no ultimo dia del mes
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)!=1
				&&secondDateCalendar.before(sixMonth)&&(secondDateCalendar.after(oneYear))&&secondDateCalendar.get(Calendar.DAY_OF_MONTH)!=secondDateCalendar.getMaximum(Calendar.DAY_OF_MONTH))
			return USE_CASE_13;
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 1 y 6 meses domingo
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)!=1
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			return USE_CASE_14;
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 1 y 6 meses no domingo
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)!=1
				&&secondDateCalendar.before(oneMonth)&&(secondDateCalendar.after(sixMonth))&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
			return USE_CASE_15;
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin menos de un mes
		if(firstDateCalendar.before(sixMonth)&&(firstDateCalendar.after(oneYear))&&firstDateCalendar.get(Calendar.DAY_OF_MONTH)!=1
				&&secondDateCalendar.after(oneMonth))
			return USE_CASE_16;
		// consulta fecha inicio mas de un año, fecha fin ultimo dia del año
		if(firstDateCalendar.before(oneYear)&&secondDateCalendar.get(Calendar.DAY_OF_YEAR)==secondDateCalendar.getMaximum(Calendar.DAY_OF_YEAR))
			return USE_CASE_17;
		// consulta fecha inicio mas de un año, fecha fin anterior a seis meses
		if(firstDateCalendar.before(oneYear)&&secondDateCalendar.before(oneYear))
			return USE_CASE_18;
		// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 6 y 12 meses
		if(firstDateCalendar.before(oneYear)&&secondDateCalendar.before(sixMonth)&&secondDateCalendar.after(oneYear))
			return USE_CASE_19;
		
		// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 1 y 6 meses domingo
		if(firstDateCalendar.before(oneYear)&&secondDateCalendar.before(oneMonth)&&secondDateCalendar.after(sixMonth)&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)== Calendar.SUNDAY)
		 return USE_CASE_20;
		// consulta fecha inicio mas de un año, fecha fin menos de un mes 
		if(firstDateCalendar.before(oneYear)&&secondDateCalendar.after(oneMonth))
			return USE_CASE_21;
		// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 1 y 6 meses no domingo
				if(firstDateCalendar.before(oneYear)&&secondDateCalendar.before(oneMonth)&&secondDateCalendar.after(sixMonth)&&secondDateCalendar.get(Calendar.DAY_OF_WEEK)!= Calendar.SUNDAY)
				 return USE_CASE_22;
		
		return -1000;
	}
	
	
	
	
	
	public static List<UnitData> getUnitDataByDate(Date firstDate,
			Date secondDate, String sku, String user,
			String password, String vendorId) throws DateHelperException {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		List<UnitData> unitDataList = new Vector<UnitData>();
		
		GregorianCalendar firstDateCalendar = new GregorianCalendar();
		firstDateCalendar.setTime(firstDate);
		GregorianCalendar secondDateCalendar = new GregorianCalendar();
		secondDateCalendar.setTime(secondDate);

		//if seconddate is current delete one day
		// get last day checkable, must be 1 days before if today is the initial day
		Date currentday= new Date(System.currentTimeMillis());
				GregorianCalendar currentDayCalendar = new GregorianCalendar();
				currentDayCalendar.setTime(currentday);
				if( (currentDayCalendar.get(Calendar.YEAR)==secondDateCalendar.get(Calendar.YEAR) && currentDayCalendar.get(Calendar.DAY_OF_YEAR)==secondDateCalendar.get(Calendar.DAY_OF_YEAR))){
					secondDateCalendar.add(Calendar.DATE, -1);
				
				}
		
				//check dates
				if(firstDateCalendar.get(Calendar.YEAR)>secondDateCalendar.get(Calendar.YEAR) || 
						(firstDateCalendar.get(Calendar.YEAR)==secondDateCalendar.get(Calendar.YEAR) && firstDateCalendar.get(Calendar.DAY_OF_YEAR)>secondDateCalendar.get(Calendar.DAY_OF_YEAR))){
					
					logger.error("Incorrect Dates, First date must be 2 days previous to final date");
					throw new DateHelperException("Incorrect Dates, First date must be 2 days previous to final date");
					
				}
				

				// if iterator is less or equal to deployment date we cant check units
				if (secondDateCalendar.before(firstDateCalendar)) {

					logger.error("Incorrect date");
					throw new DateHelperException(
							"We cant get Results. Incorrect dates. Wait some days");

				}
				
				
		//checkUseCase
		int usecase = getDateUseCase(firstDateCalendar.getTime(),secondDateCalendar.getTime());
		
		GregorianCalendar iteratorFirst = null;
		GregorianCalendar iteratorSecond = null;
		
		switch (usecase) {
		
		// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin entre 1 y 6 meses domingo
		case USE_CASE_0:
			//
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
				while(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
					List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
							/**propertiesFile,*/ user, password, vendorId,
							Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
							Constants.REPORT_SUBTYPE_SUMMARY_NAME,
							sdf.format(iteratorFirst.getTime()), sku);
					if (dayUnitData == null) {
						logger.error("Error Getting week units");
						throw new DateHelperException(
								"Problem getting day sales. Please see log for more information");
					}
					unitDataList.addAll(dayUnitData);
					if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7==secondDateCalendar.get(Calendar.DAY_OF_YEAR))
						return cleanUnitDataList(unitDataList);
					iteratorFirst.add(Calendar.DATE, 7);
					
					
				} 
			return cleanUnitDataList(unitDataList);
			
		// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin entre 1 y 6 meses no domingo
		case USE_CASE_1:
				logger.warn("We can get sales with this dates, we get the aproximated possible sale");
				iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
				iteratorSecond = (GregorianCalendar)secondDateCalendar.clone();
				while(iteratorSecond.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
					iteratorSecond.add(Calendar.DATE, 1);
				}
				while(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7<=iteratorSecond.get(Calendar.DAY_OF_YEAR)){
					List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
							/**propertiesFile,*/ user, password, vendorId,
							Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
							Constants.REPORT_SUBTYPE_SUMMARY_NAME,
							sdf.format(iteratorFirst.getTime()), sku);
					if (dayUnitData == null) {
						logger.error("Error Getting week units");
						throw new DateHelperException(
								"Problem getting day sales. Please see log for more information");
					}
					unitDataList.addAll(dayUnitData);
					if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7==iteratorSecond.get(Calendar.DAY_OF_YEAR))
						return cleanUnitDataList(unitDataList);
					iteratorFirst.add(Calendar.DATE, 7);
					
					
				}
				
				
			break;
		
		// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin entre 1 y 6 meses domingo
		case USE_CASE_2:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			
			while(iteratorFirst.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				iteratorFirst.add(Calendar.DATE, -1);
			}
			while(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting week units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7==secondDateCalendar.get(Calendar.DAY_OF_YEAR))
					return cleanUnitDataList(unitDataList);
				iteratorFirst.add(Calendar.DATE, 7);
				
				
			}
			
			break;
		// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin entre 1 y 6 meses no domingo
		case USE_CASE_3:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			iteratorSecond = (GregorianCalendar)secondDateCalendar.clone();
			while(iteratorFirst.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				iteratorFirst.add(Calendar.DATE, -1);
			}
			while(iteratorSecond.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				iteratorSecond.add(Calendar.DATE, 1);
			}
			while(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting week units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7==iteratorSecond.get(Calendar.DAY_OF_YEAR))
					return cleanUnitDataList(unitDataList);
				iteratorFirst.add(Calendar.DATE, 7);
				
				
			}
			
			break;
		// consulta fecha inicio entre 1 y 6 meses lunes, fecha fin menos de un mes
		case USE_CASE_4:
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting week units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7==secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
					return cleanUnitDataList(unitDataList);
				}else if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+12<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
					iteratorFirst.add(Calendar.DATE, 7);
					break;
				}
					
				iteratorFirst.add(Calendar.DATE, 7);
				
				
			} 
			while(iteratorFirst.get(Calendar.DAY_OF_YEAR)<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("there are not day sales; " +  sdf.format(iteratorFirst.getTime()));
					return cleanUnitDataList(unitDataList);
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.DATE, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
			
			
		// consulta fecha inicio entre 1 y 6 meses no lunes, fecha fin menos de un mes
		case USE_CASE_5:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				iteratorFirst.add(Calendar.DATE, -1);
			}
			while(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting week units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+7==secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
					return cleanUnitDataList(unitDataList);
				}else if(iteratorFirst.get(Calendar.DAY_OF_YEAR)+12<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
					iteratorFirst.add(Calendar.DATE, 7);
					break;
				}
					
				iteratorFirst.add(Calendar.DATE, 7);
				
				
			} 
			while(iteratorFirst.get(Calendar.DAY_OF_YEAR)<=secondDateCalendar.get(Calendar.DAY_OF_YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("there are not day sales; " +  sdf.format(iteratorFirst.getTime()));
					return cleanUnitDataList(unitDataList);
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.DATE, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
			
			
		// consulta fecha inicio y fin menos de un mes
		case USE_CASE_6:
			return getFullUnitData(firstDate, secondDate, sku, user, password, vendorId);
			
		// consulta fecha inicio entre 6 y 12 meses dia uno, y fecha de fin entre 6 y 12 meses ultimo dia del mes
		case USE_CASE_7:
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
		// consulta fecha inicio entre 6 y 12 meses no dia uno, y fecha de fin entre 6 y 12 meses ultimo dia del mes
		case USE_CASE_8:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			iteratorFirst.set(Calendar.DAY_OF_MONTH, 1);
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
		
			
			
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 6 y 12 meses no ultimo dia del mes
		case USE_CASE_9:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			

		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 1 y 6 meses domingo
		case USE_CASE_10:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			
			return cleanUnitDataList(unitDataList);
			
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin entre 1 y 6 meses no domingo
		case USE_CASE_11:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			
			return cleanUnitDataList(unitDataList);
			
		// consulta fecha inicio entre 6 y 12 meses dia uno y fecha de fin menos de un mes
		case USE_CASE_12:
			return getFullUnitData(firstDate, secondDate, sku, user, password, vendorId);
			
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 6 y 12 meses no ultimo dia del mes
		case USE_CASE_13:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
			
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 1 y 6 meses domingo
		case USE_CASE_14:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			
			return cleanUnitDataList(unitDataList);
			
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin entre 1 y 6 meses no domingo
		case USE_CASE_15:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			
			return cleanUnitDataList(unitDataList);
			
		// consulta fecha inicio entre 6 y 12 meses no dia uno y fecha de fin menos de un mes
		case USE_CASE_16:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar)firstDateCalendar.clone();
			iteratorFirst.set(Calendar.DAY_OF_MONTH, 1);
			return getFullUnitData(iteratorFirst.getTime(), secondDate, sku, user, password, vendorId);
			
		// consulta fecha inicio mas de un año, fecha fin ultimo dia del año
		case USE_CASE_17:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar) firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.YEAR)!=secondDateCalendar.get(Calendar.YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_YEARLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting year units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.YEAR, 1);
			}
			return cleanUnitDataList(unitDataList);
		
		// consulta fecha inicio mas de un año, fecha fin anterior a seis meses
		case USE_CASE_18:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar) firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.YEAR)!=secondDateCalendar.get(Calendar.YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_YEARLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting year units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.YEAR, 1);
			}
			iteratorFirst.set(Calendar.DAY_OF_YEAR, 1);
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
			
			
		// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 6 y 12 meses
		case USE_CASE_19:
			logger.warn("We can get sales with this dates, we get the aproximated possible sale");
			iteratorFirst = (GregorianCalendar) firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.YEAR)!=secondDateCalendar.get(Calendar.YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_YEARLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting year units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.YEAR, 1);
			}
			iteratorFirst.set(Calendar.DAY_OF_YEAR, 1);
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
			
		// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 1 y 6 meses domingo
		case USE_CASE_20:
			iteratorFirst = (GregorianCalendar) firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.YEAR)!=secondDateCalendar.get(Calendar.YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_YEARLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting year units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.YEAR, 1);
			}
			iteratorFirst.set(Calendar.DAY_OF_YEAR, 1);
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
			
			
		// consulta fecha inicio mas de un año, fecha fin menos de un mes
		case USE_CASE_21:
			return getFullUnitData(firstDate, secondDate, sku, user, password, vendorId);
		// consulta fecha inicio mas de un año, fecha fin no ultimo dia del mes entre 1 y 6 meses no domingo
		case USE_CASE_22:
			iteratorFirst = (GregorianCalendar) firstDateCalendar.clone();
			while(iteratorFirst.get(Calendar.YEAR)!=secondDateCalendar.get(Calendar.YEAR)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_YEARLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting year units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				iteratorFirst.add(Calendar.YEAR, 1);
			}
			iteratorFirst.set(Calendar.DAY_OF_YEAR, 1);
			while(iteratorFirst.get(Calendar.MONTH)<=secondDateCalendar.get(Calendar.MONTH)){
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(iteratorFirst.getTime()), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting month units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				iteratorFirst.add(Calendar.MONTH, 1);
				
			}
			return cleanUnitDataList(unitDataList);
		default:
			return getFullUnitData(firstDate, secondDate, sku, user, password, vendorId);
			
		}
		
		return cleanUnitDataList(unitDataList);
	}
}
