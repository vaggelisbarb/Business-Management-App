package com.iNNOS.queryprocessor;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iNNOS.model.*;

public class QueryProcessor implements IQueryProcessor{
	private ProjectManager projectmanager;
	private ClientManager clientmanager;
	private SupervisorManager supervisroManager;
	private DeliverableManager deliverableManager;
	
	
	public QueryProcessor() {
		projectmanager = new ProjectManager();
		clientmanager = new ClientManager();
		supervisroManager = new SupervisorManager();
		deliverableManager = new DeliverableManager();
	}
	
	@Override
	public boolean insertIntoProjectTable(Project project) {
		projectmanager.setProject(project);
		if (projectmanager.insertIntoProjectTable())
			return true;
		return false;
	}
	
	public void updateProjectTableEntry(String projectTitle, ArrayList<String> columnNames, ArrayList<String> columnsValues, BigInteger phoneNum) {
		
		this.projectmanager.getProject().setTitle(projectTitle);

		projectmanager.updateStringCols(columnNames, columnsValues);
		
		projectmanager.updateIntegerCols("ΤΗΛ_ΥΠΕΥΘΥΝΟΥ", phoneNum);
	}
	
	public ArrayList<Project> fetchProjectFromDB() {
		return projectmanager.fetchProjectsFromDB();
	}
	
	public boolean insertPdfIntoDB(String fileName, Project project) throws IOException {
		return projectmanager.insertPdfIntoDB(fileName, project);
	}
	
	public void insertIntoClientTable(Client client) throws SQLException {
		clientmanager.setClient(client);
		clientmanager.insertEntryIntoClientTable();
	}

	public void updateProjectEntry(Project project) {
		projectmanager.setProject(project);
		projectmanager.updateProjectEntry();
	}
	
	public ArrayList<Client> fetchClientsFromDB() {
		return clientmanager.fetchClientsFromDB();
	}
	
	public Client fetchClientByAfm(String afm) {
		return clientmanager.fetchClientByAfm(afm);
	}
	
	public boolean removeClientEntryFromDB(Client client) {
		return clientmanager.removeClientEntryFromDB(client);
	}
	
	public boolean updateClientEntry(Client client) {
		return clientmanager.updateClientEntry(client);
	}
	
	public boolean removeProjectByTitle(Project project) {
		projectmanager.setProject(project);
		return projectmanager.removeProjectByTitle();
	}
	
	public void insertSupervisorsIntoDB(Project project) {
		supervisroManager.setProject(project);
		supervisroManager.insertSupervisorsIntoDB();
	}
	
	public ArrayList<Supervisor> fetchSupervisorsOfProject(Project project) {
		return supervisroManager.fetchSupervisorsOfProject(project);
	}
	
	public boolean insertIntoDeliverableTable() {
		return this.deliverableManager.insertIntoDeliverableTable();
	}
	
	
	public boolean insertDeliverableAttachmentsIntoDB() {
		deliverableManager.getDeliverableAttachmentsManager().setDeliverable(deliverableManager.getDeliverable());
		return deliverableManager.getDeliverableAttachmentsManager().insertDeliverableAttachmentIntoDB();
	}
	
	public void removeDelAttachmentFromDB(String fileName) {
		deliverableManager.getDeliverableAttachmentsManager().setDeliverable(deliverableManager.getDeliverable());
		deliverableManager.getDeliverableAttachmentsManager().removeDelAttachmentFromDB(fileName);
	}
	
	

	public ProjectManager getProjectmanager() {
		return projectmanager;
	}

	public void setProjectmanager(ProjectManager projectmanager) {
		this.projectmanager = projectmanager;
	}

	public ClientManager getClientmanager() {
		return clientmanager;
	}

	public void setClientmanager(ClientManager clientmanager) {
		this.clientmanager = clientmanager;
	}

	public SupervisorManager getSupervisroManager() {
		return supervisroManager;
	}

	public void setSupervisroManager(SupervisorManager supervisroManager) {
		this.supervisroManager = supervisroManager;
	}

	public DeliverableManager getDeliverableManager() {
		return deliverableManager;
	}

	public void setDeliverableManager(DeliverableManager deliverableManager) {
		this.deliverableManager = deliverableManager;
	}
	
	public ArrayList<Deliverable> fetchProjectDeliverablesFromDB(){
		return this.deliverableManager.fetchProjectDeliverablesFromDB();
	}
	
	public ArrayList<String> fetchDeliverableAttachmentsFromDB(){
		return this.deliverableManager.getDeliverableAttachmentsManager().fetchDeliverableAttachmentsFromDB();
	}
	
	public void removeDeliverableFromDB() {
		this.deliverableManager.getDeliverableAttachmentsManager().removeAllDelAttachmentsFromDB();
		this.deliverableManager.removeDeliverableFromDB();
	}
	
}
