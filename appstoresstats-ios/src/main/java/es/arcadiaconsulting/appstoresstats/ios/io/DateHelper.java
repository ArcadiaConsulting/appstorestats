package es.arcadiaconsulting.appstoresstats.ios.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			return formatter.parse(utc);
		} catch (ParseException e) {
			logger.error("incorrect Date",e );
			return null;
		}
		
	}
	
	
	
	public static List<UnitData> getUnitDataByDate(Date deploymentDate,
			Date queryDate, String sku,/** String propertiesFile,*/ String user,
			String password, String vendorId) throws DateHelperException {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

		List<UnitData> unitDataList = new Vector<UnitData>();

		// iterator for compare dates
		GregorianCalendar dateIterator = new GregorianCalendar();
		dateIterator.setTime(queryDate);

		GregorianCalendar deploymentDateCalendar = new GregorianCalendar();
		deploymentDateCalendar.setTime(deploymentDate);

		// get last day checkable, must be 2 days before
		dateIterator.add(Calendar.DATE, -2);

		// if iterator is less or equal to deployment date we cant check units
		if (dateIterator.before(deploymentDateCalendar)) {

			logger.error("Incorrect date");
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
					sdf.format(yearIterator), sku);
			if (dayUnitData == null) {
				logger.error("Error Getting year units");
				throw new DateHelperException(
						"Problem getting day sales. Please see log for more information");
			}
			unitDataList.addAll(dayUnitData);
			deploymentyear = deploymentyear+1;
			yearIterator.add(Calendar.YEAR, 1);
			
			//if query is on first day of year return response
			if(dateIterator.get(Calendar.MONTH)==1&&dateIterator.get(Calendar.DAY_OF_MONTH)==1)
				return unitDataList;
			
		}
		
		//iterate for months (iterate to month)		
		GregorianCalendar monthIterator = dateIterator;
		while(monthIterator.get(Calendar.MONTH)-1>0){
			monthIterator.add(Calendar.MONTH, -1);
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_MONTHLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(monthIterator), sku);
			if (dayUnitData == null) {
				logger.error("Error Getting month units");
				throw new DateHelperException(
						"Problem getting day sales. Please see log for more information");
			}
			unitDataList.addAll(dayUnitData);
			
			//if query is first day of month return unit data
			if(dateIterator.get(Calendar.DAY_OF_MONTH)==dateIterator.getActualMaximum(Calendar.DAY_OF_MONTH))
					return unitDataList;			
		}
		
		//iterate for weeks
		//first do petiton for day to sunday
		GregorianCalendar weekIterator = new GregorianCalendar(dateIterator.get(Calendar.YEAR),dateIterator.get(Calendar.MONTH),1);
		//si cae en 1 el lunes y la fecha de consulta es superior al 7 que sera domingo se hara la consulta unicamente con la semana
		if(weekIterator.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY && dateIterator.get(Calendar.DAY_OF_MONTH)>=7){
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(weekIterator), sku);
			if (dayUnitData == null) {
				logger.error("Error Getting week units");
				throw new DateHelperException(
						"Problem getting day sales. Please see log for more information");
			}
			unitDataList.addAll(dayUnitData);
			if(dateIterator.get(Calendar.DAY_OF_MONTH)==7)
				return unitDataList;
			weekIterator.add(Calendar.DAY_OF_MONTH, 7);
			
		}else{
		//si no coincide con la primera semana llegamos al domingo de la primera semana dia a dia
		do{
			
				
				List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
						/**propertiesFile,*/ user, password, vendorId,
						Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
						Constants.REPORT_SUBTYPE_SUMMARY_NAME,
						sdf.format(weekIterator), sku);
				if (dayUnitData == null) {
					logger.error("Error Getting week units");
					throw new DateHelperException(
							"Problem getting day sales. Please see log for more information");
				}
				unitDataList.addAll(dayUnitData);
				
				//si es el mismo dia que el ultimo dia consultable se retorna
				if(weekIterator.get(Calendar.YEAR)==dateIterator.get(Calendar.YEAR)&&
						weekIterator.get(Calendar.MONTH)==dateIterator.get(Calendar.MONTH)&&
						weekIterator.get(Calendar.DAY_OF_MONTH)==dateIterator.get(Calendar.DAY_OF_MONTH))
						return unitDataList;
				
				weekIterator.add(Calendar.DAY_OF_MONTH, 1);
				
			
		}while(weekIterator.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY);
		}
		//hacemos la consulta de semanas mientras el dia del iterador mas 6 sea menor o igual que la fecha de la query
		while(weekIterator.get(Calendar.DAY_OF_MONTH)+6<=dateIterator.get(Calendar.DAY_OF_MONTH)){
			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_WEEDLY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(weekIterator), sku);
			if (dayUnitData == null) {
				logger.error("Error Getting week units");
				throw new DateHelperException(
						"Problem getting day sales. Please see log for more information");
			}
			unitDataList.addAll(dayUnitData);
			if(weekIterator.get(Calendar.DAY_OF_MONTH)+6==dateIterator.get(Calendar.DAY_OF_MONTH))
				return unitDataList;
			weekIterator.add(Calendar.DAY_OF_MONTH, 7);
		}
		//hacemos por ultimo la consulta hasta llegar al dia de la consulta
		while(weekIterator.get(Calendar.DAY_OF_MONTH)<=dateIterator.get(Calendar.DAY_OF_MONTH)){

			List<UnitData> dayUnitData = Autoingestion.getUnitsByDate(
					/**propertiesFile,*/ user, password, vendorId,
					Constants.REPORT_TYPE_SALES, Constants.DATE_TYPE_DAILY,
					Constants.REPORT_SUBTYPE_SUMMARY_NAME,
					sdf.format(weekIterator), sku);
			if (dayUnitData == null) {
				logger.error("Error Getting day units");
				throw new DateHelperException(
						"Problem getting day sales. Please see log for more information");
			}
			unitDataList.addAll(dayUnitData);
			weekIterator.add(Calendar.DAY_OF_WEEK, 1);
			
		}
		return unitDataList;
		
		
		
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

}
