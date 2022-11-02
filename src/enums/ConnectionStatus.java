/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

/**
 *
 * @author ondra
 */
public enum ConnectionStatus {
    UNKNOWN(false), DISCONNECTED(false), CONNECTED(true), CLOSED(false);

	boolean connected;

	ConnectionStatus(boolean b) {
		this.connected = b;
	}

	public boolean getConnectionStatusBoolean() {
		return this.connected;
	}
}
