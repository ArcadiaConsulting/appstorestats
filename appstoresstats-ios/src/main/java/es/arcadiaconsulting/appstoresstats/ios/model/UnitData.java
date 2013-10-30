package es.arcadiaconsulting.appstoresstats.ios.model;

public class UnitData {

	public String countryCode;
	
	public int units;
	
	public UnitData(String countryCode, int units) {
		super();
		this.countryCode = countryCode;
		this.units = units;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public int getUnits() {
		return units;
	}
	public void setUnits(int units) {
		this.units = units;
	}
}
