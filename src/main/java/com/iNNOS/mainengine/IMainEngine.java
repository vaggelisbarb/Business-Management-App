package com.iNNOS.mainengine;

import com.iNNOS.model.Client;
import com.iNNOS.model.Deliverable;
import com.iNNOS.model.Project;
import com.iNNOS.queryprocessor.Database;

public interface IMainEngine {
	// Δημιουργία καινούριου έργου
	public boolean createNewProject(Project projet);
	
	// Δημιουργία νέου πελάτη
	public boolean createNewClient(Client client);
	
	// Δημιουργία καινούριου παραδοτέου
	public Deliverable createNewDeliverbale(String identificationCode, String delivTitle, double contractualValue, String deadlineDate, String implementationMode);

	// Σύνδεση με cloud database
	public Database establishDbConnection();

}
