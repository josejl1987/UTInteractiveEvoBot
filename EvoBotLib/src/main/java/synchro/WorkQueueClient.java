/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synchro;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import synchro.Job.Estado;

/**
 *
 * @author Jose
 */
public class WorkQueueClient {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WorkQueueClient.class);

    /*
     * To change this template, choose Tools | Templates and open the template
     * in the editor.
     */

    private Socket clientSocket;
    Estado status;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public WorkQueueClient(int port) {
        try {
            clientSocket = new Socket("localhost", port);
              output = new ObjectOutputStream(clientSocket.getOutputStream());
              input = new ObjectInputStream(clientSocket.getInputStream());
             
        } catch (UnknownHostException ex) {
			logger.error("WorkQueueClient(int)", ex); //$NON-NLS-1$

       
        } catch (IOException ex) {
			logger.error("WorkQueueClient(int)", ex); //$NON-NLS-1$

     
        }


    }

    public void close(){
        try {
            this.input.close();
            this.output.close();
            clientSocket.close();
        } catch (IOException ex) {
            logger.warn("Closing client socket error", ex);
        }
    }
    public SyncMessage readMessage(SyncMessage msg) throws IOException, ClassNotFoundException {

        return (SyncMessage) input.readObject();
        
    }

    public void sendMessage(SyncMessage msg) throws IOException {
 ;
        output.writeObject(msg);

    }
}
