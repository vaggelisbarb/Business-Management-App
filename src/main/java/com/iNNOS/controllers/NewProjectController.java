package com.iNNOS.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.iNNOS.constants.CommonConstants;
import com.iNNOS.mainengine.MainEngine;
import com.iNNOS.model.Client;
import com.iNNOS.model.Project;
import com.iNNOS.model.Supervisor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

public class NewProjectController implements Initializable{
	
    @FXML private Label attachmentInfoLabel;
    @FXML private TableColumn<Supervisor, String> supervisorEmailCol;
    @FXML private TableColumn<Supervisor, String> supervisorNameCol;
    @FXML private TableColumn<Supervisor, String> supervisorPhoneCol;
    @FXML private TableView<Supervisor> supervisorTable; 
    @FXML private TextField budgetProject;
    @FXML private TextField projectTitle;
    @FXML private TextField protocolText;
    @FXML private Label attachField;
    @FXML private TextField adaText;
    @FXML private DatePicker deadlineProjectDate;
    @FXML private DatePicker startProjectDate; 
    @FXML private ChoiceBox<String> chooseClientChoiseBox;
    
	FileChooser chooser;
	ObservableList<Supervisor> observableList = FXCollections.observableArrayList();
	File uploadedFile;
	
	MainEngine eng;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		chooser = new FileChooser();

		supervisorNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
		supervisorPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		supervisorEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		supervisorTable.setItems(observableList);

		eng = MainEngine.getMainEngineInstance();
		
		ArrayList<Client> clients = eng.fetchClientsFromDB();
		ObservableList<String> clientsObsList = null;
		if (clients!=null) {
			clientsObsList = FXCollections.observableArrayList();
			for (Client cl : clients)
				clientsObsList.add(cl.getFullName()+", ΑΦΜ: "+cl.getAfm()+", ΔΗΜΟΣ: "+cl.getMunicipality());
		}
		chooseClientChoiseBox.setItems(clientsObsList);
	}
	
    @FXML void verifyProjectOnAcion(ActionEvent event) throws IOException {
    	
    	if (validateKeyFields()) {
        	// Creating new Project object based on the given field values
        	Project projectToAdd = new Project();
        	projectToAdd.setTitle(projectTitle.getText());
        	projectToAdd.getAda().add(adaText.getText());
        	projectToAdd.getStartingDates().add(getStartDate(event));
        	projectToAdd.getDeadlineDates().add(getDeadlineDate(event));
        	projectToAdd.setProtocolNumber(protocolText.getText()); 
        	projectToAdd.setContractBudget(Double.parseDouble(budgetProject.getText()));
        	
        	// Choise box selection
        	String selectedClientInfo = chooseClientChoiseBox.getSelectionModel().getSelectedItem();
        	
        	// Get client's AFM
        	String result = selectedClientInfo.substring(selectedClientInfo.indexOf(", ΑΦΜ: ") + 7, selectedClientInfo.indexOf(", ΔΗΜΟΣ"));
        	
    		// A connection between app & database established
    		eng.establishDbConnection();
    		// Selected client is been fetched from DB with it's data
        	Client client = eng.fetchClientByAfm(result);
        	projectToAdd.setClient(client);
        	
        	// List with Supervisor objects (Null is acceptable)
        	ArrayList<Supervisor> supervisorsList= checkForSupervisors();
        	if (supervisorsList != null) {
        		projectToAdd.setSupervisors(supervisorsList);
        		System.out.println(projectToAdd.getSupervisors().get(0).getFullName());
        	}
        		
        	projectToAdd.setAttachment(uploadedFile.getName());
        	
    		// Store project data into Database
    		if (eng.createNewProject(projectToAdd)) {
    			eng.generateAlertDialog(AlertType.CONFIRMATION,
    					"Επιτυχής ενημέρωση Βάσης",
    					"Το έργο {"+projectToAdd.getTitle()+"} εισήχθηκε επιτυχώς στο σύστημα!").showAndWait();
    			if (uploadedFile != null)
    				eng.uploadFileIntoCloud(uploadedFile, CommonConstants.CONTRACTS_BUCKET_NAME, projectToAdd.getTitle());
    		}
    		
    	}
    }
    
    /**
     * @return True if the following fields are fillen : Title, starting date, deadline date, client info
     */
    private boolean validateKeyFields() {
    	if (projectTitle.getText().equals("") || startProjectDate.getEditor().getText().equals("") || deadlineProjectDate.getEditor().getText().equals("") ||
    			chooseClientChoiseBox.getSelectionModel().getSelectedItem().equals("")) {
    		eng.generateAlertDialog(AlertType.WARNING,
    				"Κενά Πεδία",
    				"Συμπληρώστε τα πεδία `Τίτλος Έργου` ή/και `Ημ/νια Έναρξης-Λήξης Έργου` ή/και Επιλέξτε πελάτη").showAndWait();
    		return false;
    	}
    	return true;
    }
    
   
   @FXML Date getStartDate(ActionEvent event) {
	   return Date.valueOf(startProjectDate.getValue());
   }
    

   @FXML Date getDeadlineDate(ActionEvent event) {
	   return Date.valueOf(deadlineProjectDate.getValue());
   }
    
   @FXML void cleanProjectOnAction(ActionEvent event) {
    	projectTitle.setText("");
    	startProjectDate.getEditor().clear();
    	deadlineProjectDate.getEditor().clear();
    	budgetProject.setText("");
    	protocolText.setText("");
    	adaText.setText("");
    	
    	deleteChosenSupervisors(event);
   }

   @FXML void loadAttachmentOnAction(ActionEvent event) {
	   Window stage = projectTitle.getScene().getWindow();
	   uploadedFile = chooser.showOpenDialog(stage);
	   if (uploadedFile != null)
		   attachmentInfoLabel.setText(uploadedFile.getName());
	   else
		   attachmentInfoLabel.setText("Δεν έχει επιλεγεί συννημένο");  
   }

   @FXML void cancelAttachmentOnAction(ActionEvent event) {
	   
   }
   
   @FXML void AddNewSupervisor(ActionEvent event) {
	// Create the custom dialog.
	   Dialog<Pair<String, String>> dialog = new Dialog<>();
	   dialog.setTitle("Καρτέλα Εισαγωγής Υπευθύνου");
	   dialog.setHeaderText("Εισάγετε στοιχεία Υπεύθυνου έργου");

	   // Set the button types.
	   ButtonType addButtonType = new ButtonType("Εισαγωγή", ButtonData.OK_DONE);
	   dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

	   // Create the username and password labels and fields.
	   GridPane grid = new GridPane();
	   grid.setHgap(10);
	   grid.setVgap(10);
	   grid.setPadding(new Insets(20, 150, 10, 10));

	   TextField name = new TextField();
	   name.setPromptText("Δώσε ονομ/νυμο");
	   TextField phoneNum = new TextField();
	   phoneNum.setPromptText("Στοιχεία τηλεφώνου");
	   TextField email = new TextField();
	   email.setPromptText("Στοιχεία ηλ.διεύθυνσης");

	   grid.add(new Label("Ονοματεπώνυμο:"), 0, 0);
	   grid.add(name, 1, 0);
	   grid.add(new Label("Τηλ. Επικοινωνίας"), 0, 1);
	   grid.add(phoneNum, 1, 1);
	   grid.add(new Label("E-mail"), 0, 2);
	   grid.add(email, 1, 2);

	   // Enable/Disable login button depending on whether a username was entered.
	   Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
	   addButton.setDisable(true);

	   // Do some validation (using the Java 8 lambda syntax).
	   name.textProperty().addListener((observable, oldValue, newValue) -> {
	       addButton.setDisable(newValue.trim().isEmpty());
	   });

	   dialog.getDialogPane().setContent(grid);

	   // Request focus on the username field by default.
	   Platform.runLater(() -> name.requestFocus());

	   // Convert the result to a username-password-pair when the login button is clicked.
	   dialog.setResultConverter(dialogButton -> {
	       if (dialogButton == addButtonType) {
	    	   Supervisor supervisorToAdd = new Supervisor(name.getText(), phoneNum.getText(), email.getText());
	    	   supervisorTable.getItems().add(supervisorToAdd);	       
	       }
	       return null;
	   });

	   Optional<Pair<String, String>> result = dialog.showAndWait();


   }

   @FXML void deleteChosenSupervisors(ActionEvent event) {
	   observableList.remove(supervisorTable.getSelectionModel().getSelectedIndex());
   }

   @FXML void deleteAllSupervisors(ActionEvent event) {
	   observableList.removeAll(observableList);
   }
   
   /**
    * @return an Arraylist of Supervisor objects that exist in the table of supervisors of the project
    */
   private ArrayList<Supervisor> checkForSupervisors() {
	   if (!supervisorTable.getItems().isEmpty()) {
		   ArrayList<Supervisor> svList = new ArrayList<>();
		   for (Supervisor sv : supervisorTable.getItems())
			   svList.add(sv);
		   return svList;
	   }
	   return null;
   }

}
