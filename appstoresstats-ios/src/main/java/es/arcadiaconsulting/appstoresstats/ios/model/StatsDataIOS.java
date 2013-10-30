package es.arcadiaconsulting.appstoresstats.ios.model;

import java.util.Date;
import java.util.List;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.CommonStatsData.Platform;

public class StatsDataIOS extends CommonStatsData{
	
	public List<UnitData> unitDataList;

	public List<UnitData> getUnitDataList() {
		return unitDataList;
	}

	public void setUnitDataList(List<UnitData> unitDataList) {
		this.unitDataList = unitDataList;
	}

	public StatsDataIOS() {
		super();
		setPlatform(Platform.iOS);
		// TODO Auto-generated constructor stub
	}
	
	public StatsDataIOS(String sku, Date endDate, Date initDate){
		super();
		
		setAppId(sku);
		setEndDate(endDate);
		setInitDate(initDate);
	}
	

}
