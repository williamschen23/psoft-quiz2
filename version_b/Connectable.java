/**
 * 
 */
package edu.rpi.csci2600.u25.kuzmik2;

import java.io.IOException;

/**
 * @author Konstantin Kuzmin
 *
 */
public interface Connectable {
	public void connect() throws IOException;
	public void send(String message);	
	public String receive();
	public int getPort();
}
