package com.picavi.json_rpc_simple_client.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.picavi.json_rpc_server.model.Credentials;
import com.picavi.json_rpc_server.model.JsonRpcError;
import com.picavi.json_rpc_server.model.JsonRpcLoginAnswer;
import com.picavi.json_rpc_server.model.JsonRpcRequest;
import com.picavi.json_rpc_server.model.JsonRpcResponse;
import com.picavi.json_rpc_server.model.PicklistRequestParameters;

public class SimpleRpcClient {
	
	private static final Logger LOGGER = LogManager.getLogger(SimpleRpcClient.class);
	
	private static final String SERVER_URI = "http://localhost:4711/";
	private static final String RESOURCE_PATH_SYNC = "";
	private static final String RESOURCE_PATH_ASYNC = "async";
	
	private static int id = 0;
	
	private static final String jsonRPC = "2.0";
	
	private List<SimpleRpcClientCommunicationListener> communicationListeners;
	
	public SimpleRpcClient() {
		final int expectedListeners = 1;//not many listeners are expected...
		communicationListeners = new ArrayList<SimpleRpcClientCommunicationListener>(expectedListeners);
	}
	
	/**
	 * Login the user with his password.
	 * 
	 * @param username
	 *        The user's name
	 * 
	 * @param passwd
	 *        The user's password
	 * 
	 * @throws IllegalStateException
	 *         An {@link IllegalStateException} is thrown if the login was not successful
	 */
	public JsonRpcResponse login(String username, String password) throws IllegalStateException {
		LOGGER.info("login: user: {}, password: <not shown here>", username);
		//create a request for the login
		JsonRpcRequest request = createLoginRequest(username, password);
		
		//convert to JSON
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json;
		try {
			json = ow.writeValueAsString(request);
			informSendListeners(json);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
			LOGGER.error("Json representation failed", e);
			throw new IllegalStateException(e);
		}
		
		//send the request to the server via POST
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(SERVER_URI).path(RESOURCE_PATH_SYNC);
		
		LOGGER.info("Sending POST request to url: {}; request: {}", SERVER_URI + RESOURCE_PATH_SYNC, toOneLineJson(json));
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new IllegalStateException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			JsonRpcResponse resp = getJsonRpcResponse(response);
			//parse the left objects
			JsonRpcLoginAnswer loginAnswer = JsonRpcLoginAnswer.fromParameters(resp.getResult());
			resp.setResult(loginAnswer);
			JsonRpcError error = JsonRpcError.fromParameters(resp.getError());
			resp.setError(error);
			return resp;
		}
		else {
			throw new IllegalArgumentException("The response was expected to contain data, but it's empty");
		}
	}
	
	/**
	 * Create a login request from a username and a password
	 */
	private JsonRpcRequest createLoginRequest(String username, String password) {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setId(getNextId());
		request.setJsonRpc(jsonRPC);
		request.setMethod("system.login");
		request.setParams(new Credentials(username, password, "", ""));
		return request;
	}
	
	/**
	 * Logout the user
	 * 
	 * @param sessionId
	 *        The id of the session that is ended
	 * 
	 * @throws IllegalStateException
	 *         An {@link IllegalStateException} is thrown when the logout was not successful
	 */
	public JsonRpcResponse logout(String sessionId) throws IllegalStateException {
		LOGGER.info("logout: user: {}", sessionId);
		//create a request for the logout
		JsonRpcRequest request = createLogoutRequest(sessionId);
		
		//convert to JSON
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json;
		try {
			json = ow.writeValueAsString(request);
			informSendListeners(json);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
			LOGGER.error("Json representation failed", e);
			throw new IllegalStateException(e);
		}
		
		//send the request to the server via POST
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(SERVER_URI).path(RESOURCE_PATH_SYNC);
		
		LOGGER.info("Sending POST request to url: {}; request: {}", SERVER_URI + RESOURCE_PATH_SYNC, toOneLineJson(json));
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new IllegalStateException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			JsonRpcResponse resp = getJsonRpcResponse(response);
			return resp;
		}
		else {
			throw new IllegalArgumentException("The response was expected to contain data, but it's empty");
		}
	}
	
	/**
	 * Create a logout request by a session id
	 */
	private JsonRpcRequest createLogoutRequest(String sessionId) {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setId(getNextId());
		request.setJsonRpc(jsonRPC);
		request.setMethod("system.logout");
		request.setParams(sessionId);
		return request;
	}
	
	/**
	 * Get the picklist from the server
	 * 
	 * @param ident
	 *        The identification of the picks
	 * 
	 * @throws IllegalStateException
	 *         An {@link IllegalStateException} is thrown when the request fails
	 */
	public JsonRpcResponse getPickList(String sessionId, String ident, boolean async) throws IllegalStateException {
		LOGGER.info("requesting picklist from server; identifier: {}, async: {}", ident, async);
		//create a request to get the picklist from the server
		JsonRpcRequest request = createGetPickListRequest(sessionId, ident);
		
		//convert to JSON
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json;
		try {
			json = ow.writeValueAsString(request);
			informSendListeners(json);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
			LOGGER.error("Json representation failed", e);
			throw new IllegalStateException(e);
		}
		
		//send the request to the server via POST
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(SERVER_URI);
		//choose the sync or async path
		if (async) {
			webTarget = webTarget.path(RESOURCE_PATH_ASYNC);
		}
		else {
			webTarget = webTarget.path(RESOURCE_PATH_SYNC);
		}
		
		LOGGER.info("Sending POST request to url: {}; request: {}", SERVER_URI + RESOURCE_PATH_SYNC, toOneLineJson(json));
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code and return the contained data (if it's OK)
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new IllegalStateException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			JsonRpcResponse resp = getJsonRpcResponse(response);
			return resp;
		}
		else {
			throw new IllegalArgumentException("The response was expected to contain data, but it's empty");
		}
	}
	
	/**
	 * Create a getPickList request by the session id and the identifier
	 */
	private JsonRpcRequest createGetPickListRequest(String sessionId, String ident) {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setId(getNextId());
		request.setJsonRpc(jsonRPC);
		request.setMethod("orderPicking.getPickingList");
		request.setParams(new PicklistRequestParameters(sessionId, ident));
		return request;
	}
	
	/**
	 * Get a JsonRpcResponse from a Response object. (Deserializes JSON)
	 */
	private JsonRpcResponse getJsonRpcResponse(Response response) {
		String responseText = response.readEntity(String.class);
		LOGGER.info("Received response including the JSON text: {}", responseText);
		informResponseListeners(responseText);
		ObjectMapper mapper = new ObjectMapper();
		try {
			//"manually" parse JSON to Object
			JsonRpcResponse resp = mapper.readValue(responseText, JsonRpcResponse.class);
			return resp;
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("The response could not be read or parsed: " + responseText);
		}
	}
	
	/**
	 * Get the next possible id for the request and increase the count
	 */
	private String getNextId() {
		id++;
		return Integer.toString(id);
	}
	
	/**
	 * Create a one lined json text from a pretty text (for the logs)
	 */
	private String toOneLineJson(String json) {
		return Arrays.asList(json.split("\n")).stream().map(s -> s.trim()).collect(Collectors.joining(" "));
	}
	
	/**
	 * Inform the listeners that a message was sent to the server
	 */
	private void informSendListeners(String sendText) {
		for (SimpleRpcClientCommunicationListener listener : communicationListeners) {
			listener.receiveSent(sendText);
		}
	}
	/**
	 * Inform the listeners that a message was received from the server
	 */
	private void informResponseListeners(String responseText) {
		for (SimpleRpcClientCommunicationListener listener : communicationListeners) {
			listener.receiveReceived(responseText);
		}
	}
	
	/**
	 * Add a communication listener (see {@link SimpleRpcClientCommunicationListener})
	 */
	public void addCommunicationListener(SimpleRpcClientCommunicationListener listener) {
		communicationListeners.add(listener);
	}
	/**
	 * Remove a communication listener (see {@link SimpleRpcClientCommunicationListener})
	 * 
	 * (To be removed the listener has to be equal to a registered listener (comparing by the Object.equals(Object) method))
	 */
	public void removeCommunicationListener(SimpleRpcClientCommunicationListener listener) {
		communicationListeners.remove(listener);
	}
}