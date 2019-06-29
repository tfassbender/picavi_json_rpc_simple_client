package com.picavi.json_rpc_simple_client.client;

public class SimpleRpcClient {
	
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
	public void getPickList(String ident) throws IllegalStateException {
		//TODO get the picklist
	}
}