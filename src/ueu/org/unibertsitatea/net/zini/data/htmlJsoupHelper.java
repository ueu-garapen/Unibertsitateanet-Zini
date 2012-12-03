/**
 * Copyright (C) 2012  Udako Euskal Unibertsitatea informatikaria@ueu.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ueu.org.unibertsitatea.net.zini.data;

import java.io.IOException;
import java.lang.reflect.Method;

//import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import ueu.org.unibertsitatea.net.zini.Konstanteak;

public class htmlJsoupHelper {
	private Document doc;
	// Hau ez da beharrezkoa DBan informazioa gorde eta jaso egiten bada.
	// public ArrayList<Titulazioa> titulazioak = new ArrayList<Titulazioa>();
	private Context context;
	private final String url;

	public htmlJsoupHelper(String url, Context context) throws IOException {
		this.context = context;

		this.url = url;
		// Ezarri konexioa webgunearekin eduki erauzketa bideratzeko eta klaseko
		// doc atributuan gorde

		this.doc = Jsoup.connect(this.url).timeout(10 * 1000).get();
	}


	/**
	 * Titulazio zerrenda eskuratu unibertsitatea.net webgunetik eta datu-basean
	 * kargatu
	 * 
	 * @throws IOException
	 */
	public void getTitulaziozerrendaDB() throws IOException {
		String url = "";
		int b_start = 0;

		int orriak = titulazioOrriKopurua();

		int uneko_orria = 0;
		// orrietatik nabigatzen joan titulazio guztiak eskuratzeko
		while (uneko_orria < orriak) {
			if (b_start != 0) {

				url = Konstanteak.URL_ZERRENDA + "?b_start=" + b_start;

				this.doc = Jsoup.connect(url).timeout(10 * 1000).get();
			}

			this.titulazioakEskuratuDB();
			uneko_orria++;
			b_start = uneko_orria * 10;
		}
	}


	/**
	 * Orri jakin bateko titulazio zerrenda eskuratzeko. Orri horretako
	 * titulazioak DBan gehitzen dira.
	 * 
	 * @return
	 */
	private void titulazioakEskuratuDB() {
		Element ikasketak_div = this.doc.getElementById("ikasketa-zerrenda");
		DbUtil dbUtil = new DbUtil(this.context);
		dbUtil.open();

		if (ikasketak_div != null) {
			Elements ikasketaElements = ikasketak_div
					.getElementsByAttributeValue("class", "ikasketa");
			for (Element ikasketaElement : ikasketaElements) {
				// printTitulazioa(ikasketaElement);
				// Titulazioa objektua sortu
				Titulazioa tit = gordeTitulazioa(ikasketaElement);
				// TODO DB-an errorerik gertatu bada tratatu
				// Titulazioa datubasean gorde. Geroago informazio gehiagorekin
				// osatuko da.
				dbUtil.insertTitulazioa(tit, false); // buelta honetan
														// titulazioaren
														// informazioa ez dago
														// guztiz osatuta:
														// izenburua, ikasketa,
														// fakultatea,
														// unibertsitatea, url,
														// herria, lurraldea
														// eremuak osatu dira
														// soilik
				// zerrendan gorde. Hau ez da beharrezkoa DBarekin lan egiten
				// delako. DBa bera dagoelako lotuta ListActivityrekin eta ez
				// zerrenda.
			}
		}
		dbUtil.close();
	}

	/**
	 * Webgunean informazioa hainbat orritan banatuta dago. Orri kopurua
	 * eskuratzen da metodo honen bitartez. Errorea egon bada konexioa 0 orri
	 * direla zehazten da.
	 * 
	 */
	private int titulazioOrriKopurua() {
		int orrikop = 0;
		if (this.doc != null) { // Weborriarekin konexioa ezarri ez bada
			Elements nabigazio_barra = this.doc.getElementsByAttributeValue(
					"class", "listingBar");
			Elements orriak = nabigazio_barra.select("a[href]");
			if (orriak.size() > 0) { // orri bakarra bada ez da erakusten
										// nabigazio
										// barrarik
				orrikop = Integer.parseInt(orriak.last().text());
			}
		}
		return orrikop;
	}

	/**
	 * Sortu Titulazio objektu bat
	 * 
	 * @param tit
	 * @return
	 */
	public Titulazioa gordeTitulazioa(Element tit) {
		Titulazioa titulazioa = new Titulazioa();

		Element izenburua_element = tit.getElementsByAttributeValue("class",
				"titulazioa").first();
		titulazioa.izenburua = izenburua_element.text();

		int pos;

		// Titulazio mota izenburutik eratorri
		if ((pos = titulazioa.izenburua.toLowerCase().indexOf(
				"Gradu bikoitza".toLowerCase())) > 0) {
			titulazioa.ikasketa = titulazioa.izenburua.substring(pos);
		} else if ((pos = titulazioa.izenburua.toLowerCase().indexOf(
				"Gradua".toLowerCase())) > 0) {
			titulazioa.ikasketa = titulazioa.izenburua.substring(pos);
		}

		titulazioa.fakultatea = tit
				.getElementsByAttributeValue("class", "ikasgunea").first()
				.text();
		titulazioa.unibertsitatea = tit
				.getElementsByAttributeValue("class", "erakundea").first()
				.text();
		titulazioa.url = izenburua_element.attr("abs:href");
		titulazioa.herria = tit.getElementsByAttributeValue("class", "herria")
				.first().text();
		titulazioa.lurraldea = tit
				.getElementsByAttributeValue("class", "lurraldea").first()
				.text();

		return titulazioa;
	}

	/**
	 * DEBUG egiteko metodoa. Titulazioa aurkezteko prestatzen da.
	 * 
	 * @param tit
	 */
	public static void printTitulazioa(Element tit) {
		String izenburua;
		String ikasketa = "";
		String unibertsitatea;
		String fakultatea;
		String herria;
		String lurraldea;
		String url;

		Element izenburua_element = tit.getElementsByAttributeValue("class",
				"titulazioa").first();
		izenburua = izenburua_element.text();

		int pos;

		if ((pos = izenburua.indexOf("Gradu bikoitza")) > 0) {
			ikasketa = izenburua.substring(pos);
		} else if ((pos = izenburua.indexOf("Gradua")) > 0) {
			ikasketa = izenburua.substring(pos);
		}


		fakultatea = tit.getElementsByAttributeValue("class", "ikasgunea")
				.first().text();
		unibertsitatea = tit.getElementsByAttributeValue("class", "erakundea")
				.first().text();

		fakultatea = tit.getElementsByAttributeValue("class", "ikasgunea")
				.first().text();
		unibertsitatea = tit.getElementsByAttributeValue("class", "erakundea")
				.first().text();

		herria = tit.getElementsByAttributeValue("class", "herria").first()
				.text();
		lurraldea = tit.getElementsByAttributeValue("class", "lurraldea")
				.first().text();
		url = izenburua_element.attr("abs:href");
	}

	/**
	 * Titulazioaren informazioa osorik badago DBa orduan bertatik eskuratu
	 * bestela unibertsitatea.net webgunetik jaso eta DBan gorde.
	 * 
	 * @return Titulazioa
	 */
	public Titulazioa titulazioaEskuratu(String izenburua) {

		Titulazioa titulazioa = new Titulazioa();
		DbUtil dbUtil = new DbUtil(this.context);
		dbUtil.open();

		if (!dbUtil.titulazioaTaulaHutsik()) { // Datu-basea osaturik badago
			Cursor uneko_kurtsorea = dbUtil.getTitulazioa(this.url);
			if (uneko_kurtsorea.getCount() == 1) { // emaitzarik egon bada eta
													// gainera bakarra bada
													// aztertu ea osorik dagoen
													// informazioa
				uneko_kurtsorea.moveToFirst();
				int osorik = uneko_kurtsorea.getInt(uneko_kurtsorea
						.getColumnIndex("osorik"));

				if (osorik == 1) {
					// Titulazioa DBtik lortu
					titulazioa.izenburua = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("izenburua"));
					titulazioa.ikasketa = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("ikasketa"));
					titulazioa.ezaugarriak = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("ezaugarriak"));
					titulazioa.fakultatea = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("fakultatea"));
					titulazioa.unibertsitatea = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("unibertsitatea"));
					titulazioa.unibertsitate_mota = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("unibertsitate_mota"));
					titulazioa.kredituak = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("kredituak"));
					titulazioa.herria = uneko_kurtsorea
							.getString(uneko_kurtsorea.getColumnIndex("herria"));
					titulazioa.lurraldea = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("lurraldea"));
					titulazioa.url = uneko_kurtsorea.getString(uneko_kurtsorea
							.getColumnIndex("url"));
					titulazioa.info_url = uneko_kurtsorea.getString(uneko_kurtsorea
							.getColumnIndex(DbUtil.KEY_URL_INFO));
				} else {
					titulazioa = titulazioaEskuratuWeb();
					titulazioa.url = this.url;
					// informazio hau (izenburua) ez da eskuratzen web-etik
					// aurretik datu-basean gordeta egon behar delako
					/*titulazioa.izenburua = uneko_kurtsorea
							.getString(uneko_kurtsorea
									.getColumnIndex("izenburua"));
					*/
					titulazioa.izenburua = izenburua;
					// DBko inforazioa ez dagoenez osatuta eguneratu
					dbUtil.eguneratuTitulazioa(titulazioa);
				}
			} else {
				//titulazioa = titulazioaEskuratuWeb();
				//titulazioa.izenburua = izenburua;
				//titulazioa.url = this.url;
				// DBko inforazioa ez dagoenez osatuta eguneratu
				// edo insert egin beharko litzateke? Kurtsorea hutsik egon bada
				// erregistroa DBa ez zegoelako da.
				// kasu hau gerta daiteke? Ez honi deitzen zaiolako zerrendatik,
				// behin datu-basea elikatuta.
				// hala ere eskuz ezabatzen bada
				//dbUtil.insertTitulazioa(titulazioa, true);
				//dbUtil.eguneratuTitulazioa(titulazioa);
			}
			// datu basean ez badago osorik kargatuta eguneratu DBan.
			uneko_kurtsorea.close();
			dbUtil.close();
		}
		return titulazioa;
	}

	private Titulazioa titulazioaEskuratuWeb() {
		Titulazioa titulazioa = new Titulazioa();
		Element ikasketak_div = this.doc.getElementById("titulaziodatuak");

		if (ikasketak_div != null) {
			Elements ikasketaElements = ikasketak_div.select("p");
			for (Element ikasketaElement : ikasketaElements) {
				Element eremua_element = ikasketaElement.getElementsByTag(
						"strong").first();
				if (eremua_element != null) {
					String eremua = eremua_element.text().replace(":", "")
							.trim().toLowerCase();
					Node datua_node = eremua_element.nextSibling();
					String datua = "";
					if (datua_node instanceof TextNode) { // textua bada
						datua = ((TextNode) datua_node).text()
								.replace(": ", "").trim();
						// Erreflexioa erabilita titulazio klasearen setter
						// metodo egokiari deitu
						try {
							Class<?> titulazioa_klasea = Class
									.forName("ueu.org.unibertsitatea.net.zini.data.Titulazioa");
							// Object titulazioa_obj =
							// titulazioa_klasea.newInstance();
							Method myMethod;
							Object paramsObj[] = { datua };
							// paramsObj[0] = datua;
							if (eremua.indexOf("-") >= 0) {
								eremua = eremua.replace("-", "_");
							}
							myMethod = titulazioa_klasea.getDeclaredMethod(
									"set" + eremua,
									new Class[] { String.class });
							myMethod.invoke(titulazioa, paramsObj);
						} catch (ClassNotFoundException e) {

						} catch (NoSuchMethodException e) {
						} catch (Exception e) {
						}
					}

					// Informazio gehiago esteka eskuratu
					Element info_gehiago = ikasketaElements.select("a[href]")
							.last();
					if (info_gehiago != null) {
						String info_gehiago_esteka = info_gehiago
								.attr("abs:href");
						titulazioa.info_url = info_gehiago_esteka;
					}
				}
			}
		}
		return titulazioa;
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

}