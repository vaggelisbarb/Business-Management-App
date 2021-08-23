package com.iNNOS.queryprocessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iNNOS.model.*;

public class SupervisorManager {
	private Database db;
	private Project project;
	private ArrayList<Supervisor> supervisors;
	
	public SupervisorManager() {
		db = Database.getDatabaseInstance();
		project = new Project();
		supervisors = new ArrayList<>();
	}

	public SupervisorManager(Project project) {
		db = Database.getDatabaseInstance();
		this.project = project;
		this.supervisors = project.getSupervisors();
	}
	
	
	/**
	 * Checks if the list of project supervisors is empty. If not, their data are inserted into the DB table
	 */
	public void insertSupervisorsIntoDB() {
		String sqlIsertSupervisor = "INSERT INTO ΥΠΕΥΘΥΝΟΙ (ΤΙΤΛΟΣ_ΕΡΓΟΥ, ΗΜ_ΕΝΑΡΞΗΣ_ΕΡΓΟΥ, ΟΝΟΜΑ_ΥΠΕΥΘΥΝΟΥ, ΤΗΛ_ΥΠΕΥΘΥΝΟΥ, email_ΥΠΕΥΘΥΝΟΥ) "
				+ "VALUES (?, ?,?, ?, ?)";
		if (project.getSupervisors().size() != 0) {
			for (int i=0; i<project.getSupervisors().size(); i++) {
				try {
					PreparedStatement ps = db.getConnection().prepareStatement(sqlIsertSupervisor);
					ps.setString(1, project.getTitle());
					ps.setDate(2, project.getStartingDates().get(project.getStartingDates().size()-1));
					ps.setString(3, project.getSupervisors().get(i).getFullName());
					ps.setString(4,  project.getSupervisors().get(i).getPhoneNumber());
					ps.setString(5, project.getSupervisors().get(i).getEmail());
					ps.execute();
					System.out.printf("_______\nSupervisor {%s} of the project {%s} was inserted into ΥΠΕΥΘΥΝΟΙ table\n______",
							project.getSupervisors().get(i).getFullName(), project.getTitle());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public ArrayList<Supervisor> fetchSupervisorsOfProject(Project project) {
		String sqlSelect = "SELECT * FROM ΥΠΕΥΘΥΝΟΙ"
				+ " WHERE ΤΙΤΛΟΣ_ΕΡΓΟΥ = ? AND ΗΜ_ΕΝΑΡΞΗΣ_ΕΡΓΟΥ = ?";
		ArrayList<Supervisor> supervisorsList = new ArrayList<>();
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement(sqlSelect);
			ps.setString(1, project.getTitle());
			ps.setDate(2, project.getStartingDates().get(project.getStartingDates().size()-1));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {	
				Supervisor supervisor = processSupervisorFromTable(rs);
				supervisorsList.add(supervisor);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return supervisorsList;
	}
	
	
	/**
	 * @param rs
	 * @return Supervisor object from a single entry of the table
	 */
	private Supervisor processSupervisorFromTable(ResultSet rs) {
		Supervisor supervisor = new Supervisor();
		
		try {
			System.out.println(rs.getString(3));
			supervisor.setFullName(rs.getString(3));
			supervisor.setPhoneNumber(rs.getString(4));
			supervisor.setEmail(rs.getString(5));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return supervisor;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ArrayList<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(ArrayList<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}
	
	
	
}
