package com.iNNOS.model;

import java.util.ArrayList;

public class Deliverable {
	 private String identificationCode;
	 private String delivTitle;
	 private double contractualValue;
	 private java.sql.Date deadlineDate;
	 private String implementationMode;
	 private ArrayList<String> attachmentsList;
	 
	 
	public Deliverable() {
		attachmentsList = new ArrayList<>();
	} 
	 
	public Deliverable(String identificationCode, String delivTitle, double contractualValue, java.sql.Date deadlineDate,
			String implementationMode, ArrayList<String> attachmentsList) {
		this.identificationCode = identificationCode;
		this.delivTitle = delivTitle;
		this.contractualValue = contractualValue;
		this.deadlineDate = deadlineDate;
		this.implementationMode = implementationMode;
		this.attachmentsList = attachmentsList;
	}

	public String toString() {
		return "["+identificationCode+"] "
				+ delivTitle+"\nΣυμβατική αξία: "
				+contractualValue+"\nDeadline: "
				+deadlineDate.toString()
				+"\nΥλοποίηση: "+implementationMode;
	}
	
	public String getIdentificationCode() {
		return identificationCode;
	}

	public void setIdentificationCode(String identificationCode) {
		this.identificationCode = identificationCode;
	}

	public String getDelivTitle() {
		return delivTitle;
	}

	public void setDelivTitle(String delivTitle) {
		this.delivTitle = delivTitle;
	}

	public double getContractualValue() {
		return contractualValue;
	}

	public void setContractualValue(double contractualValue) {
		this.contractualValue = contractualValue;
	}

	public java.sql.Date getDeadlineDate() {
		return deadlineDate;
	}

	public void setDeadlineDate(java.sql.Date deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public String getImplementationMode() {
		return implementationMode;
	}

	public void setImplementationMode(String implementationMode) {
		this.implementationMode = implementationMode;
	}

	public ArrayList<String> getAttachmentsList() {
		return attachmentsList;
	}

	public void setAttachmentsList(ArrayList<String> attachmentsList) {
		this.attachmentsList = attachmentsList;
	}
	
	 
			 
}
