package com.autodyne;

import java.io.*;
import java.util.*;
import java.util.regex.*;


public class GenerateClamping {

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

	public void createFile(Tool tool, String rootDir) {
		this.moduleName = tool.getModuleName();
		try{
			File dir = new File(rootDir + "\\Clamping Modules\\" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + "\\");
			if(! dir.exists()) {
				dir.mkdirs();
			}
			PrintWriter writer = new PrintWriter(rootDir + "\\Clamping Modules\\" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + "\\" + moduleName + ".mod", "UTF-8");
			addHeader(writer);
			addModuleStart(writer);
			addChangeLog(writer);
			addStartVariables(writer, tool);
			addClampProcedure(writer, tool);
			addCleaningProcedure(writer);
			addEmptyProcedure(writer, tool);
			addPartsInProcedure(writer, tool);
			if(tool.getNumNests() > 1) {
				addPTOProcedure(writer, tool);
			}
			addOpenProcedure(writer, tool);
			addClosedProcedure(writer, tool);
			addRedHerringProcedure(writer, tool);
			addCheckPartsProcedure(writer, tool);
			
			addFooter(writer);
			writer.close();
		} catch (IOException e) {
			System.out.println("SCRIPT FAILED DURING MOD GENERATION");
			e.printStackTrace();
		}
	}

	private void addHeader(PrintWriter w) {
		w.println("%%%");
		w.println("  VERSION:1");
		w.println("  LANGUAGE:ENGLISH");
		w.println("%%%");
		w.println();
	}

	private void addModuleStart(PrintWriter w) {
		w.println("MODULE " + this.moduleName);
	}

	private void addChangeLog(PrintWriter w) {
		w.println("	!$$_BRI_SEQUENCE");
		w.println("	!******************************************************************");
		w.println("	!******************************************************************");
		w.println("	!*");
	    w.println("	!*             B rose - R oboter - I nterface  (BRI)");
	    w.println("	!*       -------------------------------------------------");
	    w.println("	!*");
	    w.println("	!* Changelog:");
	    w.println("	!* -----------");
	    w.println("	!*");
	    w.println("	!*   Version | Datum    | Name       | Beschreibung");
	    w.println("	!*   --------+----------+------------+-----------------------------------------------");
	    w.println("	!*    v1.00  | 08.06.10 | GRAN       | Base version");
	    w.println("	!*           |          |            |");
	    w.println("	!*           |          |            |");
	    w.println("	!*           |          |            |");
	    w.println("	!******************************************************************");
	    w.println("	!******************************************************************");
	    w.println();
	}

	private void addStartVariables(PrintWriter w, Tool tool) {
		w.println("	!********************************************************");
		w.println("	!*                     Variablen                        *");
		w.println("	!********************************************************");
		if(tool.getNumNests() == 2) {
			w.println("	LOCAL VAR bool bOpenAll;");
			w.println("	LOCAL VAR bool bOpenN1;");
			w.println("	LOCAL VAR bool bOpenN2;");
		}
		w.println("	LOCAL VAR bool bRedHerringOK;");
		if(tool.getNumNests() == 2) {
			w.println("	LOCAL VAR bool bClearNest1;");
			w.println("	LOCAL VAR bool bClearNest2;");
		}
		w.println();
		w.println("	! Persistent Variables for Types --");
		for(int i = 0; i < tool.getPartTypes().length; i++) {
			w.println("	LOCAL PERS bool b" + tool.getPartTypes()[i] + ":=TRUE;");
		}
		w.println();
	}
	
	private void addClampProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX()                                    *");
		w.println("	!*                                                         *");
		w.print("	!*  Automatic sequence: " + this.moduleName);
		int len = ("	!*  Automatic sequence: " + this.moduleName).length();
		for(int i = len; i < 60; i++) {
			w.print(" ");
		}
		w.println("*");
		len = ("	!*  Part type: " + tool.getVariant()).length();
		w.print("	!*  Part type: " + tool.getVariant());
		for(int i = len; i < 60; i++) {
			w.print(" ");
		}
		w.println("*");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  15.02.2017     1.0         A.Sinclair        created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "(");
		w.println("	bool inFirstRUN,");
		w.println("	num inGraphindex,");
		w.println("	INOUT udtBBI_Graph inGraph,");
		w.println("	INOUT udtBBI_GraphInt inGraphInt)");
		w.println();
		w.println("		! Variables Types STN" + tool.getPosition().substring(0,1));
		w.println("		!Frame " + tool.getPosition());
		for(int i = 0; i < tool.getPartTypes().length; i++) {
		w.println("		b" + tool.getPartTypes()[i] + ":=(TypeBase_ActualType{" + indices.get(tool.getPosition()) + "}.TypeNo=" + indices.get(tool.getPosition()) + ");");
		}
		w.println("		! Configuration of fixture");
		w.println("		bConfig_PTO_Fixture_" + tool.getPosition() + ":=FALSE;");
		w.println("		bConfig_PTXO_Fixture_" + tool.getPosition() + ":=FALSE;");
		w.println();
		w.println("		! BRI Graph");
		w.println("		TEST inGraph.CurS_NO");
		w.println("		CASE 0:");
		w.println("			RETURN;");
		w.println("			!----------------------------------------------------------------");
		w.println("		CASE 1:");
		w.println("			! : " + this.moduleName + " : Clamping/Unclamping ?");
		w.println("			inGraph.StepName:=GetMUI_Text(1\\inTextTable:=nTextTableGraph);");
		w.println("			!----------------------------------------------------------------");
		w.println();
		w.println("			!*** Special function step ***");
		w.println("			inGraphInt.WaitValue:=0.0;			! Step wait time");
		w.println("			inGraphInt.SV_Value:=0.0;			! Step supervision");
		w.println("			inGraphInt.JumpS_NO:=0;				! Step destination (<> 0 label by switching condition)");
		w.println("			!*** Special function end ***");
		w.println();
		w.println("			! Step transition - Leave step");
		w.println("			IF bSQ_CheckParts_" + tool.getPosition() + " AND bSQ_Clamping_" + tool.getPosition() + " AND (bSQ_UnClamping_" + tool.getPosition() + "=FALSE) THEN");
		w.println("			    inGraphInt.JumpS_NO:=5;");
		w.println("			    inGraphInt.Trans:=TRUE;");
		w.println("			ELSEIF bSQ_Clamping_" + tool.getPosition() + " AND (bSQ_UnClamping_" + tool.getPosition() + "=FALSE) AND (bSQ_CheckParts_" + tool.getPosition() + "=FALSE) THEN");
		w.println("			    inGraphInt.JumpS_NO:=10;");
		w.println("			    inGraphInt.Trans:=TRUE;");
		w.println("			ELSEIF bSQ_UnClamping_" + tool.getPosition() + " AND (bSQ_Clamping_" + tool.getPosition() + "=FALSE) AND (bSQ_CheckParts_" + tool.getPosition() + "=FALSE) THEN");
		w.println("			    inGraphInt.JumpS_NO:=20;");
		w.println("			    inGraphInt.Trans:=TRUE;");
		w.println("			ENDIF");
		w.println();
		w.println("			! Step action");
		w.println("			IF inGraphInt.Trans THEN");
		w.println("			    bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=FALSE;");
		w.println("			    bPartIn_N2_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=FALSE;");
		w.println("			    bPartIn_N3_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=FALSE;");
		w.println("			    bPartIn_N4_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=FALSE;");
		w.println("			    bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1:=FALSE;");
		w.println("			    bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N2:=FALSE;");
		w.println("			    bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N3:=FALSE;");
		w.println("			    bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N4:=FALSE;");
		w.println("			    bSQ_Clamping_" + tool.getPosition() + "_OK:=FALSE;");
		w.println("			    bSQ_UnClamping_" + tool.getPosition() + "_OK:=FALSE;");
		w.println("			    bSQ_CheckParts_" + tool.getPosition() + "_OK:=FALSE;");
		w.println("			ENDIF");
        w.println();
        w.println("			!----------------------------------------------------------------");
        w.println("		CASE 5:");
        w.println("			! : " + this.moduleName + " : Check Parts");
        w.println("			inGraph.StepName:=GetMUI_Text(2\\inTextTable:=nTextTableGraph);");
        w.println("			!----------------------------------------------------------------");
        w.println();
        w.println("			!*** Special function step ***");
        w.println("			inGraphInt.WaitValue:=0.0;			! Step wait time");
        w.println("			inGraphInt.SV_Value:=0.0;			! Step supervision");
        w.println("			inGraphInt.JumpS_NO:=0;				! Step destination (<> 0 label by switching condition)");
        w.println("			!*** Special function end ***");
        w.println();
        w.println("			! Check Parts");
        w.println("			IF (bSQ_RedHerringTest_" + tool.getPosition().substring(0,1) + "AC=FALSE) AND (bSQ_RedHerringTest_" + tool.getPosition().substring(0,1) + "BD=FALSE) THEN");
        w.println("				proc" + this.moduleName + "_CheckParts;");
        w.println("			ENDIF");
        w.println();
        w.println("			! Step condition - Leave step");
        w.println("			inGraphInt.Trans:=TRUE;");
        w.println();
        w.println("			! Leave step -- Unclamping ends");
        w.println("			bSQ_Clamping_" + tool.getPosition() + ":=FALSE;");
        w.println("			bSQ_CheckParts_" + tool.getPosition() + ":=FALSE;");
        w.println("			bSQ_CheckParts_" + tool.getPosition() + "_OK:=TRUE;");
        w.println("			inGraphInt.JumpS_NO:=0;");
        w.println();
        w.println("			!----------------------------------------------------------------");
        w.println("		CASE 10:");
        w.println("			! : " + this.moduleName + " : Clamping");
        w.println("			inGraph.StepName:=GetMUI_Text(2\\inTextTable:=nTextTableGraph);");
        w.println("			!----------------------------------------------------------------");
        w.println();
        w.println("			!*** Special function step ***");
        w.println("			inGraphInt.WaitValue:=0.0;			! Step wait time");
        w.println("			inGraphInt.SV_Value:=0.0;			! Step supervision");
        w.println("			inGraphInt.JumpS_NO:=0;				! Step destination (<> 0 label by switching condition)");
        w.println("			!*** Special function end ***");
        w.println();
        w.println("			!Call the clamping sequence for a Red Herring Test");
        w.println("			IF bSQ_RedHerringTest_" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + " AND (DOutput(doAutoOn)=high) THEN");
        w.println("				proc" + this.moduleName + "_RedHerring bRedHerringOK;");
        w.println("				IF bRedHerringOK bSQ_RedHerringTest_" + tool.getPosition().substring(0,1) + tool.getFrameGroup() + ":=FALSE;");
        w.println("				GOTO jumpClampEnd;");
        w.println("			ENDIF");
        w.println();
        w.println("			! Check Parts");
        w.println("			proc" + this.moduleName + "_CheckParts;");
        w.println();
        if(tool.getNumNests() == 1) {
        	w.println("			IF (bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "=FALSE) THEN");
        } else {
        	w.println("			IF (bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "=FALSE AND bPartIn_N2_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "=FALSE) THEN");
        }
        w.println("				SetSK 1,WP;");
        w.println("				SetSK 2,WP;");
        w.println("				WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "0\\in1low,di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + ",GetMUI_Text(16\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
        w.println("				WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "2\\in1low,di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "3,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + ",GetMUI_Text(17\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
        w.println("				SetSK 3,WP;");
        if(tool.getNumNests() != 1) {
			w.println("				SetSK 10,WP;");
        }
        w.println("				GOTO jumpClampEnd;");
        w.println("			ENDIF");
        w.println();
        for(int i = 0; i < tool.getClamping().length; i++) {
        	w.println("			!! " + tool.getClamping()[i]);
        	Pattern p = Pattern.compile("V\\d+");
        	Matcher m = p.matcher(tool.getClamping()[i]);
        	while(m.find()) {
        		if(tool.getClamping()[i].substring(m.start() + 1, m.end()).equals("5")) {
        			continue;
        		}
        		w.println("			SetSK " + tool.getClamping()[i].substring(m.start() + 1, m.end()) + ",WP;");
        		w.println("			IF b" + tool.getPartTypes()[0] + " AND bPartIn_N" + tool.getNumNests() + "_" +tool.getFrameGroup() + tool.getPosition().substring(0,1) + " THEN");
        		w.println("				WaitGraphDI log1,log1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + ",GetMUI_Text(" + (15 + Integer.parseInt(tool.getClamping()[i].substring(m.start() + 1, m.end()))) + "\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
        		w.println("			ENDIF");
        	}
        	w.println();
          w.println("			IF bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + " GOTO jumpClampEnd;");
          w.println();
        }
        w.println("			!! Airsense");
        w.println("			IF bPartIn_N" + tool.getNumNests() + "_" +tool.getFrameGroup() + tool.getPosition().substring(0,1) + " THEN");
        w.println("				SetSK 5,WP;");
        w.println("				WaitTime 0.7;");
        w.println("				bLoadAirGaugesValues:=TRUE;");
        w.println("				sdiAirGauges{" + indices.get(tool.getPosition()) + "}:=\"1\";");
        w.println("				WaitTime 0.1;");
        w.println("				IF (TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_1<>0) AND (DOutput(sdoAirGaugesOFF)=low) THEN");
        w.println("					WaitGraphNum nActualAirGauges_" + tool.getPosition() + "\\inBiggerEqu:=TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_min_1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + ",GetMUI_Text(36\\inTextTable:=nTextTableGraph)\\inSV_Value:=1.0;");
        w.println("					WaitGraphNum nActualAirGauges_" + tool.getPosition() + "\\inSmallerEqu:=TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + ",GetMUI_Text(37\\inTextTable:=nTextTableGraph)\\inSV_Value:=1.0;");
        w.println("					IF ((nActualAirGauges_" + tool.getPosition() + "<TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_min_1) OR (nActualAirGauges_" + tool.getPosition() + ">TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_1)) AND bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + " THEN");
        w.println("						Incr nErrorAirGauges_" + tool.getPosition() + ";");
        w.println("						nActualShowErrorAirGauges_" + tool.getFrameGroup() + ":=nActualAirGauges_" + tool.getPosition() + ";");
        w.println("					ENDIF");
        w.println("				ENDIF");
        w.println("			ENDIF");
        w.println("			");
        w.println("			IF bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + " GOTO jumpClampEnd;");
        w.println("			");
        w.println("			!! De-energize Check Pressure (V5)");
        w.println("			SetSK 5, DL;");
        w.println("			sdiAirGauges{" + indices.get(tool.getPosition()) + "}:=\"0\";");
        w.println();
        w.println("			! Check Part In");
        w.println("			IF bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1=FALSE AND bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + " THEN");
        w.print("				proc" + this.moduleName + "_PartsIn bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1));
        if(tool.getNumNests() ==2) {
        	w.println(", bPartIn_N2_" + tool.getFrameGroup() + tool.getPosition().substring(0,1));
        }
        w.println(";");
        w.println("				IF bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + " nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}:=10;");
        w.println("			ELSE");
        w.println("				bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=FALSE;");
        w.println("				nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}:=0;");
        w.println("			ENDIF");
        if(tool.getNumNests() == 2) {
        	w.println();
        	w.println("			! Is part from N1 turned over into N2?");
        	w.println("			IF bPartMustBe_" + tool.getFrameGroup() + "_N2_S" + tool.getPosition().substring(0,1) + " AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>51) AND b" + tool.getPartTypes()[0] + " AND BBI_Mode.Auto THEN");
        	w.println("				WaitGraph bPartIn_N2_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ",blog1,blog1,blog1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1,\"Part is not turned over in Nest2\"\\inSV_Value:=1.0;");
        	w.println("			ENDIF");
        	w.println();
        	w.println("			! Part must be in subfixture");
        	w.println("			IF (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1=FALSE) AND (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N2=FALSE) THEN");
        	w.println("				IF (diWeldingBlocked=low) THEN");
        	w.println("					bPartMustBe_" + tool.getFrameGroup() + "_N2_S" + tool.getPosition().substring(0,1) + ":=bPartIn_N1_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + " AND bConfig_PTO_Fixture_" + tool.getPosition() + " AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>51) AND BBI_Mode.Auto;");
        	w.println("				ELSE");
        	w.println("					bPartMustBe_" + tool.getFrameGroup() + "_N2_S" + tool.getPosition().substring(0,1) + ":=FALSE;");
        	w.println("				ENDIF");
        	w.println("			ENDIF");
        }
        w.println();
        w.println("			! Jump Label End");
        w.println("			jumpClampEnd:");
        w.println();
        w.println("			! Step condition - Leave step");
        w.println("			inGraphInt.Trans:=TRUE;");
        w.println();
        w.println("			! Leave step --- Clamping ends");
        w.println("			bSQ_Clamping_" + tool.getPosition() + ":=FALSE;");
        w.println("			bSQ_Clamping_" + tool.getPosition() + "_OK:=TRUE;");
        w.println();
        w.println("			!----------------------------------------------------------------");
        w.println("		CASE 20:");
        w.println("			! : " + this.moduleName + " : Unclamping");
        w.println("			inGraph.StepName:=GetMUI_Text(3\\inTextTable:=nTextTableGraph);");
        w.println("			!----------------------------------------------------------------");
        w.println();
        w.println("			!*** Special function step ***");
        w.println("			inGraphInt.WaitValue:=0.0;			! Step wait time");
        w.println("			inGraphInt.SV_Value:=0.0;			! Step supervision");
        w.println("			inGraphInt.JumpS_NO:=0;				! Step destination (<> 0 label by switching condition)");
        w.println("			!*** Special function end ***");
        w.println();
        if(tool.getNumNests() == 2) {
        	w.println("			! : Fixture partial unclamping (without NOK part turn over)");
        	w.println("			IF bNOK_Sequence4ReLoad_S" + tool.getPosition().substring(0,1) + " THEN");
        	w.println("				bOpenN1:=(nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}<>51) OR ((nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}=51) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>51) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>41));");
        	w.println("				bOpenN2:=(nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>51);");
        	w.println("			ELSE");
        	w.println("				bOpenN1:=(nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}<>50) OR ((nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}=50) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>50) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>40)) OR ((nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}=50) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}=50));");
        	w.println("				bOpenN2:=(nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}<>50) OR ((nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{1}=50) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{2}=50));");
        	w.println("			ENDIF");
        }
        for(int i = 0; i < tool.getUnclamping().length; i++) {
        	w.println("			!! " + tool.getUnclamping()[i]);
        	Pattern p = Pattern.compile("V\\d+");
        	Matcher m = p.matcher(tool.getUnclamping()[i]);
        	while(m.find()) {
        		if(tool.getUnclamping()[i].substring(m.start() + 1, m.end()).equals("5")) {
        			continue;
        		}
        		w.print("			");
        		if(tool.getNumNests() == 2) {
        			w.print("IF bOpenN" + tool.getNumNests() + " ");
        		}
        		String toggle = "HP";
        		if(tool.getUnclamping()[i].contains("returned")) {
        			toggle = "HP";
        		} else if(tool.getUnclamping()[i].contains("advanced")) {
        			toggle = "WP";
        		} else if(tool.getUnclamping()[i].contains("De-energize")) {
        			toggle = "DL";
        		}
        		w.println("SetSK " + tool.getUnclamping()[i].substring(m.start() + 1, m.end()) + "," + toggle + ";");
        		w.print("			");
        		if(tool.getNumNests() == 2) {
        			w.print("IF bOpenN" + tool.getNumNests() + " ");
        		}
        		w.println("WaitGraphDI log1,log1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + tool.getNumNests() + ",GetMUI_Text(" + (25 + Integer.parseInt(tool.getUnclamping()[i].substring(m.start() + 1, m.end()))) + "\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
        	}
          w.println();
        }
        w.println("			!! Energize Free-Blow Pressure for nozzle clean (V5),"); 
        w.println("			IF ((DOutput(doWeldingBlocked)=low) AND BBI_Mode.Auto AND bPartsInOK_S" + tool.getPosition().substring(0,1) + ") THEN");
        w.println("				SetSK 4, WP;");
        w.println("				SetSK 5, WP;");
        w.println("				WaitTime 0.5;");
        w.println("				bLoadAirGaugesValues:=TRUE;");
        w.println("				sdiAirGauges{" + indices.get(tool.getPosition()) + "}:=\"1\";");
        w.println("				WaitTime 0.1;");
        w.println("				IF (TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_3<>0) AND (DOutput(sdoAirGaugesOFF)=low) THEN");
        w.println("					! Check if Analog Value is Greater than SP");
        w.println("					IF (nActualAirGauges_" + tool.getPosition() + ">TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_3) THEN");
        w.println("						! Fixture Unclamping");
        w.println("						! De-energize Check Pressure");
        w.println("						SetSK 5, DL;");
        w.println("						! Energize Free Blow for Clean Out");
        w.println("						SetSK 5, HP;");
        w.println("						WaitTime 1.0;");
        w.println("						! Fixture Unclamping");
        w.println("						! De-energize Free Blow");  	
        w.println("						SetSK 5, DL;");
        w.println("						! Energize Check Pressure");
        w.println("						SetSK 5, WP;");
        w.println("						WaitTime 0.5;");
        w.println("						bLoadAirGaugesValues:=TRUE;");
        w.println("						WaitTime 0.1;");
        w.println("						! Check if Analog Value is Greater than SP");
        w.println("						WaitGraphNum nActualAirGauges_" + tool.getPosition() + "\\inSmallerEqu:=TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_3\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1,GetMUI_Text(38\\inTextTable:=nTextTableGraph)\\inSV_Value:=1.0;");
        w.println("					ENDIF");
        w.println("				ENDIF");
        w.println("				! Fixture Unclamping");
        w.println("				SetSK 4, HP;");
        w.println("				SetSK 5, DL;");
        w.println("				sdiAirGauges{" + indices.get(tool.getPosition()) + "}:=\"0\";");
        w.println("			ENDIF");
        w.println("			WaitTime 0.2;");
        w.println("			! Step condition - Leave step");
        w.println("			inGraphInt.Trans:=TRUE;");
        w.println();
        w.println("			! Leave step -- Unclamping ends");
        w.println("			bSQ_UnClamping_" + tool.getPosition() + ":=FALSE;");
        w.println("			bSQ_UnClamping_" + tool.getPosition() + "_OK:=TRUE;");
        w.println();
        w.println("			!----------------------------------------------------------------");
        w.println("		DEFAULT:");
        w.println("			BBI_Graph_Release{BBI_Graph_TaskNo}:=FALSE;");
        w.println("			inGraphInt.End:=TRUE;");
        w.println("		ENDTEST");
        w.println("	ERROR");
        w.println("		bSQ_Clamping_" + tool.getPosition() + ":=FALSE;");
        w.println("		bSQ_Clamping_" + tool.getPosition() + "_OK:=FALSE;");
        w.println("		bSQ_UnClamping_" + tool.getPosition() + ":=FALSE;");
        w.println("		bSQ_UnClamping_" + tool.getPosition() + "_OK:=FALSE;");
        w.println("		IF (ERRNO=ERR_GraphBreak) THEN");
        w.println("			SkipWarn;");
        w.println("			RAISE;");
        w.println("		ENDIF");
        w.println("		ErrorLog BRI_TaskName+\": Error in \"+inGraph.ModName,\"Error: \"+IntToStr(ERRNO);");
        w.println("		RETURN;");
    	w.println("	UNDO");
    	w.println("		! finally");
		w.println("	ENDPROC");
	}

	private void addCleaningProcedure(PrintWriter w) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Prcedure procXXX_Cleaning()                            *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure for cleaning AirGauges.                      *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  04.11.2010      1.0        A. Gran           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_Cleaning()");
		w.println("		SetSK TypeUser_ActualType{BBI_Graph{globalGraphindex}.StationNo}.AirGaugesClean,WP;");
		w.println("		WaitTime 2;");
		w.println("		SetSK TypeUser_ActualType{BBI_Graph{globalGraphindex}.StationNo}.AirGaugesClean,HP;");
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	UNDO");
		w.println("		! finally");
		w.println("	ENDPROC");
	}

	private void addEmptyProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX_Empty()                              *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure check fixture, is fixture empty?             *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  04.11.2010      1.0        A. Gran           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_Empty(");
		w.println("		VAR bool outResult)");
		if(tool.getNumNests() > 1) {
			w.println("		VAR bool bEmptyN1;");
			w.println("		VAR bool bEmptyN2;");
		}
		w.println("		outResult:=FALSE;");
		w.println();
		w.println("		! All sensors are prepared for fixture empty?");
		if(tool.getNumNests() > 1) {
			w.println("		IF bOpenN2 and (bOpenN1=false) THEN");
			if(tool.getPartSensors()[0].equals("-1")) {
				System.out.println("PartSensors empty");
				w.println();
				w.println("	ERROR");
				w.println("		RETURN;");
				w.println("	ENDPROC");
				return;
			}
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(tool.getPartSensors()[0]);
			boolean result = m.find();
			if(!result) {
				w.println();
				w.println("	ERROR");
				w.println("		RETURN;");
				w.println("	ENDPROC");
				return;
			}
			String sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
			w.print("			outResult:=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
			for(int k = 1; k < tool.getPartSensors().length; k++) {
				m = p.matcher(tool.getPartSensors()[k]);
				m.find();
				sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				} else {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				}
			}
			w.println(";");
			w.println("			!nest 2 only!!!");
			w.println("		ELSEIF (bopenN2=false) and bopenN1 THEN");
			p = Pattern.compile("\\d+");
			m = p.matcher(tool.getPartSensors()[0]);
			m.find();
			sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
			w.print("			outResult:=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
			for(int k = 1; k < tool.getPartSensors().length; k++) {
				m = p.matcher(tool.getPartSensors()[k]);
				m.find();
				sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				} else {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				}
			}
			w.println(";");
			w.println("			! Nest 1 only!");
			w.println("		ELSE");
			p = Pattern.compile("\\d+");
			m = p.matcher(tool.getPartSensors()[0]);
			m.find();
			sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
			w.print("			bEmptyN1:=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
			for(int k = 1; k < tool.getPartSensors().length; k++) {
				m = p.matcher(tool.getPartSensors()[k]);
				m.find();
				sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				} else {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				}
			}
			w.println(";");
			p = Pattern.compile("\\d+");
			m = p.matcher(tool.getPartSensors()[0]);
			m.find();
			sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
			w.print("			bEmptyN2:=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
			for(int k = 1; k < tool.getPartSensors().length; k++) {
				m = p.matcher(tool.getPartSensors()[k]);
				m.find();
				sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				} else {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				}
			}
			w.println(";");
			w.println("			IF bEmptyN1 THEN");
			w.println("				bClearNest1:=TRUE;");
			w.println("			ENDIF");
			w.println("			IF bEmptyN2 THEN");
			w.println("				bClearNest2:=TRUE;");
			w.println("			ENDIF");
			w.println("			outResult:=bClearNest1 AND bClearNest2;");
			w.println("			!both N1 and N2!!");
			w.println("			IF outResult THEN");
			w.println("				bClearNest1:=FALSE;");
			w.println("				bClearNest2:=FALSE;");
			w.println("			ENDIF");
			w.println("		ENDIF");
		} else {
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(tool.getPartSensors()[0]);
			m.find();
			String sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
			if(sensor.length()==1) {
				w.print("		bPartIn_N1" + "_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=(di" + sensorMap.get(tool.getPosition()) + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
			} else {
				w.print("		bPartIn_N1" + "_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
			}
			for(int k = 1; k < tool.getPartSensors().length; k++) {
				m = p.matcher(tool.getPartSensors()[k]);
				m.find();
				sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				} else {
					w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
				}
			}
			w.println(";");
			w.println("		outResult:=bPartIn_N1" + "_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ";");
		}
		w.println();
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}

	private void addPartsInProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX_PartsIn()                            *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure check fixture, is fixture with parts loaded? *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  04.11.2010      1.0        A. Gran           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_PartsIn(");
		w.print("		INOUT bool InOutResult_N1");
		if(tool.getNumNests()>1) {
			w.println(",");
			w.print("		INOUT bool InOutResult_N2");
		}
		w.println(")");
		w.println("		! Variable deklarieren");
		for(int i = 0; i < tool.getNumNests(); i++) {
			w.println("		VAR bool bResult_N" + (i + 1) + ";");
		}
		for(int i = 0; i < tool.getNumNests(); i++) {
			w.println("		bResult_N" + (i + 1) + ":=InOutResult_N" + (i + 1) + ";");
		}
		for(int i = 0; i < tool.getNumNests(); i++) {
			w.println("		InOutResult_N" + (i + 1) + ":=FALSE;");
		}
		w.println();
		for(int j = 0; j < tool.getNumNests(); j++) {
			w.println("		IF bResult_N" + (tool.getNumNests() - j) + " THEN");
			for(int k = 1; k < tool.getSensors().length; k++) {
				if(tool.getPartSensors()[0].equals("-1")) {
					w.println();
					w.println("	ERROR");
					w.println("		RETURN;");
					w.println("	ENDPROC");
					return;
				}
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(tool.getSensors()[k]);
				m.find();
				String sensor = tool.getSensors()[k].substring(m.start(),m.end());
				if(sensor.equals("8")) {
					continue;
				}
				if(sensor.length()==1) {
					w.println("			WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + sensor + ",log1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + (tool.getNumNests() - j) + ",GetMUI_Text(" + (3 + k) + "\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
				} else {
					w.println("			WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensor + ",log1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + (tool.getNumNests() - j) + ",GetMUI_Text(" + (3 + k) + "\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
				}
			}
			w.println("		ENDIF");
		}
		w.println();
		w.println("		! All sensors are prepared for part in?");
		for(int i = 0; i < tool.getNumNests(); i++) {
			w.println("		InOutResult_N" + (i + 1) + ":=bResult_N" + (i + 1) + " AND (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + (i + 1) + "=FALSE) AND (nState_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + "_N{" + (i + 1) + "}<>51);");
		}
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}

	private void addPTOProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX_PTO()                               *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure check fixture, is PTO completed?            *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  04.11.2010      1.0        A. Gran           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_PTO(");
		w.println("		INOUT bool outResult)");
		w.println();
		w.println("!! Select the sensors required for PTO");
		if(tool.getPartSensors()[0].equals("-1")) {
			System.out.println("PartSensors empty");
			w.println();
			w.println("	ERROR");
			w.println("		RETURN;");
			w.println("	ENDPROC");
			return;
		}
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(tool.getPartSensors()[0]);
		m.find();
		String sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
		if(sensor.length()==1) {
			w.print("		outResult:=(di" + sensorMap.get(tool.getPosition()) + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
		} else {
			w.print("		outResult:=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=low)");
		}
		for(int k = 1; k < tool.getPartSensors().length; k++) {
			m = p.matcher(tool.getPartSensors()[k]);
			m.find();
			sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
			if(sensor.length()==1) {
				w.print(" and " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
			} else {
				w.print(" and " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=low)");
			}
		}
		w.println(";");
		w.println();
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}

	private void addOpenProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX_Open()                               *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure check fixture, is fixture open?              *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  04.11.2010      1.0        A. Gran           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_Open(");
		w.println("		VAR bool outResult)");
		w.println("		outResult:=FALSE;");
		w.println();
		w.println("		! All sensors are prepared for fixture open?");
		w.println("		outResult:=(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "0=high) AND (di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "1=low) AND (di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "2=high) AND (di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "3=low);");
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}

	private void addClosedProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX_Closed()                             *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure check fixture, is fixture closed?            *");
		w.println("	!*                                                         *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  04.11.2010      1.0        A. Gran           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_Closed(");
		w.println("		VAR bool outResult)");
		w.println("		outResult:=FALSE;");
		w.println();
		w.println("		! All sensors are prepared for fixture open?");
		w.println("		outResult:=(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "0=low) AND (di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "1=high) AND (di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "2=low) AND (di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "3=high);");
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}

	private void addRedHerringProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Procedure procXXX_RedHerring()                         *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure to check Red Herring Clamp Sequence OK?      *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  05.26.2014      1.0        C. Shaw           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_RedHerring(");
		w.println("		VAR bool outResult)");
		w.println("		outResult:=FALSE;");
		w.println();
		w.println("		bDoNotCallProg_S" + tool.getPosition().substring(0,1) + ":=TRUE;");
		w.println();
		w.println("		! Destaco Clamps Advance - Sensors should not be made");
		w.println("		SetSK 1,WP;");
		w.println("		SetSK 2,WP;");
		w.println("		Waittime 2.5;");
		w.println("		WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "0\\in1Low,di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "1\\in2Low,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1,GetMUI_Text(16\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
		w.println("		WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "2\\in1Low,di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + "3\\in2Low,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1,GetMUI_Text(17\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
		w.println();
		w.println("		SetSK 5,WP;");
		w.println("		! Air Sense from valve 5");
		w.println("		Waittime 1.5;");
		w.println("		bLoadAirGaugesValues:=TRUE;");
		w.println("		sdiAirGauges{" + indices.get(tool.getPosition()) + "}:=\"1\";");
		w.println();
		w.println("		!The pressure must be between ...min_2 and ...max_2");
		w.println("		IF (TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_2 <> 0) and (DOutput(sdoAirGaugesOFF)=low) then");
		w.println("			WaitGraphNum nActualAirGauges_" + tool.getPosition() + "\\inBiggerEqu:=TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_min_2\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1,GetMUI_Text(39\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
		w.println("			WaitGraphNum nActualAirGauges_" + tool.getPosition() + "\\inSmallerEqu:=TypeUser_ActualType{" + indices.get(tool.getPosition()) + "}.SetValue_Analog_max_2\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1,GetMUI_Text(40\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
		w.println("		ENDIF");
		w.println("		SetSK 5,DL;");
		w.println("		sdiAirGauges{" + indices.get(tool.getPosition()) + "}:=\"0\";");
		w.println();
		w.println("		outResult:=(NOT (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N1)) AND (NOT (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N2)) AND (NOT (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N3)) AND (NOT (bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N4));");
		w.println("		WaitGraph outResult,blog1,blog1,blog1\\inLogOr:=blog1,GetMUI_Text(41\\inTextTable:=nTextTableGraph)\\inSV_Value:=0.2;");
		w.println();
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}

	private void addCheckPartsProcedure(PrintWriter w, Tool tool) {
		w.println("	!");
		w.println("	!***********************************************************");
		w.println("	!*  Prcedure procXXX_CheckParts()                          *");
		w.println("	!*                                                         *");
		w.println("	!*  Procedure for checking parts present.                  *");
		w.println("	!*                                                         *");
		w.println("	!*  Date:          Version:    Programmer:       Reason:   *");
		w.println("	!*  13.12.2016      1.0        C. Shaw           created   *");
		w.println("	!***********************************************************");
		w.println("	PROC proc" + this.moduleName + "_CheckParts()");
		w.println();
		for(int i = 0; i < tool.getPartTypes().length; i++) {
			w.println("		IF b" + tool.getPartTypes()[i] + " THEN");
			for(int j = 0; j < tool.getNumNests(); j++) {
				if(tool.getPartSensors()[0].equals("-1")) {
					System.out.println("PartSensors empty");
					w.println();
					w.println("	ERROR");
					w.println("		RETURN;");
					w.println("	ENDPROC");
					return;
				}
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(tool.getPartSensors()[0]);
				m.find();
				String sensor = tool.getPartSensors()[0].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.print("		bPartIn_N" + (tool.getNumNests() - j) + "_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=(di" + sensorMap.get(tool.getPosition()) + indices.get(tool.getPosition()) + "B" + sensor + "=high)");
				} else {
					w.print("		bPartIn_N" + (tool.getNumNests() - j) + "_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + ":=(di" + indices.get(tool.getPosition()) + "B" + sensor + "=high)");
				}
				for(int k = 1; k < tool.getPartSensors().length; k++) {
					m = p.matcher(tool.getPartSensors()[k]);
					m.find();
					sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
					if(sensor.length()==1) {
						w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=high)");
					} else {
						w.print(" or " + "(di" + indices.get(tool.getPosition()) + "B" + tool.getPartSensors()[k].substring(m.start(),m.end()) + "=high)");
					}
				}
				w.println(";");
			}
			w.println("		ENDIF");
		}
		w.println();
		for(int j = 0; j < tool.getNumNests(); j++) {
			w.println("		IF bPartIn_N" + (tool.getNumNests() - j) + "_" + tool.getFrameGroup() + tool.getPosition().substring(0,1) + " THEN");
			for(int k = 1; k < tool.getPartSensors().length; k++) {
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(tool.getPartSensors()[k]);
				m.find();
				String sensor = tool.getPartSensors()[k].substring(m.start(),m.end());
				if(sensor.length()==1) {
					w.println("				WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensorMap.get(tool.getPosition()) + sensor + ",log1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + (tool.getNumNests() - j) + ",GetMUI_Text(" + (3 + k) + "\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
				} else {
					w.println("				WaitGraphDI di" + indices.get(tool.getPosition()) + "B" + sensor + ",log1,log1,log1\\inLogOr:=bSQ_Frame" + tool.getPosition().substring(0,1) + "ClampingNOK_" + tool.getFrameGroup() + "_N" + (tool.getNumNests() - j) + ",GetMUI_Text(" + (3 + k) + "\\inTextTable:=nTextTableGraph)\\inSV_Value:=2.0;");
				}
			}
			w.println("		ENDIF");
		}
		w.println();
		w.println("	ERROR");
		w.println("		RETURN;");
		w.println("	ENDPROC");
	}
	
	private void addFooter(PrintWriter w) {
		w.println("ENDMODULE");
	}
}
