package com.picavi.json_rpc_simple_client.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.picavi.json_rpc_server.model.JsonRpcRequest;
import com.picavi.json_rpc_server.model.JsonRpcResponse;

public class SimpleRpcClient {
	
	private static final Logger LOGGER = LogManager.getLogger(SimpleRpcClient.class);
	
	private static final String SERVER_URI = "http://localhost:9130";
	private static final String RESOURCE_PATH_SYNC = "/";
	private static final String RESOURCE_PATH_ASYNC = "/async";
	
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
		
		//send the request to the server via POST
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(SERVER_URI).path(RESOURCE_PATH_SYNC);
		
		LOGGER.info("Sending POST request to url: {}", SERVER_URI + RESOURCE_PATH_SYNC);
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(request, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new IllegalStateException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			return response.readEntity(JsonRpcResponse.class);
		}
		else {
			throw new IllegalArgumentException("The response was expected to contain data, but it's empty");
		}
	}
	
	private JsonRpcRequest createLoginRequest(String username, String password) {
		//TODO create the login request
		return null;
	}
	
	/**
	 * Logout the user
	 * 
	 * @param username
	 *        The name of the user that is logged out
	 * 
	 * @throws IllegalStateException
	 *         An {@link IllegalStateException} is thrown when the logout was not successful
	 */
	public JsonRpcResponse logout(String username) throws IllegalStateException {
		LOGGER.info("logout: user: {}", username);
		//create a request for the logout
		JsonRpcRequest request = createLogoutRequest(username);
		
		//send the request to the server via POST
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(SERVER_URI).path(RESOURCE_PATH_SYNC);
		
		LOGGER.info("Sending POST request to url: {}", SERVER_URI + RESOURCE_PATH_SYNC);
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(request, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new IllegalStateException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			return response.readEntity(JsonRpcResponse.class);
		}
		else {
			throw new IllegalArgumentException("The response was expected to contain data, but it's empty");
		}
	}
	
	private JsonRpcRequest createLogoutRequest(String username) {
		// TODO Auto-generated method stub
		return null;
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
	public JsonRpcResponse getPickList(String ident, boolean async) throws IllegalStateException {
		LOGGER.info("requesting picklist from server; identifier: {}, async: {}", ident, async);
		//create a request to get the picklist from the server
		JsonRpcRequest request = createGetPickListRequest(ident);
		
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
		
		LOGGER.info("Sending POST request to url: {}", SERVER_URI + RESOURCE_PATH_SYNC);
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(request, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code and return the contained data (if it's OK)
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new IllegalStateException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			return response.readEntity(JsonRpcResponse.class);
		}
		else {
			throw new IllegalArgumentException("The response was expected to contain data, but it's empty");
		}
	}
	
	private JsonRpcRequest createGetPickListRequest(String ident) {
		// TODO Auto-generated method stub
		return null;
	}
}