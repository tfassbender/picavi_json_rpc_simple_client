package com.picavi.json_rpc_simple_client.view;

import java.net.URL;
import java.util.ResourceBundle;

import com.picavi.json_rpc_simple_client.client.SimpleRpcClient;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SimpleRpcController implements Initializable {
	
	@FXML
	private TextField textFieldUserName;
	@FXML
	private PasswordField passwordFieldPassword;
	@FXML
	private Button buttonLoginLogout;
	
	@FXML
	private Button buttonSendRequest;
	@FXML
	private TextField textFieldIdentification;
	
	@FXML
	private TextArea textAreaRequest;
	@FXML
	private TextArea textAreaResponse;
	
	private BooleanProperty loggedIn = new SimpleBooleanProperty(false, "LoggedIn");
	
	private SimpleRpcClient client;
	
	public SimpleRpcController() {
		client = new SimpleRpcClient();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//enable or disable textfields and buttons when the user is logged in or not
		buttonSendRequest.disableProperty().bind(loggedIn.not());
		textFieldIdentification.disableProperty().bind(loggedIn.not());
		textFieldUserName.disableProperty().bind(loggedIn);
		passwordFieldPassword.disableProperty().bind(loggedIn);
		
		//change the button text when the user is logged in
		buttonLoginLogout.textProperty().bind(Bindings.createStringBinding(() -> {
			String displayedText;
			if (loggedIn.get()) {
				displayedText = "Logout";
			}
			else {
				displayedText = "Login";
			}
			return displayedText;
		}, loggedIn));
		
		//set the actions for the buttons
		buttonLoginLogout.setOnAction(e -> loginOrLogout());
		buttonSendRequest.setOnAction(e -> sendRequest());
	}
	
	private void loginOrLogout() {
		//send the request via the client
		boolean loginSuccessful = true;
		String username = textFieldUserName.getText();
		try {
			if (loggedIn.get()) {
				String passwd = passwordFieldPassword.getText();
				client.login(username, passwd);
			}
			else {
				client.logout(username);
			}
		}
		catch (IllegalStateException ise) {
			loginSuccessful = false;
			String errorType;
			if (loggedIn.get()) {
				errorType = "logout";
			}
			else {
				errorType = "login";
			}
			
			DialogUtils.showErrorDialog(String.format("Problems with %s", errorType), String.format("The %s on the server failed", errorType),
					String.format("The %s was not successful for unknown reasons", errorType));
		}
		
		//change the logged in state 
		if (loginSuccessful) {
			loggedIn.set(!loggedIn.get());
		}
	}
	
	private void sendRequest() {
		String ident = textFieldIdentification.getText();
		try {
			client.getPickList(ident);
		}
		catch (IllegalStateException e) {
			DialogUtils.showErrorDialog("A problem occured", "Picklist couldn't be received",
					"The picklist couldn't be received from the server for unknown reasons");
		}
	}
}