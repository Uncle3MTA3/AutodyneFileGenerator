package com.autodyne;

import java.io.*;


public class GenerateErrors {

	public void createFile(Tool tool, String rootDir) {
		String moduleName = tool.getModuleName();
		try{
			File dir = new File(rootDir + "\\Error Files\\" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + "\\");
			if(! dir.exists()) {
				dir.mkdirs();
			}
			PrintWriter writer = new PrintWriter(rootDir + "\\Error Files\\" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + "\\" + moduleName + ".txt", "UTF-8");
			writer.println("# " + moduleName + ".txt - BBI text description file");
			writer.println("#");
			writer.println("# DESCRIPTION:");
			writer.println("# BBI text file for RAPID development: english");
			writer.println("#");
			writer.println(moduleName + "::");
			addInfo(writer, tool);
			writer.close();
		} catch (IOException e) {
			System.out.println("SCRIPT FAILED DURING ERROR GENERATION");
			e.printStackTrace();
		}
	}
	
	private void addInfo(PrintWriter w, Tool tool) {
		w.println("0:");
		w.println("not used");
		w.println("1:");
		w.println("Clamping/Unclamping?");
		w.println("2:");
		w.println("Clamping Sequence");
		w.println("3:");
		w.println("Unclamping Sequence");
//		if(tool.getPartSensors().equals("-1")) {
//			System.out.println("No sensors found in schematic");
//			for (int i = 4; i < 16; i++) {
//				w.println((i) + ":");
//				w.println("not used");
//			}
//		} else {
			for (int i = 0; i < tool.getPartSensors().length; i++) {
				if(tool.getPartSensors()[0].equals("-1")) {
					break;
				}
				w.println((4 + i) + ":");
				w.println(tool.getPartSensors()[i].substring(5));
			}
			for (int i = 4 + tool.getPartSensors().length; i < 16; i++) {
				w.println((i) + ":");
				w.println("not used");
			}
//		}
		w.println("16:");
		w.println("Valve 1 WP - " + tool.getValves()[0].substring(3));
		w.println("17:");
		w.println("Valve 2 WP - " + tool.getValves()[1].substring(3));	
		w.println("18:");
		w.println("Valve 3 WP - " + tool.getValves()[2].substring(3));
		w.println("19:");
		w.println("Valve 4 WP - " + tool.getValves()[3].substring(3));
		w.println("20:");
		w.println("Valve 5 WP - " + tool.getValves()[4].substring(3));
		w.println("21:");
		w.println("Valve 6 WP - " + tool.getValves()[5].substring(3));
		w.println("22:");
		w.println("Valve 7 WP - " + tool.getValves()[6].substring(3));
		w.println("23:");
		w.println("Valve 8 WP - " + tool.getValves()[7].substring(3));
		w.println("24:");
		w.println("Valve 9 WP - " + tool.getValves()[8].substring(3));
		w.println("25:");
		w.println("Valve 10 WP - " + tool.getValves()[9].substring(4));
		w.println("26:");
		w.println("Valve 1 HP - " + tool.getValves()[10].substring(3));
		w.println("27:");
		w.println("Valve 2 HP - " + tool.getValves()[11].substring(3));
		w.println("28:");
		w.println("Valve 3 HP - " + tool.getValves()[12].substring(3));
		w.println("29:");
		w.println("Valve 4 HP - " + tool.getValves()[13].substring(3));
		w.println("30:");
		w.println("Valve 5 HP - " + tool.getValves()[14].substring(3));
		w.println("31:");
		w.println("Valve 6 HP - " + tool.getValves()[15].substring(3));
		w.println("32:");
		w.println("Valve 7 HP - " + tool.getValves()[16].substring(3));
		w.println("33:");
		w.println("Valve 8 HP - " + tool.getValves()[17].substring(3));
		w.println("34:");
		w.println("Valve 9 HP - " + tool.getValves()[18].substring(3));
		w.println("35:");
		w.println("Valve 10 HP - " + tool.getValves()[19].substring(4));
		w.println("36:");
		w.println("Air Sense Failed - Analog Value Less than Minimum");
		w.println("37:");
		w.println("Air Sense Failed - Analog Value Greater than Maximum");
		w.println("38:");
		w.println("Air Sense Cleaning Failed - Analog Value Greater than Minimum");
		w.println("39:");
		w.println("Air Sense Failed Red Herring - Value Less than Min Setpoint");
		w.println("40:");
		w.println("Air Sense Failed Red Herring - Value Greater than Max Setpoint");
		w.println("41:");
		w.println("Red Herring Test Failed");
		w.println("#");
		w.println("# End of file");
		w.println();
	}
}
