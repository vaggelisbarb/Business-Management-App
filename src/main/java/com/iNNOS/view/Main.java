package com.iNNOS.view;

import com.iNNOS.controllers.FXMLDocumentController;
import com.iNNOS.mainengine.MainEngine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		MainEngine.getMainEngineInstance().establishDbConnection();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/FXMLDocument.fxml"));
		FXMLDocumentController controller = new FXMLDocumentController();
		controller.setGetHostController(getHostServices());
		loader.setController(controller);
		
		MainEngine.getMainEngineInstance().initHostServices(getHostServices());
		
		Parent root = loader.load();
		
		//Image innosLogo = new Image("/view/graphics/innos_logo.jpg");
		
		Scene sc = new Scene(root);

		stage.setTitle("Σύστημα Διαχείρησης Έργων iNNOS");
		stage.setScene(sc);
		//stage.setResizable(false);
		//stage.getIcons().add(innosLogo);
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
