package com.autodyne;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class GenerateClamping {

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

	public void createFile(Tool tool, String rootDir) {
        String moduleName = tool.getModuleName();
		try{
            File dir = new File(rootDir + "\\Clamping Modules\\");
			if(! dir.exists()) {
				dir.mkdirs();
			}
            PrintWriter writer = new PrintWriter(rootDir + "\\Clamping Modules\\" + moduleName + ".mod", "UTF-8");
            processModule(writer, tool);
			writer.close();
		} catch (IOException e) {
			System.out.println("SCRIPT FAILED DURING MODULE GENERATION");
			e.printStackTrace();
		}
	}

	private void processModule(PrintWriter w, Tool tool) {
	    try {
            String in;
            String out;
            Scanner template;
            if (tool.getNumNests() > 1) {
                template = new Scanner(new File("C:\\Data\\Programs\\Clamping_2nest.mod"));
            } else {
                template = new Scanner(new File("C:\\Data\\Programs\\Clamping_1nest.mod"));
            }
            while(template.hasNext()) {
                in = template.nextLine();
                /*%clamping_sequence%			includes step comments, valve actuation and rough sensors
	             %unclamping_sequence%		includes step comments, valve actuation and rough sensors
	             %part_sensors%				list of waitdi for sensors
	             %valve_sensors%				list of waitdi for sensors
	             %part_type%					i.e. C53462, E04466, etc...
	             %frame%						i.e. 1A, 2B, etc...
	             %part_name%					i.e. DAG LH OB LR, etc...
	             %side%						i.e. 1, 2
	             %frame_group%				i.e. AC, BD, etc...
	             %part_sensor_or%			sensor=high or sensor=high, etc...
	            */
                out = in.replaceAll("di1B200","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "0");
                out = out.replaceAll("di1B201","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "1");
                out = out.replaceAll("di1B202","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "2");
                out = out.replaceAll("di1B203","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "3");
                out = out.replaceAll("di5B300","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "0");
                out = out.replaceAll("di5B301","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "1");
                out = out.replaceAll("di5B302","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "2");
                out = out.replaceAll("di5B303","di" + indices.get(tool.getPosition()) + "B" +
                        sensorMap.get(tool.getPosition()) + "3");
                out = out.replaceAll("%clamping_sequence%",tool.getClampingSequence());
                out = out.replaceAll("%unclamping_sequence%",tool.getUnclampingSequence());
                out = out.replaceAll("%part_sensors%",tool.getPartSensorCheck());
                out = out.replaceAll("%valve_sensors%",tool.getValveSensorCheck());
                out = out.replaceAll("%part_type%",tool.getModuleName()
                                .substring(0,tool.getModuleName().length() - 3));
                out = out.replaceAll("%frame%",tool.getPosition());
                StringBuilder filler = new StringBuilder();
                for(int i = 0; i < 44 - tool.getVariant().length(); i++) {
                    filler.append(" ");
                }
                out = out.replaceAll("%part_name%",tool.getVariant() + filler + "*");
                out = out.replaceAll("%side%",tool.getPosition().substring(0,1));
                out = out.replaceAll("%frame_group%",tool.getFrameGroup());
                out = out.replaceAll("%part_sensor_andor%",tool.getPartSensorAndOr("OR"));
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
                out = out.replaceAll("%date%",dateFormat.format(new Date()));
                String username = System.getProperty("user.name");
                String output = username.substring(0, 1).toUpperCase() + username.substring(1);
                filler = new StringBuilder();
                for(int i = 0; i < 18 - output.length(); i++) {
                    filler.append(" ");
                }
                out = out.replaceAll("%name%",output + filler + "created   *");
                w.println(out);
            }
        }
        catch(FileNotFoundException e) {
	        System.out.println("Template file not found in ");
        }
    }
}
