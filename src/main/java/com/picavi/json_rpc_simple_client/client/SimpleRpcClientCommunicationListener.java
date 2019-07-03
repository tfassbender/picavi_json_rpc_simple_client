package com.picavi.json_rpc_simple_client.client;

/**
 * An interface for listeners that want to receive the client - server communication
 */
public interface SimpleRpcClientCommunicationListener {
	
	public void receiveSent(String sentJson);
	
	public void receiveReceived(String receivedJson);
}