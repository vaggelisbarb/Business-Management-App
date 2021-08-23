package com.iNNOS.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Supervisor {
	private StringProperty fullName;
	private StringProperty phoneNumber;
	private StringProperty email;
	
	public Supervisor(String fullName, String phoneNumber, String email) {
		this.fullName = new SimpleStringProperty(fullName);
		this.phoneNumber = new SimpleStringProperty(phoneNumber);
		this.email = new SimpleStringProperty(email);
	}

	public Supervisor() {
		this.fullName = new SimpleStringProperty();
		this.phoneNumber = new SimpleStringProperty();
		this.email = new SimpleStringProperty();
	}

	public String getFullName() {
		return fullName.get();
	}

	public void setFullName(String fullName) {
		this.fullName.set(fullName);
	}

	public String getPhoneNumber() {
		return phoneNumber.get();
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber.set(phoneNumber);
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
	}
	
	public StringProperty getNamePropery() {
		return fullName;
	}
	
	public StringProperty getPhoneProperty() {
		return phoneNumber;
	}
	
	public StringProperty getEmailProperty() {
		return email;
	}
	
	public void setNameProperty(StringProperty nameProperty) {
		this.fullName = nameProperty;
	}
}
