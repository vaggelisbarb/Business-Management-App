package com.iNNOS.model;


import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {
	private StringProperty fullName;
	private StringProperty afm;
	private StringProperty address;
	private StringProperty municipality;
	private LongProperty phoneNumber;
	private StringProperty webpage;
	private StringProperty details;
	
	
	public Client(String fullName, String afm, String address, String municipality, Long phoneNumber, String webpage, String details) {
		this.fullName = new SimpleStringProperty(fullName);
		this.afm = new SimpleStringProperty(afm);
		this.address = new SimpleStringProperty(address);
		this.municipality = new SimpleStringProperty(municipality);
		this.phoneNumber = new SimpleLongProperty(phoneNumber);
		this.webpage = new SimpleStringProperty(webpage);
		this.details = new SimpleStringProperty(details);
	}


	public Client() {
		this.fullName = new SimpleStringProperty();
		this.afm = new SimpleStringProperty();
		this.address = new SimpleStringProperty();
		this.municipality = new SimpleStringProperty();
		this.phoneNumber = new SimpleLongProperty();
		this.webpage = new SimpleStringProperty();
		this.details = new SimpleStringProperty();
	}


	public StringProperty getNameProperty() {
		return fullName;
	}
	
	public String getFullName() {
		return fullName.get();
	}


	public void setFullName(String fullName) {
		this.fullName.set(fullName);
	}


	public String getAfm() {
		return afm.get();
	}


	public void setAfm(String afm) {
		this.afm.set(afm);
	}


	public String getAddress() {
		return address.get();
	}


	public void setAddress(String address) {
		this.address.set(address);
	}


	public Long getPhoneNumber() {
		return phoneNumber.get();
	}


	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber.set(phoneNumber);
	}


	public String getWebpage() {
		return webpage.get();
	}


	public void setWebpage(String webpage) {
		this.webpage.set(webpage);
	}


	public String getMunicipality() {
		return municipality.get();
	}


	public void setMunicipality(String municipality) {
		this.municipality.set(municipality);
	}


	public String getDetails() {
		return details.get();
	}


	public void setDetails(String details) {
		this.details.set(details);
	}
	
	

}
