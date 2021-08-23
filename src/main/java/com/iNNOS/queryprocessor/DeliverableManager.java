package com.iNNOS.queryprocessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.iNNOS.model.Deliverable;
import com.iNNOS.model.Project;

public class DeliverableManager {
	private Database database;
	private Deliverable deliverable;
	private Project project;
	private DeliverableAttachmentsManager deliverableAttachmentsManager;
	
	public DeliverableManager() {
		database = Database.getDatabaseInstance();
		deliverableAttachmentsManager = new DeliverableAttachmentsManager();
	}
	
	public DeliverableManager(Deliverable deliverable, Project project) {
		database = Database.getDatabaseInstance();
		this.deliverable = deliverable;
		this.project = project;
		deliverableAttachmentsManager = new DeliverableAttachmentsManager();
	}
	
	public boolean insertIntoDeliverableTable() {
		String sqlInsert = "INSERT INTO ΠΑΡΑΔΟΤΕΑ "
				+ "(ΚΩΔΙΚΟΣ, "
				+ "ΤΙΤΛΟΣ_ΕΡΓΟΥ, "
				+ "ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ, "
				+ "ΣΥΜΒΑΤΙΚΗ_ΑΞΙΑ, "
				+ "ΗM_ΛΗΞΗΣ, "
				+ "ΤΡΟΠΟΣ_ΥΛΟΠΟΙΗΣΗΣ) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		PreparedStatement stm;
		try {
			stm = database.getConnection().prepareStatement(sqlInsert);
			stm.setString(1, deliverable.getIdentificationCode());
			stm.setString(2, project.getTitle());
			stm.setString(3, deliverable.getDelivTitle());
			stm.setDouble(4, deliverable.getContractualValue());
			stm.setDate(5, deliverable.getDeadlineDate());
			stm.setString(6, deliverable.getImplementationMode());
			stm.execute();
			stm.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void updateDeliverableEntry() {
		String sqlUpdate = "UPDATE ΠΑΡΑΔΟΤΕΑ"
				+ "SET "
				+ "ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ?, "
				+ "ΣΥΜΒΑΤΙΚΗ ΑΞΙΑ = ?, "
				+ "ΗΜ_ΛΗΞΗΣ = ?, "
				+ "ΤΡΟΠΟΣ ΥΛΟΠΟΙΗΣΗΣ = ? "
				+ "WHERE ΚΩΔΙΚΟΣ = ? AND ΤΙΤΛΟΣ_ΕΡΓΟΥ = ?";
		
		try {
			PreparedStatement ps = database.getConnection().prepareStatement(sqlUpdate);
			ps.setString(1, deliverable.getDelivTitle());
			ps.setDouble(2, deliverable.getContractualValue());
			ps.setDate(3, deliverable.getDeadlineDate());
			ps.setString(4, deliverable.getImplementationMode());
			ps.setString(5, deliverable.getIdentificationCode());
			ps.setString(6, project.getTitle());
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeDeliverableFromDB() {
		String sqlDelete = "DELETE FROM ΠΑΡΑΔΟΤΕΑ "
				+ "WHERE ΚΩΔΙΚΟΣ = ? AND ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ?";
		
		PreparedStatement ps;
		try {
			ps = database.getConnection().prepareStatement(sqlDelete);
			ps.setString(1, deliverable.getIdentificationCode());
			ps.setString(2, deliverable.getDelivTitle());
			System.out.println("__ "+ps+ " __");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public ArrayList<Deliverable> fetchProjectDeliverablesFromDB() {
		String sqlSelect = "SELECT * FROM ΠΑΡΑΔΟΤΕΑ "
				+ "WHERE ΤΙΤΛΟΣ_ΕΡΓΟΥ = ?";
		ArrayList<Deliverable> deliverableList = new ArrayList<>();

		try {
			PreparedStatement ps = database.getConnection().prepareStatement(sqlSelect);
			ps.setString(1, project.getTitle());
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Deliverable deliverable = processDeliverableRow(rs);
				deliverableList.add(deliverable);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deliverableList = mergeSameDeliverable(deliverableList);
		
		return deliverableList;
	}
	
	private Deliverable processDeliverableRow(ResultSet rs) {
		Deliverable deliverable = new Deliverable();
		
		try {
			deliverable.setIdentificationCode(rs.getString(1));
			project.setTitle(rs.getString(2));
			deliverable.setDelivTitle(rs.getString(3));
			deliverable.setContractualValue(rs.getDouble(4));
			deliverable.setDeadlineDate(rs.getDate(5));
			deliverable.setImplementationMode(rs.getString(6));
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deliverable;
	}
	
	// merging similar Deliverable objects and filling in a list with all the attachments of this deliverable
	private ArrayList<Deliverable> mergeSameDeliverable(ArrayList<Deliverable> originalDeliverableList){
		ArrayList<Deliverable> mergedDeliverableList = new ArrayList<>();
		
		for (int i=0; i<originalDeliverableList.size(); i++) {
			for (int j=i+1; j<mergedDeliverableList.size(); j++) {
				if (isSameDeliverable(originalDeliverableList.get(i), originalDeliverableList.get(j))) {
					if (!isDeliverableInList(originalDeliverableList.get(i), mergedDeliverableList)) {
						originalDeliverableList.get(i).getAttachmentsList().add(originalDeliverableList.get(j).getAttachmentsList().get(0));
					}	
				}
			}
			mergedDeliverableList.add(originalDeliverableList.get(i));
		}
		return mergedDeliverableList;
	}
	
	private boolean isDeliverableInList(Deliverable deliv, ArrayList<Deliverable> delivList) {
		for (Deliverable d: delivList) {
			if (isSameDeliverable(deliv, d))
				return true;
		}
		return false;
	}
	
	private boolean isSameDeliverable(Deliverable del1, Deliverable del2) {
		if (del1.getDelivTitle().equals(del2.getDelivTitle()) &&
				del1.getIdentificationCode().equals(del2.getIdentificationCode()))
			return true;
		return false;
	}
	

	public Deliverable getDeliverable() {
		return deliverable;
	}

	public void setDeliverable(Deliverable deliverable) {
		this.deliverable = deliverable;
		this.deliverableAttachmentsManager.setDeliverable(deliverable);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public DeliverableAttachmentsManager getDeliverableAttachmentsManager() {
		return deliverableAttachmentsManager;
	}

	public void setDeliverableAttachmentsManager(DeliverableAttachmentsManager deliverableAttachmentsManager) {
		this.deliverableAttachmentsManager = deliverableAttachmentsManager;
	}
	
	
	//public void insertAttachmentToDbEntry()
	
	
}	
