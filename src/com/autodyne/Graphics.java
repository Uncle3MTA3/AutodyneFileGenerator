package com.autodyne;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;



public class Graphics extends JFrame implements ActionListener{
	private static final String BUILD_NUMBER = ".129";

	private static final long serialVersionUID = 1L;
	private JTextField pdfLocTextField,imageLocTextField;
	private JButton pdfLoc;
	private JButton imageLoc;
	private final JProgressBar pb = new JProgressBar(0, 100);
	private JCheckBox clampingCB, errorsCB, sensorsCB, typeCB, excelCB;
	private final JList<String> partList = new JList<>();
    private JMenuItem exit, about, instructions;
    private JButton startB, exitB;

    private Graphics() {
        System.out.println("Program Started");
        //System.out.println("Using JDK version " + System.getProperty("java.version"));
        System.out.println("Using JRE version " + System.getProperty("java.runtime.version"));
        System.out.println("Build " + BUILD_NUMBER.substring(1));
		prepareGUI();
	}
	
	private void prepareGUI() {
        ImageIcon img = new ImageIcon("C:\\Data\\_Test\\logo.jpg");
        this.setIconImage(img.getImage());
		int y = 500;
		int x = 700;
		this.setSize(x, y);
		this.setTitle("Autodyne/Brose File Generator - build 1.2" + BUILD_NUMBER);
    	this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }

        // load PDF schematic
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

        // load images folder
	    JLabel imageLocText = new JLabel("Tool Images Folder");
	    imageLocText.setBounds(25,75,200,25);
	    add(imageLocText);
	    imageLocTextField = new JTextField();
	    imageLocTextField.setBounds(25,100,200,25);
	    add(imageLocTextField);
	    imageLoc = new JButton("Browse");
	    imageLoc.setBounds(225,100,100,25);
	    imageLoc.addActionListener(this);
	    add(imageLoc);

        // set up menu system
	    JMenuBar mb = new JMenuBar();
	    JMenu file = new JMenu("File");  
	    exit = new JMenuItem("Exit");
	    exit.addActionListener(this);
	    file.add(exit);
	    JMenu help = new JMenu("Help");
	    instructions = new JMenuItem("Instructions");
        about = new JMenuItem("About");
	    help.add(instructions);
        help.add(about);
        instructions.addActionListener(this);
        about.addActionListener(this);
	    mb.add(file);
	    mb.add(help);
	    setJMenuBar(mb);

	    // set up check boxes
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

	    // set up display window
	    JScrollPane scrollList = new JScrollPane(partList);
	    scrollList.setBounds(25,150,400,200);
	    scrollList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    add(scrollList);

	    // set up progress bar
	    pb.setBounds(50, y -125, x -275,25);
	    pb.setStringPainted(true);
	    add(pb);

	    // set up buttons
		startB = new JButton("Start");
	    startB.setBounds(x -200, y -125,75,25);
        startB.addActionListener(this);
	    add(startB);
		exitB = new JButton("Exit");
	    exitB.setBounds(x -125, y -125,75,25);
	    exitB.addActionListener(this);
	    add(exitB);
	}

	private void loadPDFFile() {
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
    }

    private void loadImageDirectory() {
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
    }

    private void startProgram() {
        final SchematicParser worker = new SchematicParser(pdfLocTextField.getText(),
                imageLocTextField.getText(),
                clampingCB.isSelected(),
                errorsCB.isSelected(),
                sensorsCB.isSelected(),
                typeCB.isSelected(),
                excelCB.isSelected(),
                partList,
                pb);
        worker.execute();
    }

	private void instructionsPopup() {
        String output =
                "<html><h2>Modify template file using following schema:</h2>" +
                  "<table>" +
                    "<tr>" +
                      "<th>variable name</th>" +
                      "<th>details</th>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%clamping_sequence%</b></td>" +
                      "<td>includes step comments, valve actuation and rough sensors</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%unclamping_sequence%</b></td>" +
                      "<td>includes step comments, valve actuation and rough sensors</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%part_sensors%</b></td>" +
                      "<td>waitdi list of part sensors</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%valve_sensors%</b></td>" +
                      "<td>waitdi list of all sensors</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%part_type%</b></td>" +
                      "<td>i.e. C53462, E04466, etc...</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%frame%</b></td>" +
                      "<td>i.e. 1A, 2B, etc...</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%part_name%</b></td>" +
                      "<td>i.e. DAG LH OB LR, etc...</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%side%</b></td>" +
                      "<td>i.e. 1, 2</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%frame_group%</b></td>" +
                      "<td>i.e. AC, BD, etc...</td>" +
                    "</tr>" +
                    "<tr>" +
                      "<td><b>%part_sensor_andor%</b></td>" +
                      "<td>sensor=high and/or sensor=high, etc...</td>" +
                    "</tr>" +
                  "</table>" +
                  "<li>Brose for pdf schematic" +
                  "<li> Optional: brose for tool image directory" +
                  "<li>Select which files you want to create" +
                  "<li>Press start" +
                  "<li>Files will be generated in same location as pdf file" +
                "</html>";
        JOptionPane.showMessageDialog(null,output);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == about) {
            String output = "Version: 1.0\nBuild: " + BUILD_NUMBER.substring(1);
            JOptionPane.showMessageDialog(null, output);
        } else if(e.getSource() == pdfLoc){
            loadPDFFile();
        } else if(e.getSource() == imageLoc){
            loadImageDirectory();
        } else if(e.getSource() == instructions) {
            instructionsPopup();
        } else if(e.getSource() == startB) {
            startProgram();
        } else if(e.getSource() == exit || e.getSource() == exitB) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Graphics().setVisible(true));
	}
}
