package com.autodyne;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GenerateSensorMap {

	private String moduleName;
	private Map<String, String> indices = new HashMap<>();
	{
		indices.put("1A","1");
		indices.put("1B","2");
		indices.put("1C","3");
		indices.put("1D","4");
		indices.put("2A","5");
		indices.put("2B","6");
		indices.put("2C","7");
		indices.put("2D","8");
	}
	private Map<String, String> sensorMap = new HashMap<>();
	{
		sensorMap.put("1A","20");
		sensorMap.put("1B","21");
		sensorMap.put("1C","22");
		sensorMap.put("1D","23");
		sensorMap.put("2A","30");
		sensorMap.put("2B","31");
		sensorMap.put("2C","32");
		sensorMap.put("2D","33");
	}

	private String imagePath;
	
	public void createFile(Tool tool, String rootDir, String imageDir) {
		this.imagePath = imageDir;
		this.moduleName = tool.getModuleName();
		try{
			File dir = new File(rootDir + "\\Sensor Maps\\" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + "\\");
			if(! dir.exists()) {
				dir.mkdirs();
			}
			PrintWriter writer = new PrintWriter(rootDir + "\\Sensor Maps\\" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + "\\" + moduleName + ".xml", "UTF-8");
			addInfo(tool, writer);
			writer.close();
		} catch (IOException e) {
			System.out.println("SCRIPT FAILED DURING XML GENERATION");
			e.printStackTrace();
		}
	}
	
	private void addInfo(Tool tool, PrintWriter w) {
		w.println("<?xml version=\"1.0\" encoding=\"us-ascii\" standalone=\"yes\"?>");
		w.println("<BRI_PCapp>");
	    w.println("<Splitter Direction=\"Top\" Maximized=\"1\" SplitterCenter=\"1\" Width=\"494\" Width1=\"1916\" Height1=\"494\" Width2=\"1916\" Height2=\"451\" />");
	    if(!this.imagePath.equals("")) {
	    	w.println("<Image2 Parent=\"uiPanel1\" Layout=\"Stretch\" Data=\"" + getHash() + "\" />");
	    }
	    int x = 100;
	    int y = 320;
	    Pattern p = Pattern.compile("\\d+");
	    for(int k = 0; k < tool.getSensors().length; k++) {
			Matcher m = p.matcher(tool.getSensors()[k]);
			m.find();
			String sensor = tool.getSensors()[k].substring(m.start(),m.end());
			if(sensor.length()==1) {
				w.println("<Sensor Width=\"82\" Parent=\"uiPanel2\" AddInformation=\"\" cylinderBMK=\"\" SensorName=\"di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getSensors()[k].substring(m.start(),m.end()) + "\" sensorBMK=\"Sxx" + tool.getSensors()[k].substring(m.start(),m.end()) + "\" Height=\"20\" Y=\"" + (y + (k * 20)%80) + "\" X=\"" + (x + (100 * k/4)) + "\" TextPosition=\"MiddleLeft\" />");
			} else {
				w.println("<Sensor Width=\"82\" Parent=\"uiPanel2\" AddInformation=\"\" cylinderBMK=\"\" SensorName=\"di" + indices.get(tool.getPosition()) + "B" + tool.getSensors()[k].substring(m.start(),m.end()) + "\" sensorBMK=\"Sx" + tool.getSensors()[k].substring(m.start(),m.end()) + "\" Height=\"20\" Y=\"" + (y + (k * 20)%80) + "\" X=\"" + (x + (100 * k/4)) + "\" TextPosition=\"MiddleLeft\" />");
			}
		}
	    w.println("<Viereck TextPosition=\"TopCenter\" X=\"489\" Y=\"278\" Width=\"90\" Height=\"25\" VariablenNamePath=\"BRI_Cycle;BRI_UserData;nActualAirGauges_" + tool.getPosition() + "\" VariablenValue=\"BRI_Cycle;BRI_UserData;nActualAirGauges_" + tool.getPosition() + "$0$True$15$15$True$15$1$255\" TextSize=\"12\" />");
		w.println("<PDF2 />");
		w.println("</BRI_PCapp>");
	}
	
	private String getHash() {
		String fileName = moduleName.substring(0,moduleName.length() - 3) + ".jpg";
		File dir = new File(imagePath + "\\" + fileName);
		try {
			dir = resizeImage(imagePath, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileInputStream imageInFile;
		String hashImage = "";
		try {
			imageInFile = new FileInputStream(dir);
			byte imageData[] = new byte[(int) dir.length()];
			imageInFile.read(imageData);
			hashImage = Base64.getEncoder().encodeToString(imageData);
			imageInFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hashImage;
	}
	
	private File resizeImage(String filePath, String modName) throws IOException {
		Image img;
		BufferedImage tempJPG;
		File newFileJPG;
		img = ImageIO.read(new File(filePath + "\\" + modName));
		double ratio;
		ratio = (double) img.getWidth(null)/img.getHeight(null);
        tempJPG = resizeImage(img, (int) (464 * ratio));
        newFileJPG = new File(filePath + "\\" + modName);
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = iter.next();
        
        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(0.6f);
        
        writer.setOutput(new FileImageOutputStream(newFileJPG));

        writer.write(null, new IIOImage(tempJPG, null, null), jpegParams);
     	writer.dispose();
     	return newFileJPG;
	}
	
	private BufferedImage resizeImage(final Image image, int width) {
        final BufferedImage bufferedImage = new BufferedImage(width, 464, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, 464, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}
