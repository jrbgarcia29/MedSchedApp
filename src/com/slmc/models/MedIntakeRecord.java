package com.slmc.models;

public class MedIntakeRecord {

	private int recordId;
	private String pin;
	private String medName;
	private String actualDateTime;
	private String uploaded;
	
	
	public MedIntakeRecord() {
		// TODO Auto-generated constructor stub
	}

	public MedIntakeRecord(String pin, String medName, String actualDateTime) {
		this.pin = pin;
		this.medName = medName;
		this.actualDateTime = actualDateTime;
	}
	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
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

	public String getActualDateTime() {
		return actualDateTime;
	}
	public void setActualDateTime(String actualDateTime) {
		this.actualDateTime = actualDateTime;
	}

	public String isUploaded() {
		return uploaded;
	}

	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}

		
}
