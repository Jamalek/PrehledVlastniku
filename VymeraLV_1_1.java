import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class VymeraLV_1_1 {
	static POIFSFileSystem input;
	static HSSFWorkbook wbIN;
	static HSSFSheet sheetIN;
	static HSSFWorkbook wbOUT;
	static HSSFSheet sheetOUT;

	static HSSFRow row;
	static int cols;
	static int rows;
	static int[] rOUT;
	static boolean[] SJM;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		zkonvertuj("W:\\MÁLEK\\Podklady pro zádosti KoPÚ_Dvoriste_Chroustov\\Dvoriste.xls");
	}

	public static void zkonvertuj(String path) throws FileNotFoundException, IOException {
		File fileIN = new File("tmp");
		Files.copy(new File(path).toPath(), fileIN.toPath());
		input = new POIFSFileSystem(new FileInputStream(fileIN));
		wbIN = new HSSFWorkbook(input);
		sheetIN = wbIN.getSheetAt(0);
		wbOUT = new HSSFWorkbook();
		sheetOUT = wbOUT.createSheet(wbIN.getSheetAt(0).getSheetName());
		
		rows = sheetIN.getPhysicalNumberOfRows();
		cols = sheetIN.getRow(0).getPhysicalNumberOfCells();
		rOUT = new int[rows];
		SJM = new boolean[rows];

		row = sheetOUT.createRow(0);
		row.createCell(0).setCellValue("LV");
		row.createCell(1).setCellValue("parcela_kmen");
		row.createCell(2).setCellValue("parcela_podlomeni");
		row.createCell(3).setCellValue("vymera");
		row.createCell(4).setCellValue("vymera_LV");
		row.createCell(5).setCellValue("vymera_podil");
		row.createCell(5).setCellValue("podil_na_celku");
		row.createCell(6).setCellValue("kultura");
		row.createCell(7).setCellValue("subjekt");
		row.createCell(8).setCellValue("adresa");
		row.createCell(9).setCellValue("subjekt_BSM1");
		row.createCell(10).setCellValue("adresa_BSM1");
		row.createCell(11).setCellValue("subjekt_BSM2");
		row.createCell(12).setCellValue("adresa_BSM2");
		row.createCell(13).setCellValue("podil_citatel");
		row.createCell(14).setCellValue("podil_jmenovatel");
		
		dopln_radky();
		vytvor_radky();
		
		skopiruj_sloupec("cislo_lv", 0);
		skopiruj_sloupec("cislo_parcely", 1);
		skopiruj_sloupec("poddeleni_cisla_par", 2);
		skopiruj_sloupec("vymera", 3);
		
		skopiruj_sloupec("druh_pozemku", 6);
		skopiruj_sloupec("subjekt", 7);
		skopiruj_sloupec("adresa", 8);
		skopiruj_sloupec("subjekt_BSM1", 9);
		skopiruj_sloupec("adresa_BSM1", 10);
		skopiruj_sloupec("subjekt_BSM2", 11);
		skopiruj_sloupec("adresa_BSM2", 12);
		skopiruj_sloupec("podil_citatel", 13);
		skopiruj_sloupec("podil_jmenovatel", 14);
		
		nacti_vymery();
		FileOutputStream fileOut = new FileOutputStream(path.substring(0, path.lastIndexOf('.'))+" výstup"+path.substring(path.lastIndexOf('.')));
		wbOUT.write(fileOut);
		fileOut.close();
		fileIN.deleteOnExit();
		System.exit(0);
	}
	
	private static void dopln_radky() {
		for (int r = 1; r < sheetIN.getPhysicalNumberOfRows(); r++) {
			HSSFRow row0 = sheetIN.getRow(r-1);
			HSSFRow row1 = sheetIN.getRow(r);
			try {
				row1.getCell(0).getNumericCellValue();
			} catch (Exception e) {
				skopiruj(row0, row1, sloupec("typrav_kod"));
			}
		}
	}

	private static void skopiruj(HSSFRow from, HSSFRow to, int sloupec) {
		for (int c = 0; c < sloupec; c++) {
			try {
				switch (from.getCell(c).getCellType()) {
				case 0:
					to.createCell(c).setCellValue(from.getCell(c).getNumericCellValue());
					break;
				case 1:
					to.createCell(c).setCellValue(from.getCell(c).getStringCellValue());
					break;
				default:
					break;
				}
			} catch (Exception e) {}
		}
	}

	private static void vytvor_radky() throws IOException {
		int i = 1;
		for (int r = 1; r < rows; r++) {
			row = sheetIN.getRow(r);
			String dp;
			try {
				dp = row.getCell(sloupec("druh_pozemku")).getStringCellValue();
			} catch (Exception e) {
				continue;
			}
			if (dp.equalsIgnoreCase("orná pùda") || dp.equalsIgnoreCase("zahrada") || dp.equalsIgnoreCase("ovocný sad")) {
				sheetOUT.createRow(i);
				rOUT[r] = i;
				i++;
			}
		}
	}

	private static int sloupec(String hledany_nazev) {
		for (int c = 0; c < cols; c++) {
			String nazev = sheetIN.getRow(0).getCell(c).getStringCellValue();
			if (nazev.equalsIgnoreCase(hledany_nazev)) {
				return c;
			} 
		}
		return (Integer) null;
	}

	private static void skopiruj_sloupec(String nazev, int sloupecOUT) {
		boolean BSM = nazev.contains("BSM"); 
		int sloupecIN = sloupec(nazev);
		for (int r = 1; r < rows; r++) {
			if (!BSM || (BSM && SJM[r])) {
				if (rOUT[r] == 0) continue;
				row = sheetOUT.getRow(rOUT[r]);
				try {
					row.createCell(sloupecOUT).setCellValue(sheetIN.getRow(r).getCell(sloupecIN).getNumericCellValue());
				} catch (Exception e) {
					try {
						row.createCell(sloupecOUT).setCellValue(sheetIN.getRow(r).getCell(sloupecIN).getStringCellValue());
					} catch (Exception e2) {
						SJM[r] = true;
					}
				}
			}
		}
	}

	private static void nacti_vymery() {
		int[] vymeraLV = new int[100002];
		for (int rOUT = 1; rOUT < sheetOUT.getPhysicalNumberOfRows(); rOUT++) {
			if (rOUT > 1 && bOUT(rOUT, 1) == bOUT(rOUT-1, 1) && bOUT(rOUT, 2) == bOUT(rOUT-1, 2)) {
				continue;
			}
			row = sheetOUT.getRow(rOUT);
			vymeraLV[(int) row.getCell(0).getNumericCellValue()] += row.getCell(3).getNumericCellValue();
		}
		for (int rOUT = 1; rOUT < sheetOUT.getPhysicalNumberOfRows(); rOUT++) {
			row = sheetOUT.getRow(rOUT);
			double vymera = vymeraLV[(int) row.getCell(0).getNumericCellValue()];
			row.createCell(4).setCellValue((int)(vymera));
			double citatel = row.getCell(13).getNumericCellValue();
			double jmenovatel = row.getCell(14).getNumericCellValue();
			row.createCell(5).setCellValue((int)(vymera*citatel/jmenovatel));
		}
	}

	private static double bOUT(int r, int c) {
		return sheetOUT.getRow(r).getCell(c).getNumericCellValue();
	}
}
