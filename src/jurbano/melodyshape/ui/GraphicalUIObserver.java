package jurbano.melodyshape.ui;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jurbano.melodyshape.MelodyShape;
import jurbano.melodyshape.model.Melody;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class GraphicalUIObserver extends JFrame implements UIObserver {
	private JTextField textFieldCutoff;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTable table;

	public GraphicalUIObserver() {
		setResizable(false);
		setTitle("MelodyShape v" + MelodyShape.VERSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton btnRun = new JButton("Run");
		btnRun.setEnabled(false);
		btnRun.setBounds(333, 147, 89, 39);
		getContentPane().setLayout(null);

		JLabel lblQueryProgress = new JLabel("Query progress:");
		lblQueryProgress.setBounds(10, 147, 98, 14);
		lblQueryProgress.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(lblQueryProgress);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(118, 144, 203, 21);
		getContentPane().add(progressBar);
		getContentPane().add(btnRun);

		JLabel lblOverallProgress = new JLabel("Overall progress:");
		lblOverallProgress.setBounds(10, 172, 98, 16);
		lblOverallProgress.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(lblOverallProgress);

		JProgressBar progressBar_1 = new JProgressBar();
		progressBar_1.setStringPainted(true);
		progressBar_1.setBounds(118, 170, 203, 21);
		getContentPane().add(progressBar_1);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setEnabled(false);
		textField_1.setBounds(78, 9, 250, 20);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);

		JLabel lblQueryFile = new JLabel("Queries:");
		lblQueryFile.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQueryFile.setBounds(10, 12, 59, 14);
		getContentPane().add(lblQueryFile);

		JLabel lblCollection = new JLabel("Collection:");
		lblCollection.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCollection.setBounds(10, 39, 59, 16);
		getContentPane().add(lblCollection);

		textField_2 = new JTextField();
		textField_2.setEnabled(false);
		textField_2.setEditable(false);
		textField_2.setBounds(78, 37, 250, 20);
		getContentPane().add(textField_2);
		textField_2.setColumns(10);

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.setBounds(333, 8, 89, 23);
		getContentPane().add(btnBrowse);

		JButton btnBrowse_1 = new JButton("Browse...");
		btnBrowse_1.setBounds(333, 36, 89, 23);
		getContentPane().add(btnBrowse_1);

		JPanel panelOptions = new JPanel();
		panelOptions.setBorder(new TitledBorder(null, "Options",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelOptions.setBounds(10, 61, 412, 75);
		getContentPane().add(panelOptions);
		panelOptions.setLayout(null);

		JLabel lblAlgorithm = new JLabel("Algorithm:");
		lblAlgorithm.setBounds(10, 22, 58, 16);
		panelOptions.add(lblAlgorithm);
		lblAlgorithm.setHorizontalAlignment(SwingConstants.RIGHT);

		JComboBox<String> comboBoxAlgorithms = new JComboBox<String>();
		comboBoxAlgorithms.setBounds(73, 20, 127, 20);
		panelOptions.add(comboBoxAlgorithms);

		JLabel lblThreads = new JLabel("Threads:");
		lblThreads.setHorizontalAlignment(SwingConstants.RIGHT);
		lblThreads.setBounds(10, 50, 58, 14);
		panelOptions.add(lblThreads);

		JComboBox<Integer> comboBoxThreads = new JComboBox<Integer>();
		comboBoxThreads.setBounds(73, 47, 127, 20);
		panelOptions.add(comboBoxThreads);

		JCheckBox chckbxCutoff = new JCheckBox("Cutoff:");
		chckbxCutoff.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JCheckBox cb = (JCheckBox) arg0.getSource();
				if(cb.isSelected()){
					textFieldCutoff.setEnabled(true);
				}else{
					textFieldCutoff.setEnabled(false);
				}
			}
		});
		chckbxCutoff.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxCutoff.setBounds(208, 18, 62, 24);
		panelOptions.add(chckbxCutoff);

		textFieldCutoff = new JTextField();
		textFieldCutoff.setEnabled(false);
		textFieldCutoff.setBounds(275, 19, 125, 20);
		panelOptions.add(textFieldCutoff);
		textFieldCutoff.setColumns(10);

		JCheckBox chckbxSingleLineMode = new JCheckBox("Single line mode");
		chckbxSingleLineMode.setBounds(208, 43, 118, 24);
		panelOptions.add(chckbxSingleLineMode);

		JPanel panelResults = new JPanel();
		panelResults.setBorder(new TitledBorder(null, "Results",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelResults.setBounds(10, 197, 412, 244);
		getContentPane().add(panelResults);
		panelResults.setLayout(null);

		table = new JTable();
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setBounds(10, 22, 392, 211);
		panelResults.add(table);
		
		
		
		
		
		for(String alg: MelodyShape.ALGORITHMS)
			comboBoxAlgorithms.addItem(alg);
		comboBoxAlgorithms.setSelectedIndex(0);	
		int maxThreads = Runtime.getRuntime().availableProcessors();
		for(int i = 1; i <= maxThreads;i++)
			comboBoxThreads.addItem(i);
		comboBoxThreads.setSelectedIndex(maxThreads-1);
	}

	@Override
	public void start() {
		this.setSize(440, 480);
		this.setVisible(true);		
	}

	@Override
	public void updateProgressComparer(Melody query, int numQuery, int totalQueries, double progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStartRanker(Melody query, int numQuery, int totalQueries) {
		// TODO Auto-generated method stub
		
	}
}
