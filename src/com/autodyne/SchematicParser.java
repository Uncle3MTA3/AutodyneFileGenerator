package com.autodyne;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*This is the meat of the program
 * This class reads in the pdf file and extracts the important data
 * it then adds the information to a Tool object that is stored in a List
 */

public class SchematicParser extends SwingWorker<Integer, String>{

	private final String NEW_LINE = System.getProperty("line.separator");
	
	private File path;
	private String imagePath;
	private List<Tool> tools;
	private boolean makeClamping;
	private boolean makeErrors;
	private boolean makeSensors;
	private boolean makePartType;
	private boolean makeExcel;
	private JList<String> status;
	private final JProgressBar bar;
	private String parts[] = new String[100];
	
	SchematicParser(String filePath, String imagePath, boolean clamping, boolean errors, boolean sensors, boolean partType, boolean excelSum, JList<String> jlist, JProgressBar pb) {
		this.path = new File(filePath);
		this.imagePath = imagePath;
		this.tools = new ArrayList<>();
		this.makeClamping = clamping;
		this.makeErrors = errors;
		this.makeSensors = sensors;
		this.makePartType = partType;
		this.makeExcel = excelSum;
		this.status = jlist;
		this.bar = pb;
	}
	
	protected Integer doInBackground() {
		try {
			this.parser();
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
    private void parser() throws IOException {
    	PDDocument document = PDDocument.load(path);
        try {
        	Splitter splitter = new Splitter();
        	List<PDDocument> Pages = splitter.split(document);
			for (PDDocument pd : Pages) {
				PDFTextStripper PDFStrip = new PDFTextStripper();
				String text = PDFStrip.getText(pd);
				if (text.contains("Mech. Layout")) {
					processAir(text);
				} else if (text.contains("SEQUENCE")) {
					processAir(text);
				} else {
					processElec(text);
				}
				pd.close();
			}
        	document.close();
			String directory = path.getParent();
        	int index = 0;
        	int numFiles = 0;
        	if(this.makeClamping) {
        		numFiles += tools.size();
			}
			if(this.makeSensors) {
				numFiles += tools.size();
			}
			if(this.makeErrors) {
				numFiles += tools.size();
			}
			if(this.makeExcel) {
				numFiles += 1;
			}
			if(this.makePartType) {
				numFiles += 1;
			}
			System.out.println("numFiles : " + numFiles);
			System.out.print("Progress : 0%");
			double progress;
			for (Tool tool : tools) {
				if (this.makeClamping) {
					parts[index] = "Generating " + tool.getModuleName() + " clamping module.";
                    index++;
					this.status.setListData(parts);
					this.status.ensureIndexIsVisible(index);
					new GenerateClamping().createFile(tool, directory);
                    progress = (1.0*index)/numFiles;
					bar.setValue((int)(progress * 100));
					System.out.print("\rProgress : " + (int)(progress * 100) + "%");
					Thread.sleep(100);
					bar.repaint();
				}
				if (this.makeSensors) {
					parts[index] = "Generating " + tool.getModuleName() + " xml file.";
                    index++;
					this.status.setListData(parts);
                    this.status.ensureIndexIsVisible(index);
					new GenerateSensorMap().createFile(tool, directory, imagePath);
                    progress = (1.0*index)/numFiles;
                    bar.setValue((int)(progress * 100));
					System.out.print("\rProgress : " + (int)(progress * 100) + "%");
                    Thread.sleep(100);
                    bar.repaint();
				}
				if (this.makeErrors) {
					parts[index] = "Generating " + tool.getModuleName() + " error file.";
                    index++;
					this.status.setListData(parts);
                    this.status.ensureIndexIsVisible(index);
					new GenerateErrors().createFile(tool, directory);
                    progress = (1.0*index)/numFiles;
                    bar.setValue((int)(progress * 100));
					System.out.print("\rProgress : " + (int)(progress * 100) + "%");
                    Thread.sleep(100);
                    bar.repaint();
				}
			}
        	if(this.makeExcel) {
        		parts[index] = "Generating Tool Placement";
                index++;
        		this.status.setListData(parts);
                this.status.ensureIndexIsVisible(index);
        		new GeneratePlacement().createFile(tools, directory, path.getName());
                progress = (1.0*index)/numFiles;
                bar.setValue((int)(progress * 100));
				System.out.print("\rProgress : " + (int)(progress * 100) + "%");
                Thread.sleep(100);
                bar.repaint();
        	}
        	if(this.makePartType) {
        		parts[index] = "Generating Part Type Database";
                index++;
        		this.status.setListData(parts);
                this.status.ensureIndexIsVisible(index);
        		new GenerateTypedata().createFile(tools, directory);
                progress = (1.0*index)/numFiles;
                bar.setValue((int)(progress * 100));
				System.out.print("\rProgress : " + (int)(progress * 100) + "%");
                Thread.sleep(100);
                bar.repaint();
        	}
        	parts[index] = "Finished";
			bar.setValue(100);
            System.out.println();
            bar.repaint();
        	this.status.setListData(parts);
            this.status.ensureIndexIsVisible(index);
        } catch (IOException e) {
        	document.close();
        	System.err.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void processAir(String page) {
    	Pattern p = Pattern.compile("\\d{5}-\\d{2}");
    	Matcher m = p.matcher(page);
    	String layout;
    	m.find();
    	layout = page.substring(m.start(),m.end());
    	boolean found = false;
		for (Tool tool1 : tools) {
			if (tool1.getSerial().equals(layout)) {
				found = true;
			}
		}
    	if(!found) {
    		tools.add(new Tool(layout));
    	}
    	p = Pattern.compile("Tooling for Ass'y #");
    	m = p.matcher(page);
    	while(m.find()) {
    		int eol = page.substring(m.end()).indexOf("(");
			for (Tool tool : tools) {
				if (tool.getSerial().equals(layout)) {
					tool.addPartTypes(page.substring(m.end(), m.end() + eol).replace(" ", ""));
				}
			}
    	}
    	p = Pattern.compile("Sx+\\d+ ");
    	m = p.matcher(page);
    	while(m.find()) {
    		int eol = page.substring(m.start()).indexOf("\n") - 1;
			for (Tool tool : tools) {
				if (tool.getSerial().equals(layout)) {
					tool.addPartSensor(page.substring(m.start(), m.start() + eol));
				}
			}
    	}
    	p = Pattern.compile("Sx+\\d+");
    	m = p.matcher(page);
    	while(m.find()) {
			for (Tool tool : tools) {
				if (tool.getSerial().equals(layout)) {
					tool.addSensor(page.substring(m.start(), m.end()));
				}
			}
    	}
    	Pattern pPtartCycle = Pattern.compile("Start cycle");
    	Matcher mStartCycle = pPtartCycle.matcher(page);
    	Pattern pAfterWelding = Pattern.compile("After welding");
    	Matcher mAfterWelding = pAfterWelding.matcher(page);
    	int nests = 0;
    	while(mStartCycle.find()) {
    		nests++;
    		mAfterWelding.find();
			for (Tool tool : tools) {
				if (tool.getSerial().equals(layout)) {
					tool.addClamping(page.substring(mStartCycle.end() + 2, mAfterWelding.start() - 2));
					tool.setNumNests(nests);
				}
			}
    	}
    	pAfterWelding = Pattern.compile("After welding:");
    	mAfterWelding = pAfterWelding.matcher(page);
    	Pattern pWeldedPartUnloaded = Pattern.compile("Welded part unloaded");
    	Matcher mWeldedPartUnloaded = pWeldedPartUnloaded.matcher(page);
    	while(mAfterWelding.find()) {
    		mWeldedPartUnloaded.find();
			for (Tool tool : tools) {
				if (tool.getSerial().equals(layout)) {
					tool.addUnclamping(page.substring(mAfterWelding.end() + 2, mWeldedPartUnloaded.start() - 2));
				}
			}
    	}
    	StringBuilder valveName;
    	Pattern pValve = Pattern.compile("(\\d+\\.\r\n)|(\\d+\r\n\\.)");
    	Matcher mValve = pValve.matcher(page);
    	int first = 0;
    	int second = 0;
    	int lines = 0;
    	if(mValve.find()) {
    		first = mValve.start();
    		if(first > 0) {
    			String tmpPage = page.substring(first);
    			valveName = new StringBuilder();
    			String line = tmpPage.substring(0,tmpPage.indexOf(NEW_LINE));
    			while(line.length()<5) {
    				valveName.append(line.replace("\n", "").replace("\r", ""));
    				tmpPage = tmpPage.substring(tmpPage.indexOf(NEW_LINE) + 1);
    				line = tmpPage.substring(0,tmpPage.indexOf(NEW_LINE));
    				second += line.length();
    				lines++;
    			}
				for (Tool tool : tools) {
					if (tool.getSerial().equals(layout)) {
						tool.addValves(valveName.toString());
					}
				}
    		}
    	}
    	pValve = Pattern.compile("(\\d+\\.\r\n)|(\\d+\r\n\\.)");
    	mValve = pValve.matcher(page.substring(first + second + lines));
    	String temp = page.substring(first + second + lines);
    	if(mValve.find()) {
    		second = mValve.start();
    		if(second > 0) {
    			String tmpPage = temp.substring(second);
    			valveName = new StringBuilder();
    			String line = tmpPage.substring(0,tmpPage.indexOf(NEW_LINE));
    			while(line.length()<5) {
    				valveName.append(line.replace("\n", "").replace("\r", ""));
    				tmpPage = tmpPage.substring(tmpPage.indexOf(NEW_LINE) + 1);
    				line = tmpPage.substring(0,tmpPage.indexOf(NEW_LINE));
    			}
				for (Tool tool : tools) {
					if (tool.getSerial().equals(layout)) {
						tool.addValves(valveName.toString());
					}
				}
    		}
    	}
    }
    
    private void processElec(String page) {
    	Pattern p = Pattern.compile("\\d{5}-\\d{2}");
    	Matcher m = p.matcher(page);
    	String serial;
    	m.find();
    	serial = page.substring(m.start(),m.end());
    	boolean found = false;
		for (Tool tool1 : tools) {
			if (tool1.getSerial().equals(serial)) {
				found = true;
			}
		}
    	if(!found) {
    		tools.add(new Tool(serial));
    	}
    	p = Pattern.compile("Variant ");
    	m = p.matcher(page);
    	while(m.find()) {
    		int eol = page.substring(m.start()).indexOf("\n") - 1;
			for (Tool tool : tools) {
				if (tool.getSerial().equals(serial)) {
					tool.setVariant(page.substring(m.start() + "Variant ".length(), m.start() + eol));
				}
			}
    	}
    	p = Pattern.compile("PP\\d.\\d\\d");
    	m = p.matcher(page);
    	while(m.find()) {
			for (Tool tool : tools) {
				if (tool.getSerial().equals(serial)) {
					tool.setMachine(page.substring(m.start(), m.end()));
				}
			}
    	}
    	p = Pattern.compile("Frame");
    	m = p.matcher(page);
    	while(m.find()) {
			for (Tool tool : tools) {
				if (tool.getSerial().equals(serial)) {
					tool.setPosition(page.substring(m.start(), m.end() + 10));
				}
			}
    	}
    }
}
