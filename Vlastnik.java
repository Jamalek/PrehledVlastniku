import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.poi.hssf.usermodel.HSSFCell;


public class Vlastnik extends Loader implements Comparable<Vlastnik>{
	public static ArrayList<Vlastnik> seznamVlastniku = new ArrayList<Vlastnik>();

	public ArrayList<LV> seznamLV = new ArrayList<LV>();
	public ArrayList<Integer> seznamPodiluCitatel = new ArrayList<Integer>();
	public ArrayList<Integer> seznamPodiluJmenovatel = new ArrayList<Integer>();
	
	public long rc;
	public long ic;
	public String subjekt;
	public String adresa;

	public Boolean SJM;
	public long rc_BSM1;
	public String subjekt_BSM1;
	public String adresa_BSM1;
	public long rc_BSM2;
	public String subjekt_BSM2;
	public String adresa_BSM2;
	
	public int vymera = 0;

	static int pocet;
	public static Vlastnik getVlastnik(HSSFCell os_typ, HSSFCell rc, HSSFCell ic, HSSFCell subjekt, HSSFCell adresa, HSSFCell rc_BSM1, HSSFCell subjekt_BSM1, HSSFCell adresa_BSM1, HSSFCell rc_BSM2, HSSFCell subjekt_BSM2, HSSFCell adresa_BSM2) {
		for (Vlastnik vlastnik : seznamVlastniku) {
			try {
			if (vlastnik.rc != 0 && vlastnik.rc == loadLongValue(rc)) {
				return vlastnik;
			} else if (vlastnik.ic != 0 && vlastnik.ic == loadLongValue(ic)) {
				return vlastnik;
			} else if (vlastnik.rc_BSM1 != 0 && vlastnik.rc_BSM1 == loadLongValue(rc_BSM1)) {
				return vlastnik;
			} else if (vlastnik.rc == 0 && vlastnik.ic == 0 && vlastnik.subjekt != null && vlastnik.subjekt.equalsIgnoreCase(loadStringValue(subjekt))) {
				return vlastnik;
			}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		Vlastnik novyVlastnik = new Vlastnik(os_typ, rc, ic, subjekt, adresa, rc_BSM1, subjekt_BSM1, adresa_BSM1, rc_BSM2, subjekt_BSM2, adresa_BSM2);
		pridejDoSznamu(novyVlastnik);
		return novyVlastnik;
	}

	private static void pridejDoSznamu(Vlastnik novyVlastnik) {
		// TODO Auto-generated method stub
		seznamVlastniku.add(novyVlastnik);
	}

	private Vlastnik(HSSFCell os_typ, HSSFCell rc, HSSFCell ic, HSSFCell subjekt, HSSFCell adresa, HSSFCell rc_BSM1, HSSFCell subjekt_BSM1, HSSFCell adresa_BSM1, HSSFCell rc_BSM2, HSSFCell subjekt_BSM2, HSSFCell adresa_BSM2) {
		try {
			if (loadStringValue(os_typ).equalsIgnoreCase("BSM")) {
				this.subjekt = loadStringValue(subjekt);
				this.rc_BSM1 = loadLongValue(rc_BSM1);
				this.subjekt_BSM1 = loadStringValue(subjekt_BSM1);
				this.adresa_BSM1 = loadStringValue(adresa_BSM1);
				this.rc_BSM2 = loadLongValue(rc_BSM2);
				this.subjekt_BSM2 = loadStringValue(subjekt_BSM2);
				this.adresa_BSM2 = loadStringValue(adresa_BSM2);
			} else {
				this.rc = loadLongValue(rc);
				this.ic = loadLongValue(ic);
				this.subjekt = loadStringValue(subjekt);
				this.adresa = loadStringValue(adresa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pridejLV(LV lv, int citatel, int jmenovatel) {
		if (!seznamLV.contains(lv)) {
			seznamLV.add(lv);
			seznamPodiluCitatel.add(citatel);
			seznamPodiluJmenovatel.add(jmenovatel);
		}
	}
	
	public static void sectiVymery() {
		LV.sectiVymery();
		for (Vlastnik vlastnik : seznamVlastniku) {
			vlastnik.vymera = 0;
			for (int i = 0; i < vlastnik.seznamLV.size(); i++) {
				vlastnik.vymera += ((double)vlastnik.seznamLV.get(i).vymera
						/ (double)vlastnik.seznamPodiluJmenovatel.get(i)
						* (double)vlastnik.seznamPodiluCitatel.get(i));
			}
		}
	}

	@Override
	public int compareTo(Vlastnik vlastnik) {
		return vlastnik.vymera - this.vymera;
	}
	
	public static void seradPodleVymery() {
		Collections.sort(seznamVlastniku);
	}
}
