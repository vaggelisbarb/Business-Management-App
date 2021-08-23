package com.iNNOS.queryprocessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.iNNOS.model.*;

public class ClientManager {
	private Database db;
	private Client client;
	
	
	public ClientManager() {
		db = Database.getDatabaseInstance();
	}
	
	public ClientManager(Client client) {
		this.client = client;
		db = Database.getDatabaseInstance();
	}
	
	
	/**
	 * @return true if client data are inserted into DB successfully
	 */
	public boolean insertEntryIntoClientTable() {
		String sqlInsert = "INSERT INTO ΠΕΛΑΤΕΣ VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement stm;
			stm = db.getConnection().prepareStatement(sqlInsert);
			
			stm.setString(1, this.client.getFullName());
			stm.setString(2, this.client.getAfm());
			stm.setString(3, this.client.getAddress());
			stm.setString(4, this.client.getMunicipality());
			stm.setString(5, String.valueOf(this.client.getPhoneNumber()));
			stm.setString(6, this.client.getWebpage());
			stm.setString(7, this.client.getDetails());
			
			stm.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			db.disconnect();
		}
		
		return false;
	}
	
	
	public ArrayList<Client> fetchClientsFromDB(){
		String sqlSelect = "SELECT * FROM ΠΕΛΑΤΕΣ";
		ArrayList<Client> clientList = new ArrayList<>();
		
		try {
			PreparedStatement ps = db.getConnection().prepareStatement(sqlSelect);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Client client = processClientTableRow(rs);
				clientList.add(client);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return clientList;
				
	}
	
	
	/**
	 * @param afm String of afm to be searched
	 * @return a Client object with this afm 
	 */
	public Client fetchClientByAfm(String afm) {
		String sqlSelect = "SELECT * FROM ΠΕΛΑΤΕΣ WHERE ΑΦΜ = ?";
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = db.getConnection().prepareStatement(sqlSelect);
			ps.setString(1, afm);
			rs = ps.executeQuery();
			if (rs.next())
				return processClientTableRow(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Client processClientTableRow(ResultSet rs) {
		Client client = new Client();
		
		try {
			client.setFullName(rs.getString(1));
			client.setAfm(rs.getString(2));
			client.setAddress(rs.getString(3));
			client.setMunicipality(rs.getString(4));
			client.setPhoneNumber(Long.parseLong(rs.getString(5)));
			client.setWebpage(rs.getString(6));
			client.setDetails(rs.getString(7));
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return client;
		
	}
	
	public boolean removeClientEntryFromDB(Client client) {
		String sqlDelete = "DELETE FROM ΠΕΛΑΤΕΣ WHERE ΟΝΟΜΑ = ? AND ΑΦΜ = ?";
		
		PreparedStatement ps;
		try {
			ps = db.getConnection().prepareStatement(sqlDelete);
			ps.setString(1, client.getFullName());
			ps.setString(2, client.getAfm());
			ps.executeUpdate();
			
			ps.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}
	
	public boolean updateClientEntry(Client client) {
		String sqlUpdate = "UPDATE ΠΕΛΑΤΕΣ SET ΔΙΕΥΘΥΝΣΗ = ?, ΔΗΜΟΣ = ?, ΤΗΛΕΦΩΝΟ_ΕΠΙΚΟΙΝΩΝΙΑΣ = ?, ΙΣΤΟΣΕΛΙΔΑ = ?, ΠΛΗΡΟΦΟΡΙΕΣ = ? WHERE ΟΝΟΜΑ = ? AND ΑΦΜ = ?";
	
		PreparedStatement ps;
		try {
			ps = db.getConnection().prepareStatement(sqlUpdate);
			ps.setString(1, client.getAddress());
			ps.setString(2, client.getMunicipality());
			ps.setString(3, String.valueOf(client.getPhoneNumber()));
			ps.setString(4, client.getWebpage());
			ps.setString(5, client.getDetails());
			ps.setString(6, client.getFullName());
			ps.setString(7, client.getAfm());
			
			ps.executeUpdate();
			ps.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	
}
