import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;


public class Parcela extends Loader implements Comparable<Parcela>{
	private static ArrayList<Parcela> seznamParcel = new ArrayList<Parcela>();
	
	public final LV lv;
	public final String typ_evidence;
	public final int kmen;
	public final int podlomeni;
	public final int vymera;
	public final String druh_pozemku;
	
	private Parcela(HSSFCell cisloLV, HSSFCell typ_evidence, HSSFCell kmen, HSSFCell podlomeni, HSSFCell vymera, HSSFCell druh_pozemku) {
		this.lv = LV.getLV(loadIntValue(cisloLV));
		this.typ_evidence = loadStringValue(typ_evidence);
		this.kmen = loadIntValue(kmen);
		this.podlomeni = loadIntValue(podlomeni);
		this.vymera = loadIntValue(vymera);
		this.druh_pozemku = loadStringValue(druh_pozemku);
		lv.pridejParcelu(this);
	}
	
	public static Parcela getParcela(HSSFCell cisloLV, HSSFCell typ_evidence, HSSFCell kmen, HSSFCell podlomeni, HSSFCell vymera, HSSFCell druh_pozemku) {
		for (Parcela parcela : seznamParcel) {
			if (parcela.kmen == loadIntValue(kmen) && parcela.podlomeni == loadIntValue(podlomeni) && parcela.lv.cisloLV == loadIntValue(cisloLV)) {
				return parcela;
			}
		}
		Parcela novaParcela = new Parcela(cisloLV, typ_evidence, kmen, podlomeni, vymera, druh_pozemku);
		seznamParcel.add(novaParcela);
		return novaParcela;
	}

	@Override
	public int compareTo(Parcela parcela) {
		return (int) (this.kmen-parcela.kmen+(double)(this.podlomeni-parcela.podlomeni)/1000);
	}
}
