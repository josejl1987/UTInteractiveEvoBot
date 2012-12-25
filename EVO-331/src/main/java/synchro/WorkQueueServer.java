/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synchro;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Level;
import knowledge.Memoria;

/**
 *
 * @author Jose
 */
public class WorkQueueServer implements Runnable {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(WorkQueueServer.class);
    ServerSocket server;
    Set<Integer> currentJobList;
    public HashMap<Integer, Job> jobList;

    public HashMap<Integer, Job> getJobList() {
        return jobList;
    }
    public ArrayList<Integer> remainingJobList;
    public boolean ready = true;
    private int count = 0;
    private int population_size;
    private int interations;
    protected Memoria mem;
    private int matchTime;
    private Timer timer;
    private int maxThreadsNum;

    public void close() {
        try {
            server.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WorkQueueServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getMaxThreadsNum() {
        return maxThreadsNum;
    }

    public void setMaxThreadsNum(int maxThreadsNum) {
        this.maxThreadsNum = maxThreadsNum;
    }

    public Memoria getMem() {
        return mem;
    }

    public void setMem(Memoria mem) {
        this.mem = mem;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(int matchTime) {
        this.matchTime = matchTime;
    }
    private int numAvailableThreads;

    public int getNumAvailableThreads() {
        return numAvailableThreads;
    }

    public void setNumAvailableThreads(int numAvailableThreads) {
        if (numAvailableThreads > this.maxThreadsNum) {
            this.numAvailableThreads = this.maxThreadsNum;
        } else {
            this.numAvailableThreads = numAvailableThreads;
        }
    }

    public void setMemoria(Memoria mem) {
        this.mem = mem;
    }

    public WorkQueueServer(int port) {
        try {
            server = new ServerSocket(port);

        } catch (UnknownHostException ex) {
            logger.error("WorkQueueServer(int)", ex); //$NON-NLS-1$


        } catch (IOException ex) {
            logger.error("WorkQueueServer(int)", ex); //$NON-NLS-1$


        }
        currentJobList = new HashSet<Integer>();
        jobList = new HashMap<Integer, Job>();
    }

    public void init() {
        this.updateRemainingList(true);
        this.currentJobList.clear();
    }

    @Override
    public synchronized void run() {

        try {

            while (true) {
                Socket clientSocket = this.server.accept();
                clientSocket.setSoTimeout(matchTime * 60 * 1200);

                if (!remainingJobList.isEmpty()) {
                    Integer id = remainingJobList.get(0);
                    remainingJobList.remove(0);
                    currentJobList.add(id);

                    new Thread(
                            new WorkQueueServerThread(clientSocket, id, this)).start();
                }

            }
        } catch (IOException ex) {
            logger.error("run()", ex); //$NON-NLS-1$


        }

    }

    public void updateRemainingList(boolean ignoreCurrent) {
        int currentGeneration = mem.getCurrentGeneration();
        remainingJobList = (ArrayList<Integer>) mem.getRemainingIndividuals();
        if (currentJobList != null && !ignoreCurrent) {
            for (Integer i : currentJobList) {

                remainingJobList.remove(i);
            }
        }

    }
}
