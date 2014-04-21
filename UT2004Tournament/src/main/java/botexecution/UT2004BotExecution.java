package botexecution;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.utils.StreamSink;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.flag.ImmutableFlag;

/**
 * Class that wrapps the execution of the bot from the jar defined by
 * {@link UT2004BotExecutionConfig}. <p><p> The class is not suitable for
 * inheritance ... better copy-paste and adjust for yourself.
 *
 * @author Jimmy
 */
public class UT2004BotExecution {

    protected UT2004BotExecutionConfig config;
    /**
     * Flag whether the bot is running, we're synchronizing access to this class
     * on this flag. <p><p> Set to TRUE ONLY IN
     * {@link UT2004BotExecution#start()} and back to FALSE ONLY IN
     * {@link UT2004BotExecution#shutdown(boolean)}. Do not alter the flag from
     * anywhere else!
     */
    protected final Flag<Boolean> running = new Flag<Boolean>(false);
    /**
     * When {@link UT2004BotExecution#start()}ed it contains the bot process.
     */
    protected Process botProcess = null;
    /**
     * Sink for the STDOUT of the bot, it may be redirected to
     * {@link UT2004BotExecution#log}.
     */
    protected StreamSink streamSinkOutput = null;
    /**
     * Sink for the STDERR of the bot, it may be redirected to
     * {@link UT2004BotExecution#log}.
     */
    protected StreamSink streamSinkError = null;
    /**
     * Log used by the object, specified during the construction.
     */
    protected Logger log;

    /**
     * @param config bot execution configuration (contains path to jar to run)
     * @param log log to be used (stdout/stderr of the bot will be redirected
     * there as well with {@link Level#INFO})
     */
    public UT2004BotExecution(UT2004BotExecutionConfig config, Logger log) {
        this.log = log;
        this.config = config;
    }
    /**
     * Task that will kill the BOT process when user forgets to do so before the
     * JVM is killed.
     */
    protected Runnable shutDownHook = new Runnable() {
        @Override
        public void run() {
            if (botProcess != null) {
                botProcess.destroy();
            }
        }
    };
    protected Thread shutDownHookThread;
    /**
     * Task that is waiting for the end of the bot process to switch
     * {@link UT2004BotExecution#running} back to false and shutdown all other
     * utility threads (like sink).
     */
    protected Runnable waitForEnd = new Runnable() {
        @Override
        public void run() {
            try {
                botProcess.waitFor();
                // BOT PROCESS HAS STOPPED WHEN THE JVM REACHES HERE
            } catch (InterruptedException e) {
                // we've been interrupted!
                synchronized (running) {
                    if (!running.getFlag()) {
                        // okey we're not running, screw it...
                        return;
                    }
                }
                // hups, we're still running, warn the user!
                if (log != null && log.isLoggable(Level.WARNING)) {
                    log.warning("Interrupted while waiting for the botProcess(" + config.getBotId().getToken() + ") to end!");
                }

            } finally {
                // perform clean-up
                synchronized (running) {
                    if (!running.getFlag()) {
                        // okey we're not running, somebody has already shutdown, screw it...
                        return;
                    }
                    if (waitForEndThread == Thread.currentThread()) {
                        // we're still in the middle of the same execution... (shut down only if it is the same bot execution)
                        shutdown(true);
                    }
                }
            }
        }
    };
    protected Thread waitForEndThread;

    /**
     * Start the bot process. Throws {@link PogamutIOException} if it fails to
     * start the JVM. <p><p> It is wise to observe the state of the
     * {@link UT2004BotExecution#getRunning()} flag to obtain the state of the
     * process (whether the process is running or is dead). Usage of
     * {@link FlagListener} is advised, see
     * {@link ImmutableFlag#addListener(FlagListener)}.
     *
     * @param host GB2004 host
     * @param port GB2004 bot port
     */
    public void start(String host, int port) throws PogamutIOException {
        synchronized (running) {
            if (running.getFlag()) {
                throw new PogamutException("Could not start the bot again, it is already running! stop() it first!", log, this);
            }

            if (log != null && log.isLoggable(Level.WARNING)) {
                log.warning("Starting bot: " + config);
            }

            if (!config.isBotJarExist()) {
                throw new PogamutException("Could not start the bot according to config " + config + " as the bot jar does not exist at specified place " + config.getJarFile().getAbsolutePath() + "!", this);
            }
            
            

            String javaHome = System.getProperty("JAVA_HOME");

            boolean linux = System.getProperty("os.name").toLowerCase().contains("linux");

            boolean server=false;
            if(server){
                javaHome="/home/josejl/jdk1.7.0_45";
            }

            String debug;


            String command =
                    (javaHome == null
                    ? (linux ? "java" : "java.exe")
                    : javaHome + (linux ? "/bin/java" : "\\bin\\java.exe"));

            String fullCommand = command + "-” \"-D" + PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey() + "=" + host + "\""
                    + " \"-D" + PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey() + "=" + port + "\""
                    + " -jar \"" + config.getJarFile().getAbsolutePath() + "\"";

            if (log != null && log.isLoggable(Level.INFO)) {
                log.info("Executing command: " + fullCommand);
            }
            boolean flag = false;
            debug = debug = "-agentlib:jdwp=transport=dt_socket,address=localhost:9014,server=n,suspend=y";
            if (!fullCommand.contains("EXPERT")) {
                flag = false;
            }
            ProcessBuilder procBuilder;
            if (!flag) {
                procBuilder =
                        new ProcessBuilder(
                        command,
                        "-XX:+UseCompressedStrings",
                       
                        "-D" + PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey() + "=" + host,
                        "-D" + PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey() + "=" + port,
                        "-jar",
                        config.getJarFile().getAbsolutePath());
                procBuilder.directory(new File(config.getJarFile().getParent()));
            } else {
                procBuilder =
                        new ProcessBuilder(
                        command,
                        "-D" + PogamutUT2004Property.POGAMUT_UT2004_BOT_HOST.getKey() + "=" + host,
                        "-D" + PogamutUT2004Property.POGAMUT_UT2004_BOT_PORT.getKey() + "=" + port,
                        debug,
                        "-jar",
                        config.getJarFile().getAbsolutePath());
                procBuilder.directory(new File(config.getJarFile().getParent()));
            }
        
        try {
            botProcess = procBuilder.start();
        } catch (IOException e) {
            // failed to start the process
            if (log != null && log.isLoggable(Level.SEVERE)) {
                log.severe("Could not start the bot: " + e.getMessage());
            }
            botProcess = null;
            throw new PogamutIOException("Failed to start the botProcess(" + config.getBotId().getToken() + "). IOException: " + e.getMessage(), e, this);
        }

        // INITIALIZE ALL THREADS
        if (config.isRedirectStdErr()) {
            streamSinkError = new StreamSink(config.getBotId().getToken() + "-StdErrSink", botProcess.getErrorStream(), log, config.getBotId().getToken() + "-StdErr");
        } else {
            streamSinkError = new StreamSink(config.getBotId().getToken() + "-StdErrSink", botProcess.getErrorStream());
        }
        streamSinkError.start();
        if (config.isRedirectStdOut()) {
            streamSinkOutput = new StreamSink(config.getBotId().getToken() + "-StdOutSink", botProcess.getInputStream(), log, config.getBotId().getToken() + "-StdOut");
        } else {
            streamSinkOutput = new StreamSink(config.getBotId().getToken() + "-StdOutSink", botProcess.getInputStream());
        }
        streamSinkOutput.start();
        shutDownHookThread = new Thread(shutDownHook, config.getBotId().getToken() + "-JVMShutdownHook");
        Runtime.getRuntime().addShutdownHook(shutDownHookThread);
        waitForEndThread = new Thread(waitForEnd, config.getBotId().getToken() + "-WaitForProcessEnd");
        running.setFlag(true);    // we're ONLINE!
        waitForEndThread.start(); // execute the thread that will wait for the end of the process to switch 'running' back to false
    }
}
/**
 * Shutdowns the process and cleans-up everything, preparing the object to be
 * {@link UT2004BotExecution#start()}ed again.
 *
 * @param waitForEndThread whether we're being called from the
 * {@link UT2004BotExecution#waitForEnd} thread.
 */
protected void shutdown(boolean waitForEndThread) {
		synchronized(running) {
			if (!running.getFlag()) {
				// we're not running, screw it!
				return;
			}
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("Shutting down botProcess(" + config.getBotId().getToken() + ")!");
			}

			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying botProcess(" + config.getBotId().getToken() + ").");
			}
			if (botProcess != null) {
				try {
                                    Thread.sleep(15000);
					botProcess.destroy();
				} catch (Exception e) {
				}
			}
			botProcess = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying streamSinkError(" + config.getBotId().getToken() + ").");
			}
			try {
				if (streamSinkError != null) streamSinkError.interrupt();
			} catch (Exception e) {
			}
			streamSinkError = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying streamSinkOutput(" + config.getBotId().getToken() + ").");
			}
			try {
				if (streamSinkOutput != null) streamSinkOutput.interrupt();
			} catch (Exception e) {
			}
			streamSinkOutput = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying waitForEnd(" + config.getBotId().getToken() + ").");
			}
			if (!waitForEndThread) {
				try {
					if (this.waitForEndThread != null) this.waitForEndThread.interrupt();
				} catch (Exception e) {
				}
			}
			this.waitForEndThread = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... removing shutDownHook(" + config.getBotId().getToken() + ").");
			}
			if (shutDownHookThread != null) {
				try {
					Runtime.getRuntime().removeShutdownHook(shutDownHookThread);
				} catch (Exception e) {
				}	
			}
			shutDownHookThread = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... setting running-flag(" + config.getBotId().getToken() + ") to FALSE.");
			}
			running.setFlag(false);
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("Shutdown(" + config.getBotId().getToken() + ") finished.");
			}
		}
	}
	
	/**
	 * Shuts down the bot process (if it is running, otherwise does nothing).
	 */
	public void stop() {
		shutdown(false);
	}
    
    /**
     * Process of the bot (non-null if started).
     * @return
     */
    public Process getBotProcess() {
        return botProcess;
    }
	
    /**
     * Flag of the state of the bot process. True == process is running (has been started using {@link UT2004BotExecution#start()}), 
     * False == process was not started or has been already terminated, either by itself or via {@link UT2004BotExecution#stop()}.
     * @return
     */
	public ImmutableFlag<Boolean> getRunning() {
		return running.getImmutable();
	}
	
	/**
	 * Flag of the state of the bot process. True == process is running (has been started using {@link UT2004BotExecution#start()}), 
     * False == process was not started or has been already terminated, either by itself or via {@link UT2004BotExecution#stop()}.
	 * @return
	 */
	public boolean isRunning() {
		return running.getFlag();
	}
	
}
