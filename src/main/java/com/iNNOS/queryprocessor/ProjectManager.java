package com.iNNOS.queryprocessor;


import java.io.IOException;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iNNOS.model.Project;

public class ProjectManager {
	private Database db;
	private Project project;
	private DeliverableManager deliverablemanager;
	
	public ProjectManager() {
		db = Database.getDatabaseInstance();
		project = new Project();
		deliverablemanager = new DeliverableManager();
	}
	
	public ProjectManager(Project project) {
		this.project = project;
		db = Database.getDatabaseInstance();
		deliverablemanager = new DeliverableManager();
	}
	
	
	/**
	 * @param project Project's data to be inserted into database table Î•Î¡Î“Î‘ 
	 * @return 1 if statement is executed successfully. Otherwise returns 0.
	 */
	public boolean insertIntoProjectTable() {	
		String sqlInsert = "INSERT INTO ΕΡΓΑ "
				+ "(ΟΝΟΜΑ_ΠΕΛΑΤΗ, "
				+ "ΤΙΤΛΟΣ, "
				+ "ΗΜ_ΕΝΑΡΞΗΣ_ΕΡΓΟΥ, "
				+ "ΗΜ_ΛΗΞΗΣ_ΕΡΓΟΥ, "
				+ "ΠΡΟΥΠΟΛΟΓΙΣΜΟΣ_ΣΥΜΒΑΣΗΣ, "
				+ "ΑΡ_ΠΡΩΤΟΚΟΛΛΟΥ, "
				+ "ΑΔΑ, "
				+ "ΟΛΟΚΛΗΡΩΜΕΝΟ) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
		PreparedStatement stm;
		try {
			stm = db.getConnection().prepareStatement(sqlInsert);
			stm.setString(1, project.getClient().getFullName());
			stm.setString(2, project.getTitle());
			stm.setDate(3, project.getStartingDates().get(project.getStartingDates().size()-1));
			stm.setDate(4, project.getDeadlineDates().get(project.getDeadlineDates().size()-1));
			stm.setString(5, String.valueOf(project.getContractBudget()));
			stm.setString(6, project.getProtocolNumber());
			stm.setString(7, project.getAda().get(project.getAda().size()-1));
			stm.setBoolean(8, project.isCompleted());
			stm.execute();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	/**
	 * @param columns
	 * @param colValues
	 */
	public void updateStringCols(ArrayList<String> columns, ArrayList<String> colValues) {
		for(int i = 0; i<columns.size(); i++) {
			if (colValues.get(i) != null) {
				String updateQuery = String.format("UPDATE ΕΡΓΑ "
						+ "set %s = ? "
						+ "WHERE ΤΙΤΛΟΣ = ?", columns.get(i));
				PreparedStatement preparedStatement;
					
				try {
					preparedStatement = db.getConnection().prepareStatement(updateQuery);
					preparedStatement.setString(1, colValues.get(i));
					preparedStatement.setString(2, this.project.getTitle());
			
					preparedStatement.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		}
	}
	
		
	public void updateIntegerCols(String colName, BigInteger value ) {
		if (value != null) {
			String updateIntQuery = "UPDATE ΕΡΓΑ "
					+ "set ΤΗΛ_ΥΠΕΥΘΥΝΟΥ = ? "
					+ "WHERE ΤΙΤΛΟΣ = ?";
			PreparedStatement stm;
			try {
				stm = db.getConnection().prepareStatement(updateIntQuery);
				
				stm.setLong(1, Long.parseLong(String.valueOf(value)));
				stm.setString(2, this.project.getTitle());
			
				stm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
	
	
	public void updateProjectEntry() {
		String sqlUpdate = "UPDATE ΕΡΓΑ SET ΗΜ_ΕΝΑΡΞΗΣ_ΕΡΓΟΥ = ?, "
				+ "ΗΜ_ΛΗΞΗΣ_ΕΡΓΟΥ = ?, "
				+ "ΠΡΟΥΠΟΛΟΓΙΣΜΟΣ_ΣΥΜΒΑΣΗΣ = ?, "
				+ "ΑΡ_ΠΡΩΤΟΚΟΛΛΟΥ = ?, "
				+ "ΑΔΑ = ?, "
				+ "ΟΛΟΚΛΗΡΩΜΕΝΟ = ? "
				+ "WHERE ΤΙΤΛΟΣ = ? AND ΟΝΟΜΑ_ΠΕΛΑΤΗ = ?";
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement(sqlUpdate);
			ps.setDate(1, project.getStartingDates().get(project.getStartingDates().size()-1));
			ps.setDate(2, project.getDeadlineDates().get(project.getDeadlineDates().size()-1));
			ps.setString(3, String.valueOf(project.getContractBudget()));
			ps.setString(4, project.getProtocolNumber());
			ps.setString(5, project.getAda().get(project.getAda().size()-1));
			ps.setBoolean(6, project.isCompleted());
			ps.setString(7, project.getTitle());
			ps.setString(8, project.getClient().getFullName());
			ps.executeUpdate();
			
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * @param fileName the name of project's file(simvasi) to store into ΕΡΓΑ table
	 * @return True if the database is updated with the fileName
	 * @throws IOException
	 */
	public boolean insertPdfIntoDB(String fileName, Project project) throws IOException {
		String sqlInsert = "UPDATE ΕΡΓΑ SET ΣΥΝΗΜΜΕΝΟ = ? WHERE ΤΙΤΛΟΣ = ?";
		try {
			PreparedStatement ps = db.getConnection().prepareStatement(sqlInsert);
			ps.setString(1, fileName);
			ps.setString(2, project.getTitle());
			ps.executeUpdate();
			System.out.println(ps.toString());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * @return an ArrayList of Projects, with their data been fetched from Database
	 */
	public ArrayList<Project> fetchProjectsFromDB(){
		String sqlSelect = "SELECT * FROM ΕΡΓΑ";
		ArrayList<Project> projectsList = new ArrayList<>();
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement(sqlSelect);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {	
				Project project = processProjectTableRow(rs);
				projectsList.add(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return projectsList;
	}
	
	
	/**
	 * @param rs
	 * @return a Project object containing a single row's data about a project.
	 */
	private Project processProjectTableRow(ResultSet rs) {
		Project project = new Project();
		
		try {
			project.setTitle(rs.getString(1));
			project.getClient().setFullName(rs.getString(2));
			project.getStartingDates().add(rs.getDate(3));
			project.getDeadlineDates().add(rs.getDate(4));
			project.setContractBudget(Double.parseDouble(rs.getString(5)));	
			project.setProtocolNumber(rs.getString(6));
			project.getAda().add(rs.getString(7));
			project.setAttachment(rs.getString(8));
			project.setCompleted(rs.getBoolean(9));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return project;
	}
	

	/**
	 * @return True if the selected Project object has been removed from DB
	 */
	public boolean removeProjectByTitle() {
		String sqlDelete = "DELETE FROM ΕΡΓΑ "
								+ "WHERE ΤΙΤΛΟΣ = ? AND ΟΝΟΜΑ_ΠΕΛΑΤΗ = ?";
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement(sqlDelete);
			ps.setString(1, project.getTitle());
			ps.setString(2, project.getClient().getFullName());
			ps.executeUpdate();
			
			ps.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public DeliverableManager getDeliverablemanager() {
		return deliverablemanager;
	}

	public void setDeliverablemanager(DeliverableManager deliverablemanager) {
		this.deliverablemanager = deliverablemanager;
	}

}
