// Copyright (C) 2013  Julián Urbano <urbano.julian@gmail.com>
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.

package jurbano.melodyshape.ui;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jurbano.melodyshape.MelodyShape;
import jurbano.melodyshape.comparison.MelodyComparer;
import jurbano.melodyshape.model.Melody;
import jurbano.melodyshape.model.MelodyCollection;
import jurbano.melodyshape.ranking.Result;
import jurbano.melodyshape.ranking.ResultRanker;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.JTextArea;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

/**
 * Runs an algorithm with options specified through a graphical user interface.
 * 
 * @author Julián Urbano
 * @see UIObserver
 */
@SuppressWarnings("serial")
public class GraphicalUIObserver extends JFrame implements UIObserver {
	protected JTextField textFieldCutoff;
	protected JTextField textFieldQueries;
	protected JTextField textFieldCollection;
	protected JButton btnRun;
	protected JProgressBar progressBarQuery;
	protected JProgressBar progressBarOverall;
	protected JButton btnBrowseQueries;
	protected JButton btnBrowseCollection;
	protected JComboBox<String> comboBoxAlgorithms;
	protected JComboBox<Integer> comboBoxThreads;
	protected JCheckBox chckbxCutoff;
	protected JCheckBox chckbxSingleLineMode;

	protected JFileChooser chooser;
	protected boolean running;
	protected Thread thread;
	protected ArrayList<Melody> queries;
	protected MelodyCollection coll;
	protected JLabel lblStatus;
	protected JTextArea textAreaResults;
	protected JScrollPane scrollPane;
	protected JPopupMenu popupMenu;
	protected JMenuItem mntmCopyAll;
	protected JMenuItem mntmNewMenuItem;

	/**
	 * Constructs a new {@code GraphicalUIObserver}.
	 */
	public GraphicalUIObserver() {
		setResizable(false);
		setTitle("MelodyShape v" + MelodyShape.VERSION);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		btnRun = new JButton("Run");
		this.btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				synchronized (btnRun) {
					if (running) {
						thread.interrupt();
						thread = null;

						lblStatus.setText("Execution interrupted");
						btnRun.setText("Run");
						// enable gui
						btnBrowseQueries.setEnabled(true);
						textFieldQueries.setEnabled(true);
						btnBrowseCollection.setEnabled(true);
						textFieldCollection.setEnabled(true);
						comboBoxAlgorithms.setEnabled(true);
						comboBoxThreads.setEnabled(true);
						chckbxCutoff.setEnabled(true);
						textFieldCutoff.setEnabled(chckbxCutoff.isSelected());
						chckbxCutoff.setEnabled(true);
						chckbxSingleLineMode.setEnabled(true);
						running = false;
					} else {
						textAreaResults.setText("");
						progressBarQuery.setValue(0);
						progressBarOverall.setValue(0);

						btnRun.setText("Stop");

						running = true;
						// disable gui
						btnBrowseQueries.setEnabled(false);
						textFieldQueries.setEnabled(false);
						btnBrowseCollection.setEnabled(false);
						textFieldCollection.setEnabled(false);
						comboBoxAlgorithms.setEnabled(false);
						comboBoxThreads.setEnabled(false);
						chckbxCutoff.setEnabled(false);
						textFieldCutoff.setEnabled(false);
						chckbxCutoff.setEnabled(false);
						chckbxSingleLineMode.setEnabled(false);

						thread = new Thread() {
							@Override
							public void run() {								
								int kOpt = chckbxCutoff.isSelected() ? Integer.parseInt(textFieldCutoff.getText())
										: Integer.MAX_VALUE;
								
								String aOpt = comboBoxAlgorithms.getSelectedItem().toString();
								MelodyComparer comparer = MelodyShape.getMainComparer(aOpt, coll);
								ResultRanker ranker = MelodyShape.getMainRanker(aOpt, coll);
								MelodyComparer comparerRerank = MelodyShape.getRerankComparer(aOpt, coll); // for 201x-shapetime
								ResultRanker rankerRerank = MelodyShape.getRerankRanker(aOpt, coll); // for 201x-shapetime
								
								try {
									for (int queryNum = 0; queryNum < queries.size(); queryNum++) {
										Melody query = queries.get(queryNum);
										// run
										lblStatus.setText("(" + (queryNum + 1) + "/" + queries.size() + ") "
												+ query.getId() + "...");
										Result[] results = MelodyShape.runAlgorithm(comparer, comparerRerank, ranker,
												rankerRerank, kOpt, queries, queryNum, coll,
												comboBoxThreads.getSelectedIndex() + 1, GraphicalUIObserver.this);
										lblStatus.setText("(" + (queryNum + 1) + "/" + queries.size() + ") "
												+ query.getId() + "...done.");
										// print results
										StringBuffer text = new StringBuffer();
										for (int k = 0; k < kOpt && k < results.length; k++) {
											Result res = results[k];
											if (queries.size() == 1) {
												// just one query, don't output
												// query ID
												if (chckbxSingleLineMode.isSelected())
													if (k + 1 < kOpt && k + 1 < results.length)
														text.append(res.getMelody().getId() + "\t");
													else
														text.append(res.getMelody().getId() + "\n");
												else
													text.append(res.getMelody().getId() + "\t"
															+ String.format(Locale.ENGLISH, "%.8f", res.getScore())
															+ "\n");
											} else {
												// several queries, output query
												// IDs
												if (chckbxSingleLineMode.isSelected()) {
													if (k == 0)
														text.append(queries.get(queryNum).getId() + "\t");
													if (k + 1 < kOpt && k + 1 < results.length)
														text.append(res.getMelody().getId() + "\t");
													else
														text.append(res.getMelody().getId() + "\n");
												} else
													text.append(queries.get(queryNum).getId() + "\t"
															+ res.getMelody().getId() + "\t"
															+ String.format(Locale.ENGLISH, "%.8f", res.getScore())
															+ "\n");
											}
										}
										textAreaResults.setText(textAreaResults.getText() + text.toString());
									}
								} catch (Exception ex) {
									lblStatus.setText("Execution interrupted");
								} finally {
									synchronized (btnRun) {
										thread = null;

										// enable gui
										btnBrowseQueries.setEnabled(true);
										textFieldQueries.setEnabled(true);
										btnBrowseCollection.setEnabled(true);
										textFieldCollection.setEnabled(true);
										comboBoxAlgorithms.setEnabled(true);
										comboBoxThreads.setEnabled(true);
										chckbxCutoff.setEnabled(true);
										textFieldCutoff.setEnabled(chckbxCutoff.isSelected());
										chckbxCutoff.setEnabled(true);
										chckbxSingleLineMode.setEnabled(true);
										btnRun.setText("Run");
										running = false;
									}
								}
							}
						};
						thread.start();
					}
				}
			}
		});
		btnRun.setEnabled(false);
		btnRun.setBounds(335, 148, 89, 47);
		getContentPane().setLayout(null);

		JLabel lblQueryProgress = new JLabel("Query progress:");
		lblQueryProgress.setBounds(12, 151, 98, 14);
		lblQueryProgress.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(lblQueryProgress);

		progressBarQuery = new JProgressBar();
		progressBarQuery.setStringPainted(true);
		progressBarQuery.setBounds(120, 148, 203, 21);
		getContentPane().add(progressBarQuery);
		getContentPane().add(btnRun);

		JLabel lblOverallProgress = new JLabel("Overall progress:");
		lblOverallProgress.setBounds(12, 176, 98, 16);
		lblOverallProgress.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(lblOverallProgress);

		progressBarOverall = new JProgressBar();
		progressBarOverall.setStringPainted(true);
		progressBarOverall.setBounds(120, 174, 203, 21);
		getContentPane().add(progressBarOverall);

		textFieldQueries = new JTextField();
		textFieldQueries.setEditable(false);
		textFieldQueries.setEnabled(false);
		textFieldQueries.setBounds(80, 13, 250, 20);
		getContentPane().add(textFieldQueries);
		textFieldQueries.setColumns(10);

		JLabel lblQueryFile = new JLabel("Queries:");
		lblQueryFile.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQueryFile.setBounds(12, 16, 59, 14);
		getContentPane().add(lblQueryFile);

		JLabel lblCollection = new JLabel("Collection:");
		lblCollection.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCollection.setBounds(12, 43, 59, 16);
		getContentPane().add(lblCollection);

		textFieldCollection = new JTextField();
		textFieldCollection.setEnabled(false);
		textFieldCollection.setEditable(false);
		textFieldCollection.setBounds(80, 41, 250, 20);
		getContentPane().add(textFieldCollection);
		textFieldCollection.setColumns(10);

		btnBrowseQueries = new JButton("Browse...");
		btnBrowseQueries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "MIDI files (*.mid;*.midi)";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						return f.getName().toLowerCase().endsWith(".mid")
								|| f.getName().toLowerCase().endsWith(".midi");
					}
				});
				int ret = chooser.showOpenDialog(GraphicalUIObserver.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File[] files = chooser.getSelectedFiles();

					try {
						lblStatus.setText("Reading queries...");
						ArrayList<Melody> newqueries = new ArrayList<Melody>();
						StringBuffer text = new StringBuffer();
						for (File file : files) {
							text.append(";");
							text.append(file.getName());
							Melody query = MelodyShape.readQueries(file).get(0);
							newqueries.add(query);
						}
						lblStatus.setText("Reading queries... " + newqueries.size() + " read.");

						textFieldQueries.setText(text.deleteCharAt(0).toString());
						queries = newqueries;

						textFieldQueries.setEnabled(true);
						btnBrowseCollection.setEnabled(true);
					} catch (IllegalArgumentException ex) {
						if (queries == null) {
							textFieldQueries.setEnabled(false);
							btnBrowseCollection.setEnabled(false);
						}
						lblStatus.setText("Error reading queries.");
						JOptionPane.showMessageDialog(GraphicalUIObserver.this, ex.getMessage(), "Invalid query file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnBrowseQueries.setEnabled(false);
		btnBrowseQueries.setBounds(335, 12, 89, 23);
		getContentPane().add(btnBrowseQueries);

		btnBrowseCollection = new JButton("Browse...");
		this.btnBrowseCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileFilter(null);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ret = chooser.showOpenDialog(GraphicalUIObserver.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						lblStatus.setText("Reading documents...");
						MelodyCollection newcoll = MelodyShape.readCollection(chooser.getSelectedFile());
						if (newcoll.size() == 0)
							throw new IllegalArgumentException("No MIDI files in the directory.");
						lblStatus.setText("Reading documents... " + newcoll.size() + " read.");

						textFieldCollection.setText(chooser.getSelectedFile().getName());
						coll = newcoll;

						textFieldCollection.setEnabled(true);
						comboBoxAlgorithms.setEnabled(true);
						comboBoxThreads.setEnabled(true);
						chckbxCutoff.setEnabled(true);
						textFieldCutoff.setEditable(chckbxCutoff.isSelected());
						chckbxSingleLineMode.setEnabled(true);
						btnRun.setEnabled(true);
					} catch (IllegalArgumentException ex) {
						lblStatus.setText("Error reading documents.");
						if (coll == null) {
							textFieldCollection.setEnabled(false);
							comboBoxAlgorithms.setEnabled(false);
							comboBoxThreads.setEnabled(false);
							chckbxCutoff.setEnabled(false);
							textFieldCutoff.setEditable(false);
							chckbxSingleLineMode.setEnabled(false);
							btnRun.setEnabled(false);
						}
						JOptionPane.showMessageDialog(GraphicalUIObserver.this, ex.getMessage(),
								"Invalid collection directory", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnBrowseCollection.setEnabled(false);
		btnBrowseCollection.setBounds(335, 40, 89, 23);
		getContentPane().add(btnBrowseCollection);

		JPanel panelOptions = new JPanel();
		panelOptions.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelOptions.setBounds(12, 65, 412, 75);
		getContentPane().add(panelOptions);
		panelOptions.setLayout(null);

		JLabel lblAlgorithm = new JLabel("Algorithm:");
		lblAlgorithm.setBounds(10, 22, 58, 16);
		panelOptions.add(lblAlgorithm);
		lblAlgorithm.setHorizontalAlignment(SwingConstants.RIGHT);

		comboBoxAlgorithms = new JComboBox<String>();
		comboBoxAlgorithms.setEnabled(false);
		comboBoxAlgorithms.setBounds(73, 20, 127, 20);
		panelOptions.add(comboBoxAlgorithms);

		JLabel lblThreads = new JLabel("Threads:");
		lblThreads.setHorizontalAlignment(SwingConstants.RIGHT);
		lblThreads.setBounds(10, 50, 58, 14);
		panelOptions.add(lblThreads);

		comboBoxThreads = new JComboBox<Integer>();
		comboBoxThreads.setEnabled(false);
		comboBoxThreads.setBounds(73, 47, 127, 20);
		panelOptions.add(comboBoxThreads);

		chckbxCutoff = new JCheckBox("Cutoff:");
		this.chckbxCutoff.setSelected(true);
		chckbxCutoff.setEnabled(false);
		chckbxCutoff.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JCheckBox cb = (JCheckBox) arg0.getSource();
				if (cb.isSelected()) {
					textFieldCutoff.setEnabled(true);
				} else {
					textFieldCutoff.setEnabled(false);
				}
			}
		});
		chckbxCutoff.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxCutoff.setBounds(208, 18, 62, 24);
		panelOptions.add(chckbxCutoff);

		textFieldCutoff = new JTextField();
		this.textFieldCutoff.setEnabled(false);
		this.textFieldCutoff.setText("10");
		textFieldCutoff.setBounds(275, 19, 125, 20);
		textFieldCutoff.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				check();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				check();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				check();
			}

			protected void check() {
				String text = textFieldCutoff.getText();
				try {
					int k = Integer.parseInt(text);
					if (k > 0) {
						btnRun.setEnabled(true);
					} else {
						Toolkit.getDefaultToolkit().beep();
						btnRun.setEnabled(false);
					}
				} catch (NumberFormatException ex) {
					Toolkit.getDefaultToolkit().beep();
					btnRun.setEnabled(false);
				}
			}
		});
		panelOptions.add(textFieldCutoff);

		chckbxSingleLineMode = new JCheckBox("Single line mode");
		chckbxSingleLineMode.setEnabled(false);
		chckbxSingleLineMode.setBounds(208, 43, 118, 24);
		panelOptions.add(chckbxSingleLineMode);

		JPanel panelResults = new JPanel();
		panelResults.setBorder(new TitledBorder(null, "Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelResults.setBounds(12, 201, 412, 244);
		getContentPane().add(panelResults);
		panelResults.setLayout(null);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setBounds(12, 22, 388, 210);
		panelResults.add(this.scrollPane);

		this.textAreaResults = new JTextArea();
		this.scrollPane.setViewportView(this.textAreaResults);
		this.textAreaResults.setFont(new Font("Courier New", Font.PLAIN, 12));
		this.textAreaResults.setTabSize(4);
		this.textAreaResults.setEditable(false);

		this.popupMenu = new JPopupMenu();
		addPopup(this.textAreaResults, this.popupMenu);

		this.mntmCopyAll = new JMenuItem("Copy All");
		this.mntmCopyAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(textAreaResults.getText()), null);
			}
		});
		this.popupMenu.add(this.mntmCopyAll);

		for (String alg : MelodyShape.ALGORITHMS)
			comboBoxAlgorithms.addItem(alg);
		comboBoxAlgorithms.setSelectedIndex(0);
		int maxThreads = Runtime.getRuntime().availableProcessors();
		for (int i = 1; i <= maxThreads; i++)
			comboBoxThreads.addItem(i);
		comboBoxThreads.setSelectedIndex(maxThreads - 1);

		this.lblStatus = new JLabel("Select query files first");
		this.lblStatus.setEnabled(false);
		this.lblStatus.setBounds(12, 450, 353, 14);
		getContentPane().add(this.lblStatus);

		this.mntmNewMenuItem = new JMenuItem("About...");
		this.mntmNewMenuItem.setBounds(370, 449, 54, 21);
		getContentPane().add(this.mntmNewMenuItem);
		this.mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(GraphicalUIObserver.this, MelodyShape.COPYRIGHT_NOTICE);
			}
		});
	}

	@Override
	public void start() {
		this.queries = null;
		this.coll = null;
		this.chooser = new JFileChooser();
		this.thread = null;
		this.running = false;

		this.setSize(440, 498);
		this.btnBrowseQueries.setEnabled(true);
		this.setVisible(true);
	}

	@Override
	public void updateProgressComparer(Melody query, int numQuery, int totalQueries, double progress) {
		this.progressBarQuery.setValue((int) (progress * 100));
		double overall = ((double) numQuery) / totalQueries;
		overall += progress / totalQueries;
		this.progressBarOverall.setValue((int) (overall * 100));
	}

	@Override
	public void updateStartRanker(Melody query, int numQuery, int totalQueries) {
		// TODO Auto-generated method stub

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
