package com.iNNOS.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.iNNOS.mainengine.MainEngine;
import com.iNNOS.model.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class NewClientController implements Initializable{
	
    @FXML
    private TextArea clientDetails;

    @FXML
    private TextField phoneNumber;

    @FXML
    private TextField address;

    @FXML
    private TextField municipality;

    @FXML
    private TextField fullName;

    @FXML
    private TextField afm;

    @FXML
    private TextField webpage;

    @FXML
    private TextField email;
    
    MainEngine eng;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	    
    @FXML
    void addNewClientOnAction(ActionEvent event) {
    	if (validateKeyFields()) {
    		// Creating Client object by extracting data from the UI's textfields
    		String clientName = fullName.getText();
    		String clientAfm = afm.getText();
    		String clientAddress = address.getText();
    		String clientDimos = checkField(municipality.getText());
    		Long phone = Long.parseLong(phoneNumber.getText());
    		String page = checkField(webpage.getText());
    		String details = checkField(clientDetails.getText());
    		
    		Client clientToAdd = new Client(clientName, clientAfm, clientAddress, clientDimos, phone, page, details);
    		
    		// A connection between app & database established
    		eng = MainEngine.getMainEngineInstance();
    		eng.establishDbConnection();
    		
    		if (eng.createNewClient(clientToAdd)) {
    			eng.generateAlertDialog(AlertType.INFORMATION,
    					"Νέος πελάτης εισήχθηκε στη Βάση",
    					"Ο πελάτης : "+clientName+"\nΑ.Φ.Μ : "+clientAfm+"\nεισήχθηκε επιτυχώς στο σύστημα").showAndWait();
    			removeTextOnAction(event);
    			
    		}
    	}
    }

    @FXML
    void removeTextOnAction(ActionEvent event) {
    	fullName.setText("");
    	afm.setText("");
    	address.setText("");
    	municipality.setText("");
    	phoneNumber.setText("");
    	email.setText("");
    	webpage.setText("");
    	clientDetails.setText("");
    }
    
    private String checkField(String field) {
    	if (field.equals(""))
    		return null;
    	return field;
    }
    
    private boolean validateKeyFields() {
    	if (fullName.getText().equals("") || afm.getText().equals("")) {
    		eng.generateAlertDialog(AlertType.WARNING,
    				"Κενά Πεδία",
    				"Παρακαλώ εισάγεται Ονοματεπώνυμο ή/και Α.Φ.Μ πελάτη").showAndWait();
    		return false;
    	}
    	return true;
    }
    
	
}
