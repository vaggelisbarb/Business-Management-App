package com.iNNOS.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.iNNOS.mainengine.MainEngine;
import com.iNNOS.model.Client;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class ClientsController implements Initializable{
	
    @FXML
    private TableColumn<Client, String> addressCol;

    @FXML
    private TableColumn<Client, String> detailsCol;

    @FXML
    private TableColumn<Client, String> nameCol;

    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, String> municipalityCol;

    @FXML
    private TableColumn<Client, Long> phoneNumCol;

    @FXML
    private TableColumn<Client, String> webpageCol;

    @FXML
    private TableColumn<Client, String> afmCol;
    
    @FXML
    private TextArea detailsTextField;

    @FXML
    private TextField phoneNumTextField;

    @FXML
    private TextField webpageTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField afmTextField;

    @FXML
    private TextField municipalityTextField;
    
    @FXML
    private TextField currentProjectTextField;
    
    @FXML
    private Label fullNameLabel;
    
    @FXML
    private AnchorPane infoPane;
    
	MainEngine mainengine;
	ObservableList<Client> observableList = FXCollections.observableArrayList();
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainengine = MainEngine.getMainEngineInstance();
		
		clientsTable.setOnMouseClicked((MouseEvent event) -> {
	        if(event.getButton().equals(MouseButton.PRIMARY)){
	        	infoPane.setDisable(false);

	        	updateInfo(getSelectedClient());
	        	setNode(infoPane);
	        }
	    });


		nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
		afmCol.setCellValueFactory(new PropertyValueFactory<>("afm"));
		
		updateClientsTable();
	}
	
	
	private void updateInfo(Client client) {
		fullNameLabel.setText(client.getFullName());
		afmTextField.setText(client.getAfm());
		addressTextField.setText(client.getAddress());
		phoneNumTextField.setText(String.valueOf(client.getPhoneNumber()));
		municipalityTextField.setText(client.getMunicipality());
		webpageTextField.setText(client.getWebpage());
		detailsTextField.setText(client.getDetails());
	}
	
	private Client updateClientFromTextFields() {
		Client clientToBeUpdated = new Client();
		clientToBeUpdated.setFullName(fullNameLabel.getText());
		clientToBeUpdated.setAfm(afmTextField.getText());
		clientToBeUpdated.setAddress(addressTextField.getText());
		clientToBeUpdated.setMunicipality(municipalityTextField.getText());
		clientToBeUpdated.setPhoneNumber(Long.parseLong(phoneNumTextField.getText()));
		clientToBeUpdated.setWebpage(webpageTextField.getText());
		clientToBeUpdated.setDetails(detailsTextField.getText());
		
		return clientToBeUpdated;
	}

	private void updateClientsTable() {		
		ArrayList<Client> clientsList = mainengine.fetchClientsFromDB();
		clientsTable.setItems(observableList);
		if (clientsList != null) {
			for (Client client : clientsList)
				clientsTable.getItems().add(client);
		}
		
	}
	
    @FXML
    void refreshClientsTable(ActionEvent event) {
    	observableList.removeAll(observableList);
    	updateClientsTable();
    	mainengine.generateAlertDialog(AlertType.INFORMATION, "Ανανέωση επιτυχής", "Η λίστα πελατών ανανεώθηκε επιτυχώς.").show();
    }
	
    @FXML
    void editClient(ActionEvent event) {
    	afmTextField.setEditable(true);
    	addressTextField.setEditable(true);
    	municipalityTextField.setEditable(true);
    	phoneNumTextField.setEditable(true);
    	webpageTextField.setEditable(true);
    	detailsTextField.setEditable(true);
    }
    

    @FXML
    void saveClientInfo(ActionEvent event) {
    	Alert saveClientAlert = mainengine.generateAlertDialog(AlertType.CONFIRMATION, "Επιβεβαίωση αποθήκευσης", "Είστε βέβαιοι ότι θέλετε να αποθηκέυσετε τις αλλαγές για τον πελάτη "+clientsTable.getSelectionModel().getSelectedItem().getFullName()+ " ;");
    	
    	// Ask user to confirm the row's delete
    	Optional<ButtonType> result = saveClientAlert.showAndWait();
    	if(!result.isPresent())
    	    // alert is exited, no button has been pressed.
    		saveClientAlert.close();
    	else if(result.get() == ButtonType.OK) {
    	     //oke button is pressed
        	mainengine.establishDbConnection();
        	mainengine.updateClientEntry(updateClientFromTextFields());
    	}else if(result.get() == ButtonType.CANCEL)
    	    // cancel button is pressed
    		saveClientAlert.close();
    }


    @FXML
    void deleteClient(ActionEvent event) {
    	Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

    	Alert deleteClientAlert = mainengine.generateAlertDialog(AlertType.INFORMATION, "Διαγραφή εγγραφής", "Είστε σίγουροι για την διαγραφή του πελάτη : "+selectedClient.getFullName()+ " ;");
    	
    	// Ask user to confirm the row's delete
    	Optional<ButtonType> result = deleteClientAlert.showAndWait();
    	if(!result.isPresent())
    	    // alert is exited, no button has been pressed.
    		deleteClientAlert.close();
    	else if(result.get() == ButtonType.OK) {
    	     //oke button is pressed
        	clientsTable.getItems().remove(selectedClient);
        	mainengine.establishDbConnection();
        	mainengine.removeClientEntryFromDB(selectedClient);	
    	}else if(result.get() == ButtonType.CANCEL)
    	    // cancel button is pressed
    		deleteClientAlert.close();

    	
    }
    
	//Set selected node to a content holder
    private void setNode(Node node) {
        //holderPane.getChildren().clear();
        //holderPane.getChildren().add((Node) node);

        FadeTransition ft = new FadeTransition(Duration.millis(200));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }
	
    
    public Client getSelectedClient() {
    	return clientsTable.getSelectionModel().getSelectedItem();
    }
}
