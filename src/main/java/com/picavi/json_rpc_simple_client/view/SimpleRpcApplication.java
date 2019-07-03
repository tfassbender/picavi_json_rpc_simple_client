package com.picavi.json_rpc_simple_client.view;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimpleRpcApplication extends Application {
	
	private SimpleRpcController controller;
	
	public static final String APPLICATION_NAME = "Simple RPC Client";
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			URL fxmlUrl = getClass().getResource("SimpleRpcClient.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			controller = new SimpleRpcController();
			fxmlLoader.setController(controller);
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.setTitle(APPLICATION_NAME);
			primaryStage.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}