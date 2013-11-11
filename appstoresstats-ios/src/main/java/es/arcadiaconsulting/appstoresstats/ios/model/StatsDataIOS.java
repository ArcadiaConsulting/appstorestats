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
	
	public StatsDataIOS(String sku, Date endDate, Date initDate, String appName){
		super();
		
		setAppId(sku);
		setEndDate(endDate);
		setInitDate(initDate);
		setAppName(appName);
	}
	

}
