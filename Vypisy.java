import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class Vypisy {
	private static String path;
	private static HSSFWorkbook workBook;
	private static HSSFSheet sheet;
	
	private static HSSFRow row;
	
	private static int prm = 1;
	private static int rm = 0;

	private static void v(int integer, int i, int j) {
		v((long) integer, i, j);
	}

	private static void v(long cislo, int i, int j) {
		if (cislo == 0) return;
		if (i > rm) rm = i;
		try {
			sheet.getRow(i).getCell(j).setCellValue(cislo);
		} catch (Exception e) {
			try {
				sheet.getRow(i).createCell(j).setCellValue(cislo);
			} catch (Exception e2) {
				try {
					sheet.createRow(i).createCell(j).setCellValue(cislo);
				} catch (Exception e3) {
					System.err.println("Èíslo \""+cislo+"\" nebylo vepsáno do buòky");
				}
			}
		}
	}

	private static void v(String string, int i, int j) {
		if (string == null) return;
		if (i > rm) rm = i;
		try {
			sheet.getRow(i).getCell(j).setCellValue(string);
		} catch (Exception e) {
			try {
				sheet.getRow(i).createCell(j).setCellValue(string);
			} catch (Exception e2) {
				try {
					sheet.createRow(i).createCell(j).setCellValue(string);
				} catch (Exception e3) {
					System.err.println("Øetìzec \""+string+"\" nebyl vepsán do buòky");
				}
			}
		}
	}
	
	public static void vypis(String path) {
		
		init(path);
		vypisNejvetsiVlastniky();
		vypisParcely();
		close();
	}
	
	private static void vypisParcely() {
		LV.seradLV();
		LV.seradParcely();
		LV.sectiVymery();
//		for (LV lv : LV.seznamLV) {
//			System.out.println(lv.cisloLV);
//			for (Vlastnik vlastnik : lv.seznamVlastniku) {
//				System.out.println("\t"+vlastnik.subjekt);
//			}
//		}
		sheet = workBook.createSheet("Listy vlastnictví");
		
		row = sheet.createRow(0);
		v("LV", 0, 0);
		v("výmìra LV m2", 0, 1);

		v("parcela", 0, 2);
		v("výmìra parcely m2", 0, 3);
		v("kultura", 0, 4);

		v("subjekt", 0, 5);
		v("adresa", 0, 6);
		v("iè", 0, 7);
		v("subjekt SJM1", 0, 8);
		v("adresa SJM1", 0, 9);
		v("subjekt SJM2", 0, 10);
		v("adresa SJM2", 0, 11);
		v("podíl", 0, 12);

		ArrayList<LV> seznamLV = LV.seznamLV;
		LV lv;
		ArrayList<Parcela> seznamParcel;
		Parcela parcela;
		ArrayList<Vlastnik> seznamVlastniku;
		Vlastnik vlastnik;
		ArrayList<Integer> seznamPodiluCitatel;
		int podilCitatel;
		ArrayList<Integer> seznamPodiluJmenovatel;
		int podilJmenovatel;
		hranice();
		for (int i = 0; i < seznamLV.size(); i++) {
			lv = seznamLV.get(i);
			seznamParcel = lv.seznamParcel;
			seznamVlastniku = lv.seznamVlastniku;
			seznamPodiluCitatel = lv.seznamPodiluCitatel;
			seznamPodiluJmenovatel = lv.seznamPodiluJmenovatel;
			v(lv.cisloLV, prm, 0);
			v(lv.vymera, prm, 1);
			for (int j = 0; j < seznamParcel.size(); j++) {
				parcela = seznamParcel.get(j);
				if (parcela.podlomeni == 0) {
					v(String.valueOf(parcela.kmen), prm + j, 2);
				} else {
					v(parcela.kmen+"/"+parcela.podlomeni, prm + j, 2);
				}
				v(parcela.vymera, prm + j, 3);
				v(parcela.druh_pozemku, prm + j, 4);
			}
			for (int j = 0; j < seznamVlastniku.size(); j++) {
				vlastnik = seznamVlastniku.get(j);
				podilCitatel = seznamPodiluCitatel.get(j);
				podilJmenovatel = seznamPodiluJmenovatel.get(j);
				v(vlastnik.subjekt, prm + j, 5);
				v(vlastnik.adresa, prm + j, 6);
				v(vlastnik.ic, prm + j, 7);
				v(vlastnik.subjekt_BSM1, prm + j, 8);
				v(vlastnik.adresa_BSM1, prm + j, 9);
				v(vlastnik.subjekt_BSM2, prm + j, 10);
				v(vlastnik.adresa_BSM2, prm + j, 11);
				v(podilCitatel+"/"+podilJmenovatel, prm + j, 12);
			}
			hranice();
			prm = ++rm;
		}
	}
	
	private static void hranice() {
		row = sheet.getRow(rm);
		int cells = sheet.getRow(0).getPhysicalNumberOfCells();
		for (int i = 0; i < cells; i++) {
			try {
				row.getCell(i).setCellStyle(bottomBorder);
			} catch (Exception e) {
				row.createCell(i).setCellStyle(bottomBorder);
			}
		}
	}

	private static void vypisNejvetsiVlastniky() {
		Vlastnik.sectiVymery();
		Vlastnik.seradPodleVymery();
		sheet = workBook.createSheet("Vlastníci podle vel. podílu");
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("subjekt");
		row.createCell(1).setCellValue("podílová výmìra");
		row.createCell(2).setCellValue("seznam LV");
		row.createCell(3).setCellValue("výmìra LV m2");
		for (int r = 0; r < Vlastnik.seznamVlastniku.size(); r++) {
			row = sheet.createRow(r+1);
			Vlastnik vlastnik = Vlastnik.seznamVlastniku.get(r);
			if (vlastnik.vymera == 0) return;
			row.createCell(0).setCellValue(vlastnik.subjekt);
			row.createCell(1).setCellValue(vlastnik.vymera);
			String lvcka = vlastnik.seznamLV.get(0).cisloLV+"";
			String podilNaLV = ((int)((double)vlastnik.seznamLV.get(0).vymera*vlastnik.seznamPodiluCitatel.get(0)/vlastnik.seznamPodiluJmenovatel.get(0)))+"";
			for (int i = 1; i < vlastnik.seznamLV.size(); i++) {
				int podilNaLVint = (int)((double)vlastnik.seznamLV.get(i).vymera*vlastnik.seznamPodiluCitatel.get(i)/vlastnik.seznamPodiluJmenovatel.get(i));
				if (podilNaLVint == 0) continue;
				lvcka += ", " + vlastnik.seznamLV.get(i).cisloLV;
				podilNaLV += ", " + podilNaLVint;
			}
			if (podilNaLV.charAt(0) == '0') {
				lvcka = lvcka.substring(3);
				podilNaLV = podilNaLV.substring(podilNaLV.indexOf(" ")+1);
			}
			row.createCell(2).setCellValue(lvcka);
			row.createCell(3).setCellValue(podilNaLV);
		}
	}

	private static HSSFCellStyle bottomBorder;

	public static void init(String path) {
		Vypisy.path = path;
		workBook = new HSSFWorkbook();
		bottomBorder = workBook.createCellStyle();
		bottomBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	}

	public static void close() {
		try {
			FileOutputStream fileOut = new FileOutputStream(path.substring(0, path.lastIndexOf('.'))+" pÅ™ehled vlastníkÅ¯ a parcel"+".xls");
			workBook.write(fileOut);
			workBook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
