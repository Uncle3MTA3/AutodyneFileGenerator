//import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class GeneratePlacement {
	
	/**
	 * Creates an Excel file with details of tooling placement.
	 * The filepath must be the absolute path.
	 * <p>
	 * This method will fail if the file is already open
	 *
	 * @param  filePath  the string representation of the directory to place the file
	 * @param  tools List of ToolObject to be iterated through
	 */
	
	@SuppressWarnings("deprecation")
	public void createFile(List<Tool> tools, String filePath, String fileName) {
		String title = fileName.substring(0,fileName.indexOf("."));
		
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        CellStyle blankStyle = workbook.createCellStyle();
        blankStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        blankStyle.setBorderBottom(CellStyle.BORDER_THIN);
        blankStyle.setBorderTop(CellStyle.BORDER_THIN);
        blankStyle.setBorderRight(CellStyle.BORDER_THIN);
        blankStyle.setBorderLeft(CellStyle.BORDER_THIN);
        
        int rowCount = 0;
        for(Tool tool : tools) {
        	Row row = sheet.createRow(rowCount++);
            
        	String[] values = {tool.getSerial(),
        			tool.getMachine(),
        			tool.getModuleName().substring(0, tool.getModuleName().length() - 3),
        			tool.getPosition(),
        			tool.getVariant(),
        			tool.getPathModule(),
        			tool.getModuleName()};
        			//"";
        	for(int i = 0; i < values.length; i++) {
        		Cell cell = row.createCell(i);
        		if(row.getRowNum() % 2 != 0) {
                	cell.setCellStyle(style);
                } else {
                	cell.setCellStyle(blankStyle);
                }
        		cell.setCellValue(values[i]);
        	}
        }
        
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        
        try {
        	FileOutputStream outputStream = new FileOutputStream(filePath + "\\" + title + ".xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
