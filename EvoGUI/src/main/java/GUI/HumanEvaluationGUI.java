package GUI;

import evolutionaryComputation.Individual;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class HumanEvaluationGUI extends JDialog {
	private JTable table;
        private ArrayList<Boolean> selectedGroups=new ArrayList<Boolean>();

    public ArrayList<Boolean> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(ArrayList<Boolean> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }
        private ArrayList<JCheckBox> checkBoxList=new ArrayList<JCheckBox>();

    public ArrayList<JCheckBox> getCheckBoxList() {
        return checkBoxList;
    }

    public void setCheckBoxList(ArrayList<JCheckBox> checkBoxList) {
        this.checkBoxList = checkBoxList;
    }
	/**
	 * Launch the application.
	 */

	/**
	 * Create the dialog.
	 */
	public HumanEvaluationGUI(Individual individual,int id) {
		setBounds(100, 100, 781, 300);
                
                    Individual[] individualArray=new Individual[1];
                individualArray[0]=individual;
             
		getContentPane().setLayout(new MigLayout("", "[grow]", "[59.00px,grow][][][30.00,grow][][][][grow][][35px]"));
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, "cell 0 0,grow");
			{
				table = new JTable();
				scrollPane.setViewportView(table);
				table.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"New column"
					}
				));
			}
			BotsGUIMainWindow.populateTable(individualArray, table);
                       table.getModel().setValueAt(id, 0, 0);
		}
		{
			JCheckBox chkBox1 = new JCheckBox("\u00BFMide bien las distancias a la hora de atacar y defender?\r\n ");
			getContentPane().add(chkBox1, "cell 0 1");
                        checkBoxList.add(chkBox1);
		}
		{
			JCheckBox chkBox2 = new JCheckBox("\u00BFSelecciona las armas correctamente seg\u00FAn la situaci\u00F3n?");
			getContentPane().add(chkBox2, "cell 0 2");
                        checkBoxList.add(chkBox2);
		}
		{
			JCheckBox chkBox3 = new JCheckBox("\u00BFDetermina correctamente su comportamiento ofensivo/defensivo en relaci\u00F3n a sus puntos de vida?");
			getContentPane().add(chkBox3, "cell 0 3");
                        checkBoxList.add(chkBox3);
		}
		{
			JCheckBox chkBox4 = new JCheckBox("\u00BFAsume riesgos correctamente?");
			getContentPane().add(chkBox4, "cell 0 4");
                        checkBoxList.add(chkBox4);
		}
		{
			JCheckBox chkBox5 = new JCheckBox("\u00BFPersigue a los enemigos durante un tiempo razonable?");
			getContentPane().add(chkBox5, "cell 0 5");
                        checkBoxList.add(chkBox5);
		}
		{
			JCheckBox chkBox6 = new JCheckBox("\u00BFRecoge los items correctamente?");
			getContentPane().add(chkBox6, "cell 0 6");
                        checkBoxList.add(chkBox6);
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, "cell 0 7,grow");
			panel.setLayout(new MigLayout("", "[][]", "[][]"));
			{
				JButton btnNewButton = new JButton("Continuar");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
                                            selectedGroups.clear();
                                            
                                            for(JCheckBox chk:checkBoxList){
                                                selectedGroups.add(chk.isSelected());
                                            }
                                      
					}
				});
				panel.add(btnNewButton, "cell 0 0");
			}
			{
				JButton btnEvaluar = new JButton("Evaluar");
				panel.add(btnEvaluar, "cell 1 0");
			}
		}
	}

       
}
