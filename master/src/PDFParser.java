import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.JList;
import javax.swing.SwingWorker;

import org.apache.pdfbox.multipdf.*;
import org.apache.pdfbox.pdmodel.*;
//import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.*;

/*This is the meat of the program
 * This class reads in the pdf file and extracts the important data
 * it then adds the information to a Tool object that is stored in a List
 */

public class PDFParser extends SwingWorker<Integer, String>{

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
	private String parts[] = new String[100];
	
	public PDFParser(String filePath) {
		this.path = new File(filePath);
		this.tools = new ArrayList<>();
	}
	
	PDFParser(String filePath, String imagePath, boolean clamping, boolean errors, boolean sensors, boolean partType, boolean excelSum, JList<String> jlist) {
		this.path = new File(filePath);
		this.imagePath = imagePath;
		this.tools = new ArrayList<>();
		this.makeClamping = clamping;
		this.makeErrors = errors;
		this.makeSensors = sensors;
		this.makePartType = partType;
		this.makeExcel = excelSum;
		this.status = jlist;
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
			for (Tool tool : tools) {
				System.out.println(tool.getSerial());
				if (this.makeClamping) {
					parts[index] = "Generating " + tool.getModuleName() + " clamping module.";
					this.status.setListData(parts);
					new GenerateClamping().createFile(tool, directory);
					index++;
				}
				if (this.makeSensors) {
					parts[index] = "Generating " + tool.getModuleName() + " xml file.";
					this.status.setListData(parts);
					new GenerateSensorMap().createFile(tool, directory, imagePath);
					index++;
				}
				if (this.makeErrors) {
					parts[index] = "Generating " + tool.getModuleName() + " error file.";
					this.status.setListData(parts);
					new GenerateErrors().createFile(tool, directory);
					index++;
				}
			}
        	if(this.makeExcel) {
        		parts[index] = "Generating Tool Placement";
        		this.status.setListData(parts);
        		new GeneratePlacement().createFile(tools, directory, path.getName());
        	}
        	if(this.makePartType) {
        		parts[index] = "Generating Part Type Database";
        		this.status.setListData(parts);
        		new GenerateTypedata().createFile(tools, directory);
        	}
        	parts[index] = "Finished";
        	this.status.setListData(parts);
        } catch (IOException e) {
        	document.close();
            e.printStackTrace();
        }
    }
    
    private void processAir(String page) {
    	//textStr[i+2].substring(0,6).replace(".", "") + "_" + product + "_" + "STN" + textStr[i+2].substring(21,22) + "_" + frame + "_T_ROB1"
    	//machineNAme + product + stn# + T_ROB1
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
//    					System.out.println(valveName);
						tool.addValves(valveName.toString());
					}
				}
    		}
    	}
    	pValve = Pattern.compile("(\\d+\\.\r\n)|(\\d+\r\n\\.)");
    	mValve = pValve.matcher(page.substring(first + second + lines));
    	String temp = page.substring(first + second + lines);
    	//System.out.println(page.substring(first + second + lines));
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
//    					System.out.println(valveName);
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
