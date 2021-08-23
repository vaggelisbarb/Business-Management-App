package com.iNNOS.queryprocessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iNNOS.model.Deliverable;


public class DeliverableAttachmentsManager {
	private Deliverable deliverable;
	
	public DeliverableAttachmentsManager() {
		deliverable = new Deliverable();
	}
	
	
	public ArrayList<String> fetchDeliverableAttachmentsFromDB(){
		ArrayList<String> attachments = new ArrayList<>();
		
		String sqlSelect = "SELECT * FROM ΣΥΝΗΜΜΕΝΑ_ΠΑΡΑΔΟΤΕΟΥ " 
				+ "WHERE ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ? AND ΚΩΔΙΚΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ?";
		
		try {
			PreparedStatement ps = Database.getDatabaseInstance().getConnection().prepareStatement(sqlSelect);
			ps.setString(1, deliverable.getDelivTitle());
			ps.setString(2, deliverable.getIdentificationCode());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next())
				attachments.add(rs.getString(3));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return attachments;
	}
	
	public boolean insertDeliverableAttachmentIntoDB() {
		String sqlInsert = "INSERT INTO ΣΥΝΗΜΜΕΝΑ_ΠΑΡΑΔΟΤΕΟΥ"
				+ "(ΚΩΔΙΚΟΣ_ΠΑΡΑΔΟΤΕΟΥ, "
				+ "ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ, "
				+ "ΟΝΟΜΑ_ΣΥΝΗΜΜΕΝΟΥ)"
				+ "VALUES (?, ?, ?)";
				
		
		if (deliverable.getAttachmentsList().size()!=0) {
			for (int i=0; i<deliverable.getAttachmentsList().size(); i++) {
				if(!isAttachmentStoredIntoDB(deliverable.getAttachmentsList().get(i))) {
					try {
						System.out.println(deliverable.getIdentificationCode());
						PreparedStatement ps = Database.getDatabaseInstance().getConnection().prepareStatement(sqlInsert);
						ps.setString(1, deliverable.getIdentificationCode());
						ps.setString(2, deliverable.getDelivTitle());
						ps.setString(3, deliverable.getAttachmentsList().get(i));
						ps.execute();
				
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}else
			return true;
		return false;
	}

	
	private boolean isAttachmentStoredIntoDB(String attachment) {
		ArrayList<String> storedAttachments = fetchDeliverableAttachmentsFromDB();
		for (String att: storedAttachments) {
			if (attachment.equals(att))
				return true;
		}
		return false;
	}

	public void removeAllDelAttachmentsFromDB() {
		String sqlDelete = "DELETE FROM ΣΥΝΗΜΜΕΝΑ_ΠΑΡΑΔΟΤΕΟΥ "
				+ "WHERE ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ? "
				+ "AND ΚΩΔΙΚΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ?";
		
		PreparedStatement ps;
		try {
			ps = Database.getDatabaseInstance().getConnection().prepareStatement(sqlDelete);
			ps.setString(1, deliverable.getDelivTitle());
			ps.setString(2, deliverable.getIdentificationCode());
			System.out.println("__ "+ps+ " __");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void removeDelAttachmentFromDB(String fileName) {
		String sqlDelete = "DELETE FROM ΣΥΝΗΜΜΕΝΑ_ΠΑΡΑΔΟΤΕΟΥ "
				+ "WHERE ΤΙΤΛΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ? "
				+ "AND ΚΩΔΙΚΟΣ_ΠΑΡΑΔΟΤΕΟΥ = ? "
				+ "AND ΟΝΟΜΑ_ΣΥΝΗΜΜΕΝΟΥ = ?";
		
		PreparedStatement ps;
		try {
			ps = Database.getDatabaseInstance().getConnection().prepareStatement(sqlDelete);
			ps.setString(1, deliverable.getDelivTitle());
			ps.setString(2, deliverable.getIdentificationCode());
			ps.setString(3, fileName);
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public Deliverable getDeliverable() {
		return deliverable;
	}

	public void setDeliverable(Deliverable deliverable) {
		this.deliverable = deliverable;
	}	
	
}
