import java.io.File;
import java.io.FileInputStream;

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
	
	public static void parseFile2(File xlsFile) {
		loadFile(xlsFile);
		for (; r < rows; r++) {
			int pocetPrazdnychRadku = getNumEmptyLines();
			
			
			for (int i = r; i <= r+pocetPrazdnychRadku; i++) {
				if (neniVPU(c("druh_pozemku", r))) {
					continue;
				} else if (LV.exists(loadIntValue(c("cislo_lv",r))) {
					LV.getLV(loadIntValue(c("cislo_lv",r))
					LV.addParcel();
				} else {
					
				}
				
				p(r+1+" "+(i-r)+" "+c("subjekt", i).toString());
			}
			
			//p(r+1+" "+pocetPrazdnychRadku);
			r+=getNumEmptyLines();
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

	public static void parseFile(File xlsFile) {
		loadFile(xlsFile);
		Parcela parcela;
		LV lv = null;
		Vlastnik vlastnik;
		int procentoMinule = -1;
		for (; r < rows; r++) {
			int procento = (int)(((double) r)/((double) rows)*100.0);
			if (procento != procentoMinule) p("Naèteno: "+procento);
			
			HSSFRow vlastnikRow = sheetIN.getRow(r);
			if (jeNovy(vlastnikRow)) {
				if (neniVPU(c("druh_pozemku", r))) {
					dalsi();
					if (nacteno ) break;
					continue;
				}
				parcela = Parcela.getParcela(
						c("cislo_lv", r), 
						c("typ_evidence", r), 
						c("cislo_parcely", r), 
						c("poddeleni_cisla_par", r), 
						c("vymera", r), 
						c("druh_pozemku", r));
				lv = parcela.lv;
				lv.pridejParcelu(parcela);
			}
			vlastnik = Vlastnik.getVlastnik(
					c("os_typ", r), 
					c("rc", r), 
					c("ic", r), 
					c("subjekt", r), 
					c("adresa", r), 
					c("rc_BSM1", r), 
					c("subjekt_BSM1", r), 
					c("adresa_BSM1", r), 
					c("rc_BSM2", r), 
					c("subjekt_BSM2", r), 
					c("adresa_BSM2", r));
			if (vlastnik != null) {
				vlastnik.pridejLV(lv, loadIntValue(c("podil_citatel", r)), loadIntValue(c("podil_jmenovatel", r)));
				try {
					lv.pridejVlastnika(vlastnik, loadIntValue(c("podil_citatel", r)), loadIntValue(c("podil_jmenovatel", r)));
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

	private static void dalsi() {
		do
			r++;
		while (r<rows && !jeNovy(sheetIN.getRow(r+1)));
	}

	private static boolean neniVPU(HSSFCell cDruhPozemku) {
		String druh_pozemku = cDruhPozemku.getStringCellValue();
		if (druh_pozemku.equalsIgnoreCase("orná pùda") || 
				druh_pozemku.equalsIgnoreCase("chmelnice") || 
				druh_pozemku.equalsIgnoreCase("vinice") || 
				druh_pozemku.equalsIgnoreCase("zahrada") || 
				druh_pozemku.equalsIgnoreCase("ovocný sad") || 
				druh_pozemku.equalsIgnoreCase("trvalý travní porost") || 
				druh_pozemku.equalsIgnoreCase("")) {
			return false;
		}
		return true;
		/*if (druh_pozemku.equalsIgnoreCase("zastavìná plocha a nádvoøí")) return true;
		else return false;*/
	}

	private static HSSFCell c(String string, int r) {
		return sheetIN.getRow(r).getCell(sloupec(string));
	}

	private static boolean jeNovy(HSSFRow currentRow) {
		try {
			return loadIntValue(currentRow.getCell(0)) != 0;
		} catch (Exception e) {
			nacteno = true;
			return true;
		}
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
