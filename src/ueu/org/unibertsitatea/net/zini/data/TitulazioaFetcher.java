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

import android.content.Context;
import android.util.Log;

import ueu.org.unibertsitatea.net.zini.Konstanteak;


import java.io.IOException;
import java.util.ArrayList;


public class TitulazioaFetcher {

    private static final String CLASSTAG = TitulazioaFetcher.class.getSimpleName();
    //private static final String QBASE = "http://www.google.com/base/feeds/snippets?bq=[review%20type:restaurant]";
    private String query;
    private final int start;
    //private static final String uninet_url = "http://www.unibertsitatea.net/zer-ikasi-non-ikasi/bilaketa_aurreratua#c1=&b_start=0&c2=Bizkaia&c8=Gradua&c7=historia";
    private static final String QBASE = "http://www.unibertsitatea.net/zer-ikasi-non-ikasi/bilaketa_aurreratua#c1=";
    private Context context;

    /**
     * APIrako deia prestatzeko baina ez da erabiltzen bertsio honetan
     */
    public TitulazioaFetcher(String titulazioMota, String titulazioLurraldea, String titulazioBilaketaIrizpidea, int start, Context context) {

        Log.v(Konstanteak.LOGTAG, " " + TitulazioaFetcher.CLASSTAG + " titulazioLurraldea = " + titulazioLurraldea + " titulazioMota = " + titulazioMota + " start = "
            + start);
        this.context = context;
        this.start = start;
        

        // Eraiki APIrako galdera
        // Bertsio honetan ez da erabiltzen
        this.query = TitulazioaFetcher.QBASE;
        if ((titulazioMota != null) && !titulazioMota.equals("Guztiak")){
        	this.query += "&c8=" + titulazioMota.trim();
        }
        if ((titulazioLurraldea != null) && !titulazioLurraldea.equals("Guztiak")){
        	this.query += "&c2=" + titulazioLurraldea.trim();
        }
        if ((titulazioBilaketaIrizpidea != null) && !titulazioBilaketaIrizpidea.equals("")){
        	this.query += "&c7=" + titulazioBilaketaIrizpidea.trim();
        }        

        this.query += "&b_start=" + this.start;

        Log.v(Konstanteak.LOGTAG, " " + TitulazioaFetcher.CLASSTAG + " query - " + this.query);
    }
    
    public TitulazioaFetcher(Context context) {
    	this.start = 0;
    	this.context = context;
    }

    /**
     * htmlJsoup bidez titulazioen informazioa erauzten da www.unibertsitatea.net webgunetik
     * Titulazio zerrenda bat bueltatzen da. Hala ere, htmlJsoupHelper klase laguntzaileak Datu-basea kargatzen dut. DBa hornitzaile batekin lotuta da, beraz DB eguneratuta zerrenda eguneratzen da.
     * Horregatik ez da beharrezkoa zerrendarik bueltatzea.
     * @return 
     */
    public ArrayList<Titulazioa> getTitulazioak(String url) {
        long startTime = System.currentTimeMillis();
        ArrayList<Titulazioa> results = null;

        try {
        	htmlJsoupHelper hh = new htmlJsoupHelper(url,this.context);
            //results = hh.getTitulaziozerrenda();
        	hh.getTitulaziozerrendaDB();
            long duration = System.currentTimeMillis() - startTime;
            Log.v(Konstanteak.LOGTAG, " " + TitulazioaFetcher.CLASSTAG + " call and parse duration - " + duration);
            return results;
        } catch (Exception e) {
            Log.e(Konstanteak.LOGTAG, " " + TitulazioaFetcher.CLASSTAG, e);
        }
        return results;

    }
    
    public void getTitulazioakDB(String url) throws IOException {
        long startTime = System.currentTimeMillis();

        	htmlJsoupHelper hh = new htmlJsoupHelper(url,this.context);
            //results = hh.getTitulaziozerrenda();
        	hh.getTitulaziozerrendaDB();
            long duration = System.currentTimeMillis() - startTime;
            Log.v(Konstanteak.LOGTAG, " " + TitulazioaFetcher.CLASSTAG + " call and parse duration - " + duration);
    }
}