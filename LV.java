import java.util.ArrayList;
import java.util.Collections;


public class LV implements Comparable<LV>{
	public static ArrayList<LV> seznamLV = new ArrayList<LV>();
	
	public ArrayList<Parcela> seznamParcel = new ArrayList<Parcela>();
	public ArrayList<Vlastnik> seznamVlastniku = new ArrayList<Vlastnik>();
	public ArrayList<Integer> seznamPodiluCitatel = new ArrayList<Integer>();
	public ArrayList<Integer> seznamPodiluJmenovatel = new ArrayList<Integer>();
	
	public final int cisloLV;
	public int vymera = 0;
	
	private LV(int cislo_listu_vlastnictvi) {
		this.cisloLV = cislo_listu_vlastnictvi;
		seznamLV.add(this);
	}
	
	public static LV getLV(int cislo_listu_vlastnictvi) {
//		System.out.println(cislo_listu_vlastnictvi);
		for (LV lv : seznamLV) {
			if (lv.cisloLV == cislo_listu_vlastnictvi) {
//				System.out.println(lv.cisloLV);
				return lv;
			}
		}
		LV noveLV = new LV(cislo_listu_vlastnictvi);
		seznamLV.add(noveLV);
//		System.out.println(noveLV.cisloLV);
		return noveLV;
	}
	
	public void pridejVlastnika(Vlastnik vlastnik, int podilCitatel, int podilJmanovatel) {
		if (!seznamVlastniku.contains(vlastnik)) {
			seznamVlastniku.add(vlastnik);
			seznamPodiluCitatel.add(podilCitatel);
			seznamPodiluJmenovatel.add(podilJmanovatel);
		}
	}
	
	public void pridejParcelu(Parcela parcela) {
		if (!seznamParcel.contains(parcela)) {
			seznamParcel.add(parcela);
		}
	}
	
	public static void sectiVymery() {
		for (LV lv : seznamLV) {
			lv.vymera = 0;
			for (Parcela parcela : lv.seznamParcel) {
				lv.vymera += parcela.vymera;
			}
		}
	}
	
	public static void seradParcely() {
		for (LV lv : seznamLV) {
			Collections.sort(lv.seznamParcel);
		}
	}

	public static void seradLV() {
		Collections.sort(LV.seznamLV);
	}

	@Override
	public int compareTo(LV lv) {
		return this.cisloLV - lv.cisloLV;
	}
}
