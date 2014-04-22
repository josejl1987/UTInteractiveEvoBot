/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.sun.management.OperatingSystemMXBean;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import deathmatch.UT2004DeathMatch1v1;
import evolutionaryComputation.Individual;
import evolutionaryComputation.IndividualV1;
import genetic.*;
import knowledge.Memoria;
import org.apache.log4j.Logger;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.Replacement;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.swing.ObjectSwingRenderer;
import org.uncommons.watchmaker.swing.SwingConsole;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;
import synchro.Job;
import synchro.WorkQueueServer;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import javax.swing.filechooser.FileNameExtensionFilter;
import synchro.Job.Estado;
import utilities.RandomGenerator;

/**
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
    ConfigPreferences preferences;
    private int populationLength;
    public EvolutionMonitor<IndividualV1> observer;
    private IndividualV1EvolutionEngine engine;
    private IndividualV1ChromosomeCopy chromosomeCopyOperator = new IndividualV1ChromosomeCopy();
    HashMap<Integer, Integer> evaluations = new HashMap<Integer, Integer>();
    private IndividualV1HumanEvaluation humanEva = null;

    public HashMap<Integer, Integer> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(HashMap<Integer, Integer> evaluations) {
        this.evaluations = evaluations;
    }

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
        FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML", "xml");
        fc.setFileFilter(xmlFilter);
        // In response to a button click:
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                preferences = (ConfigPreferences) xstream.fromXML(new FileReader(file));
                int load = JOptionPane
                        .showConfirmDialog(this.botsGUIMainWindow, "�Cargar datos?");
                if (load == JOptionPane.NO_OPTION) {
                    preferences.generationTableList.clear();
                    preferences.currentGeneration = 0;

                } else {
                    updateDialogs();

                    this.evaluations = preferences.evaluationsMap;
                    RandomGenerator.setRandom(preferences.rnd, Integer.parseInt(this.botsGUIMainWindow.getRandomSeedtextField().getText()));
                    if (evaluations != null) {
                        createGenerationGraph();
                    }
                    this.updateGenerationComboBox();
                    initMemoria();
                    this.setPopulation(this.preferences.generationTableList.get(preferences.generationTableList.size() - 1));
                    this.getMem().storeGenes(this.preferences.getCurrentGeneration(), -1, this.getPopulation());
                    engine.setHumanEvaluation(preferences.getHumanEvaluation());
                    this.setChromosomeCopyOperator(preferences.chromosomeCopy);
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("jMenuItem1ActionPerformed(java.awt.event.ActionEvent) - " + ex); //$NON-NLS-1$
                }
            }

        }


        updateDialogs();

    }

    public void saveXML(String filename) {
        updateParameters();
        preferences.evaluationsMap = this.evaluations;
        preferences.currentGeneration = engine.getCurrentGeneration();
        preferences.rnd = RandomGenerator.getRnd();
        preferences.chromosomeCopy = this.getChromosomeCopyOperator();
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(preferences);



        File file = new File(filename);
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




        updateParameters();

    }

    public void saveXML() {
        updateParameters();
        preferences.evaluationsMap = this.evaluations;
        preferences.rnd = RandomGenerator.getRnd();

        if (engine == null) {
            preferences.currentGeneration = 0;
        } else {
            preferences.currentGeneration = engine.getCurrentGeneration();
            preferences.humanEvaluation = engine.getHumanEvaluation();
        }
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
        preferences.parameters.put("randomSeed", this.botsGUIMainWindow.getRandomSeedtextField().getText());
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
        botsGUIMainWindow.getRandomSeedtextField().setText(preferences.parameters.get("randomSeed"));
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

    public List<Individual[]> getGenerationTableList() {
        return preferences.generationTableList;
    }

    public void setGenerationTableList(List<Individual[]> generationTableList) {
        preferences.generationTableList = generationTableList;
    }

    void runEvolution(BotsGUIMainWindow botsGUIMainWindow) {
        cancel = false;
        botsGUIMainWindow.initMemoria();
        //   this.getServer().updateRemainingList(true);
        getServer().setMatchTime(Integer.parseInt(botsGUIMainWindow.timeLimitField.getText()));
        List<EvolutionaryOperator<IndividualV1>> operators = new LinkedList<EvolutionaryOperator<IndividualV1>>();
        int xoverPoints = Integer.parseInt(botsGUIMainWindow.getCrossoverPointsText().getText());
        CandidateFactory<IndividualV1> factory = new IndividualV1Factory();
        if (xoverPoints > 0) {
            operators.add(new IndividualV1Crossover(xoverPoints));
        }



        String humanEvoStr = botsGUIMainWindow.getHumanEvaluationsField().getText();
        if (humanEvoStr != "") {
            humanEva = new IndividualV1HumanEvaluation();
            humanEva.setGenerations(humanEvoStr);
        }
        operators.add(new IndividualV1Mutation(Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100, Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100));
        operators.add(chromosomeCopyOperator);
        //    operators.add(new Replacement<IndividualV1>(factory, new Probability(1)));
        EvolutionaryOperator<IndividualV1> pipeline = new EvolutionPipeline<IndividualV1>(operators);
        FitnessEvaluator<IndividualV1> fitnessEvaluator = new IndividualV1Evaluator();
        Class<?> clazz = (Class<?>) botsGUIMainWindow.getjComboBox1().getSelectedItem();
        SelectionStrategy<Object> selection = initSelectionStrategy();
        Random rng = new MersenneTwisterRNG();
        engine = null;
        if (engine == null) {
            engine = new IndividualV1EvolutionEngine(factory, pipeline, fitnessEvaluator, selection, rng, this);
            engine.setCurrentGeneration(preferences.currentGeneration);
            engine.setEvalNum(preferences.currentEval);
            engine.setSingleThreaded(true);
        }

        if (observer == null) {
            observer = new EvolutionMonitor<IndividualV1>();
        }
        if (observer != null) {

            observer.showInFrame("Monitor", false);
        }
        //     this.setPopulation(this.getMem().loadPoblacion(26));
        populationLength = getPopulation().length;


        Collection<Individual> oldpop = Arrays.asList(this.getPopulation());
        List<EvaluatedCandidate<IndividualV1>> newpop;
        if (humanEvoStr != "") {
            IndividualV1HumanEvaluation humanEva = new IndividualV1HumanEvaluation();
            humanEva.setGenerations(humanEvoStr);
            engine.setHumanEvaluation(humanEva);
        }
        newpop = engine.evolvePopulation(this.getPopulation().length, Integer.parseInt(botsGUIMainWindow.getElitismotextField().getText()), (List<IndividualV1>) (List<?>) oldpop, new GenerationCount(Integer.parseInt(botsGUIMainWindow.getGenerationsField().getText())));
        int count = 0;
        Collections.sort(newpop);
        Collections.reverse(newpop);
        for (EvaluatedCandidate<IndividualV1> c : newpop) {
            this.getPopulation()[count] = c.getCandidate();
            this.getPopulation()[count].resetStats();
            count++;
        }

        this.preferences.setCurrentGeneration(engine.getCurrentGeneration());
    }

    public IndividualV1EvolutionEngine getEngine() {
        return engine;
    }

    public void setEngine(IndividualV1EvolutionEngine engine) {
        this.engine = engine;
    }

    public IndividualV1ChromosomeCopy getChromosomeCopyOperator() {
        return chromosomeCopyOperator;
    }

    public void setChromosomeCopyOperator(IndividualV1ChromosomeCopy chromosomeCopyOperator) {
        this.chromosomeCopyOperator = chromosomeCopyOperator;
    }

    public void killUCCServers() throws IOException {
        if (OSValidator.isWindows()) {
            Runtime.getRuntime().exec("Taskkill /im UCC.exe /f");
        } else {
            Runtime.getRuntime().exec("pkill -f ucc-bin");
        }
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

    public void hillClimbing(Individual individual) {


        observer = new EvolutionMonitor<IndividualV1>();
        observer.showInFrame("Evo", true);
        List<EvolutionaryOperator<IndividualV1>> operators = new LinkedList<EvolutionaryOperator<IndividualV1>>();
        int xoverPoints = Integer.parseInt(botsGUIMainWindow.getCrossoverPointsText().getText());
        CandidateFactory<IndividualV1> factory = new IndividualV1Factory();
        if (xoverPoints > 0) {
            operators.add(new IndividualV1Crossover(xoverPoints));
        }
        operators.add(new IndividualV1Mutation(Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100, Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100));
        operators.add(new Replacement<IndividualV1>(factory, new Probability(1)));
        EvolutionaryOperator<IndividualV1> pipeline = new EvolutionPipeline<IndividualV1>(operators);
        FitnessEvaluator<IndividualV1> fitnessEvaluator = new IndividualV1Evaluator();
        Class<?> clazz = (Class<?>) botsGUIMainWindow.getjComboBox1().getSelectedItem();
        SelectionStrategy<Object> selection = initSelectionStrategy();
        Random rng = new MersenneTwisterRNG();

        engine = new IndividualV1EvolutionEngine(factory, pipeline, fitnessEvaluator, selection, rng, this);
        engine.setCurrentGeneration(preferences.currentGeneration);
        engine.setEvalNum(preferences.currentEval);

        List<IndividualV1> a = ((IndividualV1) individual).generateHillClimbing();
        IndividualV1[] populationArray = new IndividualV1[a.size()];
        a.toArray(populationArray);
        cancel = false;
        iterateOnce(populationArray);

//        getServer().getFinishedJobList().clear();

        // setPopulation(getMem().loadPoblacion(26));

        IndividualV1[] localArray;
        localArray = PopulationAverage.meanAverage(getServer().getIndividualsIterationList(), getServer().getPopulation_size(), getServer().getInterations());
        //     saveXML("./backup.xml");

        List<IndividualV1> oldpop = Arrays.asList(localArray.clone());
        List<IndividualV1> Listv1 = (List<IndividualV1>) (List<?>) oldpop;


        int count = 0;
        IndividualV1[] copyList = new IndividualV1[localArray.length];
        for (IndividualV1 i : Listv1) {
            IndividualV1 newi;
            newi = new IndividualV1(i);
            copyList[count] = newi;
            count++;
        }

        count = 0;

        List<EvaluatedCandidate<IndividualV1>> newpop = engine.evaluate((List<IndividualV1>) (List<?>) oldpop);


        EvolutionUtils.sortEvaluatedPopulation(newpop, true);

        //      Collections.reverse(newpop);
        getGenerationTableList().add(copyList);
        updateGenerationComboBox();

        count = 0;




        if (logger.isDebugEnabled()) {
            logger.debug("nextEvolutionStep(List<EvaluatedCandidate<IndividualV1>>, int, Random) - end"); //$NON-NLS-1$
        }



    }

    public void iterateOnce(Individual[] populationArray) throws NumberFormatException {

        getServer().setMaxThreadsNum(Integer.parseInt(botsGUIMainWindow.threadsNumberField.getText()));
        getServer().setNumAvailableThreads(Integer.parseInt(botsGUIMainWindow.threadsNumberField.getText()));
        int iterations = Integer.parseInt(botsGUIMainWindow.getIterationsField().getText());
        getServer().init(populationArray.length, iterations);
        getServer().getJobList().clear();
        this.populationLength = populationArray.length;

        for (int i = 0; i < iterations * populationArray.length; i++) {
            boolean forceEvaluation = true;

            if (populationArray[i % populationArray.length].ShouldEvaluate() || forceEvaluation) {
                populationArray[i % populationArray.length].setShouldEvaluate(forceEvaluation);
                this.createJob(i, botsGUIMainWindow, populationArray);
            } else {
                for (int j = 0; j < iterations; j++) {
                    getServer().getIndividualsIterationList()[i % populationArray.length][j] = new IndividualV1(populationArray[i % populationArray.length]);
                }
            }
            getServer().getJobList().getFinishedJobs().size();
        }
        int jobSize = getServer().getJobList().size();
        int finishedJobSize = getServer().getJobList().getFinishedJobs().size();
        while (jobSize != finishedJobSize && !cancel) {


            finishedJobSize = getServer().getJobList().getFinishedJobs().size();
            if (getServer().getNumAvailableThreads() > 0 && !server.isLock()) {
                OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

                double load = osBean.getSystemCpuLoad();
                if (!cancel && load < 0.70 && getServer().getJobList().getRemainingJobs().size() > 0) {
                    int id;

                    id = getServer().getJobList().getRemainingJobs().get(0);
                    runMatch(botsGUIMainWindow, id);
                    logger.info("Lanzada partida de Individuo TX" + getServer().getMem().getCurrentGeneration() + id + " .");

                    getServer().enableTimedLock(1 * 60 * 1000);


                }


                for (Job job : server.getJobList().values()) {
                    if (job.getStatus() == Estado.Finished) {
                        job.setMatch(null);
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
            //          getServer().updateRemainingList(true);
        }
//        jobList.removeDeadThreads();
        jobList.clear();

        if (cancel) {
            try {
                this.killUCCServers();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(EvolutionMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (Job job : server.getJobList().values()) {
                job.getThread().stop();
            }
            // throw new Exception("Se ha parado el programa");
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
            //   Job newJob = createJob(id, botsGUIMainWindow);
            server.getJobList().get(id).setThread((new Thread(server.getJobList().get(id).getMatch())));
            server.getJobList().get(id).getThread().setName(server.getJobList().get(id).getMatch().getMatchName());
            server.getJobList().get(id).run();
            server.getJobList().get(id).setStatus(Job.Estado.WaitingID);
            getServer().setNumAvailableThreads(getServer().getNumAvailableThreads() - 1);
            server.updateRemainingList();
            //  jobList.add(newJob.getThread());
            if (BotsGUIMainWindow.logger.isDebugEnabled()) {
                BotsGUIMainWindow.logger.debug("runMatch() - end"); //$NON-NLS-1$
            }
        }
    }

    public void initMemoria() {

        String botpath = botsGUIMainWindow.bot1PathField.getText();
        String botfolder = botpath.substring(0, botpath.lastIndexOf(File.separator) + 1);
        Memoria.setBDNAME(botfolder + "Memoria.db");
        this.getServer().setMemoria(this.getMem());
        //   this.getServer().updateRemainingList(true);
    }

    public void updateGenerationComboBox() {

        getBotsGUIMainWindow().getGenerationsComboBox().setModel(new DefaultComboBoxModel(new String[]{"Actual"}));


        int count = 0;
        for (Individual[] population : this.preferences.getGenerationTableList()) {
            ((DefaultComboBoxModel) getBotsGUIMainWindow().getGenerationsComboBox().getModel()).addElement(count);
            count++;
        }
    }

    private void createGenerationGraph() {
        observer = new EvolutionMonitor<IndividualV1>();
        observer.showInFrame("Evo", true);
        List<EvolutionaryOperator<IndividualV1>> operators = new LinkedList<EvolutionaryOperator<IndividualV1>>();
        int xoverPoints = Integer.parseInt(botsGUIMainWindow.getCrossoverPointsText().getText());
        CandidateFactory<IndividualV1> factory = new IndividualV1Factory();
        if (xoverPoints > 0) {
            operators.add(new IndividualV1Crossover(xoverPoints));
        }

        operators.add(new IndividualV1Mutation(Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100, Double.parseDouble(botsGUIMainWindow.getMutationRatio().getText()) / 100));
        operators.add(new Replacement<IndividualV1>(factory, new Probability(1)));
        EvolutionaryOperator<IndividualV1> pipeline = new EvolutionPipeline<IndividualV1>(operators);
        FitnessEvaluator<IndividualV1> fitnessEvaluator = new IndividualV1Evaluator();
        Class<?> clazz = (Class<?>) botsGUIMainWindow.getjComboBox1().getSelectedItem();
        SelectionStrategy<Object> selection = initSelectionStrategy();
        Random rng = new MersenneTwisterRNG();

        engine = new IndividualV1EvolutionEngine(factory, pipeline, fitnessEvaluator, selection, rng, this);
        engine.setCurrentGeneration(preferences.currentGeneration);
        engine.setEvalNum(preferences.currentEval);
        engine.setSingleThreaded(true);
        int generation = 0;
        for (Individual[] v : preferences.generationTableList) {
            IndividualV1[] v2 = (IndividualV1[]) v;
            List<EvaluatedCandidate<IndividualV1>> x = engine.evaluatePopulationArray(v2);
            EvolutionUtils.sortEvaluatedPopulation(x, true);
            final Integer evalNum = evaluations.get(generation);
            observer.populationUpdate(EvolutionUtils.getPopulationData(x, true, Integer.parseInt(this.preferences.parameters.get("elitismNum")), evalNum, System.currentTimeMillis()));
            generation++;
        }
    }

    private Job createJob(int id, BotsGUIMainWindow botsGUIMainWindow, Individual[] populationArray) throws SecurityException, NumberFormatException {
        LogCategory log = new LogCategory("DeathMatch1v1");
        UT2004DeathMatch1v1 match = new UT2004DeathMatch1v1();
        log.setLevel(Level.ALL);
        match.setLog(log);

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
        match.setNativeBot2(botsGUIMainWindow.getChckbxNativeBot2().isSelected());

        botsGUIMainWindow.initMemoria();

        Job newJob = new Job();
        newJob.setId(id);
        newJob.setMatch(match);
        newJob.setStatus(Job.Estado.Init);
        newJob.setStartTime(new Timestamp(System.currentTimeMillis()));
        newJob.setThread((new Thread(match)));
        newJob.getThread().setName(match.getMatchName());
        newJob.enableTimedLock(1 * 60 * 1000);
        newJob.setIndividual(new IndividualV1(populationArray[id % this.populationLength]));
        newJob.backupIndividual = new IndividualV1(populationArray[id % this.populationLength]);
        newJob.setServer(getServer());
        getServer().getJobList().put(id, newJob);
        return newJob;
    }
}
