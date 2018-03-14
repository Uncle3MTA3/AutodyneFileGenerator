package com.autodyne;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;



public class Graphics extends JFrame implements ActionListener{
	public static final String BUILD_NUMBER = "4";

	private static final long serialVersionUID = 1L;
	private JTextField pdfLocTextField,imageLocTextField;
	private JButton pdfLoc;
	private JButton imageLoc;
	private final JProgressBar pb = new JProgressBar(0, 100);
	private JCheckBox clampingCB, errorsCB, sensorsCB, typeCB, excelCB;
	private final JList<String> partList = new JList<>();
    private JMenuItem about;
    private JButton startB;

    private Graphics() {
		prepareGUI();
		System.out.println("Program Started");
		System.out.println("Using JDK version " + System.getProperty("java.version"));
        System.out.println("Using JRE version " + System.getProperty("java.runtime.version"));
		System.out.println("Build " + BUILD_NUMBER);
	}
	
	private void prepareGUI() {
		int y = 500;
		int x = 700;
		setSize(x, y);
		setTitle("Autodyne/Brose File Generator - build " + BUILD_NUMBER);
		GroupLayout layout = new GroupLayout(this);
    	setLayout(layout);
    	layout.setAutoCreateGaps(true);
    	layout.setAutoCreateContainerGaps(true);
    	setLayout(new BorderLayout());
    	setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel pdfLocText = new JLabel("PDF Schematic");
		pdfLocText.setBounds(25,25,200,25);
		add(pdfLocText);
		pdfLocTextField = new JTextField();
		pdfLocTextField.setBounds(25,50,200,25);
		add(pdfLocTextField);
		pdfLoc = new JButton("Browse");
	    pdfLoc.setBounds(225,50,100,25);
	    pdfLoc.addActionListener(this);
	    add(pdfLoc);
	    
	    JLabel imageLocText = new JLabel("Part Images Folder");
	    imageLocText.setBounds(25,75,200,25);
	    add(imageLocText);
	    imageLocTextField = new JTextField();
	    imageLocTextField.setBounds(25,100,200,25);
	    add(imageLocTextField);
	    imageLoc = new JButton("Browse");
	    imageLoc.setBounds(225,100,100,25);
	    imageLoc.addActionListener(this);
	    add(imageLoc);
        
	    JMenuBar mb = new JMenuBar();
	    JMenu file = new JMenu("File");  
	    JMenuItem exit = new JMenuItem("Exit");
	    exit.addActionListener(e -> System.exit(0));
	    file.add(exit);
	    JMenu help = new JMenu("Help");
	    JMenuItem instructions = new JMenuItem("Instructions");
        about = new JMenuItem("About");
	    help.add(instructions);
        help.add(about);
        about.addActionListener(this);
	    mb.add(file);
	    mb.add(help);
	    setJMenuBar(mb);
	    
	    clampingCB = new JCheckBox();
	    clampingCB.setBounds(x -250,50,250,50);
	    clampingCB.setText("Generate Clamping");
	    clampingCB.setSelected(true);
	    errorsCB = new JCheckBox();
	    errorsCB.setBounds(x -250,100,250,50);
	    errorsCB.setText("Generate Errors");
	    errorsCB.setSelected(true);
	    sensorsCB = new JCheckBox();
	    sensorsCB.setBounds(x -250,150,250,50);
	    sensorsCB.setText("Generate Sensors");
	    sensorsCB.setSelected(true);
	    typeCB = new JCheckBox();
	    typeCB.setBounds(x -250,200,250,50);
	    typeCB.setText("Generate Part Type Database");
	    typeCB.setSelected(true);
	    excelCB = new JCheckBox();
	    excelCB.setBounds(x -250,250,250,50);
	    excelCB.setText("Generate Excel Summary");
	    excelCB.setSelected(true);
	    add(clampingCB);
	    add(errorsCB);
	    add(sensorsCB);
	    add(typeCB);
	    add(excelCB);
	    
	    JScrollPane scrollList = new JScrollPane(partList);
	    scrollList.setBounds(25,150,400,200);
	    scrollList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    add(scrollList);
	    
	    pb.setBounds(50, y -125, x -275,25);
	    pb.setStringPainted(true);
	    add(pb);

		startB = new JButton("Start");
	    startB.setBounds(x -200, y -125,75,25);

        startB.addActionListener(this);
	    add(startB);

		JButton exitB = new JButton("Exit");
	    exitB.setBounds(x -125, y -125,75,25);
	    exitB.addActionListener(this);
	    exitB.addActionListener(e -> System.exit(0));
	    add(exitB);

	}

	public void actionPerformed(ActionEvent e) {  
		if(e.getSource() == pdfLoc){
			JFileChooser fc = new JFileChooser("C:\\");
			FileNameExtensionFilter pdffilter = new FileNameExtensionFilter(
				     "pdf files (*.pdf)", "pdf");
			fc.setFileFilter(pdffilter);
		    int i = fc.showOpenDialog(this);
		    if(i == JFileChooser.APPROVE_OPTION){    
		        File f=fc.getSelectedFile();
		        String filepath=f.getPath();
		        pdfLocTextField.setText(filepath);
		   }
		} else if(e.getSource() == imageLoc){
			JFileChooser fc;
			if(pdfLocTextField.getText().equals("")) {
				fc = new JFileChooser("C:\\");
			} else {
				fc = new JFileChooser((new File(pdfLocTextField.getText())).getPath());
			}
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    int i = fc.showOpenDialog(this);
		    if(i == JFileChooser.APPROVE_OPTION){
		        File f = fc.getSelectedFile();
		        String filepath = f.getAbsolutePath();
		        imageLocTextField.setText(filepath);
		    }
		} else if(e.getSource() == about) {
            JOptionPane.showMessageDialog(null, "Version: 1.0\nBuild: " + BUILD_NUMBER);
        } else if(e.getSource() == startB) {
//            Runnable parser = new PDFParser(pdfLocTextField.getText(), imageLocTextField.getText(),
//                    clampingCB.isSelected(),
//                    errorsCB.isSelected(),
//                    sensorsCB.isSelected(),
//                    typeCB.isSelected(),
//                    excelCB.isSelected(),
//                    partList,
//                    pb);
//            Thread t = new Thread(parser);
//            t.start();
		final PDFParser worker = new PDFParser(pdfLocTextField.getText(), imageLocTextField.getText(),
												clampingCB.isSelected(),
												errorsCB.isSelected(),
												sensorsCB.isSelected(),
												typeCB.isSelected(),
												excelCB.isSelected(),
												partList,
                                                pb);
								worker.addPropertyChangeListener(evt -> {

								});
								worker.execute();
        }
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Graphics().setVisible(true));
	}
}
