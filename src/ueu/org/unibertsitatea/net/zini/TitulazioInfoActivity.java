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

import ueu.org.unibertsitatea.net.zini.data.Titulazioa;
import ueu.org.unibertsitatea.net.zini.data.htmlJsoupHelper;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

public class TitulazioInfoActivity extends FragmentActivity {
	
	private Titulazioa emaitza;
	private ProgressDialog progressDialog;
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();
			if (msg.what == 0) {// Errorea
				Toast.makeText(getApplicationContext(),
						"Titulazioa hutsik dago", Toast.LENGTH_LONG).show();
			} else {
				System.out.println("Horra doa.");
				System.out.println(emaitza.toString());
				setEdukia(emaitza);	
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}

		//setContentView(R.layout.details_activity_layout);
		System.out.println("info activity -- layout sortzera");
		//setContentView(R.layout.titulazio_info_layout);
		setContentView(R.layout.titulazioa_info);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//String s = extras.getString("value");
			final String url = extras.getString("titulazioUrl");
			final String izenburua = extras.getString("izenburua");

			this.progressDialog = ProgressDialog.show(this, " Lanean...",
					" Titulazioa eskuratzen", true, false);
			new Thread() {
				@Override
				public void run() {
					try {
						htmlJsoupHelper hh = new htmlJsoupHelper(url,
								getApplicationContext());
						emaitza = hh.titulazioaEskuratu(izenburua);
						if (!emaitza.isEmpty()) { // Titulazioa eskuratu bada bista
													// eguneratu
							handler.sendEmptyMessage(1);
						} else {
							handler.sendEmptyMessage(0);
						}
					} catch (Exception e) {
						handler.sendEmptyMessage(0);
					}
				}
			}.start();
			//view.setText(s);
		}
	}
	
	private void setEdukia(Titulazioa emaitza) {
		// Eskuratu pantailako elementuak
		TextView izenburua = (TextView) findViewById(R.id.izenburua_info);
		TextView ikasketa_info = (TextView) findViewById(R.id.ikasketa_info);
		TextView ezaugarriak_info = (TextView) findViewById(R.id.ezaugarriak_info);
		TextView kredituak_info = (TextView) findViewById(R.id.kredituak_info);
		TextView unibertsitatea_info = (TextView) findViewById(R.id.unibertsitatea_info);
		TextView unibertsitatea_mota_info = (TextView) findViewById(R.id.unibertsitatea_mota_info);
		TextView fakultatea_info = (TextView) findViewById(R.id.fakultatea_info);
		TextView herria_info = (TextView) findViewById(R.id.herria_info);
		TextView lurraldea_info = (TextView) findViewById(R.id.lurraldea_info);
		TextView info_gehigarria = (TextView) findViewById(R.id.info_gehiago);
		
		izenburua.setText(emaitza.izenburua);
		ikasketa_info.setText(emaitza.ikasketa);
		ezaugarriak_info.setText(emaitza.ezaugarriak);
		kredituak_info.setText(emaitza.kredituak);
		unibertsitatea_info.setText(emaitza.unibertsitatea);
		unibertsitatea_mota_info.setText(emaitza.unibertsitate_mota);
		fakultatea_info.setText(emaitza.fakultatea);
		herria_info.setText(emaitza.herria);
		lurraldea_info.setText(emaitza.lurraldea);
		System.out.println("setDatuak: " + emaitza.info_url.trim());
		if (emaitza.info_url.trim() != ""){
			System.out.println("url_info ezartzen");
			info_gehigarria.setMovementMethod(LinkMovementMethod
				.getInstance());
			info_gehigarria.setText((Html.fromHtml("<a href=\""
				+ emaitza.info_url + "\">Informazio gehiago</a>")));
			info_gehigarria.setVisibility(View.VISIBLE);
			if(!info_gehigarria.isShown()){
				System.out.println("ikusgai info gehigarria");
				info_gehigarria.setVisibility(View.VISIBLE);
			}
		}else{
			info_gehigarria.setVisibility(View.INVISIBLE);
		}
	}
}
