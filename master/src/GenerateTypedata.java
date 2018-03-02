import java.io.BufferedWriter;
//import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GenerateTypedata {

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
	private Map<String, String> angleMap = new HashMap<>();
	{
		angleMap.put("1A","-30");
		angleMap.put("1B","-30");
		angleMap.put("1C","-210");
		angleMap.put("1D","-210");
		angleMap.put("2A","30");
		angleMap.put("2B","30");
		angleMap.put("2C","210");
		angleMap.put("2D","210");
	}

	public void createFile(List<Tool> tools, String filePath) {
		int entryNumber = 1;
		try {
			ArrayList<String> machines = new ArrayList<>();
			HashMap<String,PrintWriter> machineWriter = new HashMap<>();
			for(Tool tool : tools) {
				String m = tool.getMachine();
				if(!machines.contains(m)) {
					machines.add(m);
					machineWriter.put(m, new PrintWriter(filePath + "\\" + m + "_PCapp_Typedata.dat"));
					machineWriter.get(m).println("robot serial number");
					machineWriter.get(m).close();
				}
			}
			String machine = "";
			for(Tool tool : tools) {
				if(!machine.equals(tool.getMachine())) {
					machine = tool.getMachine();
					entryNumber = 1;
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + "\\" + tool.getMachine() + "_PCapp_Typedata.dat", true));
				//PrintWriter writer = machineWriter.get(tool.getMachine());
				//PrintWriter writer = new PrintWriter(new FileOutputStream(new File(filePath + "\\" + tool.getMachine() + "_PCapp_Typedata.dat"), true));
				writer.append("$").append(String.valueOf(entryNumber)).append("\n");

				writer.append("[").append(String.valueOf(entryNumber)).append(",False,");
				writer.append("\"").append(tool.getModuleName().substring(0, tool.getModuleName().length() - 3)).append("\",");
				writer.append("\"").append(tool.getVariant()).append("\",");
				writer.append("\"").append(tool.getPathModule()).append("\",");
				writer.append("\"").append(tool.getModuleName()).append("\",");
				writer.append(indices.get(tool.getPosition())).append(",");
				writer.append("0,");
				writer.append(String.valueOf(tool.getNumNests())).append(",");
				writer.append("12]\n");
				
				writer.append("[").append(angleMap.get(tool.getPosition())).append(",");
				writer.append(angleMap.get(tool.getPosition())).append(",");
				writer.append("True,");
				writer.append("0,");
				writer.append(indices.get(tool.getPosition())).append(",");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("5,");
				writer.append("False,");
				writer.append("True,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("False,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0,");
				writer.append("0]\n");
				
				entryNumber++;
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class udtTypeData_Base {
	int typeNo;							//3
	boolean disabled = false;			//False
	String identNo;						//"C78633"
	String typeDescription;				//"DT LR LH PWR IB"
	String modName_Workdata;			//"PP236_DT_STN1_CD_T_ROB1"
	String modName_Graph;				//"C78633_1C"
	int station;						//3
	int place;							//0
	int subStation;						//2
	int setCycleTime_Type;				//12
}
class udtTypeData_User {
	int loadpos;						//-210
	int workPos;						//-210
	boolean laserOri = true;			//True
	int designIndex;					//5
	int toolCode;						//3
	boolean cylinder_1 = false;			//False
	boolean cylinder_2 = false;			//False
	boolean cylinder_3 = false;			//False
	boolean cylinder_4 = false;			//False
	boolean cylinder_5 = false;			//False
	boolean cylinder_6 = false;			//False
	boolean cylinder_7 = false;			//False
	boolean cylinder_8 = false;			//False
	int countWeldings_N1;				//2
	int countWeldings_N2;				//8
	int countWeldings_N3 = 0;			//0
	int countWeldings_N4 = 0;			//0
	int airGaugesClean = 5;				//5
	boolean deplete = false;			//False
	boolean clampsSensor = true;		//True
	boolean BMDc = false;				//False
	boolean robHelp = false;			//False
	boolean NOK_SequenceOff = false;	//False
	boolean NOK_PartTurnOver = false;	//False
	int monitorProg_N1;					//1
	int monitorProg_N2;					//2
	int monitorProg_N3 = 0;				//0
	int monitorProg_N4 = 0;				//0
	int setValue_Analog_max_1 = 0;		//3.95
	int setValue_Analog_max_2 = 0;		//2
	int setValue_Analog_max_3 = 0;		//0
	int setValue_Analog_max_4 = 0;		//0
	int setValue_Analog_min_1 = 0;		//4.55
	int setValue_Analog_min_2 = 0;		//2.5
	int setValue_Analog_min_3 = 0;		//0.3
	int setValue_Analog_min_4 = 0;		//0
}
