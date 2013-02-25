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

package ueu.org.unibertsitatea.net.zini;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import ueu.org.unibertsitatea.net.zini.data.DbUtil;
import info.guardianproject.database.sqlcipher.SQLiteException;
import ueu.org.unibertsitatea.net.zini.Konstanteak;
import ueu.org.unibertsitatea.net.zini.data.TitulazioaFetcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Zini aplikazioaren hasierako pantaila da hau.
 * 
 * @author UEU
 * 
 */
public class HasierakoActivity extends Activity {
	private Spinner lurraldea;
	private Button eskuratuTitulazioak;
	private Spinner titulazio_mota;
	private EditText bilaketa_irizpidea;

	private DbUtil dbUtil;

	private ProgressDialog progressDialog;

	// Titulazioaren karga kontrolatzko kudeatzailea
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (msg.what == 1) {
				// errorea sarean, mezua erakutsi eta aplikazioa bukatu
				StringBuilder validationText = new StringBuilder();
				validationText
						.append("Arazoak egon dira titulazioak eskuratzean.\nEgiaztatu sareko konexioa. \nAplikazioa itxiko da");
				new AlertDialog.Builder(HasierakoActivity.this)
						.setTitle("Errorea")
						.setMessage(validationText.toString())
						.setPositiveButton(
								"Segi",
								new android.content.DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int arg1) {
										HasierakoActivity.this.finish();
									}
								}).show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Ezarri leihoaren izenburuaren kolorea eta fondoa
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView.setBackgroundColor(Color.rgb(111, 201, 0));
			}
		}

		this.setTitleColor(Color.WHITE);


		// Eskuratu eta prestatu pantailako elementuak
		this.setContentView(R.layout.titulazio_irizpideak);

		this.titulazio_mota = (Spinner) findViewById(R.id.titulazioa);
		this.bilaketa_irizpidea = (EditText) findViewById(R.id.bilaketa_irizpidea);
		this.lurraldea = (Spinner) findViewById(R.id.lurraldea);
		this.eskuratuTitulazioak = (Button) findViewById(R.id.get_titulazioak_button);

		ArrayAdapter<String> lurraldeak = new ArrayAdapter<String>(this,
				R.layout.spinner_view, getResources().getStringArray(
						R.array.lurraldeak));
		lurraldeak.setDropDownViewResource(R.layout.spinner_view_dropdown);
		this.lurraldea.setAdapter(lurraldeak);

		ArrayAdapter<String> titulazio_mota = new ArrayAdapter<String>(this,
				R.layout.spinner_view, getResources().getStringArray(
						R.array.titulazio_motak));
		titulazio_mota.setDropDownViewResource(R.layout.spinner_view_dropdown);
		this.titulazio_mota.setAdapter(titulazio_mota);

		// Bilaketa bideratu
		this.eskuratuTitulazioak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleGetTitulazioak();
			}

		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		// Egiaztatu DB elikatuta dagoen
		// eta errebisatu datu-basearen iraungitze-data
		Cursor myCursor = null;
		dbUtil = new DbUtil(getApplicationContext());
		dbUtil.open();
		// eskuratu DBko titulazio guztiak
		try {
			myCursor = dbUtil.fetchAllData();
		} catch (SQLiteException e) {
			e.printStackTrace();
			System.out.println("SQLiteException");
			dbUtil.close();
		}

		// Kalkulatu iraungitze-data
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date uneko_data = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(uneko_data);
		Integer uneko_urtea = calendar.get(Calendar.YEAR);
		Date iraungitze_data = new Date();;
		Date sortze_data;
		
		//iraungitze_data = new Date();
		try {
			//Titulazioen Datu basea urtero eguneratzen da otsailerako
			iraungitze_data = new Date(dfm.parse(uneko_urtea.toString() + "-02-01 00:00:00").getTime());
			// lortu DBaren sortze data eta gehitu iraupena lortzeko iraungitze
			// data
			sortze_data = new Date(dfm.parse(dbUtil.getIranugitzeData())
					.getTime());
		} catch (ParseException e) {
			// Errorea egon bada DB-an dagoen dataren formatuarekin bueltatu
			// uneko data eta horrela lokalean dagoen DBarekin jarraituko da
			// lanean.
			// Printzipioz hau ez litzateke gertatu beharko data modu
			// automatikoan sortzen delako DBa sortzean
			e.printStackTrace();
			
			sortze_data = new Date();
		}
		if (myCursor.getCount() <= 0
				|| (uneko_data.compareTo(iraungitze_data)>0 && sortze_data.compareTo(iraungitze_data) < 0)) { // DBa
																// ez
																// badago
																// kargatuta
																// edo
																// informazioa
																// iraungituta
																// badago
			// DB kargatu unibertsitatea.net zerbitzutik. Horretarako sareko
			// konexioa behar da
			if (this.isNetworkAvailable()) {
				// Eskuratu titulazioak www.unibertsitatea.net webgunetik
				this.kargatuTitulazioak();
			} else {
				// Sareko konexioa ez dagoenez errorea ohartarazi eta gero
				// aplikazioa itxi
				StringBuilder validationText = new StringBuilder();
				validationText
						.append("Sarea ez dago eskuragarri eta beharrezkoa da. Aplikazioa itxiko da");
				new AlertDialog.Builder(this)
						.setTitle("Sare-arazoa:")
						.setMessage(validationText.toString())
						.setPositiveButton(
								"Ados",
								new android.content.DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int arg1) {
										//Ohar-leihoa itxi
										finish();
									}
								}).show();
				validationText = null;
				// Kurtsorea existitzen bada itxi eta berarekin datu-basea ere
				if (myCursor != null) {
					myCursor.close();
					dbUtil.close();
				}
				// Aplikazioa itxi
				//finish();
			}
		}

		// Kurtsorea existitzen bada itxi eta berarekin datu-basea ere
		if (myCursor != null) {
			myCursor.close();
			dbUtil.close();
		}
		
	}
	
	// Menua XML fitxategia hasieratu (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menua, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        	case R.id.menu_laguntza:
				Intent intent_laguntza = new Intent(getApplicationContext(),
						Laguntza.class);
				startActivity(intent_laguntza);        		
        		return true;
 
        	case R.id.menu_honiburuz:
				Intent intent_honiburuz = new Intent(getApplicationContext(),
						Honiburuz.class);
				startActivity(intent_honiburuz);        		
        		return true;
  			
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }	

	private void handleGetTitulazioak() {
		if (!validate()) {
			return;
		}

		// Titulazio zerrenda erakutsi
		Intent intent = new Intent(getApplicationContext(),
				TitulazioZerrendaActivity.class);

		// Bilaketa irizpideak parametro moduan pasatu
		intent.putExtra("lurraldea", this.lurraldea.getSelectedItem()
				.toString());
		intent.putExtra("ikasketa", this.titulazio_mota.getSelectedItem()
				.toString());
		intent.putExtra("irizpidea", this.bilaketa_irizpidea.getText()
				.toString());
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Formularioaren osaketa balioztatu
	private boolean validate() {
		boolean valid = true;
		StringBuilder validationText = new StringBuilder();
		// izenburua zehaztu behar da gutxienez
		if ((this.bilaketa_irizpidea.getText() == null)
				|| this.bilaketa_irizpidea.getText().toString().equals("")) {
			validationText.append("Bilaketa irizpidea ezarri");
			valid = false;
		}
		if (!valid) {
			new AlertDialog.Builder(this)
					.setTitle("Errorea")
					.setMessage(validationText.toString())
					.setPositiveButton(
							"Segi",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int arg1) {
								}
							}).show();
			validationText = null;
		}
		return valid;
	}

	/**
	 * Unibertsitatea.net webgunetik titulazioak eskuratzeko klase laguntzaile
	 * bat erabiltzen da, TitulazioaFetcher. Titulazioak DBtik kargatzen dira
	 * bertan baldin badaude bestela unibertsitatea.net-etik erauzten dira eta
	 * DBam gorde. Dena dela, beti DBtik hartuko dira titulazioak, horregatik
	 * aldez aurretik DBa elikatu beharko da
	 */
	private void kargatuTitulazioak() {
		// TODO sare-konexioa eteten bada kargatzen ari garen bitartean
		// ezabatu datu-basea guztiz osatuta ez dagoelako eta ez delako
		// baliozkoa.
		final TitulazioaFetcher rf = new TitulazioaFetcher(
				getApplicationContext());

		this.progressDialog = ProgressDialog.show(this, " Lanean...",
				" Titulazioak eskuratzen eta datu-base lokala eguneratzen",
				true, false);

		// Eskuratu titulazioak hari berri batean ProgressDialog/Handler
		// elementuei bidaltzeko
		// behin bukatuta bidali mezu huts bat handler-ari
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				try {
					rf.getTitulazioakDB(Konstanteak.URL_ZERRENDA); // localhost
																	// emuladorean
																	// 10.0.2.2
																	// da
					what = 0;

				} catch (Exception e) {
					// Baliorik gabe utzi DBa sare konexioan arazoak egon
					// direlako eta DBa ez dagoelako osaturik
					dbUtil.open();
					dbUtil.ezbatuDB();
					dbUtil.close();
					what = 1;
				}

				handler.sendEmptyMessage(what);
			}
		}.start();
	}

	public boolean isNetworkAvailable() {
		// Sareko konexioa badagoen egiaztatzeko funtzioa
		Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.d("NETWORK", "No network connection available");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}