package com.iNNOS.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import com.iNNOS.constants.CommonConstants;
import com.iNNOS.controllers.HostServiceProvider;
import com.iNNOS.mainengine.MainEngine;


public class InnosInfoController implements Initializable {
	@FXML private Hyperlink webHP;
    @FXML private Hyperlink linkedinHP;
    @FXML private Hyperlink viberHP;
    @FXML private GridPane socialMediaPane;
     
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        List<Hyperlink> links = new ArrayList<>();        
        links.add(webHP); 
        links.add(linkedinHP); 
        links.add(viberHP); 
          

	}
	
	 @FXML
     void linkedinOnAction(ActionEvent event) {
		 System.out.printf("Linkedin Hyperlink clicked! Opening browser to : %s\n\n", CommonConstants.SOCIAL_MEDIA_URLS[0]);
		 MainEngine.getMainEngineInstance().getHostServices().showDocument(CommonConstants.SOCIAL_MEDIA_URLS[0]);
	 }
	 
	 @FXML
	 void websiteOnAction(ActionEvent event) {
		 System.out.printf("Website Hyperlink clicked! Opening browser to : %s\n\n", CommonConstants.SOCIAL_MEDIA_URLS[2]);
		 MainEngine.getMainEngineInstance().getHostServices().showDocument(CommonConstants.SOCIAL_MEDIA_URLS[2]);
	 }
	
	
}
