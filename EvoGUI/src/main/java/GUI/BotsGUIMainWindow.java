/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import evolutionaryComputation.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import knowledge.Memoria;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;
import synchro.Job;
import utilities.RandomGenerator;

/**
 * @author Jose
 */
public class BotsGUIMainWindow extends javax.swing.JFrame {

    /**
     * Logger for this class
     */
    static final Logger logger = Logger.getLogger(BotsGUIMainWindow.class);
    private JComboBox jComboBox3;
    private JCheckBox[] chkboxArray;

    private void initComboBox() {
        SubClassFinder finder = new SubClassFinder();




        Set<Class<?>> v4 = new HashSet<Class<?>>();
        v4.add(RouletteWheelSelection.class);
        for (Class<?> c : v4) {

            this.getjComboBox1().addItem(c);

        }


        this.jComboBox3.addItem(ComplexFitness.class);
        //  this.jComboBox3.addItem(KadlecFitness.class);
        this.jComboBox3.addItem(RandomFitness.class);
        this.jComboBox3.addItem(NewFitness.class);

        finder = null;
        System.gc();
    }

    /**
     * Creates new form BotsGUIMainWindow
     */
    public BotsGUIMainWindow() {
        main = new EvolutionMain(this);
        initComponents();
        initComboBox();

        jPanel2.setLayout(new MigLayout("fill", "", ""));
        jPanel2.add(jTabbedPane1, "cell 0 0,growx,aligny top");

        panel_2 = new JPanel();
        jTabbedPane1.addTab("New tab", null, panel_2, null);

        generationsComboBox = new JComboBox();
        generationsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (generationsComboBox.getSelectedIndex() == 0) {
                    populateTable(main.getPopulation());
                } else {
                    populateTable(main.getGenerationTableList().get(generationsComboBox.getSelectedIndex() - 1));
                }
            }
        });
        panel_2.setLayout(new MigLayout("", "[1920px,grow]", "[22px][600px,grow,fill][::100px,shrink 0,bottom]"));
        generationsComboBox.setModel(new DefaultComboBoxModel(new String[]{"Actual"}));
        panel_2.add(generationsComboBox, "cell 0 0,alignx left,aligny center,grow");
        jScrollPane1 = new javax.swing.JScrollPane();
        panel_2.add(jScrollPane1, "cell 0 1,grow");
        jTable1 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{{null, null, null, null},
                    {null, null, null, null}, {null, null, null, null},
                    {null, null, null, null}}, new String[]{"Title 1",
                    "Title 2", "Title 3", "Title 4"}));
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        jScrollPane1.setViewportView(jTable1);

        panel_3 = new JPanel();
        panel_2.add(panel_3, "cell 0 2,growx,aligny bottom");
        panel_3.setLayout(new MigLayout("", "[]", "[]"));
        chkboxArray = new JCheckBox[IndividualV1.chromosomeGroup.values().length];
        for (int i = 0; i < chkboxArray.length; i++) {
            chkboxArray[i] = new JCheckBox(IndividualV1.chromosomeGroup.values()[i].name());
            panel_3.add(chkboxArray[i], "cell " + i + " 0");
        }

        panel_1 = new JPanel();
        jTabbedPane1.addTab("Salida", null, panel_1, null);
        panel_1.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][][bottom][][][][][][]"));

        lblTrabajosEnCurso = new JLabel("Trabajos en espera");
        panel_1.add(lblTrabajosEnCurso, "cell 0 0,alignx trailing");

        initJobListText = new JTextField();
        panel_1.add(initJobListText, "cell 1 0,growx");
        initJobListText.setColumns(10);

        lblTrabajosPendientes = new JLabel("Trabajos esperando ID");
        panel_1.add(lblTrabajosPendientes, "cell 0 2,alignx trailing");

        waitingIDJobListText = new JTextField();
        waitingIDJobListText.setColumns(10);
        panel_1.add(waitingIDJobListText, "cell 1 2,growx");

        lblTrabajosEnEjecucin = new JLabel("Trabajos en ejecución");
        panel_1.add(lblTrabajosEnEjecucin, "cell 0 4,alignx trailing");

        runningJobListText = new JTextField();
        runningJobListText.setColumns(10);
        panel_1.add(runningJobListText, "cell 1 4,growx");

        lblTrabajosLanzados = new JLabel("Trabajos terminados");
        panel_1.add(lblTrabajosLanzados, "cell 0 6,alignx trailing");

        finishedJobListText = new JTextField();
        finishedJobListText.setColumns(10);
        panel_1.add(finishedJobListText, "cell 1 6,growx");

        lblThreadsDisponibles = new JLabel("Threads disponibles");
        panel_1.add(lblThreadsDisponibles, "cell 0 8,alignx trailing");

        textField_3 = new JTextField();
        textField_3.setColumns(10);
        panel_1.add(textField_3, "cell 1 8,growx");

        btnNewButton = new JButton("New button");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                updateJobInfo();
            }
        });
        btnNewButton.setAction(action);
        panel_1.add(btnNewButton, "cell 0 15");




        jPanel3 = new javax.swing.JPanel();

        jTabbedPane1.addTab("Evoluci�n", jPanel3);
        jPanel3.setLayout(new MigLayout("", "[grow]", "[grow]"));

        btnAadir = new JButton("A�adir");
        btnAadir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        jPanel3.add(btnAadir, "pos 0.01al 1.0al,cell 0 0");

        panel = new JPanel();
        jPanel3.add(panel, "flowx,cell 0 0,grow");
        panel.setLayout(new MigLayout("", "[pref!,grow][][pref!,grow]", "[][][][][][][][][][grow][]"));

        lblMutacin = new JLabel("Mutaci�n");
        panel.add(lblMutacin, "cell 0 0");

        label = new JLabel("Variaci�n relativa (%)");
        panel.add(label, "cell 0 1");

        setMutationRatio(new JTextField());
        getMutationRatio().setText("10");
        getMutationRatio().setMaximumSize(new Dimension(25, 25));
        getMutationRatio().setColumns(10);
        panel.add(getMutationRatio(), "cell 2 1,growx");

        lblVariacinRelativa = new JLabel("Probabilidad (%)");
        panel.add(lblVariacinRelativa, "cell 0 2");

        mutationProbabilityField = new JTextField();
        mutationProbabilityField.setText("10");
        mutationProbabilityField.setMaximumSize(new Dimension(25, 25));
        panel.add(mutationProbabilityField, "cell 2 2,alignx left");
        mutationProbabilityField.setColumns(10);

        lblCrossover = new JLabel("Crossover");
        panel.add(lblCrossover, "cell 0 4,alignx left");

        lblNDePuntos = new JLabel("N� de puntos");
        panel.add(lblNDePuntos, "cell 0 5");

        crossoverPointsText = new JTextField();
        crossoverPointsText.setText("1");
        crossoverPointsText.setMaximumSize(new Dimension(25, 25));
        crossoverPointsText.setColumns(10);
        panel.add(crossoverPointsText, "cell 2 5,growx");

        lblElitismo = new JLabel("Elitismo");
        panel.add(lblElitismo, "cell 0 7");

        elitismotextField = new JTextField();
        elitismotextField.setText("1");
        elitismotextField.setMaximumSize(new Dimension(25, 25));
        elitismotextField.setColumns(10);
        panel.add(elitismotextField, "cell 2 7,growx");

        lblSemilla = new JLabel("Semilla");
        panel.add(lblSemilla, "cell 0 8");

        randomSeedtextField = new JTextField();
        randomSeedtextField.setText("1");
        randomSeedtextField.setMaximumSize(new Dimension(25, 25));
        randomSeedtextField.setColumns(10);
        panel.add(randomSeedtextField, "cell 2 8,growx");

        separator_2 = new JSeparator();
        separator_2.setBackground(Color.GREEN);
        panel.add(separator_2, "cell 1 10");

        String botpath = this.bot1PathField.getText();
        String botfolder = botpath.substring(0, botpath.lastIndexOf(File.separator) + 1);
        Memoria.setBDNAME(botfolder + "Memoria.db");
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = jTable1.rowAtPoint(e.getPoint());
                if (r >= 0 && r < jTable1.getRowCount()) {
                    jTable1.setRowSelectionInterval(r, r);
                } else {
                    jTable1.clearSelection();
                }

                final int rowindex = jTable1.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu("Menu");
                    JMenuItem menuItem = new JMenuItem("Seleccionar");
                    menuItem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            updateSelectedIndividual(rowindex);
                        }
                    });
                    popup.add(menuItem);
                    JMenuItem menuItem2 = new JMenuItem("Hill Climbing");
                    menuItem2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            main.hillClimbing(main.getPopulation()[rowindex]);
                        }
                    });
                    popup.add(menuItem2);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int r = jTable1.rowAtPoint(e.getPoint());
                if (r >= 0 && r < jTable1.getRowCount()) {
                    jTable1.setRowSelectionInterval(r, r);
                } else {
                    jTable1.clearSelection();
                }

                final int rowindex = jTable1.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu("Menu");
                    JMenuItem menuItem = new JMenuItem("Seleccionar");
                    menuItem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            updateSelectedIndividual(rowindex);
                        }
                    });
                    popup.add(menuItem);
                    JMenuItem menuItem2 = new JMenuItem("Hill Climbing");
                    menuItem2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            main.hillClimbing(main.getPopulation()[rowindex]);
                        }
                    });
                    popup.add(menuItem2);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void updateSelectedIndividual(int index) {
        for (int i = 0; i < IndividualV1.chromosomeGroup.values().length; i++) {
            this.main.getChromosomeCopyOperator().setLockGroupValue(IndividualV1.chromosomeGroup.values()[i], chkboxArray[i].isSelected());
        }
        main.getChromosomeCopyOperator().selectedCandidate = new IndividualV1((IndividualV1) this.main.getPopulation()[index]);
    }

    public JTextField getMutationProbabilityField() {
        return mutationProbabilityField;
    }

    public void setMutationProbabilityField(JTextField mutationProbabilityField) {
        this.mutationProbabilityField = mutationProbabilityField;
    }

    public JComboBox getGenerationsComboBox() {
        return generationsComboBox;
    }

    public void populateTable(Individual[] population) {
        try {

            SAXBuilder builder = new SAXBuilder();

            URL instream = BotsGUIMainWindow.class.getResource("Individual.xml");
            File xmlFile = new File(instream.toURI());
            String individualType = population[0].getClass().getSimpleName();
            try {

                Document document = (Document) builder.build(xmlFile);
                Element rootNode = document.getRootElement();
                List<Element> list = rootNode.getChildren("Individual");
                ArrayList<String> tagsList = new ArrayList<String>();
                ArrayList<String> groupList = new ArrayList<String>();
                for (Element individual : list) {

                    Element type = individual.getChild("type");
                    String typeName = type.getChildText("name");
                    if (typeName.equals(individualType)) {

                        List<Element> attributesList = individual
                                .getChildren("attributes");
                        for (Element attributes : attributesList) {

                            List<Element> singleAttribList = attributes
                                    .getChildren("attribute");
                            List<Element> chromosomesList = attributes
                                    .getChildren("chromosomes");

                            for (Element singleAttrib : singleAttribList) {
                                tagsList.add(singleAttrib.getChildText("name"));

                            }
                            for (Element singleAttrib : chromosomesList) {

                                List<Element> chromosomeList = singleAttrib
                                        .getChildren("chromosome");

                                for (Element chrom : chromosomeList) {
                                    tagsList.add(chrom.getChildText("name"));

                                }

                            }
                        }

                    }

                }
                jTable1.removeAll();
                DefaultTableModel model = new DefaultTableModel();
                model
                        .setColumnIdentifiers(tagsList.toArray());
                int count = 0;
                for (Individual i : population) {
                    Object[] fila = new Object[tagsList.size()];
                    fila[0] = count;
                    fila[1] = i.fitness();
                    fila[2] = i.getDeaths();
                    fila[3] = i.getKills();
                    fila[4] = i.getTotalDamageGiven();
                    fila[5] = i.getTotalDamageTaken();
                    fila[6] = i.getNSuperShields();
                    fila[7] = i.getNShields();
                    fila[8] = i.getTotalTimeShock();
                    fila[9] = i.getTotalTimeSniper();
                    fila[10] = 0;
                    for (int j = 0; j < i.chromosomeSize(); j++) {
                        fila[j + 11] = i.getGene(j);
                    }
                    model.addRow(fila);
                    count++;
                }
                jTable1.setModel(model);


                TableColumnAdjuster tca = new TableColumnAdjuster(jTable1);
                tca.adjustColumns();
            } catch (IOException io) {
                System.out.println(io.getMessage());
            } catch (JDOMException jdomex) {
                System.out.println(jdomex.getMessage());
            }
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(BotsGUIMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openDB(final String path) {
        if (logger.isDebugEnabled()) {
            logger.debug("openDB(String) - start"); //$NON-NLS-1$
        }

        // This is where a real application would open the file.

        ResultSet resultados;
        String sql = "SELECT * FROM Genetico";
        Connection conn = null;
        boolean success = false;
        boolean salir = false;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement stat = conn.createStatement();
            resultados = stat.executeQuery(sql);
            ResultSetMetaData metaDatos = resultados.getMetaData();

            // Se obtiene el n�mero de columnas.
            int numeroColumnas = metaDatos.getColumnCount();

            // Se crea un array de etiquetas para rellenar
            Object[] etiquetas = new Object[numeroColumnas];

            // Se obtiene cada una de las etiquetas para cada columna
            for (int i = 0; i < numeroColumnas; i++) {
                // Nuevamente, para ResultSetMetaData la primera columna es la
                // 1.
                etiquetas[i] = metaDatos.getColumnLabel(i + 1);
            }

            DefaultTableModel modelo = new DefaultTableModel();

            modelo.setColumnIdentifiers(etiquetas);
            this.jTable1.setModel(modelo);

            while (resultados.next()) {
                Object[] fila = new Object[numeroColumnas];
                for (int i = 0; i < numeroColumnas; i++) {
                    fila[i] = resultados.getObject(i + 1);
                }
                modelo.addRow(fila);
            }

            conn.close();
        } catch (Exception e) {

            if (logger.isDebugEnabled()) {
                logger.debug("openDB(String) - OCURRIO UN ERROR RECUPERANDO LOS INDIVIDUOS"); //$NON-NLS-1$
            }
            logger.error("openDB(String)", e); //$NON-NLS-1$
            currentFile = null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("openDB(String) - end"); //$NON-NLS-1$
        }
    }

    @Deprecated
    protected void initMemoria() {
        main.initMemoria();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        bot1PathField = new javax.swing.JTextField();
        timeLimitField = new javax.swing.JTextField();
        pathUTField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        threadsNumberField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        bot2PathField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        fragLimitField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        logPathField = new javax.swing.JTextField();
        mapNameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        setGenerationsField(new javax.swing.JTextField());
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        setjComboBox1(new javax.swing.JComboBox());
        jLabel11 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        setGenerationsField1(new javax.swing.JTextField());
        jLabel13 = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1
                .setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setAlignmentX(0.0F);
        jTabbedPane1.setAlignmentY(0.0F);
        jTabbedPane1.setAutoscrolls(true);

        jLabel3.setText("Threads");

        jLabel8.setText("Limite de tiempo");

        bot1PathField
                .setText("C:\\Users\\Jose\\Documents\\utevobots\\utinteractiveevobot\\evo-331\\target\\EVO-331-1.0-SNAPSHOT.jar");

        timeLimitField.setText("1");

        pathUTField.setText("c:\\ut2004");

        jLabel2.setText("Ruta bot experto");

        threadsNumberField.setText("1");

        jLabel4.setText("Ruta resultados");

        bot2PathField
                .setText("C:\\Users\\Jose\\Documents\\utevobots\\utinteractiveevobot\\EXPERT-331\\target\\EXPERT-331-1.0-SNAPSHOT.jar");

        jLabel5.setText("Ruta UT2004");

        fragLimitField.setText("100000");

        jLabel7.setText("Limite de frags");

        logPathField.setText("C:\\UT2004-Match-Results");

        mapNameField.setText("DM-TrainingDay");

        jLabel6.setText("Mapa");

        jLabel1.setText("Ruta bot evolutivo");

        getGenerationsField().setText("1");

        jLabel9.setText("Generaciones");

        jLabel10.setText("Algoritmo g�netico");

        jLabel11.setText("Algoritmo de cruce");

        jLabel12.setText("Algoritmo de fitness");

        getIterationsField().setText("1");

        jLabel13.setText("Iteraciones");

        jTabbedPane1.addTab("Principal", jPanel1);
        jPanel1.setLayout(new MigLayout("", "[74px][40px][46px][][][1044px]", "[22px][22px][22px][22px][22px][22px][22px][22px][22px][22px][22px][22px][22px][25px][][]"));
        jPanel1.add(jLabel6, "cell 0 1,alignx left,aligny center");
        jPanel1.add(mapNameField, "cell 5 1,growx,aligny top");
        jPanel1.add(jLabel7, "cell 0 8 2 1,alignx left,aligny center");
        jPanel1.add(fragLimitField, "cell 5 8,growx,aligny top");
        jPanel1.add(jLabel4, "cell 0 7 2 1,alignx left,aligny center");
        jPanel1.add(logPathField, "cell 5 7,growx,aligny top");
        jPanel1.add(jLabel9, "cell 0 5 2 1,alignx left,aligny center");
        jPanel1.add(getGenerationsField(), "cell 5 5,growx,aligny top");
        jPanel1.add(jLabel10, "cell 0 10 2 1,alignx left,aligny top");
        jPanel1.add(getjComboBox1(), "cell 5 10,growx,aligny top");
        jPanel1.add(jLabel11, "cell 0 11 2 1,alignx left,aligny top");
        jPanel1.add(jComboBox2, "cell 5 11,growx,aligny top");
        jPanel1.add(jLabel12, "cell 0 12 2 1,alignx left,aligny top");
        jPanel1.add(jComboBox3, "cell 5 12,growx,aligny top");
        jPanel1.add(jLabel1, "cell 0 2 2 1,alignx left,aligny top");
        jPanel1.add(bot1PathField, "cell 5 2,growx,aligny top");
        jPanel1.add(jLabel3, "cell 0 6,alignx left,aligny center");
        jPanel1.add(threadsNumberField, "cell 5 6,growx,aligny top");
        jPanel1.add(jLabel5, "cell 0 0,alignx left,aligny center");
        jPanel1.add(pathUTField, "cell 5 0,growx,aligny top");
        jPanel1.add(jLabel8, "cell 0 9 2 1,alignx left,aligny center");
        jPanel1.add(timeLimitField, "cell 5 9,growx,aligny top");
        jPanel1.add(jLabel2, "cell 0 3 2 1,alignx left,aligny center");
        jPanel1.add(jLabel13, "cell 0 4,alignx left,aligny center");
        jPanel1.add(getIterationsField(), "cell 5 4,growx,aligny top");
        jPanel1.add(bot2PathField, "cell 5 3,growx,aligny top");
        runButton = new javax.swing.JButton();

        runButton.setText("Iniciar");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        jPanel1.add(runButton, "cell 0 15,alignx left,aligny top");
        initializeButton = new javax.swing.JButton();

        initializeButton.setText("Inicializar");
        initializeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButton1ActionPerformed(evt);
            }
        });
        getContentPane().setLayout(new MigLayout("", "[1251px,grow]", "[491px,grow,top]"));
        jPanel1.add(initializeButton, "cell 1 15,alignx left,aligny top");

        btnParar = new JButton();
        btnParar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    main.killUCCServers();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                main.setCancel(true);
                main.jobList.killAll();
                worker.cancel(true);
            }
        });
        btnParar.setEnabled(false);
        btnParar.setText("Parar");
        jPanel1.add(btnParar, "cell 2 15");

        btnGuardar = new JButton();
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                main.saveXML();
            }
        });

        btnCargar = new JButton();
        btnCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                main.loadXML();
            }
        });
        btnCargar.setText("Cargar");
        jPanel1.add(btnCargar, "cell 3 15");
        btnGuardar.setText("Guardar");
        jPanel1.add(btnGuardar, "cell 5 15");

        getContentPane().add(jPanel2, "cell 0 0,growx,aligny top");

        jMenu3.setText("File");

        jMenuItem2.setText("Nuevo");

        jMenu3.add(jMenuItem2);

        jMenuItem1.setText("Abrir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setJMenuBar(jMenuBar2);

        pack();
        initDataBindings();
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (logger.isDebugEnabled()) {
            logger.debug("jMenuItem1ActionPerformed(java.awt.event.ActionEvent) - start"); //$NON-NLS-1$
        }
        // GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        // Create a file chooser
        final JFileChooser fc = new JFileChooser();
        // In response to a button click:
        int returnVal = fc.showDialog(this.jMenuItem1, null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = currentFile = fc.getSelectedFile();
            try {
                openDB(file.getCanonicalPath());
            } catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("jMenuItem1ActionPerformed(java.awt.event.ActionEvent) - " + ex); //$NON-NLS-1$
                }
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("jMenuItem1ActionPerformed(java.awt.event.ActionEvent) - end"); //$NON-NLS-1$
        }
    }// GEN-LAST:event_jMenuItem1ActionPerformed

    private void runButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_runButton1ActionPerformed
        String botpath = this.bot1PathField.getText();
        String botfolder = botpath.substring(0, botpath.lastIndexOf(File.separator) + 1);
        Memoria.setBDNAME(botfolder + "Memoria.db");
        main.setMem(new Memoria(true, true, 26, true));
        RandomGenerator.setRandomSeed(Integer.parseInt(this.randomSeedtextField.getText()));
        int num_individuals = Integer.parseInt(JOptionPane
                .showInputDialog("N�mero de individuos"));

        // TODO add your handling code here:1111
        IndividualV1[] newpopulation = new IndividualV1[num_individuals];

        for (int i = 0; i < num_individuals; i++) {
            IndividualV1 v1 = new IndividualV1(true, (Class<? extends IndividualStats>) this.jComboBox3.getSelectedItem());
            newpopulation[i] = v1;
        }
        final int cloneCandidate = Integer.parseInt(JOptionPane
                .showInputDialog("N�mero de individuos"));
        boolean cloneIndividual = (cloneCandidate != 0);
        if (cloneIndividual) {
            IndividualV1 v1 = newpopulation[cloneCandidate];

            for (int i = 0; i < num_individuals; i++) {
                if (i != cloneCandidate) {
                    newpopulation[i] = v1;
                }
            }
        }
        main.setPopulation(newpopulation);
        main.getMem().storeGenes(0, -1, newpopulation);
        main.observer = new EvolutionMonitor<IndividualV1>();
        main.preferences.currentGeneration = 0;
        openDB(botfolder + "Memoria.db");
        populateTable(newpopulation);
        main.getServer().setMemoria(main.getMem());
        main.setEngine(null);
        if (logger.isDebugEnabled()) {
            logger.debug("nrunButton1ActionPerformed(java.awt.event.ActionEvent) - end"); //$NON-NLS-1$
        }
    }// GEN-LAST:event_runButton1ActionPerformed

    private void enableButtonsCompleted() {
        this.runButton.setEnabled(true);
        this.initializeButton.setEnabled(true);
        this.btnParar.setEnabled(false);
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {

        this.runButton.setEnabled(false);
        this.initializeButton.setEnabled(false);
        this.btnParar.setEnabled(true);

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {

                try {
                    runEvolution();
                    if (isCancelled()) {
                        System.out.println("SwingWorker - isCancelled");

                    }

                } catch (Exception e) {
                    logger.error("Uncaught Exception in SwingWorker", e);
                }
                return null;
            }

            @Override
            public void done() {
                // Remove the "Loading images" label.
                enableButtonsCompleted();
            }
        };
        worker.execute();

    }

    @Deprecated
    private void runEvolution() {
        main.runEvolution(this);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - start"); //$NON-NLS-1$
        }

        /*
         * Set the Nimbus look and feel
         */
        // <editor-fold defaultstate="collapsed"
        // desc=" Look and feel setting code (optional) ">        
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase
         * /tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.error("main(String[])", ex); //$NON-NLS-1$

            java.util.logging.Logger.getLogger(
                    BotsGUIMainWindow.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.error("main(String[])", ex); //$NON-NLS-1$

            java.util.logging.Logger.getLogger(
                    BotsGUIMainWindow.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.error("main(String[])", ex); //$NON-NLS-1$

            java.util.logging.Logger.getLogger(
                    BotsGUIMainWindow.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.error("main(String[])", ex); //$NON-NLS-1$

            java.util.logging.Logger.getLogger(
                    BotsGUIMainWindow.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (logger.isDebugEnabled()) {
                    logger.debug("$Runnable.run() - start"); //$NON-NLS-1$
                }

                new BotsGUIMainWindow().setVisible(true);

                if (logger.isDebugEnabled()) {
                    logger.debug("$Runnable.run() - end"); //$NON-NLS-1$
                }
            }
        });

        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - end"); //$NON-NLS-1$
        }
    }
    EvolutionMain main;
    private File currentFile;
    private SwingWorker worker;
    public boolean cancel = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JTextField bot1PathField;
    javax.swing.JTextField bot2PathField;
    javax.swing.JTextField fragLimitField;
    private javax.swing.JTextField generationsField;
    private javax.swing.JTextField iterationsField;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    javax.swing.JTextField logPathField;
    javax.swing.JTextField mapNameField;
    javax.swing.JTextField pathUTField;
    private javax.swing.JButton runButton;
    private javax.swing.JButton initializeButton;
    javax.swing.JTextField threadsNumberField;
    javax.swing.JTextField timeLimitField;
    private JButton btnAadir;
    private JPanel panel;
    private JSeparator separator;
    private JSeparator separator_2;
    private JLabel lblMutacin;
    private JLabel lblVariacinRelativa;
    private JTextField mutationProbabilityField;
    private JLabel label;
    private JTextField mutationRatio;
    private JLabel lblCrossover;
    private JLabel lblNDePuntos;
    private JTextField crossoverPointsText;
    private JPanel panel_1;
    private JButton btnParar;
    private JLabel lblElitismo;
    private JTextField elitismotextField;
    private JComboBox generationsComboBox;
    private JPanel panel_2;
    private JButton btnGuardar;
    private JButton btnCargar;
    private JLabel lblTrabajosEnCurso;
    private JLabel lblTrabajosPendientes;
    private JTextField initJobListText;
    private JTextField waitingIDJobListText;
    private JLabel lblTrabajosLanzados;
    private JTextField finishedJobListText;
    private JLabel lblThreadsDisponibles;
    private JTextField textField_3;
    private JCheckBox chckbxDistancia;
    private JLabel lblSemilla;
    private JTextField randomSeedtextField;
    private JPanel panel_3;
    private JLabel lblTrabajosEnEjecucin;
    private JTextField runningJobListText;
    private JButton btnNewButton;
    private final Action action = new SwingAction();

    // End of variables declaration//GEN-END:variables
    public JLabel getLblVariacinRelativa() {
        return lblVariacinRelativa;
    }

    public void setLblVariacinRelativa(JLabel lblVariacinRelativa) {
        this.lblVariacinRelativa = lblVariacinRelativa;
    }

    public JTextField getRandomSeedtextField() {
        return randomSeedtextField;
    }

    public void setRandomSeedtextField(JTextField randomSeedtextField) {
        this.randomSeedtextField = randomSeedtextField;
    }

    public JTextField getCrossoverPointsText() {
        return crossoverPointsText;
    }

    public void setCrossoverPointsText(JTextField crossoverPointsText) {
        this.crossoverPointsText = crossoverPointsText;
    }

    public JTextField getMutationRatio() {
        return mutationRatio;
    }

    public void setMutationRatio(JTextField mutationRatio) {
        this.mutationRatio = mutationRatio;
    }

    public javax.swing.JComboBox getjComboBox1() {
        return jComboBox1;
    }

    public void setjComboBox1(javax.swing.JComboBox jComboBox1) {
        this.jComboBox1 = jComboBox1;
    }

    public javax.swing.JTextField getGenerationsField() {
        return generationsField;
    }

    public void setGenerationsField(javax.swing.JTextField generationsField) {
        this.generationsField = generationsField;
    }

    public javax.swing.JTextField getIterationsField() {
        return iterationsField;
    }

    public void setGenerationsField1(javax.swing.JTextField generationsField1) {
        this.iterationsField = generationsField1;
    }

    public JTextField getElitismotextField() {
        return elitismotextField;
    }

    public void updateJobInfo() {

        synchro.JobList jobList = this.main.getServer().getJobList();

        jobList.getRemainingJobs();
        this.initJobListText.setText(jobList.getRemainingJobs().toString());
        this.waitingIDJobListText.setText(jobList.getJobsbyStatus(Job.Estado.WaitingID).toString());
        this.finishedJobListText.setText(jobList.getFinishedJobs().toString());
        this.runningJobListText.setText(jobList.getJobsbyStatus(Job.Estado.Running).toString());
    }

    protected void initDataBindings() {
    }

    private class SwingAction extends AbstractAction {

        public SwingAction() {
            putValue(NAME, "SwingAction");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }

        public void actionPerformed(ActionEvent e) {
        }
    }
}
