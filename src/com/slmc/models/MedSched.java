package com.slmc.models;

public class MedSched {

	private int medSchedId;
	private String pin;
	private String medName;
	private String freq;
	private String startDate;
	private String endDate;
	private String startTime;
	
	public int getMedSchedId() {
		return medSchedId;
	}
	public void setMedSchedId(int medSchedId) {
		this.medSchedId = medSchedId;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getMedName() {
		return medName;
	}
	public void setMedName(String medName) {
		this.medName = medName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getFreq() {
		return freq;
	}
	public void setFreq(String freq) {
		this.freq = freq;
	}
}
