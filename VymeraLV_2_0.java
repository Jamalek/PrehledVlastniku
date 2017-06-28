import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class VymeraLV_2_0 extends Konzole{
	public static void zkonvertuj(String path) {
		String pripona = path.substring(path.indexOf(".")+1);
		if (pripona.equalsIgnoreCase("csv")) {
			File xlsFile = new File(csvToXLS(path));
			Loader.parseFile(xlsFile);
			Vypisy.vypis(path);
			xlsFile.delete();
		} else if (pripona.equalsIgnoreCase("xls")) {
			Loader.parseFile(new File(path));
			Vypisy.vypis(path);
		} else GUI.textPane.setText("Neznámý formát");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		File xlsFile = new File(csvToXLS("pøehled parcel.csv"));
		VymeraLV_2_0.zkonvertuj(xlsFile.getAbsolutePath());
		xlsFile.delete();
	}
	
	public static String csvToXLS(String csvFileAddress) {
	    try {
	        String xlsFileAddress = csvFileAddress.split("\\.")[0]+".xls";
	        HSSFWorkbook workBook = new HSSFWorkbook();
	        HSSFSheet sheet = workBook.createSheet("sheet1");
	        String currentLine=null;
	        int RowNum=0;
	        BufferedReader br = new BufferedReader(new FileReader(csvFileAddress));
	        while ((currentLine = br.readLine()) != null) {
	        	Konzole.p(currentLine);
	            String str[] = currentLine.split(";");
	            HSSFRow currentRow=sheet.createRow(RowNum);
	            RowNum++;
	            for(int i=0;i<str.length;i++){
	                currentRow.createCell(i).setCellValue(str[i].replaceAll("\"", ""));
	            }
	        }

	        FileOutputStream fileOutputStream =  new FileOutputStream(xlsFileAddress);
	        workBook.write(fileOutputStream);
	        fileOutputStream.close();
	        workBook.close();
	        return xlsFileAddress;
	    } catch (Exception ex) {
	        System.err.println("Chyba pøi konverzi do xls.");
	        System.exit(0);
	        return null;
	    }
	}
}
