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
package es.arcadiaconsulting.appstoresstats.android.model;

public class StarsRate {

	private int oneStar;
	public int getOneStar() {
		return oneStar;
	}
	public void setOneStar(int oneStar) {
		this.oneStar = oneStar;
	}
	public int getTwoStar() {
		return twoStar;
	}
	public void setTwoStar(int twoStar) {
		this.twoStar = twoStar;
	}
	public int getThreeStar() {
		return threeStar;
	}
	public void setThreeStar(int threeStar) {
		this.threeStar = threeStar;
	}
	public int getFourStar() {
		return fourStar;
	}
	public void setFourStar(int fourStar) {
		this.fourStar = fourStar;
	}
	public int getFiveStar() {
		return fiveStar;
	}
	public void setFiveStar(int fiveStar) {
		this.fiveStar = fiveStar;
	}
	private int twoStar;
	private int threeStar;
	private int fourStar;
	private int fiveStar;
	public StarsRate(int oneStar, int twoStar, int threeStar, int fourStar,
			int fiveStar) {
		super();
		this.oneStar = oneStar;
		this.twoStar = twoStar;
		this.threeStar = threeStar;
		this.fourStar = fourStar;
		this.fiveStar = fiveStar;
	}
}
