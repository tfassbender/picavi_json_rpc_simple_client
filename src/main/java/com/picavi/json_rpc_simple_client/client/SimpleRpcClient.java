package com.picavi.json_rpc_simple_client.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRpcClient {
	
	private static final Logger LOGGER = LogManager.getLogger(SimpleRpcClient.class);
	
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
	public void login(String username, String passwd) throws IllegalStateException {
		LOGGER.info("login: user: {}, password: <not shown here>", username);
		//TODO login
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
	public void logout(String username) throws IllegalStateException {
		LOGGER.info("logout: user: {}", username);
		//TODO logout
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
	public void getPickList(String ident, boolean async) throws IllegalStateException {
		LOGGER.info("requesting picklist from server; identifier: {}, async: {}", ident, async);
		//TODO get the picklist
	}
}