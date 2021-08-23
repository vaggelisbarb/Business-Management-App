package com.iNNOS.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.iNNOS.mainengine.MainEngine;

import javafx.animation.FadeTransition;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable{

   
    @FXML private AnchorPane holderPane;

    @FXML private Label navigateLbl;
    
    AnchorPane newProject, newClient, clients, projects, infoPane;
	HostServices hostServices ;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {
			newProject = FXMLLoader.load(getClass().getResource("/main/resources/NewProject.fxml"));
			clients = FXMLLoader.load(getClass().getResource("/main/resources/Clients.fxml"));
			newClient = FXMLLoader.load(getClass().getResource("/main/resources/NewClient.fxml"));
			projects = FXMLLoader.load(getClass().getResource("/main/resources/Projects.fxml"));
			infoPane = FXMLLoader.load(getClass().getResource("/main/resources/InnosInfo.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

        navigateLbl.setOnMouseClicked((MouseEvent event) -> {
        	if (event.getButton().equals(MouseButton.PRIMARY)) {
        		setNode(infoPane);
        	}
        });
        
		setNode(infoPane);
		
	}
	

	public void setGetHostController(HostServices hostServices)
	{
	    this.hostServices = hostServices;
	}       
	
	//Set selected node to a content holder
    private void setNode(Node node) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add((Node) node);

        FadeTransition ft = new FadeTransition(Duration.millis(600));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }
    

    @FXML
    void newProjectOnAction(ActionEvent event) {
    	setNode(newProject);
    }
    
    @FXML
    void addNewClientOnAction(ActionEvent event) {
    	setNode(newClient);
    }
    
    @FXML
    void clientsOnAction(ActionEvent event) {
    	setNode(clients);
    }

    @FXML
    void projectsOnAction(ActionEvent event) {
    	setNode(projects);
    }
}
