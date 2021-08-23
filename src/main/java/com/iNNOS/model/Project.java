package com.iNNOS.model;

import java.sql.Date;
import java.util.ArrayList;

public class Project {
	private String title;
	private Client client;
	
	// A project can have multiple starting & deadline dates
	// TODO : SQL query to find those dates for a specific project
	private ArrayList<Date> startingDates;
	private ArrayList<Date> deadlineDates;
	
	private double contractBudget;
	private ArrayList<Supervisor> supervisors;
	private String protocolNumber;
	
	// A project may change ADA 
	// but remains with the same title and all other information about it
	private ArrayList<String> ada;
	
	private String attachment;
	private ArrayList<Deliverable> projectDeliverables;
	private boolean completed;
	
	
	public Project() {
		client = new Client();
		supervisors = new ArrayList<>();
		startingDates = new ArrayList<>();
		deadlineDates = new ArrayList<>();
		ada = new ArrayList<>();
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public Client getClient() {
		return client;
	}


	public void setClient(Client client) {
		this.client = client;
	}


	public ArrayList<Date> getStartingDates() {
		return startingDates;
	}


	public void setStartingDates(ArrayList<Date> startingDates) {
		this.startingDates = startingDates;
	}


	public ArrayList<Date> getDeadlineDates() {
		return deadlineDates;
	}


	public void setDeadlineDates(ArrayList<Date> deadlineDates) {
		this.deadlineDates = deadlineDates;
	}


	public double getContractBudget() {
		return contractBudget;
	}


	public void setContractBudget(double contractBudget) {
		this.contractBudget = contractBudget;
	}


	public ArrayList<Supervisor> getSupervisors() {
		return supervisors;
	}


	public void setSupervisors(ArrayList<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}


	public String getProtocolNumber() {
		return protocolNumber;
	}


	public void setProtocolNumber(String protocolNumber) {
		this.protocolNumber = protocolNumber;
	}


	public ArrayList<String> getAda() {
		return ada;
	}


	public void setAda(ArrayList<String> ada) {
		this.ada = ada;
	}


	public String getAttachment() {
		return attachment;
	}


	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}


	public ArrayList<Deliverable> getProjectDeliverables() {
		return projectDeliverables;
	}


	public void setProjectDeliverables(ArrayList<Deliverable> projectDeliverables) {
		this.projectDeliverables = projectDeliverables;
	}


	public boolean isCompleted() {
		return completed;
	}


	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	
	public String toString() {
		return "PROJECT\n_____________________\nTitle: "+title+"\nStart_Date: "+startingDates.get(startingDates.size()-1)+"\nDeadline_Date: "
				+deadlineDates.get(deadlineDates.size()-1)+"\nClient: "+client.getFullName()+"\nBudget: "+contractBudget+"\nProtocol No: "+protocolNumber
				+"\nADA-ADAM: "+ada.get(ada.size()-1)+ "\nAttachment: "+"\nCompleted: "+completed+"\n_____________________";
	}
	
	
}
