package com.autodyne;

import java.util.*;
import java.util.regex.*;

public class Tool {
	private String serialNumber;
	private String variant;
	private String machine;
	private String position;
	private List<String> partTypes;
	private List<String> allSensors;
	private List<String> partSensors;
	private String[] valves;
	private List<String> clampingSequence;
	private List<String> unclampingSequence;
	private int numNests;
	private String product;
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
	
	Tool(String serial) {
		this.serialNumber = serial;
		this.partTypes = new ArrayList<>();
		this.allSensors = new ArrayList<>();
		this.partSensors = new ArrayList<>();
		this.valves = new String[]{"1. Spare","2. Spare","3. Spare","4. Spare","5. Spare","6. Spare","7. Spare","8. Spare","9. Spare","10. Spare",
				"1. Spare","2. Spare","3. Spare","4. Spare","5. Spare","6. Spare","7. Spare","8. Spare","9. Spare","10. Spare"};
		this.clampingSequence = new ArrayList<>();
		this.unclampingSequence = new ArrayList<>();
	}
	
	public String getModuleName() {
		String moduleName = getPartTypes()[0];
		if(getPartTypes().length > 1) {
			moduleName = moduleName + "_" + getPartTypes()[1].substring(getPartTypes()[1].length() - 3);
		}
		moduleName += "_" + getPosition();
		return moduleName;
	}
	
//	public void setProduct(String product) {
//		this.product = product;
//	}
	
	private String getProduct() {
		return this.product;
	}
	
	public String getPathModule() {
		return getMachine().replace(".","") + "_" + getProduct() + "_STN" +
                getPosition().substring(0,1) + "_ " + this.getFrameGroup() + "_T_ROB1";
	}
	
	public String getSerial() {
		return this.serialNumber;
	}
	
	public String getVariant() {
		return this.variant;
	}
	
	public void setVariant(String variant) {
		this.variant = variant;
	}
	
	public String getMachine() {
		return this.machine;
	}
	
	public void setMachine(String machine) {
		this.machine = machine;
	}
	
	public String getPosition() {
		return this.position;
	}
	
	public void setPosition(String position) {
		this.position = position.charAt(6) + "" +  position.charAt(14);
	}
	
	public String getFrameGroup() {
		String frameGroup = "";
		switch (this.position.substring(1)) {
			case "A":
				frameGroup = "AC";
				break;
			case "B":
				frameGroup = "BD";
				break;
			case "C":
				frameGroup = "AC";
				break;
			case "D":
				frameGroup = "BD";
				break;
		}
		return frameGroup;
	}
	
	public void addPartTypes(String partTypes) {
		String[] pt = partTypes.split("/");
		for (String aPt : pt) {
			this.partTypes.add(aPt.replace("#", ""));
		}
	}
	
	public String[] getPartTypes() {
		String[] parts = new String[partTypes.size()];
		for(int i = 0; i< partTypes.size(); i++) {
			parts[i] = partTypes.get(i);
		}
		return parts;
	}
	
	public void addSensor(String sensor) {
		this.allSensors.add(sensor);
	}
	
	public String[] getSensors() {
		String[] sensors = new String[allSensors.size()];
		for(int i = 0; i< allSensors.size(); i++) {
			sensors[i] = allSensors.get(i);
		}
		return sensors;
	}
	
	public void addPartSensor(String sensor) {
		this.partSensors.add(sensor);
	}
	
	public String[] getPartSensors() {
		if(partSensors.size() == 0) {
			return new String[]{"-1"};
		}
		String[] sensors = new String[partSensors.size()];
		for(int i = 0; i< partSensors.size(); i++) {
			sensors[i] = partSensors.get(i);
		}
		return sensors;
	}
	
	public void addValves(String valve) {
		Pattern p = Pattern.compile("\\d+\\.");
		Matcher m = p.matcher(valve);
		m.find();
		int start = m.start();
		String singleValve;
		Integer valveNumber;
		Pattern pValveNum;
		Matcher mValveNum;
		while(m.find()) {
			singleValve = valve.substring(start,m.start());
			pValveNum = Pattern.compile("\\d+\\.");
			mValveNum = pValveNum.matcher(singleValve);
			mValveNum.find();
			valveNumber = Integer.parseInt(singleValve.substring(mValveNum.start(), mValveNum.end()-1));
			if(singleValve.contains("10.")) {
				valveNumber = 10;
			}
			if(singleValve.contains("Adv.") || singleValve.contains("Check")) {
				this.valves[valveNumber - 1] = singleValve.replace("Adv.","Advance")
                        .replace("Ret.","Return")
                        .replace("Festo-Cyl.", "")
                        .replace("Festo Cyl.", "")
                        .replace("Destaco Cyl.","");
			} else {
				this.valves[valveNumber + 9] = singleValve.replace("Adv.","Advance")
                        .replace("Ret.","Return")
                        .replace("Festo-Cyl.", "")
                        .replace("Festo Cyl.", "")
                        .replace("Destaco Cyl.","");
			}
			start = m.start();
		}
		singleValve = valve.substring(start);
		pValveNum = Pattern.compile("\\d+\\.");
		mValveNum = pValveNum.matcher(singleValve);
		mValveNum.find();
		valveNumber = Integer.parseInt(singleValve.substring(mValveNum.start(), mValveNum.end()-1));
		if(singleValve.contains("10.")) {
			valveNumber = 10;
		}
		if(singleValve.contains("Adv.") || singleValve.contains("Check")) {
			this.valves[valveNumber - 1] = singleValve.replace("Adv.","Advance")
                    .replace("Festo-Cyl.", "")
                    .replace("Destaco Cyl.","");
		} else {
			this.valves[valveNumber + 9] = singleValve.replace("Ret.","Return")
                    .replace("Festo-Cyl.", "")
                    .replace("Destaco Cyl.","");
		}
	}
	
	public String[] getValves() {
		return this.valves;
	}
	
	public void addClamping(String clamping) {
		String rc = clamping.replace("\n", "").replace("\r","");
		String[] c = rc.split("Step ");
		for (String aC : c) {
			if (aC.length() > 5) {
				this.clampingSequence.add("Step " + aC);
			}
		}
	}
	
	public String[] getClamping() {
		String[] clamp = new String[clampingSequence.size()];
		for(int i = 0; i< clampingSequence.size(); i++) {
			clamp[i] = clampingSequence.get(i);
		}
		return clamp;
	}
	
	public void addUnclamping(String unclamping) {
		String ru = unclamping.replace("\n", "").replace("\r","");
		String[] u = ru.split("Step ");
		for (String anU : u) {
			if (anU.length() > 5) {
				this.unclampingSequence.add("Step " + anU);
			}
		}
	}
	
	public String[] getUnclamping() {
		String[] unclamp = new String[unclampingSequence.size()];
		for(int i = 0; i< unclampingSequence.size(); i++) {
			unclamp[i] = unclampingSequence.get(i);
		}
		return unclamp;
	}
	
	public void setNumNests(int nests) {
		this.numNests = nests;
	}
	
	public int getNumNests() {
		return numNests;
	}

	public String getClampingSequence() {
	    String output = "";
        for(int i = 0; i < this.getClamping().length; i++) {
            output += "			!! " + this.getClamping()[i] + "\n";
            Pattern p = Pattern.compile("V\\d+");
            Matcher m = p.matcher(this.getClamping()[i]);
            while(m.find()) {
                if(this.getClamping()[i].substring(m.start() + 1, m.end()).equals("5")) {
                    continue;
                }
                output += "			SetSK " + this.getClamping()[i].substring(m.start() + 1, m.end()) + ",WP;\n";
            }
            output += "			IF b" + this.getPartTypes()[0] + " AND bPartIn_N" + this.getNumNests() + "_" +
                    this.getFrameGroup() + this.getPosition().substring(0,1) + " THEN\n";
            m = p.matcher(this.getClamping()[i]);
            while(m.find()) {
                output += "				WaitGraphDI log1,log1,log1,log1\\\\inLogOr:=bSQ_Frame" + this.getPosition()
                        .substring(0,1) + "ClampingNOK_" + this.getFrameGroup() + "_N" + this.getNumNests() + "," +
                        "GetMUI_Text(" + (20 + Integer.parseInt(this.getClamping()[i].substring(m.start() +
                        1, m.end()))) + "\\\\inTextTable:=nTextTableGraph)\\\\inSV_Value:=2.0;\n";
            }
            output += "			ENDIF\n";
            output += "\n";
            output += "			IF bSQ_Frame" + this.getPosition().substring(0,1) + "ClampingNOK_" +
                    this.getFrameGroup() + "_N" + this.getNumNests() + " GOTO jumpClampEnd;\n";
            output += "\n";
        }
        return output;
    }

    public String getUnclampingSequence() {
	    String output = "";
        for(int i = 0; i < this.getUnclamping().length; i++) {
            output += "			!! " + this.getUnclamping()[i] + "\n";
            Pattern p = Pattern.compile("V\\d+");
            Matcher m = p.matcher(this.getUnclamping()[i]);
            while(m.find()) {
                if(this.getUnclamping()[i].substring(m.start() + 1, m.end()).equals("5")) {
                    continue;
                }
                output += "			";
                if(this.getNumNests() == 2) {
                    output += "IF bOpenN" + this.getNumNests() + " ";
                }
                String toggle = "HP";
                if(this.getUnclamping()[i].contains("returned")) {
                    toggle = "HP";
                } else if(this.getUnclamping()[i].contains("advanced")) {
                    toggle = "WP";
                } else if(this.getUnclamping()[i].contains("De-energize")) {
                    toggle = "DL";
                }
                output += "SetSK " + this.getUnclamping()[i].substring(m.start() + 1, m.end()) + "," + toggle + ";\n";
            }
            m = p.matcher(this.getUnclamping()[i]);
            while(m.find()) {
                if(this.getUnclamping()[i].substring(m.start() + 1, m.end()).equals("5")) {
                    continue;
                }
                output += "			";
                if(this.getNumNests() == 2) {
                    output += "IF bOpenN" + this.getNumNests() + " ";
                }
                output += "WaitGraphDI log1,log1,log1,log1\\\\inLogOr:=bSQ_Frame" + this.getPosition().substring(0,1)
                        + "ClampingNOK_" + this.getFrameGroup() + "_N" + this.getNumNests() + ",GetMUI_Text(" + (30 +
                        Integer.parseInt(this.getUnclamping()[i].substring(m.start() + 1, m.end()))) +
                        "\\\\inTextTable:=nTextTableGraph)\\\\inSV_Value:=2.0;\n";
            }
            output += "\n";
        }
        return output;
    }

    public String getPartSensorCheck() {
	    String output = "";
        for(int j = 0; j < this.getNumNests(); j++) {
            for(int k = 0; k < this.getPartSensors().length; k++) {
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(this.getSensors()[k]);
                m.find();
                String sensor = this.getPartSensors()[k].substring(m.start(),m.end());
                if(sensor.equals("8")) {
                    continue;
                }
                if(sensor.length()==1) {
                    output += "			WaitGraphDI di" + indices.get(this.getPosition()) + "B" + sensorMap.get(this
                            .getPosition()) + sensor + ",log1,log1,log1\\\\inLogOr:=bSQ_Frame" + this.getPosition()
                            .substring(0,1) + "ClampingNOK_" + this.getFrameGroup() + "_N" + (j + 1) + "," +
                            "GetMUI_Text(" + (3 + k) + "\\\\inTextTable:=nTextTableGraph)\\\\inSV_Value:=0.5;\n";
                } else {
                    output += "			WaitGraphDI di" + indices.get(this.getPosition()) + "B" + sensor + "," +
                            "log1," +
                            "log1,log1\\\\inLogOr:=bSQ_Frame" + this.getPosition().substring(0,1) + "ClampingNOK_" +
                            this.getFrameGroup() + "_N" + (j + 1) + ",GetMUI_Text(" + (3 + k) +
                            "\\\\inTextTable:=nTextTableGraph)\\\\inSV_Value:=0.5;\n";
                }
            }
        }
	    return output;
    }

    public String getValveSensorCheck() {
	    String output = "";
        for(int j = 0; j < this.getNumNests(); j++) {
            for(int k = 0; k < this.getSensors().length; k++) {
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(this.getSensors()[k]);
                m.find();
                String sensor = this.getSensors()[k].substring(m.start(),m.end());
                if(sensor.equals("8")) {
                    continue;
                }
                if(sensor.length()==1) {
                    output += "			WaitGraphDI di" + indices.get(this.getPosition()) + "B" + sensorMap.get(this
                            .getPosition()) + sensor + ",log1,log1,log1\\\\inLogOr:=bSQ_Frame" + this.getPosition()
                            .substring(0,1) + "ClampingNOK_" + this.getFrameGroup() + "_N" + (j + 1) + "," +
                            "GetMUI_Text(" + (3 + k) + "\\\\inTextTable:=nTextTableGraph)\\\\inSV_Value:=0.5;\n";
                } else {
                    output += "			WaitGraphDI di" + indices.get(this.getPosition()) + "B" + sensor + ",log1," +
                            "log1,log1\\\\inLogOr:=bSQ_Frame" + this.getPosition().substring(0,1) + "ClampingNOK_" +
                            this.getFrameGroup() + "_N" + (j + 1) + ",GetMUI_Text(" + (3 + k) +
                            "\\\\inTextTable:=nTextTableGraph)\\\\inSV_Value:=0.5;\n";
                }
            }
        }
        return output;
    }

    public String getPartSensorAndOr(String andor) {
        String out = "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(this.getPartSensors()[0]);
        m.find();
        String sensor;
        for(int k = 0; k < this.getPartSensors().length; k++) {
            m = p.matcher(this.getPartSensors()[k]);
            m.find();
            sensor = this.getPartSensors()[k].substring(m.start(),m.end());
            if(sensor.length()==1) {
                out += "(di" + indices.get(this.getPosition()) + "B" + sensorMap.get(this.getPosition()) + this
                        .getPartSensors()[k].substring(m.start(),m.end()) + "=high) " + andor + " ";
            } else {
                out += "(di" + indices.get(this.getPosition()) + "B" + this.getPartSensors()[k].substring(m
                        .start(),m.end()) + "=high) " + andor + " ";
            }
        }
        return out.substring(0,out.length() - 4) + ";";
    }
}
