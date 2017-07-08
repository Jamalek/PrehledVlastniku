import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class Loader extends Konzole{
	private static POIFSFileSystem input;
	private static HSSFWorkbook wbIN;
	private static HSSFSheet sheetIN;

	private static int cols;
	private static int rows;
	
	private static int r = 1;
	private static boolean nacteno = false;
	
	public static void parseFile(File xlsFile) {
		loadFile(xlsFile);
		for (; r < rows; r++) {
			int pocetPrazdnychRadku = getNumEmptyLines();
			if (jePredmetemPU(c("druh_pozemku", r))) {
				Parcela parcela = Parcela.getParcela(
						c("cislo_lv", r), 
						c("typ_evidence", r), 
						c("cislo_parcely", r), 
						c("poddeleni_cisla_par", r), 
						c("vymera", r), 
						c("druh_pozemku", r));
				LV lv = parcela.lv;
				
				
				for (int i = r; i <= r+pocetPrazdnychRadku; i++) {
					nactiVlastniky(lv, pocetPrazdnychRadku);
				}
			}
			r+=getNumEmptyLines();
		}
	}
	
	private static void nactiVlastniky(LV lv, int pocetPrazdnychRadku) {
		if (!lv.seznamVlastniku.isEmpty()) return;
		for (int i = 0; i <= pocetPrazdnychRadku; i++) {
			if (c("cislo_lv",r+i).toString().contains("-")) continue;
			else {
				Vlastnik vlastnik = Vlastnik.getVlastnik(
						c("os_typ", r+i), 
						c("rc", r+i), 
						c("ic", r+i), 
						c("subjekt", r+i), 
						c("adresa", r+i), 
						c("rc_BSM1", r+i), 
						c("subjekt_BSM1", r+i), 
						c("adresa_BSM1", r+i), 
						c("rc_BSM2", r+i), 
						c("subjekt_BSM2", r+i), 
						c("adresa_BSM2", r+i));
				vlastnik.pridejLV(lv, loadIntValue(c("podil_citatel", r+i)), loadIntValue(c("podil_jmenovatel", r+i)));
				lv.pridejVlastnika(vlastnik, loadIntValue(c("podil_citatel", r+i)), loadIntValue(c("podil_jmenovatel", r+i)));
			}
		}
	}

	private static int getNumEmptyLines() {
		int i = r;
		do {
			i++;
			if (i >= rows-1) break;
			} 
		while (c("kraj_kod", i).toString().equalsIgnoreCase("")
				&& !c("subjekt", i).toString().equalsIgnoreCase(""));
		return i - (r+1);
	}

	private static boolean jePredmetemPU(HSSFCell cDruhPozemku) {
		String druh_pozemku = cDruhPozemku.getStringCellValue();
		if (druh_pozemku.equalsIgnoreCase("orná pùda") || 
				druh_pozemku.equalsIgnoreCase("chmelnice") || 
				druh_pozemku.equalsIgnoreCase("vinice") || 
				druh_pozemku.equalsIgnoreCase("zahrada") || 
				druh_pozemku.equalsIgnoreCase("ovocný sad") || 
				druh_pozemku.equalsIgnoreCase("trvalý travní porost") || 
				druh_pozemku.equalsIgnoreCase("")) {
			return true;
		}
		return false;
	}

	private static HSSFCell c(String string, int r) {
		return sheetIN.getRow(r).getCell(sloupec(string));
	}

	private static void loadFile(File xlsFile) {
		try {
			input = new POIFSFileSystem(new FileInputStream(xlsFile));
			wbIN = new HSSFWorkbook(input);
			sheetIN = wbIN.getSheetAt(0);
			rows = sheetIN.getPhysicalNumberOfRows();
			cols = sheetIN.getRow(0).getPhysicalNumberOfCells();
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public static void close() {
		try {
			wbIN.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int sloupec(String hledany_nazev) {
		try {
			for (int c = 0; c < cols; c++) {
				String nazev = sheetIN.getRow(0).getCell(c).getStringCellValue();
				if (nazev.equalsIgnoreCase(hledany_nazev)) {
					return c;
				} 
			}
		} catch (Exception e) {
			System.err.println("Sloupec \""+hledany_nazev+"\" nebyl nalezen.");
			System.exit(0);
		}
		return -1;
	}

	protected static String loadStringValue(HSSFCell cell) {
		try {
			return cell.getStringCellValue();
		} catch (Exception e) {
			try {
				return String.valueOf(cell.getNumericCellValue());
			} catch (Exception e2) {
				return "";
			}
		}
	}

	protected static int loadIntValue(HSSFCell cell) {
		try {
			return (int) cell.getNumericCellValue();
		} catch (Exception e) {
			try {
				return Integer.parseInt(String.valueOf(cell.getStringCellValue()));
			} catch (Exception e2) {
				try {
					Long.parseLong(String.valueOf(cell.getStringCellValue()));
					e2.printStackTrace();
				} catch (Exception e3) {}
				return 0;
			}
		}
	}

	protected static Long loadLongValue(HSSFCell cell) {
		try {
			return (long) cell.getNumericCellValue();
		} catch (Exception e) {
			try {
				return Long.parseLong(String.valueOf(cell.getStringCellValue()));
			} catch (Exception e2) {
				return (long) 0;
			}
		}
	}
}
