/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.google.inject.internal.cglib.core.Transformer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.Replacement;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.deathmatch.UT2004DeathMatch1v1;

import evolutionaryComputation.Individual;
import evolutionaryComputation.IndividualV1;
import genetic.IndividualV1Crossover;
import genetic.IndividualV1Evaluator;
import genetic.IndividualV1EvolutionEngine;
import genetic.IndividualV1Factory;
import genetic.IndividualV1Mutation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import knowledge.Memoria;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.swing.ObjectSwingRenderer;
import org.uncommons.watchmaker.swing.SwingConsole;
import synchro.Job;
import synchro.WorkQueueServer;

/**
 *
 * @author Jose
 */
public class EvolutionMain {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(EvolutionMain.class);
    private BotsGUIMainWindow botsGUIMainWindow;
    private WorkQueueServer server;
    private Memoria mem;
    private Thread serverThread;
    JobList jobList = new JobList();
    private Individual[] population;
    private boolean cancel = false;
    private String botpath;
    private String botfolder;
    ConfigPreferences preferences;

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public EvolutionMain(BotsGUIMainWindow window) {
        preferences = new ConfigPreferences();
        server = new WorkQueueServer(4000);
        preferences.parameters = new HashMap<String, String>();
        this.botsGUIMainWindow = window;
        mem = new Memoria(false, false, 26, false);
        serverThread = new Thread(server);
        serverThread.setName("Server Thread");
        serverThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                //      logger.error("Error servidor", ex);
            }
        });
        serverThread.start();
    }

    public void loadXML() {
        updateParameters();
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(preferences);
        final JFileChooser fc = new JFileChooser();
        // In response to a button click:
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {

                preferences = (ConfigPreferences) xstream.fromXML(new FileReader(file));
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("jMenuItem1ActionPerformed(java.awt.event.ActionEvent) - " + ex); //$NON-NLS-1$
                }
            }

        }


        updateDialogs();
        this.updateGenerationComboBox();
    }

    public void saveXML() {
        updateParameters();
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(preferences);
        final JFileChooser fc = new JFileChooser();
        // In response to a button click:
        int returnVal = fc.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                BufferedWriter os = new BufferedWriter(new FileWriter(file));
                os.write(xml);
                os.flush();
                os.close();
            } catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("jMenuItem1ActionPerformed(java.awt.event.ActionEvent) - " + ex); //$NON-NLS-1$
                }
            }

        }


        updateParameters();
    }

    private void updateParameters() {

        preferences.parameters.put("evoBotPath", this.botsGUIMainWindow.bot1PathField.getText());
        preferences.parameters.put("expertBotPath", this.botsGUIMainWindow.bot2PathField.getText());
        preferences.parameters.put("mapName", this.botsGUIMainWindow.mapNameField.getText());
        preferences.parameters.put("utPath", this.botsGUIMainWindow.pathUTField.getText());
        preferences.parameters.put("threadsNum", this.botsGUIMainWindow.threadsNumberField.getText());
        preferences.parameters.put("fragLimit", this.botsGUIMainWindow.fragLimitField.getText());
        preferences.parameters.put("logPath", this.botsGUIMainWindow.logPathField.getText());
        preferences.parameters.put("timeLimit", this.botsGUIMainWindow.timeLimitField.getText());
        preferences.parameters.put("generationsNum", this.botsGUIMainWindow.getGenerationsField().getText());
        preferences.parameters.put("iterationsNum", this.botsGUIMainWindow.getIterationsField().getText());
        preferences.parameters.put("mutationRatio", this.botsGUIMainWindow.getMutationRatio().getText());
        preferences.parameters.put("mutationProbability", this.botsGUIMainWindow.getMutationProbabilityField().getText());
        preferences.parameters.put("crossoverPoints", this.botsGUIMainWindow.getCrossoverPointsText().getText());
        preferences.parameters.put("elitismNum", this.botsGUIMainWindow.getElitismotextField().getText());
    }

    private void updateDialogs() {

        botsGUIMainWindow.bot1PathField.setText(preferences.parameters.get("evoBotPath"));
        botsGUIMainWindow.bot2PathField.setText(preferences.parameters.get("expertBotPath"));
        botsGUIMainWindow.mapNameField.setText(preferences.parameters.get("mapName"));
        botsGUIMainWindow.pathUTField.setText(preferences.parameters.get("utPath"));
        botsGUIMainWindow.threadsNumberField.setText(preferences.parameters.get("threadsNum"));
        botsGUIMainWindow.fragLimitField.setText(preferences.parameters.get("fragLimit"));
        botsGUIMainWindow.logPathField.setText(preferences.parameters.get("logPath"));
        botsGUIMainWindow.timeLimitField.setText(preferences.parameters.get("timeLimit"));
        botsGUIMainWindow.getGenerationsField().setText(preferences.parameters.get("generationsNum"));
        botsGUIMainWindow.getIterationsField().setText(preferences.parameters.get("iterationsNum"));
        botsGUIMainWindow.getMutationRatio().setText(preferences.parameters.get("mutationRatio"));
        botsGUIMainWindow.getMutationProbabilityField().setText(preferences.parameters.get("mutationProbability"));
        botsGUIMainWindow.getCrossoverPointsText().setText(preferences.parameters.get("crossoverPoints"));
        botsGUIMainWindow.getElitismotextField().setText(preferences.parameters.get("elitismNum"));
    }

    public WorkQueueServer getServer() {
        return server;
    }

    public void setServer(WorkQueueServer server) {
        this.server = server;
    }

    public Memoria getMem() {
        return mem;
    }

    public void setMem(Memoria mem) {
        this.mem = mem;
    }

    public Thread getServerThread() {
        return serverThread;
    }

    public void setServerThread(Thread serverThread) {
        this.serverThread = serverThread;
    }

    public Individual[] getPopulation() {
        return population;
    }

    public void setPopulation(Individual[] population) {
        this.population = population;
    }

    private SelectionStrategy initSelectionStrategy() {
        Class<?> clazz = (Class<?>) botsGUIMainWindow.getjComboBox1().getSelectedItem();
        SelectionStrategy<Object> selection = null;
        if (clazz.getSimpleName().equals("InteractiveSelection")) {
            SwingConsole console = new SwingConsole();
            selection = new InteractiveSelection(console, new ObjectSwingRenderer(), 2, 2);
            JFrame frame = new JFrame();
            frame.add(console);
            frame.setSize(300, 200);
            frame.setVisible(true);
            console.setVisible(true);
        } else {
            try {
                selection = (SelectionStrategy<Object>) clazz.newInstance();
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(EvolutionMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(EvolutionMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return selection;
    }

    void runEvolutionOld(BotsGUIMainWindow botsGUIMainWindow) {
        cancel = false;
        botsGUIMainWindow.initMemoria();
        this.getServer().updateRemainingList(true);
        List<EvolutionaryOperator<IndividualV1>> operators = new LinkedList<EvolutionaryOperator<IndividualV1>>();
        int xoverPoints = Integer.parseInt(botsGUIMainWindow.getCrossoverPointsText().getText());
        CandidateFactory<IndividualV1> factory = new IndividualV1Factory();
        operators.add(new IndividualV1Crossover(xoverPoints));
        operators.add(new IndividualV1Mutation(Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100, Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100));
        operators.add(new Replacement<IndividualV1>(factory, new Probability(0.1)));
        EvolutionaryOperator<IndividualV1> pipeline = new EvolutionPipeline<IndividualV1>(operators);
        FitnessEvaluator<IndividualV1> fitnessEvaluator = new IndividualV1Evaluator();
        Class<?> clazz = (Class<?>) botsGUIMainWindow.getjComboBox1().getSelectedItem();
        SelectionStrategy<Object> selection = initSelectionStrategy();
        Random rng = new MersenneTwisterRNG();
        GenerationalEvolutionEngine<IndividualV1> engine;
        engine = new GenerationalEvolutionEngine<IndividualV1>(factory, pipeline, fitnessEvaluator, selection, rng);
        EvolutionMonitor<IndividualV1> observer = new EvolutionMonitor<IndividualV1>();

        engine.addEvolutionObserver(observer);
        observer.showInFrame("Monitor", false);
        for (int i = 0; i < Integer.parseInt(botsGUIMainWindow.getGenerationsField().getText()) && !cancel; i++) {
            if (this.getServer().remainingJobList.isEmpty()) {
                //                GeneticAlg geneticAlg;
                //                Class<?> geneticClass = (Class<?>) this.jComboBox1.getSelectedItem();
                //                try {
                //                    geneticAlg = (GeneticAlg) geneticClass.newInstance();
                //                    main.setPopulation main.getMem().loadPoblacion(26);
                //                    main.setPopulation geneticAlg.evolve(main.getPopulation());
                //                    main.getMem().storeGenes(main.getMem().getCurrentGeneration() + 1, -1, main.getPopulation());
                //                    initMemoria();
                //                } catch (InstantiationException ex) {
                //                    java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                //                } catch (IllegalAccessException ex) {
                //                    java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                //                }
                this.setPopulation(this.getMem().loadPoblacion(26));
                Collection<Individual> oldpop = Arrays.asList(this.getPopulation().clone());
                List<EvaluatedCandidate<IndividualV1>> newpop;
                newpop = engine.evolvePopulation(this.getPopulation().length, 2, (List<IndividualV1>) (List<?>) oldpop, new GenerationCount(2));
                int count = 0;
                for (EvaluatedCandidate<IndividualV1> c : newpop) {
                    this.getPopulation()[count] = c.getCandidate();
                    this.getPopulation()[count].resetStats();
                    count++;
                }
                this.getMem().storeGenes(this.getMem().getCurrentGeneration() + 1, -1, this.getPopulation());
                botsGUIMainWindow.initMemoria();
            }
            for (int j = 0; j < Integer.parseInt(botsGUIMainWindow.getIterationsField().getText()) && !cancel; j++) {
                if (this.getServer().remainingJobList.isEmpty()) {
                    //                GeneticAlg geneticAlg;
                    //                Class<?> geneticClass = (Class<?>) this.jComboBox1.getSelectedItem();
                    //                try {
                    //                    geneticAlg = (GeneticAlg) geneticClass.newInstance();
                    //                    main.setPopulation main.getMem().loadPoblacion(26);
                    //                    main.setPopulation geneticAlg.evolve(main.getPopulation());
                    //                    main.getMem().storeGenes(main.getMem().getCurrentGeneration() + 1, -1, main.getPopulation());
                    //                    initMemoria();
                    //                } catch (InstantiationException ex) {
                    //                    java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    //                } catch (IllegalAccessException ex) {
                    //                    java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    //                }
                    this.setPopulation(this.getMem().loadPoblacion(26));
                    this.getMem().storeGenes(this.getMem().getCurrentGeneration(), -1, this.getPopulation());
                    botsGUIMainWindow.initMemoria();
                }
                botsGUIMainWindow.main.iterateOnce();
                try {
                    this.killUCCServers();
                } catch (IOException ex) {
                    if (EvolutionMain.logger.isDebugEnabled()) {
                        EvolutionMain.logger.debug("runButtonActionPerformed(java.awt.event.ActionEvent) - " + ex); //$NON-NLS-1$
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                this.getServer().updateRemainingList(true);
            }
            if (EvolutionMain.logger.isDebugEnabled()) {
                EvolutionMain.logger.debug("runButtonActionPerformed(java.awt.event.ActionEvent) - end"); //$NON-NLS-1$
            }
        }
    }

    public List<Individual[]> getGenerationTableList() {
        return preferences.generationTableList;
    }

    public void setGenerationTableList(List<Individual[]> generationTableList) {
        preferences.generationTableList = generationTableList;
    }

    void runEvolution(BotsGUIMainWindow botsGUIMainWindow) {
        cancel = false;
        botsGUIMainWindow.initMemoria();
        this.getServer().updateRemainingList(true);
        List<EvolutionaryOperator<IndividualV1>> operators = new LinkedList<EvolutionaryOperator<IndividualV1>>();
        int xoverPoints = Integer.parseInt(botsGUIMainWindow.getCrossoverPointsText().getText());
        CandidateFactory<IndividualV1> factory = new IndividualV1Factory();
        if (xoverPoints > 0) {
            operators.add(new IndividualV1Crossover(xoverPoints));
        }
        operators.add(new IndividualV1Mutation(Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100, Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100));
        //       operators.add(new Replacement<IndividualV1>(factory, new Probability(0.1)));
        EvolutionaryOperator<IndividualV1> pipeline = new EvolutionPipeline<IndividualV1>(operators);
        FitnessEvaluator<IndividualV1> fitnessEvaluator = new IndividualV1Evaluator();
        Class<?> clazz = (Class<?>) botsGUIMainWindow.getjComboBox1().getSelectedItem();
        SelectionStrategy<Object> selection = initSelectionStrategy();
        Random rng = new MersenneTwisterRNG();
        IndividualV1EvolutionEngine engine;
        engine = new IndividualV1EvolutionEngine(factory, pipeline, fitnessEvaluator, selection, rng, this);
        EvolutionMonitor<IndividualV1> observer = new EvolutionMonitor<IndividualV1>();
        engine.addEvolutionObserver(observer);
        observer.showInFrame("Monitor", false);
        this.setPopulation(this.getMem().loadPoblacion(26));
        Collection<Individual> oldpop = Arrays.asList(this.getPopulation());
        List<EvaluatedCandidate<IndividualV1>> newpop;
        newpop = engine.evolvePopulation(this.getPopulation().length, Integer.parseInt(botsGUIMainWindow.getElitismotextField().getText()), (List<IndividualV1>) (List<?>) oldpop, new GenerationCount(Integer.parseInt(botsGUIMainWindow.getGenerationsField().getText())));
        int count = 0;
        for (EvaluatedCandidate<IndividualV1> c : newpop) {
            this.getPopulation()[count] = c.getCandidate();
            this.getPopulation()[count].resetStats();
            count++;
        }
    }

    public void killUCCServers() throws IOException {
        Runtime.getRuntime().exec("Taskkill /im UCC.exe /f");
        Runtime.getRuntime().exec("pkill UCC.exe /f");
    }

    public BotsGUIMainWindow getBotsGUIMainWindow() {
        return botsGUIMainWindow;
    }

    public void setBotsGUIMainWindow(BotsGUIMainWindow botsGUIMainWindow) {
        this.botsGUIMainWindow = botsGUIMainWindow;
    }

    public JobList getJobList() {
        return jobList;
    }

    public void setJobList(JobList jobList) {
        this.jobList = jobList;
    }

    public void iterateOnce() throws NumberFormatException {

        getServer().setMaxThreadsNum(Integer.parseInt(botsGUIMainWindow.threadsNumberField.getText()));
        getServer().setNumAvailableThreads(Integer.parseInt(botsGUIMainWindow.threadsNumberField.getText()));
        getServer().init();



        while (!getServer().remainingJobList.isEmpty() && !cancel) {
            getServer().updateRemainingList(false);
            while (!getServer().remainingJobList.isEmpty() && !cancel) {
                for (Integer id : getServer().remainingJobList) {
                    if (getServer().getNumAvailableThreads() > 0) {
                        if (!cancel) {

                            runMatch(botsGUIMainWindow, id);
                        }
                    }
                }

                try {
                    Thread.sleep(500);

                } catch (InterruptedException ex) {
                    if (BotsGUIMainWindow.logger.isDebugEnabled()) {
                        BotsGUIMainWindow.logger.debug("iterateOnce() - " + ex); //$NON-NLS-1$
                    }
                }

            }
            getServer().updateRemainingList(true);
        }

        if (BotsGUIMainWindow.logger.isDebugEnabled()) {
            BotsGUIMainWindow.logger.debug("iterateOnce() - end"); //$NON-NLS-1$
        }
    }

    void runMatch(BotsGUIMainWindow botsGUIMainWindow, int id) throws NumberFormatException {
        synchronized (this) {
            if (BotsGUIMainWindow.logger.isDebugEnabled()) {
                BotsGUIMainWindow.logger.debug("runMatch() - start"); //$NON-NLS-1$
            }

            LogCategory log = new LogCategory("DeathMatch1v1");
            UT2004DeathMatch1v1 match = new UT2004DeathMatch1v1();
            log.setLevel(Level.ALL);
            ///     log.addConsoleHandler();
            //match.setLog(log);

            // GAME CONFIGURATION
            match.setMatchName("TX-" + this.getServer().getMem().getCurrentGeneration() + "-" + id);
            match.setUnrealHome(botsGUIMainWindow.pathUTField.getText());
            match.setMapName(botsGUIMainWindow.mapNameField.getText());
            match.setFragLimit(Integer.parseInt(botsGUIMainWindow.fragLimitField.getText()));
            match.setTimeLimitInMinutes(Integer.parseInt(botsGUIMainWindow.timeLimitField.getText()));

            // BOT 1 CONFIGURATION

            match.setBot1Name("Bot evolutivo");
            match.setBot1JarPath(botsGUIMainWindow.bot1PathField.getText());

            // BOT 2 CONFIGURATION

            match.setBot2Name("Bot experto");
            match.setBot2JarPath(botsGUIMainWindow.bot2PathField.getText());

            // OUTPUT RESULT

            match.setOutputDir(botsGUIMainWindow.logPathField.getText());
            botsGUIMainWindow.initMemoria();

            getServer().setNumAvailableThreads(getServer().getNumAvailableThreads() - 1);
            Job newJob = new Job();
            newJob.setId(id);
            
            newJob.setMatch(match);
            newJob.setStatus(Job.Estado.Init);
            newJob.setStartTime(new Timestamp(System.currentTimeMillis()));
            newJob.setThread((new Thread(match)));
            newJob.setServer(getServer());
            getServer().getJobList().put(id, newJob);
            newJob.run();
            jobList.add(newJob.getThread());
            if (BotsGUIMainWindow.logger.isDebugEnabled()) {
                BotsGUIMainWindow.logger.debug("runMatch() - end"); //$NON-NLS-1$
            }
        }
    }

    public void initMemoria() {

        botpath = botsGUIMainWindow.bot1PathField.getText();
        botfolder = botpath.substring(0, botpath.lastIndexOf("\\") + 1);
        Memoria.setBDNAME(botfolder + "Memoria.db");
        this.getServer().setMemoria(this.getMem());
        this.getServer().updateRemainingList(true);
    }

    public void updateGenerationComboBox() {

        getBotsGUIMainWindow().getGenerationsComboBox().setModel(new DefaultComboBoxModel(new String[]{"Actual"}));


        int count = 0;
        for (Individual[] population : this.preferences.getGenerationTableList()) {
            ((DefaultComboBoxModel) getBotsGUIMainWindow().getGenerationsComboBox().getModel()).addElement(count);
            count++;
        }
    }
}