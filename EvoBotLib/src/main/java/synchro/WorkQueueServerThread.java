/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synchro;

import evolutionaryComputation.Individual;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import knowledge.Memoria;

/**
 *
 * @author Jose
 */
public class WorkQueueServerThread implements Runnable {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(WorkQueueServerThread.class);
    private Socket clientSocket = null;
    private WorkQueueServer server;
    private int id;

    public WorkQueueServerThread(Socket clientSocket, int id, WorkQueueServer server) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            long time = System.currentTimeMillis();
            String confirmation = "1";
            // while(confirmation!="OK"){
            SyncMessage msg = new SyncMessage(id, Job.Estado.Init);
            msg.data = Memoria.getBDNAME();
            output.writeObject(msg);
            msg = null;
            if (logger.isDebugEnabled()) {
                logger.debug("run() - Enviado mensaje con ID"); //$NON-NLS-1$
            }

            try {
                msg = (SyncMessage) input.readObject();
                server.setLock(false);
            } catch (IOException ex) {
                logger.error("Socket ID" + id + " error:" + this.clientSocket.toString(), ex); //$NON-NLS-1$

                removeRemainingJob();
                server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
            } catch (ClassNotFoundException ex) {
                logger.error("run()", ex); //$NON-NLS-1$

                server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
            }


            try {
                msg = (SyncMessage) input.readObject();
                server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
                server.getMem().storeGenes(msg.id, 0, server.getMem().getCurrentGeneration(), (Individual) msg.data);
            } catch (SocketException ex) {
                logger.error("Socket ID" + id + " error:" + this.clientSocket.toString(), ex); //$NON-NLS-1$

                removeRemainingJob();

                server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
            } catch (ClassNotFoundException ex) {
                logger.error("run()", ex); //$NON-NLS-1$

                server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
            }

            //}
            output.close();
            input.close();
            if (logger.isDebugEnabled()) {
                logger.debug("run() - Request processed: " + time); //$NON-NLS-1$
            }
            this.clientSocket.close();

        } catch (IOException ex) {
            logger.error("run()", ex); //$NON-NLS-1$


        }
    }

    private void removeRemainingJob() {
        synchronized (server.currentJobList) {
            synchronized (server.remainingJobList) {
                server.remainingJobList.add(id);
                server.currentJobList.remove(new Integer(id));
                server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
            }
        }
    }
}
