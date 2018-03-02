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
	
	Tool(String serial) {
		this.serialNumber = serial;
		this.partTypes = new ArrayList<>();
		this.allSensors = new ArrayList<>();
		this.partSensors = new ArrayList<>();
		//this.valves = new String[20];
		this.valves = new String[]{"1. Spare","2. Spare","3. Spare","4. Spare","5. Spare","6. Spare","7. Spare","8. Spare","9. Spare","10. Spare",
				"1. Spare","2. Spare","3. Spare","4. Spare","5. Spare","6. Spare","7. Spare","8. Spare","9. Spare","10. Spare"};
		//Arrays.fill(this.valves, "Spare");
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
	
	public void setProduct(String product) {
		this.product = product;
	}
	
	private String getProduct() {
		return this.product;
	}
	
	public String getPathModule() {
		return getMachine().replace(".","") + "_" + getProduct() + "_STN" + getPosition().substring(0,1) + "_T_ROB1";
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
		String[] sensors = new String[partSensors.size()];
		for(int i = 0; i< partSensors.size(); i++) {
			sensors[i] = partSensors.get(i);
		}
		return sensors;
	}
	
	public void addValves(String valve) {
//		System.out.println(valve);
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
//			System.out.println(valveNumber + " : " + singleValve + " loop");
			if(singleValve.contains("Adv.") || singleValve.contains("Check")) {
				this.valves[valveNumber - 1] = singleValve.replace("Adv.","Advance").replace("Ret.","Return").replace("Festo-Cyl.", "").replace("Festo Cyl.", "").replace("Destaco Cyl.","");
			} else {
				this.valves[valveNumber + 9] = singleValve.replace("Adv.","Advance").replace("Ret.","Return").replace("Festo-Cyl.", "").replace("Festo Cyl.", "").replace("Destaco Cyl.","");
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
//		System.out.println(valveNumber + " : " + singleValve);
		if(singleValve.contains("Adv.") || singleValve.contains("Check")) {
			this.valves[valveNumber - 1] = singleValve.replace("Adv.","Advance").replace("Festo-Cyl.", "").replace("Destaco Cyl.","");
		} else {
			this.valves[valveNumber + 9] = singleValve.replace("Ret.","Return").replace("Festo-Cyl.", "").replace("Destaco Cyl.","");
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
		//setNumNests((c.length - 1)/2);
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
}
