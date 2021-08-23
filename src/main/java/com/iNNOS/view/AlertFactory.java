package com.iNNOS.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertFactory {
	
	public AlertFactory() {}
	
	public Alert generateAlertDialog(AlertType type, String title, String context) {
		Alert newAlert = new Alert(type);
		newAlert.setTitle(title);
		newAlert.setHeaderText(null);
		newAlert.setContentText(context);
		return newAlert;
	}

	
}
