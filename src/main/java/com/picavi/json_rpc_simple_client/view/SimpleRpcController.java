package com.picavi.json_rpc_simple_client.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picavi.json_rpc_server.model.JsonRpcLoginAnswer;
import com.picavi.json_rpc_server.model.JsonRpcResponse;
import com.picavi.json_rpc_simple_client.client.SimpleRpcClient;
import com.picavi.json_rpc_simple_client.client.SimpleRpcClientCommunicationListener;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SimpleRpcController implements Initializable, SimpleRpcClientCommunicationListener {
	
	@FXML
	private TextField textFieldUserName;
	@FXML
	private TextField textFieldSessionId;
	@FXML
	private PasswordField passwordFieldPassword;
	@FXML
	private Button buttonLoginLogout;
	
	@FXML
	private CheckBox checkboxAsync;
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
		//add this controller as listener so the communication can be displayed
		client.addCommunicationListener(this);
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
	
	/**
	 * Login or logout the user by sending the request to the server
	 */
	private void loginOrLogout() {
		//send the request via the client
		boolean loginSuccessful = true;
		try {
			if (!loggedIn.get()) {
				String username = textFieldUserName.getText();
				String passwd = passwordFieldPassword.getText();
				JsonRpcResponse loginResponse = client.login(username, passwd);
				processLogin(loginResponse);
			}
			else {
				String sessionId = textFieldSessionId.getText();
				JsonRpcResponse logoutResponse = client.logout(sessionId);
				loginSuccessful = processLogout(logoutResponse);
			}
		}
		catch (IllegalStateException ise) {
			//handle exceptions (that occur when the request fails for any reason)
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
			
			ise.printStackTrace();
		}
		
		//change the logged in state (if the login/logout was successful)
		if (loginSuccessful) {
			loggedIn.set(!loggedIn.get());
		}
	}
	
	/**
	 * Process the login response and throw an {@link IllegalStateException} if the response is not OK.
	 */
	private void processLogin(JsonRpcResponse loginResponse) throws IllegalStateException {
		try {
			JsonRpcLoginAnswer loginAnswer = (JsonRpcLoginAnswer) loginResponse.getResult();
			textFieldSessionId.setText(loginAnswer.getSessionId());
		}
		catch (ClassCastException cce) {
			//should not be possible, but...
			throw new IllegalArgumentException(cce);
		}
	}
	/**
	 * Process the logout response and throw an {@link IllegalStateException} if the response is not OK.
	 */
	private boolean processLogout(JsonRpcResponse logoutResponse) throws IllegalStateException {
		boolean logoutSuccessful = (Boolean) logoutResponse.getResult();
		if (!logoutSuccessful) {
			throw new IllegalStateException("Logout was not successful");
		}
		else {
			textFieldSessionId.setText("-----");
		}
		return logoutSuccessful;
	}
	
	/**
	 * Request the picklist from the server
	 */
	private void sendRequest() {
		String sessionId = textFieldSessionId.getText();
		String ident = textFieldIdentification.getText();
		boolean async = checkboxAsync.isSelected();
		try {
			//the returned response is not used, but only displayed (by the receiveReceived(String) method)
			client.getPickList(sessionId, ident, async);
		}
		catch (IllegalStateException e) {
			//handle exceptions (that occur when the request fails for any reason)
			DialogUtils.showErrorDialog("A problem occured", "Picklist couldn't be received",
					"The picklist couldn't be received from the server for unknown reasons");
		}
	}
	
	@Override
	public void receiveSent(String sentJson) {
		//send strings are already pretty and can be directly displayed
		textAreaRequest.setText(sentJson);
	}
	
	@Override
	public void receiveReceived(String receivedJson) {
		//make the JSON text pretty first
		ObjectMapper mapper = new ObjectMapper();
		Object json;
		String pretty;
		try {
			json = mapper.readValue(receivedJson, Object.class);
			pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		}
		catch (IOException e) {
			pretty = receivedJson;
		}
		
		//display the pretty json text
		textAreaResponse.setText(pretty);
	}
}