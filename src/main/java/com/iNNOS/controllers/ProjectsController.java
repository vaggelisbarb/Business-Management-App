package com.iNNOS.controllers;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

import com.amazonaws.services.iotthingsgraph.model.SystemInstanceFilterName;
import com.iNNOS.constants.CommonConstants;
import com.iNNOS.mainengine.MainEngine;
import com.iNNOS.model.Deliverable;
import com.iNNOS.model.Project;
import com.iNNOS.model.Supervisor;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

public class ProjectsController implements Initializable{
	
    @FXML private TableView<Supervisor> supervisorsTable;
    @FXML private TableView<Deliverable> deliverablesTable;  
    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Supervisor, String> supervisorNameCol;
    @FXML private TableColumn<Supervisor, String> supervisorPhoneCol;
    @FXML private TableColumn<Supervisor, String> supervisorEmailCol;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> clientCol;
    @FXML private TableColumn<Deliverable, String> deliverableNoCol;
    @FXML private TextField deliverableDeadlineText;
    @FXML private TextField projectDeadlineText;
    @FXML private TextField clientInfoText;
    @FXML private TextField projectStartText;
    @FXML private TextField adaText;
    @FXML private Label projectTitleLabel;
    @FXML private TextField projectBudgetText;
    @FXML private TextField delvirableValueText;
    @FXML private TextField protocolNoText;
    @FXML private Pane deliverableInfoPane;
    @FXML private AnchorPane projectInfoPane;
    @FXML private CheckBox completedProjectBtn;
    @FXML private RadioButton inHouseBtn;
    @FXML private RadioButton outsourcingBtn;
    @FXML private Label deliverableTitle;
    @FXML private ListView<String> attachmentListView;
    
    private ObservableList<Project> projectsObsList = FXCollections.observableArrayList();
    private ObservableList<Supervisor> supervisorsObsList = FXCollections.observableArrayList();
    private ObservableList<Deliverable> deliverablesObsList = FXCollections.observableArrayList();
    private ObservableList<String> deliverableAttachmentsObsList = FXCollections.observableArrayList();
    
    private MainEngine mainengine;
    
    // 0-Project edit disabled, 1-Project edit enabled
    private static int editMode = 0;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Group of the 2 implementation buttons
		ToggleGroup implementationGroup = new ToggleGroup();
		inHouseBtn.setToggleGroup(implementationGroup);	outsourcingBtn.setToggleGroup(implementationGroup);
		inHouseBtn.setDisable(true);
		outsourcingBtn.setDisable(true);
		
		
		mainengine = MainEngine.getMainEngineInstance();
		
		// CellValueFactory for Projects
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));		
		clientCol.setCellValueFactory(new Callback<CellDataFeatures<Project, String>, 
				ObservableValue<String>>() {  
				@Override  
				public ObservableValue<String> call(CellDataFeatures<Project, String> data){  
					return data.getValue().getClient().getNameProperty();  
				}  
		});  
		
		
		// CellValueFactory for Supervisors
		supervisorNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
		supervisorPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		supervisorEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		
		// CellValueFactory for Deliverables
		deliverableNoCol.setCellValueFactory(new PropertyValueFactory<>("identificationCode"));
		deliverablesTable.setItems(deliverablesObsList);
		
		
		projectsTable.setOnMouseClicked((MouseEvent event) -> {
	        if(event.getButton().equals(MouseButton.PRIMARY)){
	        	projectInfoPane.setDisable(false);

	        	updateProjectsTextFields(getSelectedProject());
	        	
	        	// Remove all supervisors in the observableList, before updating with the current selected project's supervisors
	        	supervisorsObsList.removeAll(supervisorsObsList);
	        	updateSupervisorsTable();
	        	setNode(projectInfoPane);
	        	
	        	
	        	deliverableInfoPane.setDisable(false);
	        	deliverablesObsList.removeAll(deliverablesObsList);
	        	updateDeliverablesTable();
	        	
	        }
	    });
		
		
		deliverablesTable.setOnMouseClicked((MouseEvent event) -> {
			if(event.getButton().equals(MouseButton.PRIMARY)) {
				updateDeliverableTextInfo();
				setNode(deliverableInfoPane);
				
				loadDeliverableAttachments();
			}
		});
		
		//mainengine.establishDbConnection();
		updateProjectTable();
				
	}
	
	/**
	 * Fetching the attachments of the selected deliverable of a project from DB
	 */
	private void loadDeliverableAttachments() {
		deliverableAttachmentsObsList.removeAll(deliverableAttachmentsObsList);
		ArrayList<String> attachments = mainengine.fetchDeliverableAttachmentsFromDB(getSelectedDeliverable());
		for (String att : attachments) {
			deliverableAttachmentsObsList.add(att);
		}
		attachmentListView.setItems(deliverableAttachmentsObsList);
	}
	
	private void updateDeliverableTextInfo() {
		deliverableTitle.setText(getSelectedDeliverable().getDelivTitle());
		delvirableValueText.setText(String.valueOf(getSelectedDeliverable().getContractualValue()));
		deliverableDeadlineText.setText(getSelectedDeliverable().getDeadlineDate().toString());

		switch (getSelectedDeliverable().getImplementationMode()) {
		case "In House":{
			inHouseBtn.setSelected(true);
			outsourcingBtn.setSelected(false);
			break;
		}
		case "Outsourcing" : {
			inHouseBtn.setSelected(false);
			outsourcingBtn.setSelected(true);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + deliverablesTable.getSelectionModel().getSelectedItem().getImplementationMode());
		}
		
		
	}
    
    @FXML void deleteProject(ActionEvent event) {
    	Project projectToBeDeleted = projectsTable.getSelectionModel().getSelectedItem();
    	
    	Alert deleteProjectAlert = mainengine.generateAlertDialog(AlertType.INFORMATION, "Διαγραφή εγγραφής", "Είστε σίγουροι για την διαγραφή του εργου : "+projectToBeDeleted.getTitle()+" ;");
    	// Ask user to confirm the row's delete
    	Optional<ButtonType> result = deleteProjectAlert.showAndWait();
    	if(!result.isPresent())
    		// alert is exited, no button has been pressed.
    		deleteProjectAlert.close();
    	else if(result.get() == ButtonType.OK) {
    	     //oke button is pressed
        	projectsTable.getItems().remove(projectToBeDeleted);      	mainengine.establishDbConnection();
        	mainengine.removeProjectByTitle(projectToBeDeleted);	
    	}else if(result.get() == ButtonType.CANCEL)
    	    // cancel button is pressed
    		deleteProjectAlert.close();

    	
    }
    
    @FXML void refreshProjectsTable(ActionEvent event) {
    	projectsObsList.removeAll(projectsObsList);
    	updateProjectTable();
    	mainengine.generateAlertDialog(AlertType.INFORMATION, "Ανανέωση επιτυχής", "Η λίστα έργων ανανεώθηκε επιτυχώς.").show();
    }


    @FXML void uploadProjectContract(ActionEvent event) {
    	FileChooser chooser = new FileChooser();
    	File selectedFile = chooser.showOpenDialog(clientInfoText.getScene().getWindow());
    	if (selectedFile != null) {
    		Alert uploadNewContractAlert = mainengine.generateAlertDialog(AlertType.INFORMATION,
    				"Μεταφόρτωση αρχείου σύμβασης",
    				"Είστε σίγουρος ότι θέλετε να μεταφορτώσετε το αρχείο σύμβασης : "+selectedFile.getName()+ " ;");
    		
    		Optional<ButtonType> result = uploadNewContractAlert.showAndWait();
        	if(!result.isPresent())
        		// alert is exited, no button has been pressed.
        		uploadNewContractAlert.close();
        	else if(result.get() == ButtonType.OK) {
        	    //oke button is pressed
        		
        		/*	If for the selected project there is an attached file in the database,
        		* 	replace it from the base with the selected file. 
        		* 	Also delete it from the Amazon S3 bucket and upload the new one
        		*/
        		if ( (getSelectedProject() != null) && getSelectedProject().getAttachment() != null) {
        			// AMAZON S3 -> Delete previous project's contract 
        			mainengine.removeFileFromCloud(getSelectedProject().getAttachment(), CommonConstants.CONTRACTS_BUCKET_NAME, getSelectedProject().getTitle());
        			// SQL DB -> update TABLE ΕΡΓΑ (ΣΥΝΗΜΜΕΝΟ) 
        			mainengine.insertPdfIntoDB(selectedFile.getName(), getSelectedProject());
        			// AMAZON S3 -> Upload the selected file
        			mainengine.uploadFileIntoCloud(selectedFile, CommonConstants.CONTRACTS_BUCKET_NAME, getSelectedProject().getTitle());
        			
        			// Updated project entries from DB
        			updateProjectTable();
        		}
        	}else if(result.get() == ButtonType.CANCEL)
        	    // cancel button is pressed
        		uploadNewContractAlert.close();
    	}
    }
    

    @FXML void completedProjectOnAction(ActionEvent event) {
    	projectsTable.getSelectionModel().getSelectedItem().setCompleted(completedProjectBtn.isSelected());
    }
    
    @FXML void editProject(ActionEvent event) {
    	changeProjectInfoPaneEditability(true);
    	// TODO Edit disable if selected project changes. Alert must be displayed
    }
    
    
    /**
     * Upload selected file :
     * Filename -> stored into SQL DB 
     * File -> stored into Cloud
     */
    @FXML void uploadAttachment(ActionEvent event) {
    	if (isDeliverableSelected()) {
    		FileChooser chooser = new FileChooser();
    		File selectedFile = chooser.showOpenDialog(clientInfoText.getScene().getWindow());
    		if (selectedFile != null) {
    			Alert uploadNewAttachmentAlert = mainengine.generateAlertDialog(AlertType.INFORMATION,
    					"Μεταφόρτωση συνημμένο παραδοτέου",
    					"Είστε σίγουρος ότι θέλετε να μεταφορτώσετε το συνημμένο παραδοτέου: "+selectedFile.getName()+ " ;");
    		
    			Optional<ButtonType> result = uploadNewAttachmentAlert.showAndWait();
    			if(!result.isPresent())
    				// alert is exited, no button has been pressed.
    				uploadNewAttachmentAlert.close();
    			else if(result.get() == ButtonType.OK) {
    				//oke button is pressed
    				for (Iterator<String> iterator = deliverableAttachmentsObsList.iterator(); iterator.hasNext(); ) {
    					String attch = iterator.next();
    					if (attch.equals(selectedFile.getName())) {
    						//iterator.remove();
    						deliverableAttachmentsObsList.add(selectedFile.getName());
    					}	
    				}	
        		
    				getSelectedDeliverable().getAttachmentsList().add(selectedFile.getName());
    				if (mainengine.insertDeliverablesAttachmentsIntoDB(getSelectedDeliverable()))    	
    					mainengine.generateAlertDialog(AlertType.INFORMATION,
    							"Επιτυχής εισαγωγή συνημμένου",
    							"Τα συνημμένα του παραδοτέου με τίτλο : "+getSelectedDeliverable().getDelivTitle()+" εισήχθησαν στην βάση");
    				getSelectedDeliverable().getAttachmentsList().removeAll(getSelectedDeliverable().getAttachmentsList());
        		
    				mainengine.uploadFileIntoCloud(selectedFile, CommonConstants.ATTACHMENTS_BUCKET_NAME, getSelectedDeliverable().getDelivTitle());
        		
    				loadDeliverableAttachments();
    			}
        	}
    	}
    }
    
    
    private boolean isDeliverableSelected() {
    	if (getSelectedDeliverable() != null)
    		return true;
    	return false;
    }
    
    
    /**
     * Delete attachment 
     * File -> Remove from Cloud
     * Filename -> Remove from SQL DB
     */
    @FXML
    void deleteSelectedAttachment(ActionEvent event) {
    	if (isDeliverableSelected()) {
    		mainengine.removeFileFromCloud(attachmentListView.getSelectionModel().getSelectedItem(), CommonConstants.ATTACHMENTS_BUCKET_NAME, getSelectedDeliverable().getDelivTitle());
    		mainengine.removeDelAttachmentFromDB(getSelectedDeliverable(), attachmentListView.getSelectionModel().getSelectedItem());
    		deliverableAttachmentsObsList.remove(attachmentListView.getSelectionModel().getSelectedItem());
    	}
    }

    @FXML void donwloadAttachment(ActionEvent event) {
    	if (isDeliverableSelected()) {
    	
    		String  attachmentName = attachmentListView.getSelectionModel().getSelectedItem();
    	
    		if (attachmentListView.getSelectionModel().getSelectedItem() != null) {
    			Window stage = clientInfoText.getScene().getWindow();
    			// Show saveDialog and set Desktop as default directory
    			DirectoryChooser chooser = new DirectoryChooser();
    			File defaultDirectory = new File(System.getProperty("user.home") + "/Desktop");
    			chooser.setInitialDirectory(defaultDirectory);
    			File selectedDirectory = chooser.showDialog(stage);

    			// Download file drom Amazos S3 cloud to the selected directory
    			if (selectedDirectory != null) {
    				CommonConstants.LOCAL_DOWNLOAD_PATH = selectedDirectory.getAbsolutePath()+"\\";
    				mainengine.downloadFileFromCloud(attachmentName, CommonConstants.ATTACHMENTS_BUCKET_NAME, getSelectedDeliverable().getDelivTitle(), getSelectedDeliverable().getDelivTitle());
    				mainengine.generateAlertDialog(AlertType.INFORMATION, "Κατέβασμα αρχείου", "Επιτυχής λήψη του συνημμένου : "+attachmentName).showAndWait();
    		
    			}
    		}else
    			mainengine.generateAlertDialog(AlertType.INFORMATION, "Μη διαθέσιμο αρχείο", "Δεν υπάρχει διαθέσιμο αρχείο σύμβασης για το έργο").show();
    	}
    }
    
    
    /**
     * @param editable Boolean value if edit mode is enabled or disabled
     */
    private void changeProjectInfoPaneEditability(boolean editable) {
    	projectStartText.setEditable(editable);
    	projectDeadlineText.setEditable(editable);
    	protocolNoText.setEditable(editable);
    	projectBudgetText.setEditable(editable);
    	adaText.setEditable(editable);
    	completedProjectBtn.setDisable(!editable);
    }
    
    /**
     * @return True if project's textfields are edited
     */
    private boolean projectInfoUpdated() throws ParseException {
    	Project selectedProject = projectsTable.getSelectionModel().getSelectedItem();
    	if (projectStartText.getText() != selectedProject.getStartingDates().get(selectedProject.getStartingDates().size()-1).toString() 
    			|| projectDeadlineText.getText() != selectedProject.getDeadlineDates().get(selectedProject.getDeadlineDates().size()-1).toString()
    			|| projectBudgetText.getText() != String.valueOf(selectedProject.getContractBudget())
    			|| protocolNoText.getText() != selectedProject.getProtocolNumber()
    			|| adaText.getText() != selectedProject.getAda().get(selectedProject.getAda().size()-1)) {
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	    Date langDate = sdf.parse("2014/04/13");
    	    java.sql.Date sqlDate = new java.sql.Date(langDate.getTime());
    	    
    		/*selectedProject.getStartingDates().set(selectedProject.getStartingDates().size()-1,
    				new java.sql.Date(sdf.parse(
    						projectStartText.getText().substring(projectStartText.getText().indexOf("[")+1, projectStartText.getText().indexOf("]"))).getTime()));
    		
    		selectedProject.getDeadlineDates().set(selectedProject.getDeadlineDates().size()-1,
    				new java.sql.Date(sdf.parse(
    						projectStartText.getText().substring(projectDeadlineText.getText().indexOf("[")+1, projectDeadlineText.getText().indexOf("]"))).getTime()));
    		
    		*/
    		selectedProject.setContractBudget(Double.parseDouble(projectBudgetText.getText()));
    		selectedProject.setProtocolNumber(protocolNoText.getText());
    		selectedProject.getAda().set(selectedProject.getAda().size()-1, adaText.getText());
    		selectedProject.setCompleted(completedProjectBtn.isSelected());
    		return true;
    	}
    	return false;
    }
    
    @FXML void addNewDeliverable(ActionEvent event) {
 	   	if (getSelectedProject() == null) {
 	   		mainengine.generateAlertDialog(AlertType.WARNING, "Μη επιλεγμένο Έργο", "Δεν έχετε επιλέξει έργο από την Καρτέλα Έργων για να εισάγεται παραδοτέο");
 	   		return;
 	   	}
    	
    	// Create the custom dialog.
    	Dialog<Deliverable> dialog = new Dialog<>();
    	dialog.setTitle("Καρτέλα Εισαγωγής Παραδοτέου");
    	dialog.setHeaderText("Εισάγετε στοιχεία νέου Παραδοτέου");
    
    	// Set the button types.
    	ButtonType addButtonType = new ButtonType("Εισαγωγή", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

    	// Create the username and password labels and fields.
 	   	GridPane grid = new GridPane();
 	   	grid.setHgap(10);
 	   	grid.setVgap(10);
 	   	grid.setPadding(new Insets(20, 100, 10, 10));
 	   
 	   	TextField delivCode = new TextField();
 	   	delivCode.setPromptText("Δώσε κωδικό παραδοτέου");
 	   	TextField delivTitle = new TextField();
 	   	delivTitle.setPromptText("Δώσε τίτλο");
 	   	TextField contractualValue = new TextField();
 	   	contractualValue.setPromptText("Ποσό συμβατικής αξίας");
 	   	
 	   	ToggleGroup group = new ToggleGroup();
 	   	RadioButton inHouseBtn = new RadioButton();
 	   	inHouseBtn.setText("In House");
 	   	inHouseBtn.setToggleGroup(group);
 	   	inHouseBtn.setSelected(true);
 	   	RadioButton outsourcingBtn = new RadioButton();
 	   	outsourcingBtn.setText("Outsourcing");
 	   	outsourcingBtn.setToggleGroup(group);
 	   	DatePicker delivDeadline = new DatePicker();
 	   				   	
 	   	
 	   	grid.add(new Label("Κωδικός παραδοτέου"), 0, 0);
 	   	grid.add(delivCode, 1, 0);
 	   	grid.add(new Label("Τίτλος παραδοτέου"), 0, 1);
 	   	grid.add(delivTitle, 1, 1);
 	   	grid.add(new Label("Συμβατική αξία"), 0, 2);
 	   	grid.add(contractualValue, 1, 2);
 	   	grid.add(new Label("Τρόπος υλοποίησης"), 0, 3);
 	   	grid.add(inHouseBtn, 1, 3);
 	   	grid.add(outsourcingBtn, 2, 3);
 	   	grid.add(new Label("Ημερομηνία λήξης παραδοτέου"), 0, 4);
 	   	grid.add(delivDeadline, 1, 4);
 	   	
 	   	
 	   	// Enable/Disable login button depending on whether wa username was entered.
 	   	Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
 	   	addButton.setDisable(true);

 	   	// Do some validation (using the Java 8 lambda syntax).
 	   	delivCode.textProperty().addListener((observable, oldValue, newValue) -> {
 	   		addButton.setDisable(newValue.trim().isEmpty());
 	   	});

 	   	dialog.getDialogPane().setContent(grid);

 	   	// Request focus on the username field by default.
 	   	Platform.runLater(() -> delivCode.requestFocus());

 	   	// Convert the result to a username-password-pair when the login button is clicked.
 	   	dialog.setResultConverter(dialogButton -> {
 	   		if (dialogButton == addButtonType) {
 	   			Deliverable deliv = new Deliverable();
 	    	   	deliv.setIdentificationCode(delivCode.getText());
 	    	   	deliv.setDelivTitle(delivTitle.getText());
 	    	   	deliv.setDeadlineDate(java.sql.Date.valueOf(delivDeadline.getValue()));
 	    	   	deliv.setContractualValue(Double.parseDouble(contractualValue.getText()));
 	    	   	deliv.setImplementationMode(((RadioButton)group.getSelectedToggle()).getText());
 	    	   	
 	    	   	if (mainengine.insertIntoDeliverableTable(deliv, getSelectedProject()));
 	    	   		mainengine.generateAlertDialog(AlertType.INFORMATION, "Επιτυχής εισαγωγή στη βάση", deliv.toString()).show();
 	    	   	
 	    	   	
 	    	   	return deliv;
 	   		}
 	   		return null;
 	   	});
 	   	
 	   	dialog.showAndWait();
 	   	

    }


    @FXML void editDeliverable(ActionEvent event) {
    	
    }
    

    @FXML void saveDeliverable(ActionEvent event) {
    	
    }

    
    @FXML void removeDeliverable(ActionEvent event) {
    
    	mainengine.removeDeliverableFromDB(getSelectedDeliverable());
    	mainengine.generateAlertDialog(AlertType.INFORMATION,
    			"Διαγραφή Παραδοτέου",
    			"Διαγραφή παρακάτω παραδοτέου\nΤίτλος : "+getSelectedDeliverable().getDelivTitle()+"\nΚωδικός : "+getSelectedDeliverable().getIdentificationCode()).show();
    	updateDeliverablesTable();
    }
    
    	
    
    @FXML void saveProjectChanges(ActionEvent event) throws ParseException {
    	if (projectInfoUpdated()) {
    		mainengine.updateProjectEntry(getSelectedProject());
    		mainengine.generateAlertDialog(AlertType.INFORMATION,
    				"Αλλαγές στη Βάση",
    				"Το έργο με όνομα : "+getSelectedProject().getTitle()+" ανανεώθηκε επιτυχώς στη βάση!").show();
    		System.out.println(getSelectedProject().toString());
    	}
    }

    
    /**
     * A window appears for the user to select the folder in which the file will be saved.
     * App connects to AWS Cloud. Downloads the selected file.
     * @param event 'Λήψη σύμβασης' Button is pressed
     */
    @FXML void downloadContractFromCloud(ActionEvent event) {
    	String fileName = getSelectedProject().getAttachment();    	
    	
    	if (fileName != null) {
    		Window stage = clientInfoText.getScene().getWindow();
    		// Show saveDialog and set Desktop as default directory
    		DirectoryChooser chooser = new DirectoryChooser();
    		File defaultDirectory = new File(System.getProperty("user.home") + "/Desktop");
    		chooser.setInitialDirectory(defaultDirectory);
    		File selectedDirectory = chooser.showDialog(stage);

    		// Download file drom Amazos S3 cloud to the selected directory
    		if (selectedDirectory != null) {
    			CommonConstants.LOCAL_DOWNLOAD_PATH = selectedDirectory.getAbsolutePath()+"\\";
    			mainengine.downloadFileFromCloud(fileName, CommonConstants.CONTRACTS_BUCKET_NAME, getSelectedDeliverable().getDelivTitle(), CommonConstants.FOLDER_NAME);
    			mainengine.generateAlertDialog(AlertType.INFORMATION, "Κατέβασμα αρχείου", "Επιτυχής λήψη της σύμβασης : "+fileName).showAndWait();
    		}
    	}else
    		mainengine.generateAlertDialog(AlertType.INFORMATION, "Μη διαθέσιμο αρχείο", "Δεν υπάρχει διαθέσιμο αρχείο σύμβασης για το έργο").show();
    }
	
	/**
	 *  Fetching Projects from DB 
	 *  Updating javafx tableview component
	 */
	private void updateProjectTable() {		
		ArrayList<Project> projectsListFromDB= mainengine.fetchProjectsFromDB();
		if (projectsListFromDB != null) {
			for (Project project : projectsListFromDB)
				projectsObsList.add(project);
		}
		projectsTable.setItems(projectsObsList);

	}
	
	/**
	 * Fetching Supervisors of the selected Project from DB
	 * Updating javafx tableview component
	 */
	private void updateSupervisorsTable() {
		ArrayList<Supervisor> supervisorList = mainengine.fetchSupervisorsOfProject(getSelectedProject());
		if (supervisorList != null) {
			for (Supervisor sv : supervisorList)
				supervisorsObsList.add(sv);
		}
		supervisorsTable.setItems(supervisorsObsList);
	}
	
	/**
	 * Fetching Deliverable objects of the selected Project from ΠΑΡΑΔΟΤΕΑ table of DB 
	 * Updating javafx tableview component
	 */
	public void updateDeliverablesTable() {
		deliverablesObsList.removeAll(deliverablesObsList);
		ArrayList<Deliverable> deliverables = mainengine.fetchProjectDeliverablesFromDB(projectsTable.getSelectionModel().getSelectedItem());
		if (deliverables != null) {
			for (Deliverable dv : deliverables)
				deliverablesObsList.add(dv);
		}
		deliverablesTable.setItems(deliverablesObsList);
	}
	
	
	private void updateProjectsTextFields(Project project) {
		projectTitleLabel.setText(project.getTitle());
		clientInfoText.setText(project.getClient().getFullName());
		projectStartText.setText(project.getStartingDates().toString());
		projectDeadlineText.setText(project.getDeadlineDates().toString());
		projectBudgetText.setText(String.valueOf(project.getContractBudget()));
		adaText.setText(project.getAda().get(0));
		protocolNoText.setText(project.getProtocolNumber());
		completedProjectBtn.setSelected(project.isCompleted());
	}

	
	//Set selected node to a content holder
    private void setNode(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(200));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }
	
    private Project getSelectedProject() {
    	return projectsTable.getSelectionModel().getSelectedItem();
    }
    
    private Deliverable getSelectedDeliverable() {
    	return deliverablesTable.getSelectionModel().getSelectedItem();
    }
	

}
