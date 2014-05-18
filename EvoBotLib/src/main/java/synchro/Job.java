/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package synchro;

import deathmatch.UT2004DeathMatch1v1;
import evolutionaryComputation.Individual;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

/**
 *
 * @author Jose
 */
public class Job implements Runnable {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(Job.class);
    private Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread th, Throwable ex) {

            logger.error("$Thread.UncaughtExceptionHandler.uncaughtException(Thread, Throwable) - Uncaught exception here: ", ex); //$NON-NLS-1$

            if (ex.getClass().getCanonicalName().equals("UCCStartException")) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex1) {
                    java.util.logging.Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            server.setNumAvailableThreads(server.getNumAvailableThreads() + 1);
         //   server.getMem().storeGenes(id, 0, server.getMem().getCurrentGeneration(), backupIndividual);
            status=Estado.Error;
            restart();
//            try {
//           //     if(!server.remainingJobList.contains(id)){
//            //    server.remainingJobList.put(id);
//            }
//                thread.interrupt();
//            } catch (InterruptedException ex1) {
//                java.util.logging.Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex1);
//            }


        }
    };
    private WorkQueueServer server;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public UT2004DeathMatch1v1 getMatch() {
        return match;
    }

    public void setMatch(UT2004DeathMatch1v1 match) {
        this.match = match;
    }
    private Individual individual;

    public WorkQueueServer getServer() {
        return server;
    }

    public void setServer(WorkQueueServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        thread.setUncaughtExceptionHandler(h);
        if (thread != null) {
            thread.start();
        }
    }

    public void restart(){
       setThread((new Thread(match)));
       getThread().setName(match.getMatchName());
       this.status=Estado.Init;
       server.remainingJobList.add(this.id);
    }
    public enum Estado {

        Init,WaitingID, Running, Finished,Error;
    }
    Estado status;
    private Timestamp startTime;
    Thread thread;
    int id;
    private UT2004DeathMatch1v1 match;
    private String replayName;
    public Individual backupIndividual;

    public Job() {
    }

    public Job(Estado status, Job job, int id, WorkQueueServer server, Individual individual, UT2004DeathMatch1v1 match, Thread thread) {
        this.status = status;
        this.thread = thread;
        this.id = id;
        this.startTime = new Timestamp(new java.util.Date().getTime());
        this.individual = individual;
        this.server = server;
        this.match = match;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estado getStatus() {
        return status;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
        public void enableTimedLock(long W) {


       
        // Clase en la que está el código a ejecutar
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if(status.equals(Estado.WaitingID)){
                    status=Estado.Init;
                }
                
            }
        };
        // Aquí se pone en marcha el timer cada segundo.
        // Dentro de 0 milisegundos avísame cada 1000 milisegundos
        Timer lockTimer = new Timer();
        lockTimer.schedule(timerTask, W);

    }
}
