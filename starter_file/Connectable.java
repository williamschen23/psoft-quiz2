/**
 * 
 */
package edu.rpi.csci2600.f25.gildem4;

import java.io.IOException;

/**
 * @author gildem4
 *
 */
public interface Connectable {
	public void connect() throws IOException;
	public void send(String message);	
	public String receive();
	public int getPort();
}
