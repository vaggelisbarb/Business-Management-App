package com.iNNOS.mainengine;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iNNOS.constants.CommonConstants;
import com.iNNOS.controllers.HostServiceProvider;
import com.iNNOS.model.Client;
import com.iNNOS.model.Deliverable;
import com.iNNOS.model.Project;
import com.iNNOS.model.Supervisor;
import com.iNNOS.queryprocessor.Database;
import com.iNNOS.queryprocessor.QueryProcessor;
import com.iNNOS.service.CloudEntryPoint;
import com.iNNOS.service.CommonService;
import com.iNNOS.view.AlertFactory;

import javafx.application.HostServices;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MainEngine implements IMainEngine{
	private static MainEngine mainengine;
	private Database database;
	private static AlertFactory alertfactory;
	private HostServiceProvider hostserviceprovider;

	
	private MainEngine() {
		database = Database.getDatabaseInstance();
		alertfactory = new AlertFactory();
		hostserviceprovider = HostServiceProvider.getHostServiceProviderInstance();
	}
	
	
	/** Singleton Pattern.
	 * 	Public static method for getting the MainEngine instance.
	 */
	public static MainEngine getMainEngineInstance() {
		if (mainengine == null)
			mainengine = new MainEngine();
		return mainengine;
	}
	
	
	/**
	 * Connection establishment between DB and App
	 */
	@Override
	public Database establishDbConnection() {
		database.connect();
		return database;
	}
	
	public void DBdisconnect() {
		database.disconnect();
	}
	
	/**
	 * Imports project into Database and importing it's pdf attachment
	 * @param Project instance whose data is to be stored in the database 
	 * @return s
	 */
	@Override
	public boolean createNewProject(Project project) {
		QueryProcessor qp = new QueryProcessor();
		if (qp.insertIntoProjectTable(project)) {
			// Try to insert PDF data into DB
			if (project.getAttachment()!=null) {
				insertPdfIntoDB(project.getAttachment(), project);
			}
			// Inserts any supervisor that has the project into the database
			qp.insertSupervisorsIntoDB(project);
			
			DBdisconnect();
			return true;
		}
		return false;
	}
	
	public void insertPdfIntoDB(String attachmentName, Project project) {
		QueryProcessor qp = new QueryProcessor();
		
		try {
			qp.insertPdfIntoDB(attachmentName, project);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean createNewClient(Client client) {
		QueryProcessor qp = new QueryProcessor();
		try {
			qp.insertIntoClientTable(client);
			DBdisconnect();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	

	@Override
	public Deliverable createNewDeliverbale(String identificationCode, String delivTitle, double contractualValue,
			String deadlineDate, String implementationMode) {
		return null;
	}

	public ArrayList<Project> fetchProjectsFromDB() {
		QueryProcessor qp = new QueryProcessor();
		return qp.fetchProjectFromDB();
	}
	
	public ArrayList<Client> fetchClientsFromDB() {
		QueryProcessor qp = new QueryProcessor();
		return qp.fetchClientsFromDB();
	}
	
	public Client fetchClientByAfm(String afm) {
		QueryProcessor qp = new QueryProcessor();
		return qp.fetchClientByAfm(afm);
	}
	
	public boolean removeClientEntryFromDB(Client client) {
		QueryProcessor qp = new QueryProcessor();
		return qp.removeClientEntryFromDB(client);
	}
	
	public boolean updateClientEntry(Client client) {
		QueryProcessor qp = new QueryProcessor();
		return qp.updateClientEntry(client);
	}
	
	public boolean removeProjectByTitle(Project project) {
		QueryProcessor qp = new QueryProcessor();
		return qp.removeProjectByTitle(project);
	}
	
	public void updateProjectEntry(Project project) {
		QueryProcessor qp = new QueryProcessor();
		qp.updateProjectEntry(project);
	}
	
	public ArrayList<Supervisor> fetchSupervisorsOfProject(Project project) {
		QueryProcessor qp = new QueryProcessor();
		return qp.fetchSupervisorsOfProject(project);
	}
	
	/**
	 * @param file File to be uploaded into Amazon S3 cloud storage
	 */
	public void uploadFileIntoCloud(File file, String bucket, String folderName) {
		CloudEntryPoint cloudentrypoint = CloudEntryPoint.getCloudEntryPointInstance();
		if (!cloudentrypoint.getS3Client().doesObjectExist(bucket, folderName)) {
			System.out.printf("[INFO] Folder `%s` does not exists.\n[INFO] Creating folder `%s` into bucket `%s`\n", folderName, folderName, bucket);
			CommonService.createFolder(bucket, folderName, cloudentrypoint.getS3Client(), CommonConstants.SUFFIX);
		}
			System.out.printf("[INFO] Putting file `%s` into folder `%s` of bucket `%s`\n", file, folderName, bucket);
			cloudentrypoint.getCommonService().putObject(file, cloudentrypoint.getS3Client(), bucket, folderName);
	}


	/**
	 * @param fileName
	 * @param bucket
	 * @param folderName
	 */
	public void downloadFileFromCloud(String fileName, String bucket, String folderName, String title) {
		System.out.printf("[INFO] Downloading file `%s` from folder `%s` of bucket `%s`\n", fileName, folderName, bucket);
		CloudEntryPoint cloudentrypoint = CloudEntryPoint.getCloudEntryPointInstance();
		cloudentrypoint.getCommonService().getObj(cloudentrypoint.getS3Client(), fileName, bucket, title);
	}
	
	/**
	 * @param fileName
	 * @param bucket
	 * @param folderName
	 */
	public void removeFileFromCloud(String fileName, String bucket, String folderName) {
		System.out.printf("[INFO] Removing file `%s` from folder `%s` of bucket `%s`\n", fileName, folderName, bucket);
		CloudEntryPoint cloudEntryPoint = CloudEntryPoint.getCloudEntryPointInstance();
		cloudEntryPoint.getCommonService().deleteObject(cloudEntryPoint.getS3Client(), fileName, bucket);
	}
	
	/**
	 * @param type
	 * @param title
	 * @param context
	 * @return
	 */
	public Alert generateAlertDialog(AlertType type, String title, String context) {
		return alertfactory.generateAlertDialog(type, title, context);
	}
	
	public boolean insertIntoDeliverableTable(Deliverable deliv, Project project) {
		QueryProcessor qp = new QueryProcessor();
		qp.getDeliverableManager().setProject(project);
		qp.getDeliverableManager().setDeliverable(deliv);
		return qp.insertIntoDeliverableTable();
	}
	
	public ArrayList<Deliverable> fetchProjectDeliverablesFromDB(Project project){
		QueryProcessor qp = new QueryProcessor();
		qp.getDeliverableManager().setProject(project);
		return qp.fetchProjectDeliverablesFromDB();
	}
	
	public boolean insertDeliverablesAttachmentsIntoDB(Deliverable deliv) {
		QueryProcessor qp = new QueryProcessor();
		qp.getDeliverableManager().setDeliverable(deliv);
		return qp.insertDeliverableAttachmentsIntoDB();
	}
	
	public ArrayList<String> fetchDeliverableAttachmentsFromDB(Deliverable deliv) {
		QueryProcessor qp = new QueryProcessor();
		qp.getDeliverableManager().setDeliverable(deliv);
		return qp.fetchDeliverableAttachmentsFromDB();
	}
	
	public void removeDelAttachmentFromDB(Deliverable del, String fileName) {
		QueryProcessor qp = new QueryProcessor();
		qp.getDeliverableManager().setDeliverable(del);
		qp.removeDelAttachmentFromDB(fileName);
	}
	
	public void removeDeliverableFromDB(Deliverable del) {
		QueryProcessor qp = new QueryProcessor();
		qp.getDeliverableManager().setDeliverable(del);
		qp.removeDeliverableFromDB();
	}
	
	public void initHostServices(HostServices hostservices) {
		hostserviceprovider.init(hostservices);
	}
	
	public HostServices getHostServices() {
		return hostserviceprovider.getHostServices();
	}
	
}
